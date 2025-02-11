import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ThresholdSliderPanel extends JPanel {
    public ThresholdSliderPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));

        JLabel sliderLabel = new JLabel("Candle to Line Threshold:");
        sliderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sliderLabel.setForeground(new Color(200, 200, 200));
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider thresholdSlider = ThemeUtil.createStyledSlider(1, 20, ConfigPanel.candleToLineThreshold);
        thresholdSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ConfigPanel.candleToLineThreshold = thresholdSlider.getValue();
                Main.frame.repaint();
            }
        });

        add(sliderLabel);
        add(thresholdSlider);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }


}
