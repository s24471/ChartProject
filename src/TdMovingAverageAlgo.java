import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TdMovingAverageAlgo implements Algorithm {
    private int period = 9;
    private Color lineColor = Color.ORANGE;
    private float lineThickness = 2.0f;
    private boolean enabled = true;

    public TdMovingAverageAlgo() {
        loadFromJson();
    }

    @Override
    public void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        if (!enabled || rates == null || rates.isEmpty()) return;

        List<Double> tdMovingAverage = calculateTdMovingAverage(rates);
        Algorithm.drawLine(g, tdMovingAverage, lineColor, lineThickness, rates, startIndex, range, chartWidth, chartHeight);
    }

    private List<Double> calculateTdMovingAverage(List<RateInfo> rates) {
        List<Double> tdMovingAverage = new ArrayList<>(Collections.nCopies(rates.size(), null));

        for (int i = period - 1; i < rates.size(); i++) {
            double sum = 0;

            for (int j = i - period + 1; j <= i; j++) {
                RateInfo rate = rates.get(j);

                if (rate.close > rate.open) {
                    sum += rate.high;
                } else if (rate.close < rate.open) {
                    sum += rate.low;
                } else {
                    sum += rate.close;
                }
            }

            tdMovingAverage.set(i, sum / period);
        }

        return tdMovingAverage;
    }

    @Override
    public JPanel getConfigurationPanel() {
        return new TdMovingAveragePanel(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "TD Moving Average";
    }

    public void restoreDefault() {
        period = 9;
        lineColor = Color.ORANGE;
        lineThickness = 2.0f;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("enabled", enabled);
        json.put("period", period);
        json.put("lineColor", SettingsUtil.colorToHex(lineColor));
        json.put("lineThickness", lineThickness);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if (json == null) return;
        enabled = (boolean) json.get("enabled");
        period = ((Long) json.get("period")).intValue();
        lineColor = SettingsUtil.hexToColor((String) json.get("lineColor"));
        lineThickness = ((Double) json.get("lineThickness")).floatValue();
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }
}
