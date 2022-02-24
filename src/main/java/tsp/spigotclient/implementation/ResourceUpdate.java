package tsp.spigotclient.implementation;

public class ResourceUpdate {

    private final int id;
    private final int resourceId;
    private final String title;
    private final String message;

    public ResourceUpdate(int id, int resourceId, String title, String message) {
        this.id = id;
        this.resourceId = resourceId;
        this.title = title;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

}
