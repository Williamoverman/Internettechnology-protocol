package dispatchers;

import java.util.HashMap;

import connection.ClientConnection;
import managers.HeartbeatManager;
import protocol.commands.ICommandHandler;
import protocol.ClientMessenger;
import protocol.commands.ToHGame.*;
import protocol.commands.common.*;
import protocol.commands.filetransfer.*;

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
        // common commands
        commands.put("LOGON", new LogonCommand(messenger, manager, connection));
        commands.put("BROADCAST_REQ", new BroadcastCommand(messenger, connection));
        commands.put("BYE", new ByeCommand(messenger, connection));
        commands.put("ONLINE_REQ", new OnlineCommand(messenger));
        commands.put("DM_REQ", new DmCommand(messenger, connection));
        commands.put("PONG", new PongCommand(manager, messenger));

        // tails or heads game
        commands.put("TOH_INVITE_REQ", new InviteCommand(messenger, connection));
        commands.put("TOH_ACCEPT", new protocol.commands.ToHGame.AcceptCommand(messenger, connection));
        commands.put("TOH_DECLINE", new protocol.commands.ToHGame.DeclineCommand(messenger, connection));
        commands.put("TOH_CHOICE", new ChoiceCommand(messenger, connection));

        // file transfer
        commands.put("FILE_SEND_REQ", new SendCommand(messenger, connection));
        commands.put("FILE_ACCEPT", new protocol.commands.filetransfer.AcceptCommand(messenger, connection));
        commands.put("FILE_DECLINE", new protocol.commands.filetransfer.DeclineCommand(messenger, connection));
        commands.put("FILE_UPLOAD_INIT", new UploadCommand(messenger, connection));
        commands.put("FILE_DOWNLOAD_INIT", new DownloadCommand(messenger, connection));
    }

    public void dispatch(String header, String jsonBody) {
        ICommandHandler command = commands.get(header);
        if (command != null)
            command.process(jsonBody);

        if (command == null)
            messenger.sendUnknownCommand();
    }
}
