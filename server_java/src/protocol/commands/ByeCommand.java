package protocol.commands;

import protocol.ICommandHandler;
import sender.MessageSender;

public record ByeCommand(MessageSender sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
