import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class KeltnerChannelPanel extends JPanel {
    private KeltnerChannelAlgo keltnerChannelAlgo;
    private JPanel optionsPanel;

    public KeltnerChannelPanel(KeltnerChannelAlgo keltnerChannelAlgo) {
        this.keltnerChannelAlgo = keltnerChannelAlgo;
        initializeContent();
    }

    private void initializeContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        JCheckBox enableCheckbox = ThemeUtil.createStyledCheckBox("Enable Keltner Channel");
        enableCheckbox.setSelected(keltnerChannelAlgo.isEnabled());
        enableCheckbox.addActionListener(e -> {
            keltnerChannelAlgo.setEnabled(enableCheckbox.isSelected());
            optionsPanel.setVisible(enableCheckbox.isSelected());
            Main.frame.repaint();
        });

        add(enableCheckbox);

        optionsPanel = createOptionsPanel();
        optionsPanel.setVisible(keltnerChannelAlgo.isEnabled());
        add(optionsPanel);
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));

        JLabel periodLabel = ThemeUtil.createStyledLabel("Period:");
        JSpinner periodSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(keltnerChannelAlgo.getPeriod(), 1, 100, 1));
        periodSpinner.addChangeListener(e -> {
            int newValue = (Integer) periodSpinner.getValue();
            keltnerChannelAlgo.setPeriod(newValue);
            Main.frame.repaint();
        });

        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodPanel.setBackground(new Color(30, 30, 30));
        periodPanel.add(periodLabel);
        periodPanel.add(periodSpinner);

        JLabel multiplierLabel = ThemeUtil.createStyledLabel("ATR Multiplier:");
        JSpinner multiplierSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(keltnerChannelAlgo.getMultiplier(), 0.1, 10.0, 0.1));
        multiplierSpinner.addChangeListener(e -> {
            double newValue = ((Number) multiplierSpinner.getValue()).doubleValue();
            keltnerChannelAlgo.setMultiplier(newValue);
            Main.frame.repaint();
        });

        JPanel multiplierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        multiplierPanel.setBackground(new Color(30, 30, 30));
        multiplierPanel.add(multiplierLabel);
        multiplierPanel.add(multiplierSpinner);

        JPanel colorPanel = new JPanel(new GridLayout(3,3));
        colorPanel.setBackground(new Color(30, 30, 30));

        JLabel upperLabel = ThemeUtil.createStyledLabel("Upper Line Color:");
        JButton upperColorButton = new JButton();
        upperColorButton.setPreferredSize(new Dimension(30, 30));
        upperColorButton.setBackground(keltnerChannelAlgo.getUpperColor());
        upperColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Upper Line Color", keltnerChannelAlgo.getUpperColor());
            if (newColor != null) {
                keltnerChannelAlgo.setUpperColor(newColor);
                upperColorButton.setBackground(newColor);
                Main.frame.repaint();
            }
        });

        JLabel middleLabel = ThemeUtil.createStyledLabel("Middle Line Color:");
        JButton middleColorButton = new JButton();
        middleColorButton.setPreferredSize(new Dimension(30, 30));
        middleColorButton.setBackground(keltnerChannelAlgo.getMidColor());
        middleColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Middle Line Color", keltnerChannelAlgo.getMidColor());
            if (newColor != null) {
                keltnerChannelAlgo.setMidColor(newColor);
                middleColorButton.setBackground(newColor);
                Main.frame.repaint();
            }
        });

        JLabel lowerLabel = ThemeUtil.createStyledLabel("Lower Line Color:");
        JButton lowerColorButton = new JButton();
        lowerColorButton.setPreferredSize(new Dimension(30, 30));
        lowerColorButton.setBackground(keltnerChannelAlgo.getLowerColor());
        lowerColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Lower Line Color", keltnerChannelAlgo.getLowerColor());
            if (newColor != null) {
                keltnerChannelAlgo.setLowerColor(newColor);
                lowerColorButton.setBackground(newColor);
                Main.frame.repaint();
            }
        });

        colorPanel.add(upperLabel);
        colorPanel.add(upperColorButton);
        colorPanel.add(middleLabel);
        colorPanel.add(middleColorButton);
        colorPanel.add(lowerLabel);
        colorPanel.add(lowerColorButton);

        JLabel thicknessLabel = ThemeUtil.createStyledLabel("Line Thickness:");
        JSpinner thicknessSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(keltnerChannelAlgo.getLineThickness(), 1.0, 10.0, 0.1));
        thicknessSpinner.addChangeListener(e -> {
            float newValue = ((Number) thicknessSpinner.getValue()).floatValue();
            keltnerChannelAlgo.setLineThickness(newValue);
            Main.frame.repaint();
        });

        JPanel thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thicknessPanel.setBackground(new Color(30, 30, 30));
        thicknessPanel.add(thicknessLabel);
        thicknessPanel.add(thicknessSpinner);

        JButton restoreDefaultsButton = ThemeUtil.createStyledButton("Restore Defaults");
        restoreDefaultsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keltnerChannelAlgo.restoreDefault();
                periodSpinner.setValue(keltnerChannelAlgo.getPeriod());
                multiplierSpinner.setValue(keltnerChannelAlgo.getMultiplier());
                thicknessSpinner.setValue(keltnerChannelAlgo.getLineThickness());
                upperColorButton.setBackground(keltnerChannelAlgo.getUpperColor());
                middleColorButton.setBackground(keltnerChannelAlgo.getMidColor());
                lowerColorButton.setBackground(keltnerChannelAlgo.getLowerColor());
                Main.frame.repaint();
            }
        });

        panel.add(periodPanel);
        panel.add(multiplierPanel);
        panel.add(colorPanel);
        panel.add(thicknessPanel);
        panel.add(restoreDefaultsButton);

        return panel;
    }
}
