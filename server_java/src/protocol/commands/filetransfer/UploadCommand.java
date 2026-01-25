package protocol.commands.filetransfer;

import connection.ClientConnection;
import domain.filetransfer.FileTransfer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.commands.ICommandHandler;
import requests.filetransfer.UploadRequest;

import java.io.*;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public record UploadCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        UploadRequest data = gson.fromJson(jsonBody, UploadRequest.class);
        String transferId = data.transfer_id();
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

            InputStream in = connection.getInputStream();
            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 DigestOutputStream dos = new DigestOutputStream(fos, md)) {

                messenger.sendOK("FILE_UPLOAD_READY");

                long received = in.transferTo(dos);
                if (received != transfer.getSize())
                    throw new IOException("Size mismatch: " + received + " != " + transfer.getSize());

                String computed = HexFormat.of().formatHex(md.digest());
                if (!computed.equalsIgnoreCase(transfer.getChecksum()))
                    throw new IOException("Checksum mismatch");
            }

            FileTransferManager.getInstance().setUploadComplete(transferId, true);
            messenger.sendOK("FILE_UPLOAD_DONE");
        } catch (Exception e) {
            messenger.sendError("FILE_UPLOAD_DONE", e instanceof IOException && e.getMessage().contains("checksum") ? 11006 : 11007);
            File temp = transfer.getTempFile();
            if (temp != null && temp.exists())
                temp.delete();
        } finally {
            connection.exit();
        }
    }
}
