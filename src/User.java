import java.util.UUID;

public class User {
    private final String uuid;

    public User() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }
}
