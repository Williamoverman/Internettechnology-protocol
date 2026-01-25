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
import java.security.MessageDigest;
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

            messenger.sendOK("FILE_UPLOAD_READY");

            try (InputStream in = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                long remaining = transfer.getSize();
                while (remaining > 0) {
                    int toRead = (int) Math.min(buffer.length, remaining);
                    int read = in.read(buffer, 0, toRead);
                    if (read == -1) break;
                    fos.write(buffer, 0, read);
                    remaining -= read;
                }
                if (remaining != 0) {
                    connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_UPLOAD_DONE", 11007));
                    tempFile.delete();
                    connection.exit();
                    return;
                }
            }

            String calculatedChecksum = calculateChecksum(tempFile);
            if (!calculatedChecksum.equals(transfer.getChecksum())) {
                connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_UPLOAD_DONE", 11006));
                tempFile.delete();
                connection.exit();
                return;
            }

            FileTransferManager.getInstance().setUploadComplete(transferId, true);

            connection.getWriter().println(MessageFormatter.createOkResponse("FILE_UPLOAD_DONE"));
            connection.exit();
        } catch (Exception e) {
            connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_UPLOAD_DONE", 11007));
            connection.exit();
        }
    }

    private String calculateChecksum(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) > 0) {
                md.update(buffer, 0, len);
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
