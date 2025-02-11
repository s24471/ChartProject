import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeltnerChannelAlgo implements Algorithm {
    private int period = 20;
    private double multiplier = 2.0;
    private Color upperColor = Color.RED;
    private Color lowerColor = Color.GREEN;
    private Color midColor = Color.BLUE;
    private float lineThickness = 2.0f;
    private boolean enabled = true;

    public KeltnerChannelAlgo() {
        loadFromJson();
    }

    @Override
    public void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        if (!enabled || rates == null || rates.isEmpty()) return;

        KeltnerChannelLines channelLines = calculateKeltnerChannel(rates);

        Algorithm.drawLine(g, channelLines.upper, upperColor, lineThickness, rates, startIndex, range, chartWidth, chartHeight);
        Algorithm.drawLine(g, channelLines.middle, midColor, lineThickness, rates, startIndex, range, chartWidth, chartHeight);
        Algorithm.drawLine(g, channelLines.lower, lowerColor, lineThickness, rates, startIndex, range, chartWidth, chartHeight);
    }

    private KeltnerChannelLines calculateKeltnerChannel(List<RateInfo> rates) {
        List<Double> middleLine = calculateEMA(rates, period);
        List<Double> atr = calculateATR(rates, period);

        List<Double> upperLine = new ArrayList<>(Collections.nCopies(rates.size(), null));
        List<Double> lowerLine = new ArrayList<>(Collections.nCopies(rates.size(), null));

        for (int i = 0; i < rates.size(); i++) {
            if (middleLine.get(i) != null && atr.get(i) != null) {
                upperLine.set(i, middleLine.get(i) + multiplier * atr.get(i));
                lowerLine.set(i, middleLine.get(i) - multiplier * atr.get(i));
            }
        }

        return new KeltnerChannelLines(upperLine, middleLine, lowerLine);
    }

    private List<Double> calculateEMA(List<RateInfo> rates, int period) {
        List<Double> ema = new ArrayList<>(Collections.nCopies(rates.size(), null));
        if (rates.size() < period) return ema;

        double multiplier = 2.0 / (period + 1);
        double emaValue = rates.subList(0, period).stream()
                .mapToDouble(rate -> rate.close)
                .average()
                .orElse(0);

        ema.set(period - 1, emaValue);

        for (int i = period; i < rates.size(); i++) {
            emaValue = (rates.get(i).close - emaValue) * multiplier + emaValue;
            ema.set(i, emaValue);
        }

        return ema;
    }

    private List<Double> calculateATR(List<RateInfo> rates, int period) {
        List<Double> atr = new ArrayList<>(Collections.nCopies(rates.size(), null));
        List<Double> tr = new ArrayList<>();

        for (int i = 1; i < rates.size(); i++) {
            double highLow = rates.get(i).high - rates.get(i).low;
            double highClose = Math.abs(rates.get(i).high - rates.get(i - 1).close);
            double lowClose = Math.abs(rates.get(i).low - rates.get(i - 1).close);

            tr.add(Math.max(highLow, Math.max(highClose, lowClose)));
        }

        double atrValue = tr.subList(0, period).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        atr.set(period - 1, atrValue);

        for (int i = period; i < tr.size(); i++) {
            atrValue = (atrValue * (period - 1) + tr.get(i)) / period;
            atr.set(i, atrValue);
        }

        return atr;
    }

    @Override
    public JPanel getConfigurationPanel() {
        return new KeltnerChannelPanel(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "Keltner Channel";
    }

    public void restoreDefault() {
        period = 20;
        multiplier = 2.0;
        upperColor = Color.RED;
        lowerColor = Color.GREEN;
        midColor = Color.BLUE;
        lineThickness = 2.0f;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("enabled", enabled);
        json.put("period", period);
        json.put("multiplier", multiplier);
        json.put("upperColor", SettingsUtil.colorToHex(upperColor));
        json.put("lowerColor", SettingsUtil.colorToHex(lowerColor));
        json.put("midColor", SettingsUtil.colorToHex(midColor));
        json.put("lineThickness", lineThickness);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if (json == null) return;
        enabled = (boolean) json.get("enabled");
        period = ((Long) json.get("period")).intValue();
        multiplier = (double) json.get("multiplier");
        upperColor = SettingsUtil.hexToColor((String) json.get("upperColor"));
        lowerColor = SettingsUtil.hexToColor((String) json.get("lowerColor"));
        midColor = SettingsUtil.hexToColor((String) json.get("midColor"));
        lineThickness = ((Double) json.get("lineThickness")).floatValue();
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Color getUpperColor() {
        return upperColor;
    }

    public void setUpperColor(Color upperColor) {
        this.upperColor = upperColor;
    }

    public Color getLowerColor() {
        return lowerColor;
    }

    public void setLowerColor(Color lowerColor) {
        this.lowerColor = lowerColor;
    }

    public Color getMidColor() {
        return midColor;
    }

    public void setMidColor(Color midColor) {
        this.midColor = midColor;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }
}

class KeltnerChannelLines {
    public List<Double> upper;
    public List<Double> middle;
    public List<Double> lower;

    public KeltnerChannelLines(List<Double> upper, List<Double> middle, List<Double> lower) {
        this.upper = upper;
        this.middle = middle;
        this.lower = lower;
    }
}
