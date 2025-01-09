package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

import Utils.AppContext;
import Utils.LanguageSelector;
import Utils.ResourceLoader;

public class MainFrame extends JFrame {
    private final ResourceBundle messages;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final JPanel navigationPanel;
    private String currentPanel;
    
    // Default labels if resource bundle keys are missing
    private final Map<String, String> defaultLabels = new HashMap<>() {{
        put("TOURNAMENT_SELECTION", "Tournament Selection");
        put("TOURNAMENT_DETAILS", "Tournament Details");
        put("TEAM_MANAGEMENT", "Team Management");
        put("MATCH_MANAGEMENT", "Match Management");
        put("RESULT_MANAGEMENT", "Result Management");
        put("ROUND_MANAGEMENT", "Round Management");
    }};

    public MainFrame() {
        setTitle("Tournament Manager");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String language = LanguageSelector.selectLanguage();
        AppContext.setLanguage(language);
        
        messages = ResourceLoader.loadMessages(language);
        
        setLayout(new BorderLayout());
        
        navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel, BorderLayout.CENTER);

        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        initializePanels();
    }

    private String getLocalizedString(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            // If the key is not found in the resource bundle, return the default label
            return defaultLabels.getOrDefault(key, key);
        }
    }

    private JPanel createNavigationPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nav.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        String[] buttonLabels = {
            "TOURNAMENT_SELECTION", "TOURNAMENT_DETAILS", "TEAM_MANAGEMENT", "ROUND_MANAGEMENT",
            "MATCH_MANAGEMENT", "RESULT_MANAGEMENT"
        };

        for (String label : buttonLabels) {
            // Use the safer getLocalizedString method instead of direct ResourceBundle access
            JButton button = new JButton(getLocalizedString(label));
            button.setActionCommand(label);
            button.addActionListener(e -> navigateTo(e.getActionCommand()));
            nav.add(button);
        }

        return nav;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel(" Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel timeLabel = new JLabel(new java.util.Date().toString() + " ");
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        new Timer(1000, e -> timeLabel.setText(new java.util.Date().toString() + " ")).start();
        
        return statusBar;
    }

    private void initializePanels() {
        UIManager.put("Panel.background", new Color(245, 245, 245));
        UIManager.put("Button.background", new Color(230, 230, 230));
        
        mainPanel.add(createStyledPanel(new TournamentSelectionPanel(this, messages)), "TOURNAMENT_SELECTION");
        mainPanel.add(createStyledPanel(new TournamentDetailsPanel(this, messages)), "TOURNAMENT_DETAILS");
        mainPanel.add(createStyledPanel(new TeamManagementPanel(this, messages)), "TEAM_MANAGEMENT");
        mainPanel.add(createStyledPanel(new MatchManagementPanel(this, messages)), "MATCH_MANAGEMENT");
        mainPanel.add(createStyledPanel(new ResultManagementPanel(this, messages)), "RESULT_MANAGEMENT");
        
        // Add the RoundPanel
        mainPanel.add(createStyledPanel(new RoundManagementPanel(this, messages)), "ROUND_MANAGEMENT");  // Assuming tournoiId is 1 for this example
    }

    private JPanel createStyledPanel(JPanel panel) {
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        return panel;
    }

    public void navigateTo(String panelName) {
        currentPanel = panelName;
        cardLayout.show(mainPanel, panelName);
        
        for (Component comp : navigationPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(btn.getActionCommand().equals(panelName) ? 
                    new Color(200, 200, 200) : new Color(230, 230, 230));
            }
        }
    }

    public String getCurrentPanel() {
        return currentPanel;
    }

    public void start() {
        navigateTo("TOURNAMENT_SELECTION");
        setVisible(true);
    }
}
