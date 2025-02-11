import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TdMovingAveragePanel extends JPanel {
    private TdMovingAverageAlgo tdMovingAverageAlgo;
    private JPanel optionsPanel;

    public TdMovingAveragePanel(TdMovingAverageAlgo tdMovingAverageAlgo) {
        this.tdMovingAverageAlgo = tdMovingAverageAlgo;
        initializeContent();
    }

    private void initializeContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        JCheckBox enableCheckbox = ThemeUtil.createStyledCheckBox("Enable TD Moving Average");
        enableCheckbox.setSelected(tdMovingAverageAlgo.isEnabled());
        enableCheckbox.addActionListener(e -> {
            tdMovingAverageAlgo.setEnabled(enableCheckbox.isSelected());
            optionsPanel.setVisible(enableCheckbox.isSelected());
            Main.frame.repaint();
        });

        add(enableCheckbox);

        optionsPanel = createOptionsPanel();
        optionsPanel.setVisible(tdMovingAverageAlgo.isEnabled());
        add(optionsPanel);
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));

        JLabel periodLabel = ThemeUtil.createStyledLabel("Period:");
        JSpinner periodSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(tdMovingAverageAlgo.getPeriod(), 1, 100, 1));
        periodSpinner.addChangeListener(e -> {
            int newValue = (Integer) periodSpinner.getValue();
            tdMovingAverageAlgo.setPeriod(newValue);
            Main.frame.repaint();
        });

        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodPanel.setBackground(new Color(30, 30, 30));
        periodPanel.add(periodLabel);
        periodPanel.add(periodSpinner);

        JLabel colorLabel = ThemeUtil.createStyledLabel("Line Color:");
        JButton colorButton = new JButton();
        colorButton.setPreferredSize(new Dimension(30, 30));
        colorButton.setBackground(tdMovingAverageAlgo.getLineColor());
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Line Color", tdMovingAverageAlgo.getLineColor());
            if (newColor != null) {
                tdMovingAverageAlgo.setLineColor(newColor);
                colorButton.setBackground(newColor);
                Main.frame.repaint();
            }
        });

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBackground(new Color(30, 30, 30));
        colorPanel.add(colorLabel);
        colorPanel.add(colorButton);

        JLabel thicknessLabel = ThemeUtil.createStyledLabel("Line Thickness:");
        JSpinner thicknessSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(tdMovingAverageAlgo.getLineThickness(), 1.0, 10.0, 0.1));
        thicknessSpinner.addChangeListener(e -> {
            float newValue = ((Number) thicknessSpinner.getValue()).floatValue();
            tdMovingAverageAlgo.setLineThickness(newValue);
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
                tdMovingAverageAlgo.restoreDefault();
                periodSpinner.setValue(tdMovingAverageAlgo.getPeriod());
                thicknessSpinner.setValue(tdMovingAverageAlgo.getLineThickness());
                colorButton.setBackground(tdMovingAverageAlgo.getLineColor());
                Main.frame.repaint();
            }
        });

        panel.add(periodPanel);
        panel.add(colorPanel);
        panel.add(thicknessPanel);
        panel.add(restoreDefaultsButton);

        return panel;
    }
}
