import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParabolicSarAlgo implements Algorithm {
    private double initialAF = 0.02;
    private double maxAF = 0.2;
    private Color lineColor = Color.MAGENTA;
    private float lineThickness = 2.0f;
    private boolean enabled = true;

    public ParabolicSarAlgo() {
        loadFromJson();
    }

    @Override
    public void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        if (!enabled || rates == null || rates.isEmpty()) return;

        List<Double> sarValues = calculateParabolicSAR(rates);

        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        for (int i = startIndex; i < startIndex + range && i < rates.size(); i++) {
            RateInfo currRate = rates.get(i);
            if (currRate.high > maxPrice) maxPrice = currRate.high;
            if (currRate.low < minPrice) minPrice = currRate.low;
        }

        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineThickness));

        int totalWidth = chartWidth - 60;

        for (int i = startIndex; i < startIndex + range && i < sarValues.size(); i++) {
            if (sarValues.get(i) != null) {
                int x = Algorithm.calculateXPosition(i, startIndex, range, totalWidth);
                int y = Algorithm.calculateYPosition(sarValues.get(i), minPrice, maxPrice, chartHeight);
                g.fillOval(x - 2, y - 2, 4, 4);
            }
        }
    }


    private List<Double> calculateParabolicSAR(List<RateInfo> rates) {
        List<Double> sarValues = new ArrayList<>(Collections.nCopies(rates.size(), null));

        boolean isUptrend = true;
        double af = initialAF;
        double ep = rates.get(0).high;
        double sar = rates.get(0).low;

        for (int i = 1; i < rates.size(); i++) {
            RateInfo current = rates.get(i);
            RateInfo previous = rates.get(i - 1);

            sar += af * (ep - sar);

            if (isUptrend) {
                if (current.low < sar) {
                    isUptrend = false;
                    sar = ep;
                    ep = current.low;
                    af = initialAF;
                } else {
                    if (current.high > ep) {
                        ep = current.high;
                        af = Math.min(af + initialAF, maxAF);
                    }
                }
            } else {
                if (current.high > sar) {
                    isUptrend = true;
                    sar = ep;
                    ep = current.high;
                    af = initialAF;
                } else {
                    if (current.low < ep) {
                        ep = current.low;
                        af = Math.min(af + initialAF, maxAF);
                    }
                }
            }

            sarValues.set(i, sar);
        }

        return sarValues;
    }

    @Override
    public JPanel getConfigurationPanel() {
        return new ParabolicSarPanel(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "Parabolic SAR";
    }

    public void restoreDefault() {
        initialAF = 0.02;
        maxAF = 0.2;
        lineColor = Color.MAGENTA;
        lineThickness = 2.0f;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("enabled", enabled);
        json.put("initialAF", initialAF);
        json.put("maxAF", maxAF);
        json.put("lineColor", SettingsUtil.colorToHex(lineColor));
        json.put("lineThickness", lineThickness);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if (json == null) return;
        enabled = (boolean) json.get("enabled");
        initialAF = (double) json.get("initialAF");
        maxAF = (double) json.get("maxAF");
        lineColor = SettingsUtil.hexToColor((String) json.get("lineColor"));
        lineThickness = ((Double) json.get("lineThickness")).floatValue();
    }

    public double getInitialAF() {
        return initialAF;
    }

    public void setInitialAF(double initialAF) {
        this.initialAF = initialAF;
    }

    public double getMaxAF() {
        return maxAF;
    }

    public void setMaxAF(double maxAF) {
        this.maxAF = maxAF;
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
