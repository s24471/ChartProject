import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfigPanel extends ListPanel {
    public static StyleStockType styleStockType = StyleStockType.ADAPTIVE;
    public static boolean coloredLines = false;
    public static boolean coloredCandles = true;
    public static int candleToLineThreshold = 2;

    private ThresholdSliderPanel thresholdSliderPanel;
    private ColorChartPanel colorChartPanel;
    public ConfigPanel() {
        loadFromJson();
        initializeContent();
    }

    @Override
    protected void initializeContent() {
        JPanel generalSettingsPanel = new JPanel();
        generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.Y_AXIS));
        generalSettingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        generalSettingsPanel.setBackground(new Color(30, 30, 30));

        ZoomingBehaviorPanel zoomingBehaviourPanel = new ZoomingBehaviorPanel(this);
        generalSettingsPanel.add(zoomingBehaviourPanel);

        thresholdSliderPanel = new ThresholdSliderPanel();
        thresholdSliderPanel.setVisible(styleStockType == StyleStockType.ADAPTIVE);
        generalSettingsPanel.add(thresholdSliderPanel);

        colorChartPanel = new ColorChartPanel();
        generalSettingsPanel.add(colorChartPanel);

        addCategory("General Settings", generalSettingsPanel);
        refreshContentPanel();
    }

    public void updateStyleStockType(StyleStockType stockType) {
        styleStockType = stockType;
        thresholdSliderPanel.setVisible(styleStockType == StyleStockType.ADAPTIVE);
        colorChartPanel.updatedStyle(styleStockType);
        saveToJson();
        Main.frame.repaint();
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("styleStockType", styleStockType.name());
        json.put("coloredLines", coloredLines);
        json.put("coloredCandles", coloredCandles);
        json.put("candleToLineThreshold", candleToLineThreshold);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if(json == null) return;

        styleStockType = StyleStockType.valueOf((String) json.get("styleStockType"));
        coloredLines = (boolean) json.get("coloredLines");
        coloredCandles = (boolean) json.get("coloredCandles");
        candleToLineThreshold = ((Long) json.get("candleToLineThreshold")).intValue();
    }


    public enum StyleStockType {
        ADAPTIVE, LINE, CANDLE
    }
}
