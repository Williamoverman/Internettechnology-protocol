package dispatchers;

import java.util.HashMap;

import connection.ClientConnection;
import managers.HeartbeatManager;
import protocol.ICommandHandler;
import protocol.commands.*;
import protocol.ClientMessenger;

public class CommandDispatcher {
    private final HashMap<String, ICommandHandler> commands = new HashMap<>();
    private final ClientMessenger messenger;
    private final HeartbeatManager manager;
    private final ClientConnection connection;

    public CommandDispatcher(ClientMessenger messenger, HeartbeatManager manager, ClientConnection connection) {
        this.messenger = messenger;
        this.manager = manager;
        this.connection = connection;
        registerHandlers();
    }

    private void registerHandlers() {
        commands.put("LOGON", new LogonCommand(messenger, manager, connection));
        commands.put("BROADCAST_REQ", new BroadcastCommand(messenger, connection));
        commands.put("BYE", new ByeCommand(messenger, connection));
        commands.put("ONLINE_REQ", new OnlineCommand(messenger));
        commands.put("DM_REQ", new DmCommand(messenger, connection));
        commands.put("TOH_GAME", new ToHCommand(messenger, connection));
        commands.put("PONG", new PongCommand(manager));
    }

    public void dispatch(String header, String jsonBody) {
        ICommandHandler command = commands.get(header);
        if (command != null)
            command.process(jsonBody);

        if (command == null)
            messenger.sendUnknownCommand();
    }
}
