import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ColorChartPanel extends JPanel {


    JCheckBox colorLineCheckbox;
    JCheckBox colorCandlesCheckbox;
    JPanel colorOptionsPanel;

    public ColorChartPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));

        JLabel colorChartLabel = new JLabel("Color Chart:");
        colorChartLabel.setFont(new Font("Arial", Font.BOLD, 14));
        colorChartLabel.setForeground(new Color(200, 200, 200));
        colorChartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        colorCandlesCheckbox = ThemeUtil.createStyledCheckBox("Color Candles");
        colorCandlesCheckbox.setSelected(ConfigPanel.coloredCandles);
        colorCandlesCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigPanel.coloredCandles = colorCandlesCheckbox.isSelected();
                Main.frame.repaint();
            }
        });

        colorLineCheckbox = ThemeUtil.createStyledCheckBox("Color Line Chart");
        colorLineCheckbox.setSelected(ConfigPanel.coloredLines);
        colorLineCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigPanel.coloredLines = colorLineCheckbox.isSelected();
                Main.frame.repaint();
            }
        });

        colorOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        colorOptionsPanel.setBackground(new Color(30, 30, 30));
        colorOptionsPanel.add(colorCandlesCheckbox);
        colorOptionsPanel.add(colorLineCheckbox);

        add(colorChartLabel);
        add(colorOptionsPanel);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }


    public void updatedStyle(ConfigPanel.StyleStockType styleStockType) {

        colorOptionsPanel.remove(colorCandlesCheckbox);
        colorOptionsPanel.remove(colorLineCheckbox);

        if (styleStockType == ConfigPanel.StyleStockType.LINE || styleStockType == ConfigPanel.StyleStockType.ADAPTIVE) {
            colorOptionsPanel.add(colorLineCheckbox);
        }
        if (styleStockType == ConfigPanel.StyleStockType.CANDLE || styleStockType == ConfigPanel.StyleStockType.ADAPTIVE) {
            colorOptionsPanel.add(colorCandlesCheckbox);
        }
        colorOptionsPanel.revalidate();
    }

}
