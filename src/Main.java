import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static MyChart myChart;
    public static Client client;
    public static JFrame frame;
    public static JPanel chartPanel;
    public static Sidebar sidebar;
    public static Symbol currentSymbol;
    public static int periodCode = 1440;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        //System.setProperty("sun.java2d.uiScale", "1");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeFrame();
        initializeLogin();

    }


    private void initializeFrame() {

        frame = new JFrame("Stock Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1920, 880);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    private void initializeLogin(){
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground( new Color(30, 30, 30));
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel loginLabel = new JLabel("Login:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField loginField = ThemeUtil.createStyledTextField();
        loginField.setColumns(15);

        JPasswordField passwordField = ThemeUtil.createStyledPasswordField();
        passwordField.setColumns(15);

        JCheckBox saveLoginCheckBox = ThemeUtil.createStyledCheckBox("Save Login");
        saveLoginCheckBox.setOpaque(false);

        JButton loginButton = ThemeUtil.createStyledButton("Login");

        JLabel statusLabel = ThemeUtil.createStyledLabel(" ");


        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        loginPanel.add(loginLabel, gbc);
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        loginPanel.add(loginField, gbc);
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(saveLoginCheckBox, gbc);

        gbc.gridy = 3;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        loginPanel.add(statusLabel, gbc);

        String savedLogin = loadCredentialsFromFile();
        loginField.setText(savedLogin);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (login.isEmpty() || password.isEmpty()) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Please fill in all fields!");
                    return;
                }
                loginField.setEnabled(false);
                passwordField.setEnabled(false);
                loginButton.setEnabled(false);

                statusLabel.setForeground(Color.WHITE);
                statusLabel.setText("Logging in...");
                frame.repaint();
                client = new Client();
                boolean status = client.login(login, password);
                if (status) {
                    statusLabel.setText("Login successful!");
                    if(saveLoginCheckBox.isSelected()) {
                        saveCredentialsToFile(login);
                    }
                    frame.remove(loginPanel);
                    initializeProgram();
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Login failed.");
                    client.exitAll();
                    loginField.setEnabled(true);
                    passwordField.setEnabled(true);
                    loginButton.setEnabled(true);
                }
            }
        });
        frame.add(loginPanel);
        frame.repaint();
    }
    private void saveCredentialsToFile(String login) {
        File credsFile = new File("credentials.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(credsFile, false)))) {
            pw.println(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadCredentialsFromFile() {
        File credsFile = new File("credentials.txt");
        if (!credsFile.exists()) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(credsFile))) {
            String login = br.readLine();
            if (login == null) return null;
            return login.trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initializeProgram(){
        frame.setLayout(new BorderLayout(0, 0));
        populateSidebar();
        chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setOpaque(true);
        JPanel panel = new TopPanel();
        chartPanel.setDoubleBuffered(true);
        chartPanel.add(panel, BorderLayout.NORTH);

        frame.add(chartPanel, BorderLayout.CENTER);

        client.getMarginLevel();
        client.subscribeBalance();

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                SettingsUtil.saveSettings();
            }
        });
    }

    private void populateSidebar() {
        ThemeUtil.applyTheme(frame);
        SwingUtilities.updateComponentTreeUI(frame);
        List<Symbol> symbols = client.getAllSymbols();
        List<String> pinnedSymbols = importPinnedSymbolsFromFile();
        sidebar = new Sidebar(this, symbols, pinnedSymbols);
        frame.add(sidebar, BorderLayout.WEST);

        frame.revalidate();
        frame.repaint();
    }

    public static List<String> importPinnedSymbolsFromFile() {
        List<String> pinnedSymbols = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("pinnedSymbols.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String symbol = line.trim();
                pinnedSymbols.add(symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pinnedSymbols;
    }


    protected void symbolPanelMouseClicked(Symbol symbol) {
        currentSymbol = symbol;
        List<RateInfo> rateInfos = client.getChartLastRequest(symbol.symbol, periodCode);
        frame.setTitle("Stock Chart - " + symbol.description);
        chartPanelChange(rateInfos);
    }

    protected static void chartPanelChange(List<RateInfo> rateInfos) {
        if (myChart != null) {
            chartPanel.remove(myChart);
        }
        myChart = new MyChart(rateInfos);
        chartPanel.add(myChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    public static void periodCodeChanged(int periodCode) {
        if(Main.periodCode == periodCode) return;
        Main.periodCode = periodCode;

        if(currentSymbol != null) chartPanelChange(Main.client.getChartLastRequest(Main.currentSymbol.symbol, periodCode));
    }

    public static void savePinnedSymbolsToFile(List<String> pinnedSymbols) {
        System.out.println("Saving to file: " + pinnedSymbols);
        System.out.println("File path: " + new File("pinnedSymbols.txt").getAbsolutePath());

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("pinnedSymbols.txt", false)))) {
            for (String symbol : pinnedSymbols) {
                pw.println(symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showSellTradesFrame() {
        List<TradeRecord> activeTrades = client.getActiveTrades();
        if (activeTrades.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active trades available to sell.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFrame sellFrame = new JFrame("Sell Active Trades");
        sellFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sellFrame.setSize(600, 500);
        sellFrame.setLocationRelativeTo(frame);

        SellListPanel sellListPanel = new SellListPanel(activeTrades);
        sellFrame.add(sellListPanel);

        sellFrame.setVisible(true);
    }


}