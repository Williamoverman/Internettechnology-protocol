package client.protocols;

import client.utils.JsonParsers;

public interface Command {
    JsonParsers jsonParser = new JsonParsers();

    void print();
}
