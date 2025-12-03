package protocol.commands;

import protocol.ICommandHandler;
import sender.MessageSender;

public record ToHCommand(MessageSender sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
