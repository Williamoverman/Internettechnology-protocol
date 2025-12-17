package dispatchers;

import protocols.MessageHandler;
import protocols.messages.ToH.*;
import protocols.messages.common.*;

import java.util.HashMap;

public class MessageDispatcher {
    private final HashMap<String, MessageHandler> handlers = new HashMap<>();

    public MessageDispatcher() {
        registerHandlers();
    }

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
        handlers.put("ONLINE", new OnlineMessage());
        handlers.put("DM", new DmMessage());
        handlers.put("DM_RESP", new StatusMessage());

        // toh game
        handlers.put("TOH_RESP", new StatusMessage());
        handlers.put("TOH_INVITE", new InviteMessage());
        handlers.put("TOH_START", new StartMessage());
        handlers.put("TOH_DECLINED", new DeclineMessage());
        handlers.put("TOH_TIE", new TieMessage());
        handlers.put("TOH_RESULT", new ResultMessage());
        handlers.put("TOH_END", new EndResultMessage());
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
