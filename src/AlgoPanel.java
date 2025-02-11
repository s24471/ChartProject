import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AlgoPanel extends ListPanel {

    public AlgoPanel() {
        initializeContent();
    }

    @Override
    protected void initializeContent() {
        JPanel algoSettingsPanel = new JPanel();
        algoSettingsPanel.setLayout(new BoxLayout(algoSettingsPanel, BoxLayout.Y_AXIS));
        algoSettingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        algoSettingsPanel.setBackground(new Color(30, 30, 30));

        for (Algorithm algorithm : AlgorithmManager.getInstance().getAllAlgorithms()) {
            JPanel configPanel = algorithm.getConfigurationPanel();
            addCategory(algorithm.getName(), configPanel);
        }

        refreshContentPanel();
    }
}
