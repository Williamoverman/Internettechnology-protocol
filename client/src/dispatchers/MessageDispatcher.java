package dispatchers;

import protocols.MessageHandler;
import protocols.messages.*;

import java.util.HashMap;

public class MessageDispatcher {
    private final HashMap<String, MessageHandler> handlers = new HashMap<>();

    public MessageDispatcher() {
        registerHandlers();
    }

    /**
     * Register all basic protocol message receiver handlers
     */
    private void registerHandlers() {
        handlers.put("HI", new HiMessage());
        handlers.put("BROADCAST", new BroadcastMessage());
        handlers.put("LEFT", new LeftMessage());
        handlers.put("LOGON_RESP", new StatusMessage());
        handlers.put("BROADCAST_RESP", new StatusMessage());
        handlers.put("BYE_RESP", new StatusMessage());
        handlers.put("HANGUP", new HangupMessage());
        handlers.put("PONG_ERROR", new StatusMessage());
        handlers.put("JOINED", new JoinedMessage());
    }

    /**
     * dispatch message to corresponding handler
     * @param header Handler to dispatch to
     * @param jsonBody body to parse/handle
     */
    public void dispatch(String header, String jsonBody) {
        MessageHandler handler = handlers.get(header);
        if (handler != null)
            handler.handle(jsonBody);
    }

    /**
     * register new handler
     */
    public void register(String header, MessageHandler handler) {
        handlers.put(header, handler);
    }
}
