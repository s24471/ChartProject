import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Sidebar extends JPanel {
    JButton stocks;
    JButton config;
    JButton algo;
    StockList stockList;
    ConfigPanel configPanel;
    AlgoPanel algoPanel;

    public Sidebar(Main main, List<Symbol> symbols, List<String> pinnedSymbols) {
        JPanel panel = new JPanel();

        stocks = ThemeUtil.createStyledButton("Stocks");
        stocks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchPanel(stockList);
            }
        });

        algo = ThemeUtil.createStyledButton("Algo");
        algo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchPanel(algoPanel);
            }
        });

        config = ThemeUtil.createStyledButton("Config");
        config.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchPanel(configPanel);
            }
        });

        panel.setLayout(new BorderLayout());
        panel.add(stocks, BorderLayout.WEST);
        panel.add(config, BorderLayout.CENTER);
        panel.add(algo, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        stockList = new StockList(main, symbols, pinnedSymbols);
        algoPanel = new AlgoPanel();
        configPanel = new ConfigPanel();
        add(stockList, BorderLayout.CENTER);

        setPreferredSize(new Dimension(Main.frame.getWidth() / 5, getHeight()));
    }

    public void switchPanel(JPanel newPanel) {
        removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(stocks, BorderLayout.WEST);
        panel.add(config, BorderLayout.CENTER);
        panel.add(algo, BorderLayout.EAST);
        add(panel, BorderLayout.NORTH);

        add(newPanel, BorderLayout.CENTER);

        Main.frame.revalidate();
        Main.frame.repaint();
    }
}
