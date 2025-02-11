import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SellListPanel extends ListPanel {
    private final List<TradeRecord> activeTrades;

    public SellListPanel(List<TradeRecord> activeTrades) {
        this.activeTrades = activeTrades;
        initializeContent();
        refreshContentPanel();
    }

    @Override
    protected void initializeContent() {
        categoryPanels.clear();
        contentPanels.clear();

        Map<String, JPanel> groupedTrades = new LinkedHashMap<>();
        for (TradeRecord trade : activeTrades) {
            String category = trade.getSymbol();
            if (!groupedTrades.containsKey(category)) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                groupedTrades.put(category, panel);
            }

            JPanel tradePanel = createTradePanel(trade);
            groupedTrades.get(category).add(tradePanel);
        }

        for (String category : groupedTrades.keySet()) {
            addCategory(category, groupedTrades.get(category));
        }
    }

    private JPanel createTradePanel(TradeRecord trade) {
        JPanel tradePanel = new JPanel(new BorderLayout());
        tradePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tradePanel.setBackground(new Color(50, 50, 50));
        tradePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel tradeInfo = new JLabel("Volume: " + trade.getVolume() + ", Price: " + trade.getOpenPrice());
        tradeInfo.setForeground(Color.WHITE);

        JButton sellButton = new JButton("Sell");
        sellButton.setBackground(new Color(70, 70, 70));
        sellButton.setForeground(Color.WHITE);
        sellButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to sell this trade?",
                    "Confirm Sell",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (!Main.client.sellTrade(trade)) {
                    JOptionPane.showMessageDialog(this, "Error while selling.", "Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    activeTrades.remove(trade);
                    refreshList();
                    JOptionPane.showMessageDialog(this, "Trade sold successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        tradePanel.add(tradeInfo, BorderLayout.CENTER);
        tradePanel.add(sellButton, BorderLayout.EAST);
        return tradePanel;
    }

    public void refreshList() {
        initializeContent();
        refreshContentPanel();
    }
}
