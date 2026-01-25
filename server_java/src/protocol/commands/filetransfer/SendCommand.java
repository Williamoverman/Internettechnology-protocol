package protocol.commands.filetransfer;

import connection.ClientConnection;
import domain.filetransfer.FileOffer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;
import requests.filetransfer.SendRequest;

public record SendCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        SendRequest request = gson.fromJson(jsonBody, SendRequest.class);
        if (request == null || request.filename() == null || request.checksum() == null || request.size() <= 0) {
            messenger.sendError("FILE_RESP", 11004);
            return;
        }

        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("FILE_RESP", 67);
            return;
        }

        String sender = registry.getUsername(connection);
        String recipient = request.recipient();
        ClientConnection recipientConn = registry.getConnection(recipient);
        if (recipientConn == null) {
            messenger.sendError("FILE_RESP", 11000);
            return;
        }

        if (recipient.equals(sender)) {
            messenger.sendError("FILE_RESP", 11001);
            return;
        }

        FileOffer offer = new FileOffer(sender, recipient, request.filename(), request.size(), request.checksum());
        if (!FileTransferManager.getInstance().addOffer(offer)) {
            messenger.sendError("FILE_RESP", 11002);
            return;
        }

        messenger.sendOK("FILE_RESP");

        String message = MessageFormatter.createFileOffer(sender,
                request.filename(),
                request.size(),
                request.checksum());

        ClientMessenger.sendTo(recipientConn, message);
    }
}
