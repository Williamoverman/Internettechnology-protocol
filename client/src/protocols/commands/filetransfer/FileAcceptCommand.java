package protocols.commands.filetransfer;

import protocols.CommandHandler;
import senders.CommandSender;
import utils.FileTransferState;

public record FileAcceptCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String args) {
        if (FileTransferState.getPendingOffer() == null) {
            System.out.println("No pending file offer to accept.");
            return true;
        }
        sender.fileAccept();
        FileTransferState.setWaitingForAcceptResponse(true);
        System.out.println("Sent file accept, waiting for response...");
        return true;
    }
}
