package client.handlers;

import client.protocols.ServerMessageHandler;
import client.protocols.commands.BroadcastCommand;
import client.protocols.commands.HiCommand;
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
        handlers.put("LEFT", json -> jsonParsers.parseAndPrintMessage(json, "LEFT"));
        handlers.put("LOGON_RESP", jsonParsers::parseResponse);
        handlers.put("BROADCAST_RESP", jsonParsers::parseResponse);
        handlers.put("BYE_RESP", jsonParsers::parseResponse);
        handlers.put("HANGUP", jsonParsers::parseHangup);
        handlers.put("PONG_ERROR", jsonParsers::parseError);
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
