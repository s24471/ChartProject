import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class StockList extends ListPanel {
    private List<String> pinnedSymbols;
    private Map<String, List<Symbol>> symbolsByCategory;
    private List<Symbol> symbols;
    private Main main;
    private JTextField searchField;
    private String searchText;

    public StockList(Main main, List<Symbol> symbols, List<String> pinnedSymbols) {
        this.pinnedSymbols = pinnedSymbols != null ? pinnedSymbols : new ArrayList<>();
        this.symbols = symbols;
        this.main = main;

        this.searchText = "";

        this.symbolsByCategory = new HashMap<>();

        searchField = ThemeUtil.createStyledTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(new EmptyBorder(5, 5, 5, 5));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterSymbols();
            }

            public void removeUpdate(DocumentEvent e) {
                filterSymbols();
            }

            public void changedUpdate(DocumentEvent e) {
                filterSymbols();
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(searchField);
        mainPanel.add(contentPanel);

        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollPane.setViewportView(mainPanel);

        initializeContent();
    }

    private void filterSymbols() {
        searchText = searchField.getText().trim().toLowerCase();
        updateSymbolPanels();
    }

    @Override
    protected void initializeContent() {
        refreshSymbolPanel();
    }

    private void refreshSymbolPanel() {
        contentPanel.removeAll();
        categoryPanels.clear();
        contentPanels.clear();

        categorizeSymbols(searchText);

        for (String category : symbolsByCategory.keySet()) {
            List<Symbol> symbols = symbolsByCategory.get(category);

            if (symbols.isEmpty()) {
                continue;
            }

            JPanel symbolPanel = new JPanel();
            symbolPanel.setLayout(new BoxLayout(symbolPanel, BoxLayout.Y_AXIS));

            for (Symbol symbol : symbols) {
                SymbolPanel symbolPanelEntry = createSymbolPanel(symbol);
                symbolPanel.add(symbolPanelEntry);
            }

            addCategory(category, symbolPanel);
        }

        refreshContentPanel();
    }

    private SymbolPanel createSymbolPanel(Symbol symbol) {
        boolean isPinned = pinnedSymbols.contains(symbol.symbol);

        ActionListener pinListener = e -> {
            if (pinnedSymbols.contains(symbol.symbol)) {
                pinnedSymbols.remove(symbol.symbol);
            } else {
                pinnedSymbols.add(symbol.symbol);
            }
            Main.savePinnedSymbolsToFile(pinnedSymbols);
            updateSymbolPanels();

        };

        SymbolPanel panel = new SymbolPanel(symbol, isPinned, pinListener);
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    showSymbolInfoPopup(symbol);
                } else {
                    main.symbolPanelMouseClicked(symbol);
                }
            }
        });

        return panel;
    }

    private void showSymbolInfoPopup(Symbol symbol) {
        JDialog infoDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Stock Info", true);
        infoDialog.setSize(400, 300);
        infoDialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 1));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


        infoPanel.add(ThemeUtil.createStyledLabel("Symbol: " + symbol.symbol));
        infoPanel.add(ThemeUtil.createStyledLabel("Description: " + symbol.description));
        infoPanel.add(ThemeUtil.createStyledLabel("Category: " + symbol.category));

        infoDialog.add(infoPanel, BorderLayout.CENTER);

        JButton pinButton = ThemeUtil.createStyledButton(pinnedSymbols.contains(symbol.symbol) ? "Unpin" : "Pin");
        pinButton.addActionListener(e -> {
            if (pinnedSymbols.contains(symbol.symbol)) {
                pinnedSymbols.remove(symbol.symbol);
                pinButton.setText("Pin");
            } else {
                pinnedSymbols.add(symbol.symbol);
                pinButton.setText("Unpin");
            }
            Main.savePinnedSymbolsToFile(pinnedSymbols);
            updateSymbolPanels();
        });

        infoDialog.add(pinButton, BorderLayout.SOUTH);

        infoDialog.setLocationRelativeTo(this);
        infoDialog.setVisible(true);
    }

    private void categorizeSymbols(String searchText) {
        symbolsByCategory.clear();
        if (!pinnedSymbols.isEmpty()) {
            symbolsByCategory.put("Pinned", new ArrayList<>());
        }

        String lowerSearchText = searchText.toLowerCase();

        for (Symbol symbol : symbols) {
            if (!lowerSearchText.isEmpty()) {
                if (!(symbol.symbol.toLowerCase().contains(lowerSearchText) ||
                        symbol.description.toLowerCase().contains(lowerSearchText))) {
                    continue;
                }
            }

            if (pinnedSymbols.contains(symbol.symbol)) {
                symbolsByCategory.get("Pinned").add(symbol);
            } else {
                symbolsByCategory
                        .computeIfAbsent(symbol.category, k -> new ArrayList<>())
                        .add(symbol);
            }
        }
    }

    private void updateSymbolPanels() {
        refreshSymbolPanel();
    }
}
