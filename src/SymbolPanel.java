import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

public class SymbolPanel extends JPanel {
    private JLabel symbolLabel;
    private JLabel descriptionLabel;
    private JButton pinButton;
    private Symbol symbol;
    private boolean isPinned;
    private ActionListener pinListener;

    public SymbolPanel(Symbol symbol, boolean isPinned, ActionListener pinListener) {
        this.symbol = symbol;
        this.isPinned = isPinned;
        this.pinListener = pinListener;

        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setBackground(new Color(30, 30, 30));
        setBorder(new MatteBorder(1, 0, 1, 0, new Color(69, 69, 69)));
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        symbolLabel = new JLabel(symbol.trimmedSymbol);
        symbolLabel.setForeground(new Color(200, 200, 200));
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 14));
        textPanel.add(symbolLabel);

        String truncatedDescription = truncateDescription(symbol.description, 180);
        descriptionLabel = new JLabel(truncatedDescription);
        descriptionLabel.setForeground(new Color(150, 150, 150));
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textPanel.add(descriptionLabel);

        add(textPanel, BorderLayout.CENTER);

        pinButton = ThemeUtil.createStyledButton(isPinned ? "Unpin" : "Pin");
        pinButton.setPreferredSize(new Dimension(60, 25));


        pinButton.setVisible(false);

        pinButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                pinButton.setBackground(new Color(100, 100, 100));
            }

            public void mouseExited(MouseEvent evt) {
                pinButton.setBackground(new Color(70, 70, 70));
            }
        });

        pinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pinListener.actionPerformed(e);
                pinButton.setVisible(false);
            }
        });
        add(pinButton, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pinButton.setVisible(true);
                descriptionLabel.setText(truncateDescription(symbol.description, 130));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                TimeUnit.MILLISECONDS.sleep(1);
                                if(!isMouseInsidePanelOrButton(e)){
                                    pinButton.setVisible(false);
                                    descriptionLabel.setText(truncateDescription(symbol.description, 180));
                                    return;
                                }
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }).start();
            }

            @Override
            public void mouseExited(MouseEvent e) {

                if (!isMouseInsidePanelOrButton(e)) {
                    pinButton.setVisible(false);
                    descriptionLabel.setText(truncateDescription(symbol.description, 180));
                }
            }
        });
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
        pinButton.setText(pinned ? "Unpin" : "Pin");
    }

    public Symbol getSymbol() {
        return symbol;
    }


    private String truncateDescription(String description, int maxWidth) {
        FontMetrics metrics = getFontMetrics(new Font("Arial", Font.PLAIN, 12));
        if (metrics.stringWidth(description) <= maxWidth) {
            return description;
        }

        StringBuilder truncated = new StringBuilder();
        for (char c : description.toCharArray()) {
            if (metrics.stringWidth(truncated.toString() + c + "...") > maxWidth) {
                break;
            }
            truncated.append(c);
        }
        return truncated.toString() + "...";
    }


    private boolean isMouseInsidePanelOrButton(MouseEvent e) {
        if(!pinButton.isVisible()) return false;

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        try {
            Point panelLocation = getLocationOnScreen();
            Rectangle panelBounds = new Rectangle(panelLocation.x, panelLocation.y, getWidth(), getHeight());
            return panelBounds.contains(mouseLocation);
        } catch (IllegalComponentStateException ex) {

        }
        return false;

    }
}
