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

    public static String createToHInvite(String inviter) {
        Map<String, String> data = new HashMap<>();
        data.put("from", inviter);
        return "TOH_INVITE " + gson.toJson(data);
    }

    public static String createStart(String opponent, int roundNumber) {
        Map<String, Object> data = new HashMap<>();
        data.put("opponent", opponent);
        data.put("round", roundNumber);
        return "TOH_START " + gson.toJson(data);
    }

    public static String createDeclined(String decliner) {
        Map<String, String> data = new HashMap<>();
        data.put("from", decliner);
        return "TOH_DECLINED " + gson.toJson(data);
    }

    public static String createTie(int roundNumber) {
        Map<String, Integer> data = new HashMap<>();
        data.put("round", roundNumber);
        return "TOH_TIE " + gson.toJson(data);
    }

    public static String createRoundResult(int roundNumber, String coin, String winner, int scoreCurrent, int scoreOpponent) {
        Map<String, Integer> score = new HashMap<>();
        score.put("you", scoreCurrent);
        score.put("opponent", scoreOpponent);

        Map<String, Object> data = new HashMap<>();
        data.put("round", roundNumber);
        data.put("coin", coin);
        data.put("winner", winner);
        data.put("score", score);
        return "TOH_RESULT " + gson.toJson(data);
    }

    public static String createEndResult(String winner, int scoreCurrent, int scoreOpponent) {
        Map<String, Integer> finalScore = new HashMap<>();
        finalScore.put("you", scoreCurrent);
        finalScore.put("opponent", scoreOpponent);

        Map<String, Object> data = new HashMap<>();
        data.put("winner", winner);
        data.put("finalScore", finalScore);
        return "TOH_END " + gson.toJson(data);
    }
}
