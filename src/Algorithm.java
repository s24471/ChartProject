import org.json.simple.JSONObject;

import javax.swing.JPanel;
import java.awt.*;
import java.util.List;

public interface Algorithm {
    void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight);
    JPanel getConfigurationPanel();
    boolean isEnabled();
    String getName();
    JSONObject saveToJson();
    void loadFromJson();
    static void drawLine(Graphics2D g, List<Double> line, Color color, float lineThickness,
                         List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        for (int i = startIndex; i < startIndex + range && i < rates.size(); i++) {
            RateInfo currRate = rates.get(i);
            if (currRate.high > maxPrice) maxPrice = currRate.high;
            if (currRate.low < minPrice) minPrice = currRate.low;
        }

        g.setColor(color);
        g.setStroke(new BasicStroke(lineThickness));

        int totalWidth = chartWidth - 60;

        for (int i = startIndex + 1; i < startIndex + range && i < line.size(); i++) {
            if (line.get(i) == null || line.get(i - 1) == null  || line.get(i-1) == 0 || line.get(i) == 0) continue;

            int x1 = calculateXPosition(i - 1, startIndex, range, totalWidth);
            int y1 = calculateYPosition(line.get(i - 1), minPrice, maxPrice, chartHeight);
            int x2 = calculateXPosition(i, startIndex, range, totalWidth);
            int y2 = calculateYPosition(line.get(i), minPrice, maxPrice, chartHeight);

            g.drawLine(x1, y1, x2, y2);
        }
    }
    static int calculateXPosition(int index, int startIndex, int range, int totalWidth) {
        if (range <= 0) return 0;
        double pointWidth = (double) totalWidth / range;
        return (int) ((index - startIndex) * pointWidth);
    }

    static int calculateYPosition(double value, double min, double max, int chartHeight) {
        int height = chartHeight - 100;
        return (int) ((max - value) / (max - min) * height);
    }
}
