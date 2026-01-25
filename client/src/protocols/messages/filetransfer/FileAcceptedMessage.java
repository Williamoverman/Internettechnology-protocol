package protocols.messages.filetransfer;

import com.fasterxml.jackson.databind.JsonNode;
import protocols.MessageHandler;
import responses.filetransfer.PendingOfferResponse;
import utils.Config;
import utils.FileTransferState;

import java.io.*;
import java.net.Socket;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public class FileAcceptedMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            JsonNode node = mapper.readTree(jsonBody);
            String transferId = node.path("transfer_id").asText();
            String accepter = node.path("accepter").asText();

            System.out.printf("[FILE_ACCEPTED] Accepted by %s%n", accepter);

            FileTransferState.setWaitingForAcceptResponse(false);
            FileTransferState.setLastTransferId(transferId);

            File pendingUpload = FileTransferState.getAndClearPendingUpload();
            if (pendingUpload != null) {
                new Thread(() -> {
                    try {
                        uploadFile(transferId, pendingUpload);
                    } finally {
                        FileTransferState.setLastTransferId(null);
                    }
                }, "FileUploadThread-" + transferId).start();
                return;
            }

            PendingOfferResponse offer = FileTransferState.getPendingOffer();
            if (offer == null) {
                System.out.println("No pending offer → ignoring FILE_ACCEPTED");
                return;
            }

            new Thread(() -> {
                try {
                    downloadFile(transferId, offer.filename(), offer.checksum(), offer.size());
                } finally {
                    FileTransferState.setPendingOffer(null);
                    FileTransferState.setLastTransferId(null);
                }
            }, "FileDownloadThread-" + transferId).start();

        } catch (Exception e) {
            System.err.println("Bad FILE_ACCEPTED: " + e.getMessage());
        }
    }

    private void uploadFile(String transferId, File file) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             InputStream is = socket.getInputStream()) {

            readLine(is);

            writer.println("FILE_UPLOAD_INIT {\"transfer_id\":\"" + transferId + "\"}");

            String ready = readLine(is);
            if (ready == null || !ready.contains("\"status\":\"OK\"")) {
                System.out.println("Server niet klaar voor upload: " + ready);
                return;
            }

            long size = file.length();
            try (FileInputStream fis = new FileInputStream(file)) {
                long sent = fis.transferTo(socket.getOutputStream());
                if (sent != size) {
                    System.out.println("Upload incompleet: " + sent + " / " + size);
                }
            }

            String done = readLine(is);
            if (done != null && done.contains("\"status\":\"OK\"")) {
                System.out.println("Upload succesvol: " + file.getName());
            } else {
                System.out.println("Upload fout: " + done);
            }

        } catch (Exception e) {
            System.err.println("Upload mislukt: " + e.getMessage());
        }
    }

    private void downloadFile(String transferId, String filename, String expectedChecksum, long expectedSize) {
        File file = new File(filename);

        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             InputStream is = socket.getInputStream()) {

            readLine(is);

            writer.println("FILE_DOWNLOAD_INIT {\"transfer_id\":\"" + transferId + "\"}");

            String ready = readLine(is);
            if (ready == null || !ready.contains("\"status\":\"OK\"")) {
                System.out.println("Server niet klaar voor download: " + ready);
                return;
            }

            System.out.println("Download start: " + filename + " (" + expectedSize + " bytes)");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream limitedIn = new LimitedInputStream(is, expectedSize);

            try (FileOutputStream fos = new FileOutputStream(file);
                 DigestOutputStream dos = new DigestOutputStream(fos, md)) {

                long received = limitedIn.transferTo(dos);

                if (received != expectedSize) {
                    throw new IOException("Size mismatch: " + received + " != " + expectedSize);
                }

                String computed = bytesToHex(md.digest());
                if (!computed.equalsIgnoreCase(expectedChecksum)) {
                    throw new IOException("Checksum mismatch");
                }
            }

            String done = readLine(is);
            if (done != null && done.contains("\"status\":\"OK\"")) {
                System.out.println("Download succesvol: " + filename);
            } else {
                throw new IOException("Server status niet OK: " + done);
            }

        } catch (Exception e) {
            System.err.println("Download mislukt: " + e.getMessage());
            if (file.exists()) file.delete();
        }
    }

    private String readLine(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            if (b == '\n') break;
            if (b != '\r') baos.write(b);
        }
        return baos.size() == 0 && b == -1 ? null : baos.toString("UTF-8");
    }

    private static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    private static class LimitedInputStream extends FilterInputStream {
        private long left;

        public LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.left = limit;
        }

        @Override
        public int read() throws IOException {
            if (left <= 0) {
                return -1;
            }
            int result = super.read();
            if (result != -1) {
                left--;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (left <= 0) {
                return -1;
            }
            if (len > left) {
                len = (int) left;
            }
            int result = super.read(b, off, len);
            if (result != -1) {
                left -= result;
            }
            return result;
        }
    }
}
