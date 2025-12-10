package protocol;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFormatter {
    private static final Gson gson = new Gson();

    public static String createWelcome(String version) {
        Map<String, String> data = new HashMap<>();
        data.put("version", version);
        return "HI " + gson.toJson(data);
    }

    public static String createOkResponse(String header) {
        return createResponse(header, "OK", null);
    }

    public static String createUnknownCommand() {
        return "UNKNOWN_COMMAND";
    }

    public static String createPing() {
        return "PING";
    }

    public static String createBroadcast(String username, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("message", message);
        return "BROADCAST " + gson.toJson(data);
    }

    public static String createErrorResponse(String header, int code) {
        return createResponse(header, "ERROR", code);
    }

    private static String createResponse(String header, String status, Integer code) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        if (code != null)
            data.put("code", code);
        return header + " " + gson.toJson(data);
    }

    public static String createJoined(String username) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        return "JOINED " + gson.toJson(data);
    }

    public static String createLeft(String username) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        return "LEFT " + gson.toJson(data);
    }

    public static String createOnline(List<String> users) {
        Map<String, List<String>> data = new HashMap<>();
        data.put("usernames", users);
        return "ONLINE " + gson.toJson(data);
    }

    public static String createDm(String username, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("message", message);
        return "DM " + gson.toJson(data);
    }

    public static String createInvite(String username) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "invite");
        data.put("from", username);
        return "TOH_GAME " + data;
    }
}
