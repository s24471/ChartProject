import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class Sender implements Runnable {

    private int timeToSend = 0;
    private static int brakeTime = 200;
    private ArrayList<String> sendList;
    private PrintStream output;
    private final Thread t;

    public Sender(PrintStream output) {
        this.output = output;
        sendList = new ArrayList<>();
        t = new Thread(this);
        t.start();
    }

    public void exit() {
        t.interrupt();
    }

    public void add(String message) {
        synchronized (this) {
            sendList.add(message);
            this.notifyAll();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (timeToSend > 0) {
                    try {
                        Util.sleepMili(timeToSend);
                    } catch (RuntimeException e) {
                        return;
                    }
                    timeToSend = 0;
                }
                synchronized (this) {
                    while (sendList.isEmpty()) {
                        wait();
                    }

                    String message = sendList.remove(0);
                    System.out.println(message);
                    output.println(message);
                    output.flush();
                    timeToSend = brakeTime;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Sender thread was interrupted.");
        }
    }
}
