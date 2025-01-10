package Views;

import javax.swing.*;
import Models.Tournoi;
import Observer.IObserver;
import java.awt.*;
import java.util.ResourceBundle;
import Utils.AppContext;
import javax.swing.border.TitledBorder;  

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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Added more padding for better aesthetics

        add(createHeader(), BorderLayout.NORTH);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        update();  // Initial update
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Tournament Details", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));  // Improved font size and type
        header.setForeground(new Color(0, 51, 102));  // Set a custom color for header
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));  // Added space around header
        return header;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridBagLayout());  // Using GridBagLayout for more flexible positioning
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 51, 102), 2), "Tournament Details", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Added padding between components

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Name"), gbc);
        nameLabel = new JLabel();
        gbc.gridx = 1;
        detailsPanel.add(nameLabel, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Status"), gbc);
        statusLabel = new JLabel();
        gbc.gridx = 1;
        detailsPanel.add(statusLabel, gbc);

        // Match Count
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Number of matches"), gbc);
        matchCountLabel = new JLabel();
        gbc.gridx = 1;
        detailsPanel.add(matchCountLabel, gbc);

        return detailsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton backButton = createNavigationButton(messages.getString("TOURNOI_LIST"), "TOURNAMENT_SELECTION");
        JButton manageTeamsButton = createNavigationButton(messages.getString("EQUIPES"), "TEAM_MANAGEMENT");

        // Add hover effects and styling
        styleButton(backButton);
        styleButton(manageTeamsButton);

        buttonPanel.add(backButton);
        buttonPanel.add(manageTeamsButton);

        return buttonPanel;
    }

    private JButton createNavigationButton(String text, String navigationTarget) {
        JButton button = new JButton(text);
        button.addActionListener(e -> frame.navigateTo(navigationTarget));
        button.setPreferredSize(new Dimension(180, 40));  // Increased button size for better appearance
        return button;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 51, 102));  // Set background color
        button.setForeground(Color.WHITE);  // Set text color
        button.setFont(new Font("Arial", Font.BOLD, 14));  // Set a bold font
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));  // White border for contrast
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));  // Change background color on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 51, 102));  // Reset background color
            }
        });
    }

    @Override
    public void update() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        nameLabel.setText(currentTournament.getNom());
        statusLabel.setText(String.valueOf(currentTournament.getStatutName()));
        matchCountLabel.setText(String.valueOf(currentTournament.getNumberMatch()));
    }
}
