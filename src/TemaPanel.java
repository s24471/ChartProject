import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TemaPanel extends JPanel {
    private TemaAlgo temaAlgo;
    private JPanel optionsPanel;
    private LineOptionsPanel shortLine;
    private LineOptionsPanel mediumLine;
    private LineOptionsPanel longLine;

    public TemaPanel(TemaAlgo temaAlgo) {
        this.temaAlgo = temaAlgo;
        initializeContent();
    }

    private void initializeContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        JCheckBox enableCheckbox = ThemeUtil.createStyledCheckBox("Enable TEMA Algorithm");
        enableCheckbox.setSelected(temaAlgo.isEnabled());
        enableCheckbox.addActionListener(e -> {
            temaAlgo.setEnabled(enableCheckbox.isSelected());
            optionsPanel.setVisible(enableCheckbox.isSelected());
            Main.frame.repaint();
        });

        add(enableCheckbox);

        optionsPanel = createOptionsPanel();
        optionsPanel.setVisible(temaAlgo.isEnabled());
        add(optionsPanel);
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));

        shortLine = new LineOptionsPanel("Short");
        mediumLine = new LineOptionsPanel("Medium");
        longLine = new LineOptionsPanel("Long");

        panel.add(shortLine);
        panel.add(mediumLine);
        panel.add(longLine);

        JButton defaultButton = ThemeUtil.createStyledButton("Restore Default");
        defaultButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                temaAlgo.restoreDefault();
                refreshAllLines();
                Main.frame.repaint();
            }
        });
        panel.add(defaultButton);

        return panel;
    }

    private void refreshAllLines() {
        shortLine.refreshValues();
        mediumLine.refreshValues();
        longLine.refreshValues();
    }

    private class LineOptionsPanel extends JPanel {
        private String lineName;
        private JCheckBox enableCheckbox;
        private JPanel controlsPanel;
        private JSpinner thicknessSpinner;
        private JSpinner periodSpinner;
        private JButton colorButton;

        public LineOptionsPanel(String lineName) {
            this.lineName = lineName;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(30, 30, 30));
            setBorder(new EmptyBorder(5, 0, 5, 0));

            enableCheckbox = ThemeUtil.createStyledCheckBox("Show " + lineName);
            enableCheckbox.setSelected(isLineEnabled());
            enableCheckbox.addActionListener(e -> {
                setLineEnabled(enableCheckbox.isSelected());
                controlsPanel.setVisible(enableCheckbox.isSelected());
                Main.frame.repaint();
            });

            controlsPanel = createControlsPanel();
            controlsPanel.setVisible(isLineEnabled());

            add(enableCheckbox);
            add(controlsPanel);
        }

        private JPanel createControlsPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(40, 40, 40));
            panel.setBorder(new EmptyBorder(5, 20, 5, 0));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setBackground(new Color(40, 40, 40));

            JLabel colorLabel = new JLabel("Color:");
            colorLabel.setForeground(Color.WHITE);

            colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.setBackground(getLineColor());
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(null, "Choose " + lineName + " Color", getLineColor());
                if (newColor != null) {
                    colorButton.setBackground(newColor);
                    setLineColor(newColor);
                    Main.frame.repaint();
                }
            });

            JLabel thicknessLabel = new JLabel("Thickness:");
            thicknessLabel.setForeground(Color.WHITE);

            thicknessSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(getLineThickness(), 1.0, 10.0, 0.5));
            thicknessSpinner.addChangeListener(e -> {
                setLineThickness((float) (double) thicknessSpinner.getValue());
                Main.frame.repaint();
            });

            topPanel.add(colorLabel);
            topPanel.add(colorButton);
            topPanel.add(thicknessLabel);
            topPanel.add(thicknessSpinner);

            JLabel periodLabel = new JLabel(lineName + " Period:");
            periodLabel.setForeground(Color.WHITE);

            periodSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(getLinePeriod(), 1, 100, 1));
            periodSpinner.addChangeListener(e -> {
                setLinePeriod((int) periodSpinner.getValue());
                Main.frame.repaint();
            });

            panel.add(topPanel);
            panel.add(periodLabel);
            panel.add(periodSpinner);

            return panel;
        }

        public void refreshValues() {
            enableCheckbox.setSelected(isLineEnabled());
            controlsPanel.setVisible(enableCheckbox.isSelected());
            colorButton.setBackground(getLineColor());
            thicknessSpinner.setValue((double) getLineThickness());
            periodSpinner.setValue(getLinePeriod());
        }

        private boolean isLineEnabled() {
            switch (lineName) {
                case "Short":
                    return temaAlgo.isShowShort();
                case "Medium":
                    return temaAlgo.isShowMedium();
                case "Long":
                    return temaAlgo.isShowLong();
                default:
                    return false;
            }
        }

        private void setLineEnabled(boolean enabled) {
            switch (lineName) {
                case "Short":
                    temaAlgo.setShowShort(enabled);
                    break;
                case "Medium":
                    temaAlgo.setShowMedium(enabled);
                    break;
                case "Long":
                    temaAlgo.setShowLong(enabled);
                    break;
            }
        }

        private Color getLineColor() {
            switch (lineName) {
                case "Short":
                    return temaAlgo.getShortColor();
                case "Medium":
                    return temaAlgo.getMediumColor();
                case "Long":
                    return temaAlgo.getLongColor();
                default:
                    return Color.WHITE;
            }
        }

        private void setLineColor(Color color) {
            switch (lineName) {
                case "Short":
                    temaAlgo.setShortColor(color);
                    break;
                case "Medium":
                    temaAlgo.setMediumColor(color);
                    break;
                case "Long":
                    temaAlgo.setLongColor(color);
                    break;
            }
        }

        private float getLineThickness() {
            switch (lineName) {
                case "Short":
                    return temaAlgo.getShortLineThickness();
                case "Medium":
                    return temaAlgo.getMediumLineThickness();
                case "Long":
                    return temaAlgo.getLongLineThickness();
                default:
                    return 1.0f;
            }
        }

        private void setLineThickness(float thickness) {
            switch (lineName) {
                case "Short":
                    temaAlgo.setShortLineThickness(thickness);
                    break;
                case "Medium":
                    temaAlgo.setMediumLineThickness(thickness);
                    break;
                case "Long":
                    temaAlgo.setLongLineThickness(thickness);
                    break;
            }
        }

        private int getLinePeriod() {
            switch (lineName) {
                case "Short":
                    return temaAlgo.getShortPeriod();
                case "Medium":
                    return temaAlgo.getMediumPeriod();
                case "Long":
                    return temaAlgo.getLongPeriod();
                default:
                    return 1;
            }
        }

        private void setLinePeriod(int period) {
            switch (lineName) {
                case "Short":
                    temaAlgo.setShortPeriod(period);
                    break;
                case "Medium":
                    temaAlgo.setMediumPeriod(period);
                    break;
                case "Long":
                    temaAlgo.setLongPeriod(period);
                    break;
            }
        }
    }
}
