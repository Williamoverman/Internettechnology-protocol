package client.handlers;

import client.protocols.ServerMessageHandler;
import client.protocols.messages.*;
import client.utils.JsonParsers;

import java.util.HashMap;

public class MessageDispatcher {
    private final HashMap<String, ServerMessageHandler> handlers = new HashMap<>();

    public MessageDispatcher() {
        registerHandlers();
    }

    /**
     * Register all basic protocol handlers
     */
    private void registerHandlers() {
        handlers.put("HI", json -> new HiMessage(json).print());
        handlers.put("BROADCAST", json -> new BroadcastMessage(json).print());
        handlers.put("LEFT", json -> new LeftMessage(json).print());
        handlers.put("LOGON_RESP", json -> new StatusMessage(json).print());
        handlers.put("BROADCAST_RESP", json -> new StatusMessage(json).print());
        handlers.put("BYE_RESP", json -> new StatusMessage(json).print());
        handlers.put("HANGUP", json -> new HangupMessage(json).print());
        handlers.put("PONG_ERROR", json -> new StatusMessage(json).print());
    }

    /**
     * dispatch message to corresponding handler
     * @param header Handler to dispatch to
     * @param jsonBody body to parse/handle
     */
    public void dispatch(String header, String jsonBody) {
        ServerMessageHandler handler = handlers.get(header);
        handler.handle(jsonBody);
    }

    /**
     * register new handler
     */
    public void register(String header, ServerMessageHandler handler) {
        handlers.put(header, handler);
    }
}
