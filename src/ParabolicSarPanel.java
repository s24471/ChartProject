import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ParabolicSarPanel extends JPanel {
    private ParabolicSarAlgo parabolicSarAlgo;
    private JPanel optionsPanel;

    public ParabolicSarPanel(ParabolicSarAlgo parabolicSarAlgo) {
        this.parabolicSarAlgo = parabolicSarAlgo;
        initializeContent();
    }

    private void initializeContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        JCheckBox enableCheckbox = ThemeUtil.createStyledCheckBox("Enable Parabolic SAR Algorithm");
        enableCheckbox.setSelected(parabolicSarAlgo.isEnabled());
        enableCheckbox.addActionListener(e -> {
            parabolicSarAlgo.setEnabled(enableCheckbox.isSelected());
            optionsPanel.setVisible(enableCheckbox.isSelected());
            Main.frame.repaint();
        });

        add(enableCheckbox);

        optionsPanel = createOptionsPanel();
        optionsPanel.setVisible(parabolicSarAlgo.isEnabled());
        add(optionsPanel);
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));

        JLabel initialAFLabel = ThemeUtil.createStyledLabel("Initial AF:");
        JSpinner initialAFSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(parabolicSarAlgo.getInitialAF(), 0.01, 1.0, 0.01));
        initialAFSpinner.addChangeListener(e -> {
            parabolicSarAlgo.setInitialAF((Double) initialAFSpinner.getValue());
            Main.frame.repaint();
        });

        JLabel maxAFLabel = ThemeUtil.createStyledLabel("Max AF:");
        JSpinner maxAFSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(parabolicSarAlgo.getMaxAF(), 0.01, 1.0, 0.01));
        maxAFSpinner.addChangeListener(e -> {
            parabolicSarAlgo.setMaxAF((Double) maxAFSpinner.getValue());
            Main.frame.repaint();
        });

        JLabel colorLabel = ThemeUtil.createStyledLabel("Line Color:");
        JButton colorButton = new JButton();
        colorButton.setPreferredSize(new Dimension(30, 30));
        colorButton.setBackground(parabolicSarAlgo.getLineColor());
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Line Color", parabolicSarAlgo.getLineColor());
            if (newColor != null) {
                parabolicSarAlgo.setLineColor(newColor);
                colorButton.setBackground(newColor);
                Main.frame.repaint();
            }
        });

        JLabel thicknessLabel = ThemeUtil.createStyledLabel("Line Thickness:");
        JSpinner thicknessSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(parabolicSarAlgo.getLineThickness(), 1.0, 10.0, 0.1));
        thicknessSpinner.addChangeListener(e -> {
            parabolicSarAlgo.setLineThickness(((Double) thicknessSpinner.getValue()).floatValue());
            Main.frame.repaint();
        });

        JButton restoreDefaultsButton = ThemeUtil.createStyledButton("Restore Defaults");
        restoreDefaultsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parabolicSarAlgo.restoreDefault();
                initialAFSpinner.setValue(parabolicSarAlgo.getInitialAF());
                maxAFSpinner.setValue(parabolicSarAlgo.getMaxAF());
                colorButton.setBackground(parabolicSarAlgo.getLineColor());
                thicknessSpinner.setValue(parabolicSarAlgo.getLineThickness());
                Main.frame.repaint();
            }
        });

        JPanel afPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        afPanel.setBackground(new Color(30, 30, 30));
        afPanel.add(initialAFLabel);
        afPanel.add(initialAFSpinner);
        afPanel.add(maxAFLabel);
        afPanel.add(maxAFSpinner);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBackground(new Color(30, 30, 30));
        colorPanel.add(colorLabel);
        colorPanel.add(colorButton);

        JPanel thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thicknessPanel.setBackground(new Color(30, 30, 30));
        thicknessPanel.add(thicknessLabel);
        thicknessPanel.add(thicknessSpinner);

        panel.add(afPanel);
        panel.add(colorPanel);
        panel.add(thicknessPanel);
        panel.add(restoreDefaultsButton);

        return panel;
    }
}
