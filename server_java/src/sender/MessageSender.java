package sender;

import com.google.gson.Gson;
import connection.ClientConnection;

import java.util.HashMap;
import java.util.Map;

public class MessageSender {
    private final ClientConnection connection;
    private Gson gson = new Gson();

    public MessageSender(ClientConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        System.out.println("Sending Ping...");
        connection.sendMessage("PING");
    }

    public void sendUnknownCommand() {
        connection.sendMessage("UNKNOWN_COMMAND");
    }

    public void sendWelcome() {
        connection.sendMessage("HI {\"version\":\"1.0.0\"}\n");
    }

    public void sendError(String response, int code) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERROR");
        error.put("code", code);
        String json = gson.toJson(error);
        connection.sendMessage(response + " " + json);
    }

    public void sendOK(String response) {
        Map<String, String> ok = new HashMap<>();
        ok.put("status", "OK");
        String json = gson.toJson(ok);
        connection.sendMessage(response + " " + json);
    }
}
