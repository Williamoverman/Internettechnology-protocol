package protocols.commands.filetransfer;

import protocols.CommandHandler;
import senders.CommandSender;
import utils.FileTransferState;

import java.io.File;
import java.security.MessageDigest;
import java.io.FileInputStream;

public record FileSendCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String args) {
        String trimmedArgs = args == null ? "" : args.trim();
        String[] parts = trimmedArgs.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Usage: fileoffer <username> <path/to/file>");
            return true;
        }

        String recipient = parts[0];
        File file = new File(parts[1]);

        if (!file.isFile() || !file.canRead()) {
            System.out.println("File not found or not readable: " + file.getPath());
            return true;
        }

        long size = file.length();
        String checksum;
        try {
            checksum = computeSHA256(file);
        } catch (Exception e) {
            System.out.println("Cannot compute SHA-256: " + e.getMessage());
            return true;
        }

        System.out.printf("[FILE_SEND] Offering %s (%d bytes) to %s%n",
                file.getName(), size, recipient);

        sender.fileSend(recipient, file.getName(), size, checksum);

        FileTransferState.setPendingUpload(file);
        FileTransferState.setWaitingForAcceptResponse(true);

        return true;
    }

    private String computeSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
