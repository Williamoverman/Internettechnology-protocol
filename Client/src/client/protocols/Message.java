package client.protocols;

import client.utils.JsonParsers;

public interface Message {
    JsonParsers jsonParser = new JsonParsers();

    void print();
}
