import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TemaAlgo implements Algorithm {
    private int shortPeriod = 10;
    private int mediumPeriod = 20;
    private int longPeriod = 30;

    private Color shortColor = Color.BLUE;
    private Color mediumColor = Color.RED;
    private Color longColor = Color.GREEN;

    private float shortLineThickness = 2.0f;
    private float mediumLineThickness = 2.0f;
    private float longLineThickness = 2.0f;

    private boolean showShort = true;
    private boolean showMedium = true;
    private boolean showLong = true;

    private boolean enabled = true;

    public TemaAlgo() {
        loadFromJson();
    }

    @Override
    public void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        if (!enabled) return;

        TemaLines lines = calculate(rates);

        if (showShort) {
            Algorithm.drawLine(g, lines.shortLine, shortColor, shortLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
        if (showMedium) {
            Algorithm.drawLine(g, lines.mediumLine, mediumColor, mediumLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
        if (showLong) {
            Algorithm.drawLine(g, lines.longLine, longColor, longLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
    }

    @Override
    public JPanel getConfigurationPanel() {
        return new TemaPanel(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "TEMA";
    }

    public TemaLines calculate(List<RateInfo> rates) {
        if (rates == null || rates.isEmpty()) {
            throw new IllegalArgumentException("Rates list cannot be null or empty.");
        }

        List<Double> closingPrices = rates.stream()
                .map(rate -> rate != null ? rate.close : null)
                .collect(Collectors.toList());

        List<Double> shortTEMA = calculateTEMA(closingPrices, shortPeriod);
        List<Double> mediumTEMA = calculateTEMA(closingPrices, mediumPeriod);
        List<Double> longTEMA = calculateTEMA(closingPrices, longPeriod);

        return new TemaLines(shortTEMA, mediumTEMA, longTEMA);
    }

    private List<Double> calculateTEMA(List<Double> prices, int period) {
        if (prices == null || prices.isEmpty()) {
            throw new IllegalArgumentException("Prices list cannot be null or empty.");
        }

        List<Double> ema1 = calculateEMA(prices, period);
        List<Double> ema2 = calculateEMA(ema1, period);
        List<Double> ema3 = calculateEMA(ema2, period);

        List<Double> tema = new ArrayList<>(Collections.nCopies(prices.size(), null));
        for (int i = 0; i < prices.size(); i++) {
            if (ema1.get(i) != null && ema2.get(i) != null && ema3.get(i) != null) {
                tema.set(i, 3 * ema1.get(i) - 3 * ema2.get(i) + ema3.get(i));
            }
        }

        return tema;
    }

    private List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> ema = new ArrayList<>(Collections.nCopies(prices.size(), null));
        if (prices.size() < period) return ema;

        double multiplier = 2.0 / (period + 1);
        double emaValue = prices.subList(0, period).stream()
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        ema.set(period - 1, emaValue);

        for (int i = period; i < prices.size(); i++) {
            Double currentPrice = prices.get(i);
            if (currentPrice != null) {
                emaValue = (currentPrice - emaValue) * multiplier + emaValue;
                ema.set(i, emaValue);
            }
        }

        return ema;
    }

    public int getShortPeriod() {
        return shortPeriod;
    }

    public void setShortPeriod(int shortPeriod) {
        this.shortPeriod = shortPeriod;
    }

    public int getMediumPeriod() {
        return mediumPeriod;
    }

    public void setMediumPeriod(int mediumPeriod) {
        this.mediumPeriod = mediumPeriod;
    }

    public int getLongPeriod() {
        return longPeriod;
    }

    public void setLongPeriod(int longPeriod) {
        this.longPeriod = longPeriod;
    }

    public Color getShortColor() {
        return shortColor;
    }

    public void setShortColor(Color shortColor) {
        this.shortColor = shortColor;
    }

    public Color getMediumColor() {
        return mediumColor;
    }

    public void setMediumColor(Color mediumColor) {
        this.mediumColor = mediumColor;
    }

    public Color getLongColor() {
        return longColor;
    }

    public void setLongColor(Color longColor) {
        this.longColor = longColor;
    }

    public float getShortLineThickness() {
        return shortLineThickness;
    }

    public void setShortLineThickness(float shortLineThickness) {
        this.shortLineThickness = shortLineThickness;
    }

    public float getMediumLineThickness() {
        return mediumLineThickness;
    }

    public void setMediumLineThickness(float mediumLineThickness) {
        this.mediumLineThickness = mediumLineThickness;
    }

    public float getLongLineThickness() {
        return longLineThickness;
    }

    public void setLongLineThickness(float longLineThickness) {
        this.longLineThickness = longLineThickness;
    }

    public boolean isShowShort() {
        return showShort;
    }

    public void setShowShort(boolean showShort) {
        this.showShort = showShort;
    }

    public boolean isShowMedium() {
        return showMedium;
    }

    public void setShowMedium(boolean showMedium) {
        this.showMedium = showMedium;
    }

    public boolean isShowLong() {
        return showLong;
    }

    public void setShowLong(boolean showLong) {
        this.showLong = showLong;
    }

    public void restoreDefault() {
        shortPeriod = 10;
        mediumPeriod = 20;
        longPeriod = 30;
        shortColor = Color.BLUE;
        mediumColor = Color.RED;
        longColor = Color.GREEN;
        shortLineThickness = 2.0f;
        mediumLineThickness = 2.0f;
        longLineThickness = 2.0f;
        showShort = true;
        showMedium = true;
        showLong = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("enabled", enabled);
        json.put("shortColor", SettingsUtil.colorToHex(shortColor));
        json.put("mediumColor", SettingsUtil.colorToHex(mediumColor));
        json.put("longColor", SettingsUtil.colorToHex(longColor));
        json.put("shortLineThickness", shortLineThickness);
        json.put("mediumLineThickness", mediumLineThickness);
        json.put("longLineThickness", longLineThickness);
        json.put("shortPeriod", shortPeriod);
        json.put("mediumPeriod", mediumPeriod);
        json.put("longPeriod", longPeriod);
        json.put("showShort", showShort);
        json.put("showMedium", showMedium);
        json.put("showLong", showLong);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if (json == null) return;
        enabled = (boolean) json.get("enabled");
        shortColor = SettingsUtil.hexToColor((String) json.get("shortColor"));
        mediumColor = SettingsUtil.hexToColor((String) json.get("mediumColor"));
        longColor = SettingsUtil.hexToColor((String) json.get("longColor"));
        shortLineThickness = ((Double) json.get("shortLineThickness")).floatValue();
        mediumLineThickness = ((Double) json.get("mediumLineThickness")).floatValue();
        longLineThickness = ((Double) json.get("longLineThickness")).floatValue();
        shortPeriod = ((Long) json.get("shortPeriod")).intValue();
        mediumPeriod = ((Long) json.get("mediumPeriod")).intValue();
        longPeriod = ((Long) json.get("longPeriod")).intValue();
        showShort = (Boolean) json.get("showShort");
        showMedium = (Boolean) json.get("showMedium");
        showLong = (Boolean) json.get("showLong");
    }
}

class TemaLines {
    public List<Double> shortLine;
    public List<Double> mediumLine;
    public List<Double> longLine;

    public TemaLines(List<Double> shortLine, List<Double> mediumLine, List<Double> longLine) {
        this.shortLine = shortLine;
        this.mediumLine = mediumLine;
        this.longLine = longLine;
    }
}
