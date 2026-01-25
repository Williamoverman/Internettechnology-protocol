package protocol.commands.filetransfer;

import connection.ClientConnection;
import domain.filetransfer.FileTransfer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.commands.ICommandHandler;
import requests.filetransfer.UploadRequest;

import java.io.*;

public record DownloadCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        UploadRequest data = gson.fromJson(jsonBody, UploadRequest.class);
        String transferId = data.transfer_id();
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

            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                fileInputStream.transferTo(connection.getOutputStream());
            }

            FileTransferManager.getInstance().setDownloadComplete(transferId);
        } catch (Exception e) {
            messenger.sendError("FILE_DOWNLOAD_DONE", 11007);
        } finally {
            connection.exit();
        }
    }

 
}
