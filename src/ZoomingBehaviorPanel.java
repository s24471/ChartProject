import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ZoomingBehaviorPanel extends JPanel {
    public ZoomingBehaviorPanel(ConfigPanel parentPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));

        JLabel zoomLabel = new JLabel("Chart zooming behaviour:");
        zoomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        zoomLabel.setForeground(new Color(200, 200, 200));
        zoomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton candlesButton = ThemeUtil.createStyledRadioButton("Candles");
        candlesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPanel.updateStyleStockType(ConfigPanel.StyleStockType.CANDLE);
            }
        });
        candlesButton.setSelected(ConfigPanel.styleStockType == ConfigPanel.StyleStockType.CANDLE);

        JRadioButton adaptiveButton = ThemeUtil.createStyledRadioButton("Adaptive");
        adaptiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPanel.updateStyleStockType(ConfigPanel.StyleStockType.ADAPTIVE);
            }
        });
        adaptiveButton.setSelected(ConfigPanel.styleStockType == ConfigPanel.StyleStockType.ADAPTIVE);

        JRadioButton lineButton = ThemeUtil.createStyledRadioButton("Line");
        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPanel.updateStyleStockType(ConfigPanel.StyleStockType.LINE);
            }
        });
        lineButton.setSelected(ConfigPanel.styleStockType == ConfigPanel.StyleStockType.LINE);

        ButtonGroup zoomOptionsGroup = new ButtonGroup();
        zoomOptionsGroup.add(candlesButton);
        zoomOptionsGroup.add(adaptiveButton);
        zoomOptionsGroup.add(lineButton);

        JPanel zoomOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        zoomOptionsPanel.setBackground(new Color(30, 30, 30));
        zoomOptionsPanel.add(candlesButton);
        zoomOptionsPanel.add(adaptiveButton);
        zoomOptionsPanel.add(lineButton);

        add(zoomLabel);
        add(zoomOptionsPanel);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

    }
}


