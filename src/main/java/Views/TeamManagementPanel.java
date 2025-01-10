package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import DAOs.EquipeDAO;
import Utils.AppContext;
import DAOs.FactoryDAO;
import Models.Equipe;
import Observer.IObserver;
import Strategy.*;

public class TeamManagementPanel extends JPanel implements IObserver {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JTable teamTable;

    private JButton addTeamButton; 
    private JButton validateTeamsButton;

    public TeamManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        
        // Register this panel as an observer
        AppContext.addObserver(this);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTeamTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Initial button states
        addTeamButton.setEnabled(false);
        validateTeamsButton.setEnabled(false);

        // Refresh team list initially
        this.refreshTeamList();
    }

    private JPanel createHeaderPanel() {
        JLabel header = new JLabel(messages.getString("EQUIPES"), JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        header.setForeground(new Color(50, 50, 50));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JScrollPane createTeamTablePanel() {
        String[] columns = {"Team Number", "Player 1", "Player 2"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };
        teamTable = new JTable(model);
        teamTable.setFillsViewportHeight(true);
        teamTable.setRowHeight(30);
        teamTable.setFont(new Font("Arial", Font.PLAIN, 14));
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        teamTable.getTableHeader().setBackground(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(teamTable);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        addTeamButton = new JButton(messages.getString("AJ_EQ"));
        validateTeamsButton = new JButton(messages.getString("VAL_EQ"));

        // Style buttons
        styleButton(addTeamButton);
        styleButton(validateTeamsButton);

        addTeamButton.addActionListener(e -> handleAddTeam());
        validateTeamsButton.addActionListener(e -> handleValidateTeams());

        buttonPanel.add(addTeamButton);
        buttonPanel.add(validateTeamsButton);
        return buttonPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.setRolloverEnabled(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 139, 87));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 179, 113));
            }
        });
    }

    private void handleAddTeam() {
        JTextField player1Field = new JTextField(20);
        JTextField player2Field = new JTextField(20);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1, 5, 5));
        inputPanel.add(new JLabel(messages.getString("N_J1")));
        inputPanel.add(player1Field);
        inputPanel.add(new JLabel(messages.getString("N_J2")));
        inputPanel.add(player2Field);

        int result = JOptionPane.showConfirmDialog(
            this,
            inputPanel,
            messages.getString("ADD_TEAM_TITLE"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String player1 = player1Field.getText().trim();
            String player2 = player2Field.getText().trim();

            if (player1.isEmpty() || player2.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    messages.getString("PLAYERS_REQUIRED"),
                    messages.getString("ERROR_TITLE"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean success = new AddTeamStrategy(player1, player2, AppContext.getCurrentTournament().getId()).execute();
            if (success) {
                refreshTeamList();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    messages.getString("TEAM_ADDED_ERROR"),
                    messages.getString("ERROR_TITLE"),
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void handleValidateTeams() {
        boolean success = new ValidateTeamsStrategy(AppContext.getCurrentTournament().getId()).execute();
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                messages.getString("TEAMS_VALIDATED_SUCCESS"),
                messages.getString("SUCCESS_TITLE"),
                JOptionPane.INFORMATION_MESSAGE
            );
            addTeamButton.setEnabled(false);
            validateTeamsButton.setEnabled(false);
            
            frame.navigateTo("MATCH_MANAGEMENT");
        } else {
            JOptionPane.showMessageDialog(
                this,
                messages.getString("TEAMS_VALIDATION_ERROR"),
                messages.getString("ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshTeamList() {
        DefaultTableModel model = (DefaultTableModel) teamTable.getModel();
        model.setRowCount(0);  // Clear existing rows

        List<Equipe> teams = getTeamsWithNumbers();
        for (Equipe team : teams) {
            model.addRow(new Object[]{team.getNumber(), team.getJoueur1(), team.getJoueur2()});
        }

        if (AppContext.getCurrentTournament().getStatus() == 0) {
            addTeamButton.setEnabled(true);
            validateTeamsButton.setEnabled(true);
        } else {
            addTeamButton.setEnabled(false);
            validateTeamsButton.setEnabled(false);
        }
    }

    private List<Equipe> getTeamsWithNumbers() {
        EquipeDAO dao = FactoryDAO.getEquipeDAO();
        return dao.getByTournoi(AppContext.getCurrentTournament().getId());
    }

    @Override
    public void update() {
        refreshTeamList();
    }
}
