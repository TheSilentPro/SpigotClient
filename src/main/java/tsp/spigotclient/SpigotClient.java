package tsp.spigotclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tsp.spigotclient.implementation.ResourceCategory;
import tsp.spigotclient.implementation.ResourceUpdate;
import tsp.spigotclient.implementation.SpigotAuthor;
import tsp.spigotclient.implementation.SpigotCategory;
import tsp.spigotclient.implementation.SpigotIdentity;
import tsp.spigotclient.implementation.SpigotResource;
import tsp.spigotclient.implementation.SpigotResourceStatistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wrapper for Spigot's REST API
 *
 * @author TheSilentPro
 */
public class SpigotClient {

    private ExecutorService executor;
    private int timeout;

    public SpigotClient(ExecutorService executor, int timeout) {
        this.executor = executor;
        this.timeout = timeout;
    }
    
    public SpigotClient(int threads, int timeout) {
        this(Executors.newFixedThreadPool(threads, new SpigotThreadFactory()), timeout);
    }
    
    public SpigotClient(int threads) {
        this(threads, 5000);
    }

    public SpigotClient() {
        this(2);
    }

    /**
     * Retrieve a list of resources from a category
     *
     * @param id The category id
     * @param page The page
     * @return Resources list
     */
    public CompletableFuture<List<SpigotResource>> getResourcesList(int id, int page) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray main = read("?action=listResources&category=" + id + "&page=" + page).getAsJsonArray();
                List<SpigotResource> resources = new ArrayList<>();
                for (JsonElement entry : main) {
                    resources.add(buildResource(entry.getAsJsonObject()));
                }

                return resources;
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve a list of resources from a {@link SpigotCategory}
     *
     * @param category The spigot category
     * @param page The page
     * @return Resources list
     */
    public CompletableFuture<List<SpigotResource>> getResourcesList(SpigotCategory category, int page) {
        return getResourcesList(category.getId(), page);
    }

