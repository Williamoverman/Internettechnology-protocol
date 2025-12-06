package dispatchers;

import java.util.HashMap;

import connection.ClientConnection;
import managers.HeartbeatManager;
import protocol.ICommandHandler;
import protocol.commands.*;
import sender.MessageSender;

public class CommandDispatcher {
    private final HashMap<String, ICommandHandler> commands = new HashMap<>();
    private final MessageSender sender;
    private final HeartbeatManager manager;
    private final ClientConnection connection;

    public CommandDispatcher(MessageSender sender, HeartbeatManager manager, ClientConnection connection) {
        this.sender = sender;
        this.manager = manager;
        this.connection = connection;
        registerHandlers();
    }

    private void registerHandlers() {
        commands.put("LOGON", new LogonCommand(sender, manager, connection));
        commands.put("BROADCAST_REQ", new BroadcastCommand(sender));
        commands.put("BYE", new ByeCommand(sender));
        commands.put("ONLINE_REQ", new OnlineCommand(sender));
        commands.put("DM_REQ", new DmCommand(sender));
        commands.put("TOH_GAME", new ToHCommand(sender));
        commands.put("PONG", new PongCommand(manager));
    }

    public void dispatch(String header, String jsonBody) {
        ICommandHandler command = commands.get(header);
        if (command != null)
            command.process(jsonBody);

        if (command == null)
            sender.sendUnknownCommand();
    }
}
