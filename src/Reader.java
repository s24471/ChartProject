import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;

class Reader implements Runnable {
    private BufferedReader bufferedReader;
    private Thread thread;
    public Reader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        thread = new Thread(this);
        thread.start();
    }

    public void exit(){
        thread.interrupt();
    }
    public void run() {
        while (true) {
            if (!read()) {
                break;
            }
            try{
                Util.sleepMili(10);
            }catch (RuntimeException e){
                return;
            }
        }
    }

    public boolean read() {
        try {
            String s = bufferedReader.readLine();

            if (s == null) {
                System.out.println("Connection closed by server.");
                return false;
            }

            if (s.isEmpty()) return true;

            JSONObject response = (JSONObject) new JSONParser().parse(s);
            String customTag = (String) response.get("customTag");

            if (customTag == null) customTag = (String) response.get("customComment");

            Main.client.addResponse(customTag, response);

        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            System.out.println("ParseException occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
