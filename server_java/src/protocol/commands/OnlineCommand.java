package protocol.commands;

import protocol.ICommandHandler;
import protocol.ClientMessenger;

import java.util.List;

public record OnlineCommand(ClientMessenger messenger) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        List<String> allUsers = registry.getAllUsernames();
        messenger.sendOnline(allUsers);
    }
}
