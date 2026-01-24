package responses.filetransfer;

public record PendingOfferResponse(String from, String filename, long size, String checksum) {}