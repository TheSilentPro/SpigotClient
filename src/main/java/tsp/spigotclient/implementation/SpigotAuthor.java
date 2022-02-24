package tsp.spigotclient.implementation;

import java.util.List;

public class SpigotAuthor {

    private final int id;
    private final String username;
    private final int resourceCount;
    private final String avatar;
    private final List<SpigotIdentity> identities;

    public SpigotAuthor(int id, String username, int resourceCount, String avatar, List<SpigotIdentity> identities) {
        this.id = id;
        this.username = username;
        this.resourceCount = resourceCount;
        this.avatar = avatar;
        this.identities = identities;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<SpigotIdentity> getIdentities() {
        return identities;
    }

}
