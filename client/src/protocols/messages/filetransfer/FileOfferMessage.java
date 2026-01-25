package protocols.messages.filetransfer;

import com.fasterxml.jackson.databind.JsonNode;
import protocols.MessageHandler;
import responses.filetransfer.PendingOfferResponse;
import utils.FileTransferState;

public class FileOfferMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            JsonNode node = mapper.readTree(jsonBody);
            String from = node.path("from").asText();
            String filename = node.path("filename").asText();
            long size = node.path("size").asLong(0);
            String checksum = node.path("checksum").asText();

            PendingOfferResponse offer = new PendingOfferResponse(from, filename, size, checksum);

            System.out.printf("[FILE_OFFER] %s wants to send you: %s (%d bytes, sha256:%s)%n",
                    offer.from(), offer.filename(), offer.size(), offer.checksum());

            FileTransferState.setPendingOffer(offer);
        } catch (Exception e) {
            System.err.println("Bad FILE_OFFER json: " + e.getMessage());
        }
    }
}
