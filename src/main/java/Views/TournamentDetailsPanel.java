package Views;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import Utils.AppContext;

public class TournamentDetailsPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;
    
    public TournamentDetailsPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel(messages.getString("DETAIL_TOUR"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Tournament details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2));
        detailsPanel.add(new JLabel(messages.getString("NOM_TOURN")));
        detailsPanel.add(new JLabel(AppContext.getCurrentTournament().getNom()));
        System.out.println(AppContext.getCurrentTournament());
        detailsPanel.add(new JLabel(messages.getString("STATUT")));
        detailsPanel.add(new JLabel(String.valueOf(AppContext.getCurrentTournament().getStatus())));

        detailsPanel.add(new JLabel(messages.getString("NB_MATCH")));
        detailsPanel.add(new JLabel(String.valueOf(AppContext.getCurrentTournament().getNumberMatch())));

        add(detailsPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton(messages.getString("TOURNOI_LIST"));
        backButton.addActionListener(e -> frame.navigateTo("TOURNAMENT_SELECTION"));

        JButton manageTeamsButton = new JButton(messages.getString("EQUIPES"));
        manageTeamsButton.addActionListener(e -> frame.navigateTo("TEAM_MANAGEMENT"));

        buttonPanel.add(backButton);
        buttonPanel.add(manageTeamsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
