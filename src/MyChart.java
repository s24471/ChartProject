import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyChart extends JPanel {
    private List<RateInfo> rates;
    private int startIndex;
    private int dragStartIndex;
    private int range;
    private int mouseX;

    public MyChart(List<RateInfo> rates) {
        this.rates = rates;
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.black);
        startIndex = 0;
        range = rates.size();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int mouseXIndex = (int) ((mouseX / (double) getWidth()) * range + startIndex);
                int rotation = e.getWheelRotation();
                int zoomAmount = range / 10;

                if (rotation < 0 && range > 10) {
                    range = Math.max(range - zoomAmount, 10);
                } else if (rotation > 0) {
                    int oldRange = range;
                    range = Math.min(range + zoomAmount, rates.size());
                    if (startIndex + oldRange == rates.size() && range > oldRange) {
                        startIndex = Math.max(0, rates.size() - range);
                    }
                }

                startIndex = mouseXIndex - (int) ((mouseX / (double) getWidth()) * range);
                startIndex = Math.max(0, Math.min(startIndex, rates.size() - range));
                repaint();
            }
        });


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int deltaX = e.getX() - mouseX;
                    int deltaIndex = (int) (deltaX / (getWidth() / (double) range));
                    startIndex = Math.max(0, Math.min(rates.size() - range, dragStartIndex - deltaIndex));
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    mouseX = e.getX();
                    dragStartIndex = startIndex;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rates == null || rates.isEmpty()) return;
        Graphics2D g2 = (Graphics2D) g;

        drawChart(g2);
        drawDateLabels(g2);
        drawPriceLabels(g2);

        for (Algorithm algorithm : AlgorithmManager.getInstance().getAllAlgorithms()) {
            if (algorithm.isEnabled())
                algorithm.draw(g2, rates, startIndex, range, getWidth(), getHeight());
        }
    }

    private void drawPriceLabels(Graphics2D g) {
        g.setColor(Color.gray);
        int rightMargin = 50;
        double minPrice = rates.get(startIndex).low;
        double maxPrice = rates.get(startIndex).high;

        for (int i = startIndex; i < startIndex + range && i < rates.size(); i++) {
            if (rates.get(i).high > maxPrice) maxPrice = rates.get(i).high;
            if (rates.get(i).low < minPrice) minPrice = rates.get(i).low;
        }

        int numberOfLabels = 10;
        double step = (maxPrice - minPrice) / (numberOfLabels - 1);

        DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < numberOfLabels; i++) {
            double price = minPrice + i * step;
            int y = calculateYPosition(price, minPrice, maxPrice);
            String label = df.format(price);
            g.drawString(label, getWidth() - rightMargin + 5, y);

            // g.drawLine(0, y, getWidth() - rightMargin, y);
        }
    }

    private int calculateYPosition(double value, double min, double max) {
        int height = getHeight() - 100;
        return (int) ((max - value) / (max - min) * height);
    }

    private void drawDateLabels(Graphics2D g) {
        SimpleDateFormat topLabelFormat;
        SimpleDateFormat bottomLabelFormat;
        g.setColor(Color.gray);
        long startTime = rates.get(startIndex).ctm;
        long endTime = rates.get(Math.min(startIndex + range - 1, rates.size() - 1)).ctm;

        FontMetrics fm = g.getFontMetrics();

        long duration = endTime - startTime;
        if (duration < 3600L * 1000L) {
            topLabelFormat = new SimpleDateFormat("mm");
            bottomLabelFormat = new SimpleDateFormat("dd-MMM-yyyy HH");
        } else if (duration < 24L * 3600 * 1000L) {
            topLabelFormat = new SimpleDateFormat("HH");
            bottomLabelFormat = new SimpleDateFormat("dd-MMM-yyyy");
        } else if (duration < 31L * 24 * 3600 * 1000L) {
            topLabelFormat = new SimpleDateFormat("dd");
            bottomLabelFormat = new SimpleDateFormat("MMM-yyyy");
        } else if (duration < 365L * 24 * 3600 * 1000L) {
            topLabelFormat = new SimpleDateFormat("MMM");
            bottomLabelFormat = new SimpleDateFormat("yyyy");
        } else {
            topLabelFormat = new SimpleDateFormat("yyyy");
            bottomLabelFormat = new SimpleDateFormat("");
        }


        int sectionBottomStart = startIndex;
        int sectionTopStart = startIndex;
        int sectionEnd = startIndex;
        String lastBottomLabel = null, lastTopLabel = null;

        for (int i = startIndex; i <= startIndex + range && i < rates.size(); i++) {
            RateInfo info = rates.get(i);
            String topLabel = topLabelFormat.format(new Date(info.ctm));
            String bottomLabel = bottomLabelFormat.format(new Date(info.ctm));

            if (!topLabel.equals(lastTopLabel) || i == startIndex + range || i == rates.size() - 1) {
                if (lastTopLabel != null) {
                    drawTopLabel(g, lastTopLabel, sectionTopStart, sectionEnd, startIndex, fm);
                }
                sectionTopStart = i;
                lastTopLabel = topLabel;
            }

            if (!bottomLabel.equals(lastBottomLabel) || i == startIndex + range || i == rates.size() - 1) {
                if (lastBottomLabel != null) {
                    drawBottomLabel(g, lastBottomLabel, sectionBottomStart, sectionEnd, startIndex, fm);
                }
                sectionBottomStart = i;
                lastBottomLabel = bottomLabel.isEmpty() ? null : bottomLabel;
            }

            sectionEnd = i;
        }


    }

    private void drawTopLabel(Graphics2D g, String label, int sectionStart, int sectionEnd, int startIndex, FontMetrics fm) {
        int midXPosition = computeMidSectionXPosition(sectionStart, sectionEnd, startIndex);
        g.drawString(label, midXPosition - fm.stringWidth(label) / 2, getHeight() - 40);
        if (sectionEnd != sectionStart && sectionEnd != startIndex + range - 1) {
            drawSectionLine(g, sectionEnd, startIndex, 35, 55);
        }
    }

    private void drawBottomLabel(Graphics2D g, String label, int sectionStart, int sectionEnd, int startIndex, FontMetrics fm) {
        int midXPosition = computeMidSectionXPosition(sectionStart, sectionEnd, startIndex);
        g.drawString(label, midXPosition - fm.stringWidth(label) / 2, getHeight() - 15);
        if (sectionEnd != sectionStart && sectionEnd != startIndex + range - 1) {
            drawSectionLine(g, sectionEnd, startIndex, 0, 25);
        }
    }

    private int computeMidSectionXPosition(int sectionStart, int sectionEnd, int startIndex) {
        double start = (sectionStart - startIndex) * (getWidth() / (double) range);
        double end = (sectionEnd - startIndex) * (getWidth() / (double) range);
        return (int) ((start + end) / 2);
    }

    private void drawSectionLine(Graphics2D g, int sectionEnd, int startIndex, int y1, int y2) {
        int xPosition = (int) ((sectionEnd - startIndex) * (getWidth() / (double) range));
        g.setColor(Color.gray);
        float[] dashPattern = {10, 5};
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dashPattern, 0));
        g.drawLine(xPosition, getHeight() - y1, xPosition, getHeight() - y2);
    }


    private void drawChart(Graphics2D g) {
        double candleSpace = (double) (getWidth() - 60) / range;
        int candleWidth = Math.max(1, (int) candleSpace);

        if (ConfigPanel.styleStockType == ConfigPanel.StyleStockType.CANDLE
                || (candleWidth > ConfigPanel.candleToLineThreshold && ConfigPanel.styleStockType == ConfigPanel.StyleStockType.ADAPTIVE)) {
            drawCandles(g, candleWidth, candleSpace);
        } else {
            drawLineGraph(g, candleSpace);
        }
    }

    private void drawCandles(Graphics2D g, int candleWidth, double candleSpace) {
        int candleOutline = candleWidth > 1 ? 1 : 0;
        double min = rates.get(startIndex).low;
        double max = rates.get(startIndex).high;

        for (int i = startIndex; i < startIndex + range && i < rates.size(); i++) {
            RateInfo currRate = rates.get(i);
            if (currRate.high > max) max = currRate.high;
            if (currRate.low < min) min = currRate.low;
        }

        for (int i = startIndex; i < startIndex + range && i < rates.size(); i++) {
            drawCandle(rates.get(i), g, i - startIndex, candleWidth, candleSpace, candleOutline, min, max);
        }
    }

    private void drawLineGraph(Graphics2D g, double candleSpace) {
        int previousX = 0;
        int previousY = 0;
        double min = rates.get(startIndex).low;
        double max = rates.get(startIndex).high;

        previousY = calculateYPosition(rates.get(startIndex).close, min, max);
        previousX = 0;

        for (int i = startIndex + 1; i < startIndex + range && i < rates.size(); i++) {
            RateInfo currRate = rates.get(i);
            if (currRate.high > max) max = currRate.high;
            if (currRate.low < min) min = currRate.low;
        }

        for (int i = startIndex + 1; i < startIndex + range && i < rates.size(); i++) {
            int x = (int) ((i - startIndex) * candleSpace);
            int y = calculateYPosition(rates.get(i).close, min, max);


            g.setColor(Color.darkGray);
            if (ConfigPanel.coloredLines) {
                if (rates.get(i).close > rates.get(i - 1).close) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }
            }
            g.drawLine(previousX, previousY, x, y);

            previousX = x;
            previousY = y;
        }
    }


    private void drawCandle(RateInfo rate, Graphics2D g, int index, int candleWidth, double candleSpace,
                            int candleOutline, double min, double max) {
        int yHigh = calculateYPosition(rate.high, min, max);
        int yLow = calculateYPosition(rate.low, min, max);
        int yOpen = calculateYPosition(rate.open, min, max);
        int yClose = calculateYPosition(rate.close, min, max);

        if (ConfigPanel.coloredCandles) {
            Color color = rate.close > rate.open ? Color.GREEN : Color.RED;
            g.setColor(color);
        } else {
            g.setColor(Color.gray);
        }

        int xPosition = (int) (index * candleSpace) + candleOutline;
        g.drawLine(xPosition + candleWidth / 2, yHigh, xPosition + candleWidth / 2, yLow);
        g.fillRect(xPosition, Math.min(yOpen, yClose), candleWidth, Math.abs(yClose - yOpen));
    }
}
