package domain.filetransfer;

import java.io.File;
import java.util.UUID;

public class FileTransfer {
    private final String transferId;
    private final String sender;
    private final String recipient;
    private final String filename;
    private final long size;
    private final String checksum;
    private File tempFile;
    private boolean uploadComplete;
    private boolean downloadComplete;

    public FileTransfer(String sender, String recipient, String filename, long size, String checksum) {
        this.transferId = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.size = size;
        this.checksum = checksum;
        this.uploadComplete = false;
        this.downloadComplete = false;
    }

    public String getTransferId() {
        return transferId;
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

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public boolean isUploadComplete() {
        return uploadComplete;
    }

    public void setUploadComplete(boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public boolean isDownloadComplete() {
        return downloadComplete;
    }

    public void setDownloadComplete(boolean downloadComplete) {
        this.downloadComplete = downloadComplete;
    }
}