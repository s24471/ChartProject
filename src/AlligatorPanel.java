import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AlligatorPanel extends JPanel {
    private AlligatorAlgo alligatorAlgo;
    private JPanel optionsPanel;
    private LineOptionsPanel jaw;
    private LineOptionsPanel teeth;
    private LineOptionsPanel lips;

    public AlligatorPanel(AlligatorAlgo alligatorAlgo) {
        this.alligatorAlgo = alligatorAlgo;
        initializeContent();
    }

    private void initializeContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(30, 30, 30));

        JCheckBox enableCheckbox = ThemeUtil.createStyledCheckBox("Enable Alligator Algorithm");
        enableCheckbox.setSelected(alligatorAlgo.isEnabled());
        enableCheckbox.addActionListener(e -> {
            alligatorAlgo.setEnabled(enableCheckbox.isSelected());
            optionsPanel.setVisible(enableCheckbox.isSelected());
            Main.frame.repaint();
        });

        add(enableCheckbox);

        optionsPanel = createOptionsPanel();
        optionsPanel.setVisible(alligatorAlgo.isEnabled());
        add(optionsPanel);
    }


    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        jaw = new LineOptionsPanel("Jaw");
        teeth = new LineOptionsPanel("Teeth");
        lips = new LineOptionsPanel("Lips");
        panel.add(jaw);
        panel.add(teeth);
        panel.add(lips);
        JButton defaultButton = ThemeUtil.createStyledButton("Restore Default");
        defaultButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alligatorAlgo.restoreDefault();
                for (Component component : optionsPanel.getComponents()) {
                    if (component instanceof LineOptionsPanel) {
                        ((LineOptionsPanel) component).refreshValues();
                    }
                }
                Main.frame.repaint();
            }
        });
        panel.add(defaultButton);

        return panel;
    }

    private class LineOptionsPanel extends JPanel {
        private String lineName;
        private JCheckBox enableCheckbox;
        private JPanel controlsPanel;
        private JSpinner thicknessSpinner;
        private JSpinner periodSpinner;
        private JSpinner shiftSpinner;
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
            ((JSpinner.DefaultEditor) thicknessSpinner.getEditor()).getTextField().setColumns(3);
            thicknessSpinner.addChangeListener(e -> {
                float newThickness = thicknessSpinner.getValue() instanceof Double?((Double) thicknessSpinner.getValue()).floatValue():(float)thicknessSpinner.getValue();
                setLineThickness(newThickness);
                Main.frame.repaint();
            });

            topPanel.add(colorLabel);
            topPanel.add(colorButton);
            topPanel.add(thicknessLabel);
            topPanel.add(thicknessSpinner);

            JPanel periodPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            periodPanel.setBackground(new Color(40, 40, 40));

            JLabel periodLabel = new JLabel(lineName + " Period:");
            periodLabel.setForeground(Color.WHITE);

            periodSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(getLinePeriod(), 1, 100, 1));
            periodSpinner.addChangeListener(e -> {
                int newValue = (Integer) periodSpinner.getValue();
                setLinePeriod(newValue);
                Main.frame.repaint();
            });

            JLabel shiftLabel = new JLabel(lineName + " Shift:");
            shiftLabel.setForeground(Color.WHITE);

            shiftSpinner = ThemeUtil.createStyledSpinner(new SpinnerNumberModel(getLineShift(), 0, 50, 1));
            shiftSpinner.addChangeListener(e -> {
                int newValue = (Integer) shiftSpinner.getValue();
                setLineShift(newValue);
                Main.frame.repaint();
            });


            periodPanel.add(periodLabel);
            periodPanel.add(periodSpinner);
            periodPanel.add(shiftLabel);
            periodPanel.add(shiftSpinner);
            panel.add(topPanel);
            panel.add(periodPanel);

            return panel;
        }

        private boolean isLineEnabled() {
            switch (lineName) {
                case "Jaw":
                    return alligatorAlgo.isShowJaw();
                case "Teeth":
                    return alligatorAlgo.isShowTeeth();
                case "Lips":
                    return alligatorAlgo.isShowLips();
                default:
                    return false;
            }
        }

        private void setLineEnabled(boolean enabled) {
            switch (lineName) {
                case "Jaw":
                    alligatorAlgo.setShowJaw(enabled);
                    break;
                case "Teeth":
                    alligatorAlgo.setShowTeeth(enabled);
                    break;
                case "Lips":
                    alligatorAlgo.setShowLips(enabled);
                    break;
            }
        }
        public void refreshValues() {
            enableCheckbox.setSelected(isLineEnabled());
            controlsPanel.setVisible(enableCheckbox.isSelected());
            colorButton.setBackground(getLineColor());
            thicknessSpinner.setValue(getLineThickness());
            periodSpinner.setValue(getLinePeriod());
            shiftSpinner.setValue(getLineShift());
        }

        private Color getLineColor() {
            switch (lineName) {
                case "Jaw":
                    return alligatorAlgo.getJawColor();
                case "Teeth":
                    return alligatorAlgo.getTeethColor();
                case "Lips":
                    return alligatorAlgo.getLipsColor();
                default:
                    return Color.WHITE;
            }
        }

        private void setLineColor(Color color) {
            switch (lineName) {
                case "Jaw":
                    alligatorAlgo.setJawColor(color);
                    break;
                case "Teeth":
                    alligatorAlgo.setTeethColor(color);
                    break;
                case "Lips":
                    alligatorAlgo.setLipsColor(color);
                    break;
            }
        }

        private float getLineThickness() {
            switch (lineName) {
                case "Jaw":
                    return alligatorAlgo.getJawLineThickness();
                case "Teeth":
                    return alligatorAlgo.getTeethLineThickness();
                case "Lips":
                    return alligatorAlgo.getLipsLineThickness();
                default:
                    return 1.0f;
            }
        }

        private void setLineThickness(float thickness) {
            switch (lineName) {
                case "Jaw":
                    alligatorAlgo.setJawLineThickness(thickness);
                    break;
                case "Teeth":
                    alligatorAlgo.setTeethLineThickness(thickness);
                    break;
                case "Lips":
                    alligatorAlgo.setLipsLineThickness(thickness);
                    break;
            }
        }

        private int getLinePeriod() {
            switch (lineName) {
                case "Jaw":
                    return alligatorAlgo.getJawPeriod();
                case "Teeth":
                    return alligatorAlgo.getTeethPeriod();
                case "Lips":
                    return alligatorAlgo.getLipsPeriod();
                default:
                    return 1;
            }
        }

        private void setLinePeriod(int period) {
            switch (lineName) {
                case "Jaw":
                    alligatorAlgo.setJawPeriod(period);
                    break;
                case "Teeth":
                    alligatorAlgo.setTeethPeriod(period);
                    break;
                case "Lips":
                    alligatorAlgo.setLipsPeriod(period);
                    break;
            }
        }

        private int getLineShift() {
            switch (lineName) {
                case "Jaw":
                    return alligatorAlgo.getJawShift();
                case "Teeth":
                    return alligatorAlgo.getTeethShift();
                case "Lips":
                    return alligatorAlgo.getLipsShift();
                default:
                    return 0;
            }
        }

        private void setLineShift(int shift) {
            switch (lineName) {
                case "Jaw":
                    alligatorAlgo.setJawShift(shift);
                    break;
                case "Teeth":
                    alligatorAlgo.setTeethShift(shift);
                    break;
                case "Lips":
                    alligatorAlgo.setLipsShift(shift);
                    break;
            }
        }
    }
}
