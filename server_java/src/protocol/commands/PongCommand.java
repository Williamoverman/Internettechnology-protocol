package protocol.commands;

import managers.HeartbeatManager;
import protocol.ICommandHandler;

public record PongCommand(HeartbeatManager manager) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        System.out.println("Received Pong");
        manager.notifyPong();
    }
}
