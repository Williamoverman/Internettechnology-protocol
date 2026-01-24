package protocols.messages.filetransfer;

import com.fasterxml.jackson.databind.JsonNode;
import protocols.MessageHandler;
import utils.FileTransferState;

public class FileDeclinedMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            JsonNode node = mapper.readTree(jsonBody);
            String from = node.path("from").asText();

            System.out.printf("[FILE_DECLINED] %s has declined your file offer.%n", from);

            FileTransferState.setWaitingForAcceptResponse(false);
            FileTransferState.setPendingUpload(null);

        } catch (Exception e) {
            System.err.println("Bad FILE_DECLINED json: " + e.getMessage());
        }
    }
}
