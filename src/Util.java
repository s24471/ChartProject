import java.util.concurrent.TimeUnit;

public abstract class Util {
    public static void sleepMili(long n){
        try {
            TimeUnit.MILLISECONDS.sleep(n);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
