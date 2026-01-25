package protocols.commands.filetransfer;

import protocols.CommandHandler;
import senders.CommandSender;
import utils.FileTransferState;

public record FileDeclineCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String args) {
        if (FileTransferState.getPendingOffer() == null) {
            System.out.println("No pending file offer to decline.");
            return true;
        }
        sender.fileDecline();
        FileTransferState.setPendingOffer(null);
        System.out.println("Declined current file offer.");
        return true;
    }
}
