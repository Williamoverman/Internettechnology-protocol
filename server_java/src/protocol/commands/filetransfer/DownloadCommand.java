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
import java.util.Map;

public record DownloadCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(jsonBody, mapType);
        String transferId = data.get("transfer_id");
        if (transferId == null) {
            messenger.sendError("FILE_DOWNLOAD_READY", 11005);
            return;
        }

        FileTransfer transfer = FileTransferManager.getInstance().getTransfer(transferId);
        if (transfer == null) {
            messenger.sendError("FILE_DOWNLOAD_READY", 11005);
            return;
        }

        while (!transfer.isUploadComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }

        File tempFile = transfer.getTempFile();
        if (tempFile == null || !tempFile.exists()) {
            messenger.sendError("FILE_DOWNLOAD_READY", 11007);
            return;
        }

        try {
            messenger.sendOK("FILE_DOWNLOAD_READY");

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                LengthPrefixedOutputStream out = new LengthPrefixedOutputStream(dataOutputStream);
                fileInputStream.transferTo(out);
                dataOutputStream.writeInt(0);
                dataOutputStream.flush();
            }

            connection.getWriter().println(MessageFormatter.createOkResponse("FILE_DOWNLOAD_DONE"));
            connection.getWriter().flush();

            FileTransferManager.getInstance().setDownloadComplete(transferId);

            connection.exit();
        } catch (Exception e) {
            connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_DOWNLOAD_DONE", 11007));
            connection.exit();
        }
    }

    private static class LengthPrefixedOutputStream extends OutputStream {
        private final DataOutputStream out;

        LengthPrefixedOutputStream(DataOutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            byte[] single = {(byte) b};
            write(single, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (len <= 0) return;
            out.writeInt(len);
            out.write(b, off, len);
        }
    }
}
