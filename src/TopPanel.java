import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TopPanel extends JPanel {
    public static JLabel freeMarginLabel;
    public JButton buyButton;
    public JButton sellButton;
    public JPanel periodButtonPanel;

    private JButton activePeriodButton;

    public TopPanel() {
        add(ThemeUtil.createStyledLabel("Free Margin:"));
        freeMarginLabel = new JLabel("0.0");
        freeMarginLabel.setForeground(Color.WHITE);
        freeMarginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(freeMarginLabel);

        buyButton = ThemeUtil.createStyledButton("Buy");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Main.currentSymbol != null) {
                    Main.client.buyWithDialog(Main.currentSymbol);
                } else {
                    JOptionPane.showMessageDialog(Main.frame, "No symbol selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        add(buyButton);

        sellButton = ThemeUtil.createStyledButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.showSellTradesFrame();
            }
        });
        add(sellButton);

        periodButtonPanel = new JPanel();
        periodButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        periodButtonPanel.setOpaque(false);

        String[] periodOptions = {"1M", "1w", "1d", "1h", "5m", "1m"};
        for (String period : periodOptions) {
            JButton periodButton = ThemeUtil.createStyledButton(period);
            periodButton.addActionListener(new PeriodButtonActionListener(period, periodButton));
            periodButtonPanel.add(periodButton);
            if(period.equals("1d")){
                activePeriodButton = periodButton;
                highlightButton(periodButton);
            }
        }

        add(periodButtonPanel);
    }

    private void highlightButton(JButton button) {
        button.setBackground(new Color(100, 100, 100));
    }

    private void resetButtonStyle(JButton button) {
        button.setBackground(new Color(70, 70, 70));
    }

    private class PeriodButtonActionListener implements ActionListener {
        private final String period;
        private final JButton button;

        public PeriodButtonActionListener(String period, JButton button) {
            this.period = period;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (activePeriodButton != null) {
                resetButtonStyle(activePeriodButton);
            }

            activePeriodButton = button;
            highlightButton(button);

            Main.periodCodeChanged(getPeriodCode(period));
        }
    }

    private int getPeriodCode(String period) {
        return switch (period) {
            case "1m" -> 1;
            case "5m" -> 5;
            case "1h" -> 60;
            case "1d" -> 1440;
            case "1w" -> 10080;
            case "1M" -> 43200;
            default -> 1;
        };
    }
}
