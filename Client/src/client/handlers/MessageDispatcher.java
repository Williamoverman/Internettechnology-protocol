package client.handlers;

import client.protocols.ServerMessageHandler;
import client.protocols.commands.*;
import client.utils.JsonParsers;

import java.util.HashMap;

public class MessageDispatcher {
    private final HashMap<String, ServerMessageHandler> handlers = new HashMap<>();
    private final JsonParsers jsonParsers;

    public MessageDispatcher() {
        this.jsonParsers = new JsonParsers();
        registerHandlers();
    }

    /**
     * Register all basic protocol handlers
     */
    private void registerHandlers() {
        handlers.put("HI", json -> new HiCommand(json).print());
        handlers.put("BROADCAST", json -> new BroadcastCommand(json).print());
        handlers.put("LEFT", json -> new LeftCommand(json).print());
        handlers.put("LOGON_RESP", jsonParsers::parseStatusResponse);
        handlers.put("BROADCAST_RESP", jsonParsers::parseStatusResponse);
        handlers.put("BYE_RESP", jsonParsers::parseStatusResponse);
        handlers.put("HANGUP", json -> new HangupCommand(json).print());
        handlers.put("PONG_ERROR", jsonParsers::parseStatusResponse);
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
