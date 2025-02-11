import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AlligatorAlgo implements Algorithm {
    private int jawPeriod = 13;
    private int jawShift = 8;
    private int teethPeriod = 8;
    private int teethShift = 5;
    private int lipsPeriod = 5;
    private int lipsShift = 3;

    private Color jawColor = Color.BLUE;
    private Color teethColor = Color.RED;
    private Color lipsColor = Color.GREEN;
    private float jawLineThickness = 2.0f;
    private float teethLineThickness = 2.0f;
    private float lipsLineThickness = 2.0f;
    private boolean showJaw = true;
    private boolean showTeeth = true;
    private boolean showLips = true;

    private boolean enabled = true;

    public AlligatorAlgo() {
        loadFromJson();
    }

    @Override
    public void draw(Graphics2D g, List<RateInfo> rates, int startIndex, int range, int chartWidth, int chartHeight) {
        if (!enabled) return;

        AlligatorLines lines = calculate(rates);


        if (showJaw) {
            Algorithm.drawLine(g, lines.jaw, jawColor, jawLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
        if (showTeeth) {
            Algorithm.drawLine(g, lines.teeth, teethColor, teethLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
        if (showLips) {
            Algorithm.drawLine(g, lines.lips, lipsColor, lipsLineThickness, rates, startIndex, range, chartWidth, chartHeight);
        }
    }
    @Override
    public JPanel getConfigurationPanel() {
        return new AlligatorPanel(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "Alligator";
    }

    public AlligatorLines calculate(List<RateInfo> rates) {
        List<Double> closingPrices = new ArrayList<>();
        for (RateInfo rate : rates) {
            closingPrices.add(rate.close);
        }
        List<Double> jawSMMA = calculateSMMA(closingPrices, jawPeriod);
        List<Double> teethSMMA = calculateSMMA(closingPrices, teethPeriod);
        List<Double> lipsSMMA = calculateSMMA(closingPrices, lipsPeriod);

        List<Double> jaw = calculateAlligatorLine(jawSMMA, jawShift);
        List<Double> teeth = calculateAlligatorLine(teethSMMA, teethShift);
        List<Double> lips = calculateAlligatorLine(lipsSMMA, lipsShift);

        return new AlligatorLines(jaw, teeth, lips);
    }

    private List<Double> calculateSMMA(List<Double> prices, int period) {
        List<Double> smma = new ArrayList<>(Collections.nCopies(prices.size(), 0.0));
        if (prices.size() < period) return smma;

        double smmaValue = prices.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0);
        smma.set(period - 1, smmaValue);

        for (int i = period; i < prices.size(); i++) {
            smmaValue = (smma.get(i - 1) * (period - 1) + prices.get(i)) / period;
            smma.set(i, smmaValue);
        }
        return smma;
    }

    private List<Double> calculateAlligatorLine(List<Double> smma, int shift) {
        List<Double> shiftedLine = new ArrayList<>(Collections.nCopies(smma.size(), null));
        for (int i = 0; i < smma.size() - shift; i++) {
            shiftedLine.set(i + shift, smma.get(i));
        }
        return shiftedLine;
    }


    public int getJawPeriod() {
        return jawPeriod;
    }

    public void setJawPeriod(int jawPeriod) {
        this.jawPeriod = jawPeriod;
    }

    public int getJawShift() {
        return jawShift;
    }

    public void setJawShift(int jawShift) {
        this.jawShift = jawShift;
    }

    public int getTeethPeriod() {
        return teethPeriod;
    }

    public void setTeethPeriod(int teethPeriod) {
        this.teethPeriod = teethPeriod;
    }

    public int getTeethShift() {
        return teethShift;
    }

    public void setTeethShift(int teethShift) {
        this.teethShift = teethShift;
    }

    public int getLipsPeriod() {
        return lipsPeriod;
    }

    public void setLipsPeriod(int lipsPeriod) {
        this.lipsPeriod = lipsPeriod;
    }

    public int getLipsShift() {
        return lipsShift;
    }

    public void setLipsShift(int lipsShift) {
        this.lipsShift = lipsShift;
    }

    public Color getJawColor() {
        return jawColor;
    }

    public void setJawColor(Color jawColor) {
        this.jawColor = jawColor;
    }

    public Color getTeethColor() {
        return teethColor;
    }

    public void setTeethColor(Color teethColor) {
        this.teethColor = teethColor;
    }

    public Color getLipsColor() {
        return lipsColor;
    }

    public void setLipsColor(Color lipsColor) {
        this.lipsColor = lipsColor;
    }

    public float getJawLineThickness() {
        return jawLineThickness;
    }

    public void setJawLineThickness(float jawLineThickness) {
        this.jawLineThickness = jawLineThickness;
    }

    public float getTeethLineThickness() {
        return teethLineThickness;
    }

    public void setTeethLineThickness(float teethLineThickness) {
        this.teethLineThickness = teethLineThickness;
    }

    public float getLipsLineThickness() {
        return lipsLineThickness;
    }

    public void setLipsLineThickness(float lipsLineThickness) {
        this.lipsLineThickness = lipsLineThickness;
    }

    public boolean isShowJaw() {
        return showJaw;
    }

    public void setShowJaw(boolean showJaw) {
        this.showJaw = showJaw;
    }

    public boolean isShowTeeth() {
        return showTeeth;
    }

    public void setShowTeeth(boolean showTeeth) {
        this.showTeeth = showTeeth;
    }

    public boolean isShowLips() {
        return showLips;
    }

    public void setShowLips(boolean showLips) {
        this.showLips = showLips;
    }

    public void restoreDefault(){
        jawPeriod = 13;
        jawShift = 8;
        teethPeriod = 8;
        teethShift = 5;
        lipsPeriod = 5;
        lipsShift = 3;
        jawColor = Color.BLUE;
        teethColor = Color.RED;
        lipsColor = Color.GREEN;
        jawLineThickness = 2.0f;
        teethLineThickness = 2.0f;
        lipsLineThickness = 2.0f;
        showJaw = true;
        showTeeth = true;
        showLips = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JSONObject saveToJson() {
        JSONObject json = new JSONObject();
        json.put("enabled", enabled);
        json.put("jawColor", SettingsUtil.colorToHex(jawColor));
        json.put("teethColor", SettingsUtil.colorToHex(teethColor));
        json.put("lipsColor", SettingsUtil.colorToHex(lipsColor));
        json.put("jawLineThickness", jawLineThickness);
        json.put("teethLineThickness", teethLineThickness);
        json.put("lipsLineThickness", lipsLineThickness);
        json.put("jawPeriod", jawPeriod);
        json.put("teethPeriod", teethPeriod);
        json.put("lipsPeriod", lipsPeriod);
        json.put("jawShift", jawShift);
        json.put("teethShift", teethShift);
        json.put("lipsShift", lipsShift);
        json.put("showJaw", showJaw);
        json.put("showTeeth", showTeeth);
        json.put("showLips", showLips);
        json.put("enables", enabled);
        return json;
    }

    public void loadFromJson() {
        JSONObject json = SettingsUtil.loadSettings(getName());
        if(json == null) return;
        enabled = (boolean) json.get("enabled");
        jawColor = SettingsUtil.hexToColor((String) json.get("jawColor"));
        teethColor = SettingsUtil.hexToColor((String) json.get("teethColor"));
        lipsColor = SettingsUtil.hexToColor((String) json.get("lipsColor"));
        jawLineThickness = ((Double) json.get("jawLineThickness")).floatValue();
        teethLineThickness = ((Double) json.get("teethLineThickness")).floatValue();
        lipsLineThickness = ((Double) json.get("lipsLineThickness")).floatValue();
        jawPeriod = ((Long) json.get("jawPeriod")).intValue();
        teethPeriod = ((Long) json.get("teethPeriod")).intValue();
        lipsPeriod = ((Long) json.get("lipsPeriod")).intValue();
        jawShift = ((Long) json.get("jawShift")).intValue();
        teethShift = ((Long) json.get("teethShift")).intValue();
        lipsShift = ((Long) json.get("lipsShift")).intValue();
        showJaw = (Boolean) json.get("showJaw");
        showTeeth = (Boolean) json.get("showTeeth");
        showLips = (Boolean) json.get("showLips");
        enabled = (Boolean) json.get("enables");
    }

}
class AlligatorLines {
    public List<Double> jaw;
    public List<Double> teeth;
    public List<Double> lips;

    public AlligatorLines(List<Double> jaw, List<Double> teeth, List<Double> lips) {
        this.jaw = jaw;
        this.teeth = teeth;
        this.lips = lips;
    }
}