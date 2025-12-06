package protocol;

import com.google.gson.Gson;

public interface ICommandHandler {
    Gson gson = new Gson();

    void process(String jsonBody);
}
