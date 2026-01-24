package protocols.messages.common;

import protocols.MessageHandler;
import responses.common.StatusResponse;

import java.io.IOException;

public class StatusMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            StatusResponse response = mapper.readValue(jsonBody, StatusResponse.class);

            if ("OK".equalsIgnoreCase(response.status())) {
                System.out.println("[OK]");
            } else if (response.code() != null) {
                String desc = switch (response.code()) {
                    case 67 -> "You need to be logged";
                    case 5000 -> "User with this name already exists";
                    case 5001 -> "Username has an invalid format or length";
                    case 5002 -> "Already logged in";
                    case 8000 -> "Pong without ping";
                    case 9000 -> "User does not exist";
                    case 9001 -> "Cannot DM yourself";
                    case 10000 -> "User does not exist";
                    case 10001 -> "Cannot play heads or tails with yourself";
                    case 10002 -> "You or opponent are already in a game";
                    case 10003 -> "No pending invitation";
                    case 10004 -> "Invalid Choice (choices are heads/tails)";
                    case 10005 -> "No active game/game has already ended";
                    case 11000  -> "Recipient does not exist";
                    case 11001  -> "Cannot send file to yourself";
                    case 11002  -> "Sender or recipient already in a file transfer";
                    case 11003  -> "No pending offer / nothing to accept";
                    case 11004  -> "Invalid file offer request";
                    case 11005  -> "Invalid transfer ID";
                    case 11006  -> "Checksum mismatch";
                    case 11007  -> "Transfer error";
                    default -> "Unknown error: " + response.code();
                };
                System.out.println("[ERROR] " + desc);
            }
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}
