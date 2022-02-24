package tsp.spigotclient.implementation;

public enum SpigotCategory {

    SPIGOT(4),
    CHAT(14),
    TOOLS_AND_UTILITIES(15),
    MISCELLANEOUS(16),
    GAME_MODE(17),
    WORLD_MANAGEMENT(18),
    MECHANICS(22),
    ECONOMY(23),
    FUN(24),
    SKRIPT(25),
    LIBRARIES(26),
    NO_RATING(28),

    BUNGEE_SPIGOT(2),
    BUNGEE_TRANSPORTATION(5),
    BUNGEE_CHAT(6),
    BUNGEE_TOOLS_AND_UTILITIES(7),
    BUNGEE_MISCELLANEOUS(8),

    BUNGEE_PROXY(3),
    PROXY_LIBRARIES(9),
    PROXY_TRANSPORTATION(10),
    PROXY_CHAT(11),
    PROXY_TOOLS_AND_UTILITIES(12),
    PROXY_MISCELLANEOUS(13),

    STANDALONE(19),
    PREMIUM(20),
    UNIVERSAL(21),
    WEB(27);

    public static final SpigotCategory[] values = values();
    private final int id;

    SpigotCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
