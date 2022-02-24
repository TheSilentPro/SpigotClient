package tsp.spigotclient;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SpigotThreadFactory implements ThreadFactory {

    private final AtomicInteger i = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "SpigotClientThread-" + i.getAndIncrement());
    }

}
