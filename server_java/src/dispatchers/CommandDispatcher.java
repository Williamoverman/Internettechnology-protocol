package dispatchers;

import java.util.HashMap;

import managers.HeartbeatManager;
import protocol.ICommandHandler;
import protocol.commands.*;
import sender.MessageSender;

public class CommandDispatcher {
    private HashMap<String, ICommandHandler> commands = new HashMap<>();
    private MessageSender sender;
    private HeartbeatManager manager;

    public CommandDispatcher(MessageSender sender, HeartbeatManager manager) {
        registerHandlers();
        this.sender = sender;
        this.manager = manager;
    }

    private void registerHandlers() {
        commands.put("LOGON", new LogonCommand(sender, manager));
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
