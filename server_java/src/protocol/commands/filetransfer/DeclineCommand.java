package protocol.commands.filetransfer;

import connection.ClientConnection;
import domain.filetransfer.FileOffer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;

public record DeclineCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("FILE_RESP", 67);
            return;
        }

        String decliner = registry.getUsername(connection);
        FileOffer offer = FileTransferManager.getInstance().decline(decliner);
        if (offer == null) {
            messenger.sendError("FILE_RESP", 11003);
            return;
        }

        messenger.sendOK("FILE_RESP");

        ClientConnection senderConnection = registry.getConnection(offer.getSender());
        if (senderConnection != null) {
            String declinedMsg = MessageFormatter.createFileDeclined(decliner);
            ClientMessenger.sendTo(senderConnection, declinedMsg);
        }
    }
}
