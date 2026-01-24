package protocol.commands.filetransfer;

import connection.ClientConnection;
import domain.filetransfer.FileTransfer;
import managers.FileTransferManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;

public record AcceptCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("FILE_RESP", 67);
            return;
        }

        String accepter = registry.getUsername(connection);
        FileTransfer transfer = FileTransferManager.getInstance().accept(accepter);
        if (transfer == null) {
            messenger.sendError("FILE_RESP", 11003);
            return;
        }

        String acceptedMsg = MessageFormatter.createFileAccepted(transfer.getTransferId(), accepter);
        messenger.sendAccepted(transfer.getTransferId(), accepter);

        ClientConnection senderConn = registry.getConnection(transfer.getSender());
        if (senderConn != null) {
            ClientMessenger.sendTo(senderConn, acceptedMsg);
        }
    }
}
