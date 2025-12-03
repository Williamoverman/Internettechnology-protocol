package protocol.commands;

import com.google.gson.JsonSyntaxException;
import managers.HeartbeatManager;
import protocol.ICommandHandler;
import requests.LogonRequest;
import sender.MessageSender;

public record LogonCommand(MessageSender sender, HeartbeatManager manager) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        try {
            LogonRequest request = gson.fromJson(jsonBody, LogonRequest.class);
            String username = request.username();
            System.out.println(username);
            if (username == null) {
                sender.sendError("LOGON_RESP", 5001);
                return;
            }

            sender.sendOK("LOGON_RESP");
            manager.notifyPong();
            new Thread(manager::start).start();
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
