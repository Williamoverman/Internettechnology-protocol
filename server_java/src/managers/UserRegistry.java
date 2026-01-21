package managers;

import connection.ClientConnection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private static final UserRegistry instance = new UserRegistry();
    private final ConcurrentHashMap<String, ClientConnection> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ClientConnection, String> connections = new ConcurrentHashMap<>();

    private UserRegistry() {}

    public static UserRegistry getInstance() {
        return instance;
    }

    /**
     * adds user
     * @param username name of user
     * @param connection client connection
     */
    public void addUser(String username, ClientConnection connection) {
        if (username != null && !users.containsKey(username)) {
            users.put(username, connection);
            connections.put(connection, username);
            System.out.println("User added: " + username);
        }
    }

    /**
     * removes user
     * @param username to remove
     */
    public void removeUser(String username) {
        ClientConnection conn = users.remove(username);
        if (conn != null) {
            connections.remove(conn);
            System.out.println("User removed: " + username);
        }
    }

    /**
     * removes connection
     * @param connection to remove
     */
    public void removeConnection(ClientConnection connection) {
        String username = connections.remove(connection);
        if (username != null)
            users.remove(username);
    }

    /**
     * get all connections except for one
     * @param excludeUsername connection to exclude
     * @return list of different connections
     */
    public List<ClientConnection> getAllExcept(String excludeUsername) {
        return users.values().stream()
                .filter(conn -> {
                    String name = connections.get(conn);
                    return name != null && !name.equals(excludeUsername);
                })
                .toList();
    }

    /**
     * check if username exists
     * @param username username to check for
     * @return true or false
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Get username from connection
     * @param connection the connection
     * @return username
     */
    public String getUsername(ClientConnection connection) {
        return connections.get(connection);
    }

    /**
     * get connection from username
     * @param username the username to look up
     * @return the connection associated with this username
     */
    public ClientConnection getConnection(String username) {
        return users.get(username);
    }

    /**
     * Check if connection is logged in
     * @param connection the connection
     * @return true if logged in
     */
    public boolean isLoggedIn(ClientConnection connection) {
        return connections.containsKey(connection);
    }

    /**
     * Get all usernames
     * @return list of all usernames
     */
    public List<String> getAllUsernames() {
        return users.keySet().stream().toList();
    }

    public void pruneDisconnected() {
        connections.keySet().forEach(conn -> {
            if (!conn.isConnected()) {
                String username = connections.remove(conn);
                if (username != null) {
                    users.remove(username);
                    managers.ToHManager.getInstance().removePlayer(username);
                    protocol.ClientMessenger.broadcastLeft(getAllExcept(username), username);
                }
            }
        });
    }
}
