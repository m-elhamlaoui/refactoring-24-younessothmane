package Views;

import javax.swing.*;

import Models.Tournoi;
import Observer.IObserver;

import java.awt.*;
import java.util.ResourceBundle;
import Utils.AppContext;

public class TournamentDetailsPanel extends JPanel implements IObserver {

    private final MainFrame frame;
    private final ResourceBundle messages;
    private JLabel nameLabel;
    private JLabel statusLabel;
    private JLabel matchCountLabel;

    public TournamentDetailsPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        
        AppContext.addObserver(this);  // Register as an observer
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createHeader(), BorderLayout.NORTH);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        update();  // Initial update
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Tournament Details", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16));
        return header;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Tournament Details"));

        detailsPanel.add(new JLabel("Name"));
        nameLabel = new JLabel();
        detailsPanel.add(nameLabel);

        detailsPanel.add(new JLabel("Status"));
        statusLabel = new JLabel();
        detailsPanel.add(statusLabel);

        detailsPanel.add(new JLabel("Number of matches"));
        matchCountLabel = new JLabel();
        detailsPanel.add(matchCountLabel);

        return detailsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton backButton = createNavigationButton(messages.getString("TOURNOI_LIST"), "TOURNAMENT_SELECTION");
        JButton manageTeamsButton = createNavigationButton(messages.getString("EQUIPES"), "TEAM_MANAGEMENT");

        buttonPanel.add(backButton);
        buttonPanel.add(manageTeamsButton);

        return buttonPanel;
    }

    private JButton createNavigationButton(String text, String navigationTarget) {
        JButton button = new JButton(text);
        button.addActionListener(e -> frame.navigateTo(navigationTarget));
        button.setPreferredSize(new Dimension(150, 30));
        return button;
    }

    @Override
    public void update() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        nameLabel.setText(currentTournament.getNom());
        statusLabel.setText(String.valueOf(currentTournament.getStatutName()));
        matchCountLabel.setText(String.valueOf(currentTournament.getNumberMatch()));
    }
}
