package protocols.messages.filetransfer;

import com.fasterxml.jackson.databind.JsonNode;
import protocols.MessageHandler;
import responses.filetransfer.PendingOfferResponse;
import utils.Config;
import utils.FileTransferState;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

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

    private void downloadFile(String transferId, String filename, String expectedChecksum, long expectedSize) {
        File file = new File(filename);

        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT)) {
            InputStream is = socket.getInputStream();
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            readLineUnbuffered(is);

            writer.println("FILE_DOWNLOAD_INIT {\"transfer_id\":\"" + transferId + "\"}");
            writer.flush();

            String line = readLineUnbuffered(is);
            if (line == null || !line.startsWith("FILE_DOWNLOAD_READY ")) {
                System.out.println("Download not ready");
                return;
            }
            if (line.contains("\"status\":\"ERROR\"")) {
                System.out.println("Download error: " + line);
                return;
            }

            System.out.println("Starting download: " + filename + " (" + expectedSize + " bytes)");

            DataInputStream dis = new DataInputStream(is);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            long received = 0;

            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                while (true) {
                    int len = dis.readInt();
                    if (len <= 0) break;
                    if (len > buffer.length) buffer = new byte[len];
                    dis.readFully(buffer, 0, len);
                    fos.write(buffer, 0, len);
                    md.update(buffer, 0, len);
                    received += len;
                }
            }

            if (received != expectedSize) {
                System.out.println("Download size mismatch");
                file.delete();
                return;
            }

            String computed = bytesToHex(md.digest());
            if (!computed.equalsIgnoreCase(expectedChecksum)) {
                System.out.println("Download checksum mismatch");
                file.delete();
                return;
            }

            String done = readLineUnbuffered(is);
            if (done != null && done.startsWith("FILE_DOWNLOAD_DONE ") && done.contains("\"status\":\"OK\"")) {
                System.out.println("Download completed: " + filename);
            } else if (done != null) {
                System.out.println("Download finished with error: " + done);
                file.delete();
            } else {
                System.out.println("Download finished without status");
            }

        } catch (Exception e) {
            System.err.println("Download failed: " + e.getMessage());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private String readLineUnbuffered(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            if (b == '\n') break;
            if (b != '\r') bos.write(b);
        }
        if (bos.size() == 0 && b == -1) return null;
        return bos.toString("UTF-8");
    }

    private void uploadFile(String transferId, File file) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            reader.readLine();

            writer.println("FILE_UPLOAD_INIT {\"transfer_id\":\"" + transferId + "\"}");
            writer.flush();

            String line = reader.readLine();
            if (line == null || !line.startsWith("FILE_UPLOAD_READY ")) {
                System.out.println("Did not receive FILE_UPLOAD_READY");
                return;
            }
            if (line.contains("\"status\":\"ERROR\"")) {
                System.out.println("Server error on upload init: " + line);
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                LengthPrefixedOutputStream out = new LengthPrefixedOutputStream(dos);
                fis.transferTo(out);
                dos.writeInt(0);
                dos.flush();
            }

            String done = reader.readLine();
            if (done != null && done.startsWith("FILE_UPLOAD_DONE ")) {
                System.out.println();
                if (done.contains("\"status\":\"OK\""))
                    System.out.println("Upload completed successfully → " + file.getName());
                else
                    System.out.println("Upload finished with error: " + done);
            }
        } catch (IOException e) {
            System.err.println("Upload failed (I/O error): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class LengthPrefixedOutputStream extends OutputStream {
        private final DataOutputStream out;

        LengthPrefixedOutputStream(DataOutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            byte[] single = {(byte) b};
            write(single, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (len <= 0) return;
            out.writeInt(len);
            out.write(b, off, len);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
