package managers;

import domain.filetransfer.FileOffer;
import domain.filetransfer.FileTransfer;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class FileTransferManager {
    private static final FileTransferManager instance = new FileTransferManager();

    private final ConcurrentHashMap<String, FileOffer> pendingOffers = new ConcurrentHashMap<>(); // key: recipient
    private final ConcurrentHashMap<String, FileTransfer> activeTransfers = new ConcurrentHashMap<>(); // key: transferId

    private FileTransferManager() {}

    public static FileTransferManager getInstance() {
        return instance;
    }

    public boolean addOffer(FileOffer offer) {
        String recipient = offer.getRecipient();
        if (pendingOffers.containsKey(recipient) || isInTransfer(offer.getSender()) || isInTransfer(recipient))
            return false;
        pendingOffers.put(recipient, offer);
        return true;
    }

    public FileOffer getOffer(String recipient) {
        return pendingOffers.get(recipient);
    }

    public FileTransfer accept(String recipient) {
        FileOffer offer = pendingOffers.remove(recipient);
        if (offer == null)
            return null;
        FileTransfer transfer = new FileTransfer(offer.getSender(), recipient, offer.getFilename(), offer.getSize(), offer.getChecksum());
        activeTransfers.put(transfer.getTransferId(), transfer);
        return transfer;
    }

    public FileOffer decline(String recipient) {
        return pendingOffers.remove(recipient);
    }

    public FileTransfer getTransfer(String transferId) {
        return activeTransfers.get(transferId);
    }

    public void setUploadComplete(String transferId, boolean complete) {
        FileTransfer transfer = getTransfer(transferId);
        if (transfer != null)
            transfer.setUploadComplete(complete);
    }

    public void setDownloadComplete(String transferId) {
        FileTransfer transfer = getTransfer(transferId);
        if (transfer != null) {
            transfer.setDownloadComplete(true);
            if (transfer.isUploadComplete() && transfer.isDownloadComplete()) {
                activeTransfers.remove(transferId);
                File temp = transfer.getTempFile();
                if (temp != null)
                    temp.delete();
            }
        }
    }

    public boolean isInTransfer(String username) {
        return activeTransfers.values().stream()
                .anyMatch(t -> t.getSender().equals(username) || t.getRecipient().equals(username));
    }
}