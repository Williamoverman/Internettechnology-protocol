package managers;

import games.ToH;

import java.util.concurrent.ConcurrentHashMap;

public class ToHManager {
    private static final ToHManager instance = new ToHManager();

    private final ConcurrentHashMap<String, ToH> activeGames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> pendingInvites = new ConcurrentHashMap<>();

    private ToHManager() {}

    public static ToHManager getInstance() {
        return instance;
    }

    public boolean sendInvite(String from, String to) {
        
    }
}
