package dispatchers;

import protocols.CommandHandler;
import protocols.commands.ToH.*;
import protocols.commands.common.*;
import protocols.commands.filetransfer.*;
import senders.CommandSender;

import java.util.HashMap;

public class InputDispatcher {
    private final HashMap<String, CommandHandler> handlers = new HashMap<>();
    private CommandSender sender;

    public InputDispatcher(CommandSender sender) {
        this.sender = sender;
        registerHandlers();
    }

    /**
     * Register all basic protocol execution handlers
     */
    private void registerHandlers() {
        handlers.put("bye", new ByeCommand(sender));
        handlers.put("help", new HelpCommand());
        handlers.put("login", new LoginCommand(sender));
        handlers.put("broadcast", new BroadcastCommand(sender));
        handlers.put("online", new OnlineCommand(sender));
        handlers.put("dm", new DmCommand(sender));

        // toh game
        handlers.put("invite", new InviteCommand(sender));
        handlers.put("accept", new AcceptCommand(sender));
        handlers.put("decline", new DeclineCommand(sender));
        handlers.put("choice", new ChoiceCommand(sender));

        // file transfer
        handlers.put("fileoffer", new FileSendCommand(sender));
        handlers.put("fileaccept", new FileAcceptCommand(sender));
        handlers.put("filedecline", new FileDeclineCommand(sender));
    }

    /**
     * dispatch message to corresponding handler
     * @param header Handler to dispatch to
     * @param command body to execute on the server
     */
    public boolean dispatch(String header, String command) {
        CommandHandler handler = handlers.get(header);
        if (handler != null)
            return handler.execute(command);
        return false;
    }

    /**
     * register new handler
     */
    public void register(String header, CommandHandler handler) {
        handlers.put(header, handler);
    }
}