    /**
     * Retrieve a resource from an id
     *
     * @param id The resource id
     * @return Resource
     */
    public CompletableFuture<SpigotResource> getResource(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return buildResource(read("?action=getResource&id=" + id).getAsJsonObject());
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve a list of resources from an author
     *
     * @param id The author id
     * @param page The page
     * @return Author resources list
     */
    public CompletableFuture<List<SpigotResource>> getResourcesByAuthor(int id, int page) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray main = read("?action=getResourcesByAuthor&id=" + id + "&&page=" + page).getAsJsonArray();
                List<SpigotResource> resources = new ArrayList<>();
                for (JsonElement entry : main) {
                    resources.add(buildResource(entry.getAsJsonObject()));
                }

                return resources;
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve a list of categories
     *
     * @return Category list
     */
    public CompletableFuture<List<ResourceCategory>> getResourceCategoryList() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray main = read("?action=listResourceCategories").getAsJsonArray();
                List<ResourceCategory> categories = new ArrayList<>();
                for (JsonElement entry : main) {
                    JsonObject obj = entry.getAsJsonObject();
                    categories.add(new ResourceCategory(
                            obj.get("id").getAsInt(),
                            obj.get("title").getAsString(),
                            obj.get("description").getAsString()
                    ));
                }

                return categories;
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve a resource update
     *
     * @param id The resource id
     * @return Resource update
     */
    public CompletableFuture<ResourceUpdate> getResourceUpdate(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject main = read("?action=getResourceUpdate&id=" + id).getAsJsonObject();
                return new ResourceUpdate(
                        main.get("id").getAsInt(),
                        main.get("resource_id").getAsInt(),
                        main.get("title").getAsString(),
                        main.get("message").getAsString()
                );
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve a list of updates for a resource
     *
     * @param id The resource id
     * @param page The page
     * @return Resource updates list
     */
    public CompletableFuture<List<ResourceUpdate>> getResourceUpdates(int id, int page) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray main = read("?action=getResourceUpdates&id=" + id + "&page=" + page).getAsJsonArray();
                List<ResourceUpdate> updates = new ArrayList<>();
                for (JsonElement entry : main) {
                    JsonObject obj = entry.getAsJsonObject();
                    updates.add(new ResourceUpdate(
                            obj.get("id").getAsInt(),
                            obj.get("resource_id").getAsInt(),
                            obj.get("title").getAsString(),
                            obj.get("message").getAsString()
                    ));
                }

                return updates;
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve an author
     *
     * @param id The author id
     * @return The author
     */
    public CompletableFuture<SpigotAuthor> getAuthor(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject main = read("?action=getAuthor&id=" + id).getAsJsonObject();
                JsonObject jsonIdentities = main.get("identities").getAsJsonObject();
                List<SpigotIdentity> identities = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : jsonIdentities.entrySet()) {
                    identities.add(new SpigotIdentity(entry.getKey(), entry.getValue().getAsString()));
                }

                return new SpigotAuthor(
                        main.get("id").getAsInt(),
                        main.get("username").getAsString(),
                        main.get("resource_count").getAsInt(),
                        main.get("avatar").getAsString(),
                        identities
                );
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Retrieve an author from a name
     *
     * @param name The author name
     * @return The author
     */
    public CompletableFuture<SpigotAuthor> findAuthor(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject main = read("?action=findAuthor&name=" + name).getAsJsonObject();
                JsonObject jsonIdentities = main.get("identities").getAsJsonObject();
                List<SpigotIdentity> identities = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : jsonIdentities.entrySet()) {
                    identities.add(new SpigotIdentity(entry.getKey(), entry.getValue().getAsString()));
                }

                return new SpigotAuthor(
                        main.get("id").getAsInt(),
                        main.get("username").getAsString(),
                        main.get("resource_count").getAsInt(),
                        main.get("avatar").getAsString(),
                        identities
                );
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        }, executor);
    }

    /**
     * Used for building a {@link SpigotResource}
     *
     * @param json The json response of the resource
     * @return Wrapped resource
     */
    private SpigotResource buildResource(JsonObject json) {
        JsonObject premium = json.get("premium").getAsJsonObject();
        JsonObject author = json.get("author").getAsJsonObject();
        JsonObject stats = json.get("stats").getAsJsonObject();
        JsonObject reviews = stats.get("reviews").getAsJsonObject();

        List<String> versions = new ArrayList<>();
        JsonArray jsonSupportedMinecraftVersions = !json.get("supported_minecraft_versions").isJsonNull() ? json.get("supported_minecraft_versions").getAsJsonArray() : new JsonArray();
        for (JsonElement entry : jsonSupportedMinecraftVersions) {
            versions.add(entry.getAsString());
        }

        String nativeMinecraftVersion = !json.get("native_minecraft_version").isJsonNull()
                ? json.get("native_minecraft_version").getAsString()
                : "";

        return new SpigotResource(
                json.get("id").getAsInt(),
                json.get("title").getAsString(),
                json.get("tag").getAsString(),
                json.get("description").getAsString(),
                json.get("current_version").getAsString(),
                nativeMinecraftVersion,
                versions.toArray(new String[0]),
                json.get("icon_link").getAsString(),

                new SpigotResourceStatistics(
                        author.get("id").getAsInt(),
                        author.get("username").getAsString(),

                        premium.get("price").getAsDouble(),
                        premium.get("currency").getAsString(),

                        stats.get("downloads").getAsInt(),
                        stats.get("updates").getAsInt(),
                        stats.get("rating").getAsDouble(),
                        reviews.get("unique").getAsInt(),
                        reviews.get("total").getAsInt()
                )
        );
    }

    /**
     * Reads the json contents of the url
     *
     * @param url The url to read from
     * @return Json response
     * @throws IOException Error
     */
    private JsonElement read(String url) throws IOException {
        URLConnection connection = new URL("https://api.spigotmc.org/simple/0.2/index.php" + url).openConnection();
        connection.setConnectTimeout(timeout);
        connection.setRequestProperty("User-Agent", "SpigotClient");

        return JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));
    }

    /**
     * Retrieve the {@link ExecutorService} associated with this client
     *
     * @return The executor service
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Retrieve the timeout for requests
     *
     * @return The timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the {@link ExecutorService} for this client
     *
     * @param executor The executor service
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Set the timeout for requests
     *
     * @param timeout The timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
