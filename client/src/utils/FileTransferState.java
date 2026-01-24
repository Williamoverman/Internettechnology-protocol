package utils;

import responses.filetransfer.PendingOfferResponse;

import java.io.File;

public class FileTransferState {
    private static volatile File pendingUploadFile = null;
    private static volatile PendingOfferResponse pendingOffer = null;
    private static volatile String lastTransferId = null;
    private static volatile boolean waitingForAcceptResponse = false;

    public static void setPendingUpload(File file) {
        pendingUploadFile = file;
    }

    public static File getAndClearPendingUpload() {
        File f = pendingUploadFile;
        pendingUploadFile = null;
        return f;
    }

    public static void setPendingOffer(PendingOfferResponse offer) {
        pendingOffer = offer;
    }

    public static PendingOfferResponse getAndClearPendingOffer() {
        PendingOfferResponse o = pendingOffer;
        pendingOffer = null;
        return o;
    }

    public static void setWaitingForAcceptResponse(boolean value) {
        waitingForAcceptResponse = value;
    }

    public static boolean isWaitingForAcceptResponse() {
        return waitingForAcceptResponse;
    }

    public static void setLastTransferId(String id) {
        lastTransferId = id;
    }

    public static String getLastTransferId() {
        return lastTransferId;
    }

    public static PendingOfferResponse getPendingOffer() {
        return pendingOffer;
    }
}