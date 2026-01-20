package managers;

import domain.game.ToH;

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
        if (isInGame(from) || isInGame(to))
            return false;

        if (hasPendingInvite(to))
            return false;

        pendingInvites.put(to, from);
        return true;
    }

    public String acceptInvite(String accepter) {
        String inviter = pendingInvites.remove(accepter);
        if (inviter == null)
            return null;

        if (isInGame(inviter))
            return null;

        startGame(inviter, accepter);
        return inviter;
    }

    public String declineInvite(String decliner) {
        return pendingInvites.remove(decliner);
    }

    public boolean hasPendingInvite(String username) {
        return pendingInvites.containsKey(username);
    }

    private void startGame(String playerOne, String playerTwo) {
        ToH game = new ToH(playerOne, playerTwo);
        activeGames.put(playerOne, game);
        activeGames.put(playerTwo, game);
    }

    public ToH getGame(String username) {
        return activeGames.get(username);
    }

    public boolean isInGame(String player) {
        return activeGames.containsKey(player);
    }

    public void endGame(String playerOne, String playerTwo) {
        activeGames.remove(playerOne);
        activeGames.remove(playerTwo);
    }

    /**
     * Get opponent based on username
     * @param username username to get opponent from
     * @return username of the opponent
     */
    public String getOpponent(String username) {
        ToH game = getGame(username);
        if (game == null)
            return null;

        return username.equals(game.getPlayerOne()) ? game.getPlayerTwo() : game.getPlayerOne();
    }

    /**
     * Remove player from any active game or pending invite
     * @param username player to remove
     */
    public void removePlayer(String username) {
        ToH game = activeGames.remove(username);
        if (game != null) {
            String opponent = username.equals(game.getPlayerOne())
                    ? game.getPlayerTwo()
                    : game.getPlayerOne();
            activeGames.remove(opponent);
        }

        pendingInvites.remove(username);
        pendingInvites.values().removeIf(inviter -> inviter.equals(username));
    }
}
