import pl.animekkk.anlogger.Logger;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        Logger.setRuntimeSave(false);
        Logger.setMaxCache(25);
        Logger.log(Logger.Log.DEBUG, "TEST", new Date(), new AtomicInteger());

        for (int i = 0; i < 100; i++) {
            Logger.log(Logger.Log.values()[ThreadLocalRandom.current().nextInt(3)], i + "Test message");
            Thread.sleep(100);
        }
    }

}
