package requests.filetransfer;

public record SendRequest(String recipient, String filename, long size, String checksum) {}
