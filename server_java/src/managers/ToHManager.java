package managers;

public class ToHManager {
    private static final ToHManager instance = new ToHManager();

    private ToHManager() {}

    public static ToHManager getInstance() {
        return instance;
    }
}
