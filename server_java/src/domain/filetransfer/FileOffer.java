package domain.filetransfer;

public class FileOffer {
    private final String sender;
    private final String recipient;
    private final String filename;
    private final long size;
    private final String checksum;

    public FileOffer(String sender, String recipient, String filename, long size, String checksum) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.size = size;
        this.checksum = checksum;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public String getChecksum() {
        return checksum;
    }
}
