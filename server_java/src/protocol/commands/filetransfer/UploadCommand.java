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
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
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
            File temp = File.createTempFile("transfer_", null);
            transfer.setTempFile(temp);

            messenger.sendOK("FILE_UPLOAD_READY");

            try (DataInputStream dis = new DataInputStream(connection.getInputStream());
                 FileOutputStream fos = new FileOutputStream(temp)) {
                byte[] buffer = new byte[8192];
                while (true) {
                    int length = dis.readInt();
                    if (length == 0) break;
                    if (length > buffer.length) buffer = new byte[length];
                    dis.readFully(buffer, 0, length);
                    fos.write(buffer, 0, length);
                }
            }

            String calculatedChecksum = calculateChecksum(temp);
            if (!calculatedChecksum.equals(transfer.getChecksum())) {
                connection.getWriter().println(MessageFormatter.createErrorResponse("FILE_UPLOAD_DONE", 11006));
                temp.delete();
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
