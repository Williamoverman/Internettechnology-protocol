package protocol.commands.filetransfer;

import com.google.gson.reflect.TypeToken;
import connection.ClientConnection;
import domain.filetransfer.FileTransfer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;

import java.io.*;
import java.lang.reflect.Type;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

public record UploadCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(jsonBody, mapType);
        String transferId = data.get("transfer_id");
        if (transferId == null) {
            messenger.sendError("FILE_UPLOAD_READY", 11005);
            return;
        }

        FileTransfer transfer = FileTransferManager.getInstance().getTransfer(transferId);
        if (transfer == null) {
            messenger.sendError("FILE_UPLOAD_READY", 11005);
            return;
        }

        try {
            File tempFile = File.createTempFile("transfer_", null);
            transfer.setTempFile(tempFile);

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            InputStream in = new LimitedInputStream(connection.getInputStream(), transfer.getSize());
            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 DigestOutputStream dos = new DigestOutputStream(fos, md)) {

                messenger.sendOK("FILE_UPLOAD_READY");

                long received = in.transferTo(dos);

                if (received != transfer.getSize()) {
                    throw new IOException("Size mismatch: " + received + " != " + transfer.getSize());
                }

                String computed = HexFormat.of().formatHex(md.digest());
                if (!computed.equalsIgnoreCase(transfer.getChecksum())) {
                    throw new IOException("Checksum mismatch");
                }
            }

            FileTransferManager.getInstance().setUploadComplete(transferId, true);
            connection.getWriter().println(MessageFormatter.createOkResponse("FILE_UPLOAD_DONE"));

        } catch (Exception e) {
            connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_UPLOAD_DONE",
                    e instanceof IOException && e.getMessage().contains("checksum") ? 11006 : 11007));
            File temp = transfer.getTempFile();
            if (temp != null && temp.exists()) {
                temp.delete();
            }
        } finally {
            connection.exit();
        }
    }

    private static class LimitedInputStream extends FilterInputStream {
        private long left;

        public LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.left = limit;
        }

        @Override
        public int read() throws IOException {
            if (left <= 0) {
                return -1;
            }
            int result = super.read();
            if (result != -1) {
                left--;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (left <= 0) {
                return -1;
            }
            if (len > left) {
                len = (int) left;
            }
            int result = super.read(b, off, len);
            if (result != -1) {
                left -= result;
            }
            return result;
        }
    }
}
