package Views;
import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import Utils.LanguageSelector;
import Utils.ResourceLoader;

public class MainFrame extends JFrame {
    private final ResourceBundle messages;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public MainFrame() {
        setTitle("Tournament Manager");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String language = LanguageSelector.selectLanguage();
        messages = ResourceLoader.loadMessages(language);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initializePanels();
        add(mainPanel);

        setVisible(true);
    }

    
    private void initializePanels() {
        mainPanel.add(new TournamentSelectionPanel(this, messages), "TOURNAMENT_SELECTION");
        mainPanel.add(new TournamentDetailsPanel(this, messages), "TOURNAMENT_DETAILS");
        mainPanel.add(new TeamManagementPanel(this, messages), "TEAM_MANAGEMENT");
        mainPanel.add(new MatchManagementPanel(this, messages), "MATCH_MANAGEMENT");
        mainPanel.add(new ResultManagementPanel(this, messages), "RESULT_MANAGEMENT");
        mainPanel.add(new RoundManagementPanel(this, messages), "ROUND_MANAGEMENT");
    } 

    public void navigateTo(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void start() {
        setVisible(true);
    }
}
