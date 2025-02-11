import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsUtil {
    private static final String FILE_PATH = "settings.json";

    public static void saveSettings() {
        System.out.println("Seetings saved:");
        JSONObject settings = new JSONObject();

        settings.put("configPanel", Main.sidebar.configPanel.saveToJson());

        for(Algorithm algorithm: AlgorithmManager.getInstance().getAllAlgorithms()){
            System.out.println(algorithm.saveToJson().toString());
            settings.put(algorithm.getName(), algorithm.saveToJson());
        }


        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(settings.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject loadSettings(String name) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONObject settings = (JSONObject) parser.parse(reader);
            return (JSONObject) settings.get(name);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            //saveSettings();
            return null;
        }
    }

    public static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color hexToColor(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }
}
