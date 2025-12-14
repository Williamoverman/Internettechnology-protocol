package protocol.commands;

import connection.ClientConnection;
import managers.ToHManager;
import protocol.ICommandHandler;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import requests.ToHRequest;

public record ToHCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}