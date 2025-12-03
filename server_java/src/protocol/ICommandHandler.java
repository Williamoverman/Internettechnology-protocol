package protocol;

import com.google.gson.Gson;

public interface ICommandHandler {
    static final Gson gson = new Gson();

    void process(String jsonBody);
}
