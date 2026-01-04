package protocol.commands.common;

import managers.HeartbeatManager;
import protocol.ClientMessenger;
import protocol.commands.ICommandHandler;

public record PongCommand(HeartbeatManager manager, ClientMessenger messenger) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!manager.isAwaitingPong()) {
            messenger.sendPongError(8000);
            return;
        }

        manager.notifyPong();
    }
}
