import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final String HOST = "xapi.xtb.com";
    private static final String BALANCE_TAG = "balance";
    private static final int PORT = 5124;
    private static final int STREAM_PORT = 5125;
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<String, JSONObject> responses;
    private String streamSessionID;
    private Sender sender;
    private Sender streamSender;
    private Reader reader;
    private Reader streamReader;

    public Client() {
        responses = new ConcurrentHashMap<>();

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);
            sender = new Sender(new PrintStream(socket.getOutputStream()));
            reader = new Reader(new BufferedReader(new InputStreamReader(socket.getInputStream())));

            SSLSocket streamSocket = (SSLSocket) factory.createSocket(HOST, STREAM_PORT);
            streamSender = new Sender(new PrintStream(streamSocket.getOutputStream()));
            streamReader = new Reader(new BufferedReader(new InputStreamReader(streamSocket.getInputStream())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exitAll(){
        reader.exit();
        sender.exit();
        streamReader.exit();
        streamSender.exit();
    }
    public boolean login(String userId, String password) {
        JSONObject arguments = new JSONObject();
        arguments.put("userId", userId);
        arguments.put("password", password);

        JSONObject command = new JSONObject();
        command.put("command", "login");
        command.put("arguments", arguments);
        command.put("customTag", "login");

        JSONObject res = getRes("login", command.toJSONString());

        if (res != null && (boolean) res.get("status")) {
            System.out.println("Logged in");
            streamSessionID = (String) res.get("streamSessionId");
            return true;
        } else {
            System.out.println("Problem during login:");
            System.out.println(res.toJSONString());
            return false;
        }
    }


    public void getMarginLevel() {
        JSONObject command = new JSONObject();
        command.put("command", "getMarginLevel");
        command.put("customTag", "getMarginLevel");

        JSONObject res = getRes("getMarginLevel", command.toJSONString());
        JSONObject returnData = (JSONObject) res.get("returnData");
        updateBalance(returnData.get("margin_free").toString());
    }

    public List<Symbol> getAllSymbols() {
        JSONObject command = new JSONObject();
        command.put("command", "getAllSymbols");
        command.put("customTag", "getAllSymbols");

        JSONObject res = getRes("getAllSymbols", command.toJSONString());
        JSONArray returnData = (JSONArray) res.get("returnData");

        List<Symbol> symbols = new ArrayList<>();
        for (Object o : returnData) {
            JSONObject js = (JSONObject) o;
            String symbolName = js.get("symbol").toString();
            symbols.add(new Symbol(
                    symbolName,
                    symbolName.split("\\.")[0],
                    js.get("description").toString(),
                    js.get("categoryName").toString()
            ));
        }
        System.out.println(symbols.size());
        symbols.sort(Comparator.comparing(s -> s.trimmedSymbol));
        return symbols;
    }

    public void subscribeBalance() {
        JSONObject command = new JSONObject();
        command.put("command", "getBalance");
        command.put("streamSessionId", streamSessionID);
        command.put("customTag", BALANCE_TAG);
        streamSender.add(command.toJSONString());
    }

    private void updateBalance(String balance) {
        TopPanel.freeMarginLabel.setText(balance);
        TopPanel.freeMarginLabel.repaint();
    }

    public Double getPrice(Symbol symbol) {
        JSONObject args = new JSONObject();
        args.put("symbol", symbol.symbol);

        JSONObject command = new JSONObject();
        command.put("command", "getSymbol");
        command.put("customTag", "getSymbol");
        command.put("arguments", args);

        JSONObject res = getRes("getSymbol", command.toJSONString());
        JSONObject returnData = (JSONObject) res.get("returnData");
        return (Double) returnData.get("ask");
    }

    public JSONObject getSymbolDetails(Symbol symbol) {
        JSONObject command = new JSONObject();
        command.put("command", "getSymbol");
        command.put("customTag", "getSymbol");
        JSONObject args = new JSONObject();
        args.put("symbol", symbol.symbol);
        command.put("arguments", args);

        JSONObject res = getRes("getSymbol", command.toJSONString());

        if (res != null && (boolean) res.get("status")) {
            return (JSONObject) res.get("returnData");
        } else {
            System.err.println("Failed to fetch symbol details: " + res.toJSONString());
            return null;
        }
    }

    public void buyWithDialog(Symbol symbol) {
        JSONObject symbolDetails = getSymbolDetails(symbol);
        if (symbolDetails == null) {
            JOptionPane.showMessageDialog(null, "Failed to fetch symbol details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double lotMin = (double) symbolDetails.get("lotMin");
        double lotMax = (double) symbolDetails.get("lotMax");
        double lotStep = (double) symbolDetails.get("lotStep");
        double currentPrice = getPrice(symbol);

        JDialog dialog = new JDialog(Main.frame, "Buy " + symbol.symbol, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(Main.frame);
        dialog.setLayout(new GridLayout(4, 1));

        JPanel volumePanel = new JPanel(new BorderLayout());
        JLabel volumeLabel = ThemeUtil.createStyledLabel("Select Volume (Lots):");
        SpinnerNumberModel model = new SpinnerNumberModel(lotMin, lotMin, lotMax, lotStep);
        JSpinner volumeSpinner = ThemeUtil.createStyledSpinner(model);
        volumePanel.add(volumeLabel, BorderLayout.WEST);
        volumePanel.add(volumeSpinner, BorderLayout.CENTER);


        JPanel costPanel = new JPanel(new BorderLayout());
        JLabel costLabel =ThemeUtil.createStyledLabel("Estimated Cost: ");
        JLabel costValueLabel = ThemeUtil.createStyledLabel(calculateCost(currentPrice, lotMin) + " PLN");
        costPanel.add(costLabel, BorderLayout.WEST);
        costPanel.add(costValueLabel, BorderLayout.CENTER);

        volumeSpinner.addChangeListener(e -> {
            double volume = (double) volumeSpinner.getValue();
            costValueLabel.setText(calculateCost(currentPrice, volume) + " PLN");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(volumePanel);
        dialog.add(costPanel);
        JLabel jLabel = ThemeUtil.createStyledLabel("Current Price: " + currentPrice + " PLN");
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(jLabel);
        dialog.add(buttonPanel);

        confirmButton.addActionListener(e -> {
            double volume = (double) volumeSpinner.getValue();
            boolean success = executeBuyTransaction(symbol, volume, currentPrice);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Purchase failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private String calculateCost(double price, double volume) {
        return String.format("%.2f", price * volume);
    }

    private boolean executeBuyTransaction(Symbol symbol, double volume, double price) {
        JSONObject tradeTransaction = new JSONObject();
        tradeTransaction.put("cmd", 0);
        tradeTransaction.put("customComment", "BUY_" + symbol.symbol);
        tradeTransaction.put("expiration", System.currentTimeMillis() + 5000);
        tradeTransaction.put("offset", 0);
        tradeTransaction.put("order", 0);
        tradeTransaction.put("sl", 0.0);
        tradeTransaction.put("symbol", symbol.symbol);
        tradeTransaction.put("tp", 0.0);
        tradeTransaction.put("type", 0);
        tradeTransaction.put("volume", volume);
        tradeTransaction.put("price", price);

        JSONObject args = new JSONObject();
        args.put("tradeTransInfo", tradeTransaction);

        JSONObject command = new JSONObject();
        command.put("command", "tradeTransaction");
        command.put("arguments", args);
        command.put("customTag", "BUY_" + symbol.symbol);

        JSONObject res = getRes("BUY_" + symbol.symbol, command.toJSONString());
        if (res != null && (boolean) res.get("status")) {
            return true;
        } else {
            System.err.println("Error: " + (res != null ? res.get("errorDescr") : "No response"));
            return false;
        }
    }


    public void addResponse(String name, JSONObject response) {
        if ("balance".equals(response.get("command"))) {
            updateBalance(((JSONObject) response.get("data")).get("marginFree").toString());
        } else {
            responses.put(name, response);
        }
    }

    private JSONObject getRes(String name, String message) {
        sender.add(message);
        JSONObject res = null;
        while (res == null) {
            Util.sleepMili(10);
            res = responses.remove(name);
        }
        return res;
    }

    public List<TradeRecord> getActiveTrades() {
        JSONObject command = new JSONObject();
        command.put("command", "getTrades");
        command.put("customTag", "getActiveTrades");

        JSONObject args = new JSONObject();
        args.put("openedOnly", true);
        command.put("arguments", args);

        JSONObject res = getRes("getActiveTrades", command.toJSONString());
        JSONArray returnData = (JSONArray) res.get("returnData");

        List<TradeRecord> trades = new ArrayList<>();
        for (Object o : returnData) {
            JSONObject js = (JSONObject) o;
            trades.add(new TradeRecord(
                    js.get("symbol").toString(),
                    (double) js.get("volume"),
                    (double) js.get("open_price"),
                    (long) js.get("order")
            ));
        }
        return trades;
    }

    public boolean sellTrade(TradeRecord trade) {
        JSONObject tradeTransaction = new JSONObject();
        tradeTransaction.put("cmd", 1);
        tradeTransaction.put("order", trade.getOrder());
        tradeTransaction.put("symbol", trade.getSymbol());
        tradeTransaction.put("type", 2);
        tradeTransaction.put("volume", trade.getVolume());
        tradeTransaction.put("price", trade.getOpenPrice());

        JSONObject args = new JSONObject();
        args.put("tradeTransInfo", tradeTransaction);

        JSONObject command = new JSONObject();
        command.put("command", "tradeTransaction");
        command.put("arguments", args);
        command.put("customTag", "SELL_" + trade.getSymbol());

        JSONObject res = getRes("SELL_" + trade.getSymbol(), command.toJSONString());
        System.out.println("Sell Response: " + res.toJSONString());
        return (Boolean) res.get("status");
    }

    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
    public JSONObject getChartRes(String name, String message) {
        JSONParser parser = new JSONParser();
        int retryCount = 0;
        int maxRetries = 5;
        String originalName = name;

        while (retryCount < maxRetries) {
            sender.add(message);
            JSONObject res = null;
            int timeWaiting = 0;

            while (res == null && timeWaiting < 5000) {
                Util.sleepMili(10);
                timeWaiting += 10;
                res = responses.remove(name);
            }

            if (res == null) {
                retryCount++;
                continue;
            }

            if (!(Boolean) res.get("status")) {
                String errorCode = (String) res.get("errorCode");
                if ("EX009".equals(errorCode)) {
                    try {
                        JSONObject messageObj = (JSONObject) parser.parse(message);
                        JSONObject arguments = (JSONObject) messageObj.get("arguments");
                        JSONObject info = (JSONObject) arguments.get("info");
                        int period = ((Number) info.get("period")).intValue();
                        long intervalMillis = getIntervalMillis(period);
                        long maxCandles = 50000;
                        long maxTimeSpan = maxCandles * intervalMillis;
                        long currentTimeMillis = System.currentTimeMillis();
                        long newStart = currentTimeMillis - maxTimeSpan;
                        newStart = (newStart / intervalMillis) * intervalMillis;
                        info.put("start", newStart);
                        String customTag = originalName + "_retry" + retryCount;
                        messageObj.put("customTag", customTag);
                        name = customTag;
                        message = messageObj.toJSONString();;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        break;
                    }
                    retryCount++;

                } else {
                    System.out.println("Unhandled error code: " + errorCode);
                    break;
                }
            } else {
                System.out.println("Successful response received for: " + name);
                return res;
            }
        }
        System.out.println("Failed to get valid response after " + retryCount + " retries.");
        return null;
    }

    private long getIntervalMillis(int period) {
        return period * 60L * 1000L;
    }

    public List<RateInfo> getChartLastRequest(String symbol, int period) {
        JSONObject info = new JSONObject();
        info.put("period", period);

        long currentTimeMillis = System.currentTimeMillis();
        long intervalMillis = getIntervalMillis(period);
        long maxCandles = 49900;
        long maxTimeSpan = maxCandles * intervalMillis;
        long startTime = currentTimeMillis - maxTimeSpan;
        startTime = (startTime / intervalMillis) * intervalMillis;

        info.put("start", startTime);
        info.put("symbol", symbol);

        JSONObject args = new JSONObject();
        args.put("info", info);

        JSONObject command = new JSONObject();
        command.put("command", "getChartLastRequest");
        command.put("arguments", args);
        String customTag = "getChartLastRequest_" + symbol + "_" + period + "_" + ID_GENERATOR.getAndIncrement();
        command.put("customTag", customTag);

        JSONObject res = getChartRes(customTag, command.toJSONString());

        if (res == null || !(Boolean) res.get("status")) {
            System.err.println("Error: " + (res != null ? res.get("errorDescr") : "No response"));
            return Collections.emptyList();
        }

        JSONArray arr = (JSONArray) ((JSONObject) res.get("returnData")).get("rateInfos");

        List<RateInfo> rateInfos = new ArrayList<>();
        for (Object o : arr) {
            JSONObject rateInfo = (JSONObject) o;
            double high = (double) rateInfo.get("high");
            double vol = (double) rateInfo.get("vol");
            double low = (double) rateInfo.get("low");
            long ctm = (long) rateInfo.get("ctm");
            String ctmString = (String) rateInfo.get("ctmString");
            double close = (double) rateInfo.get("close");
            double open = (double) rateInfo.get("open");

            rateInfos.add(new RateInfo(high, vol, low, ctm, ctmString, close, open));
        }
        return rateInfos;
    }
}
