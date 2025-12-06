package sender;

import com.google.gson.Gson;
import connection.ClientConnection;

import java.util.HashMap;
import java.util.Map;

public class MessageSender {
    private final ClientConnection connection;
    private final Gson gson = new Gson();

    public MessageSender(ClientConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        connection.sendMessage("PING");
    }

    public void sendUnknownCommand() {
        connection.sendMessage("UNKNOWN_COMMAND");
    }

    public void sendWelcome() {
        Map<String, String> welcomeData = new HashMap<>();
        welcomeData.put("version", "1.0.0");
        String json = gson.toJson(welcomeData);
        connection.sendMessage("HI " + json);
    }

    public void sendOK(String header) {
        sendResponse(header, "OK", null);
    }

    public void sendError(String header, int code) {
        sendResponse(header, "ERROR", code);
    }

    public void sendResponse(String header, String status, Integer code) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        if (code != null)
            response.put("code", code);
        String json = gson.toJson(response);
        connection.sendMessage(header + " " + json);
    }
}
