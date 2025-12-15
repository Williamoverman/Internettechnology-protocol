package protocol.commands;

import com.google.gson.Gson;
import managers.UserRegistry;

public interface ICommandHandler {
    Gson gson = new Gson();
    UserRegistry registry = UserRegistry.getInstance();

    void process(String jsonBody);
}
