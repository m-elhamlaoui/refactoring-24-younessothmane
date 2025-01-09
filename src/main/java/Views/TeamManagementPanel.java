package Views;

import javax.swing.*;
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
    private JList<String> teamList;

    private JButton addTeamButton; // Declare instance variables for the buttons
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
        add(createTeamListPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        addTeamButton.setEnabled(false);
        validateTeamsButton.setEnabled(false);
        this.refreshTeamList();
    }

    private JPanel createHeaderPanel() {
        JLabel header = new JLabel(messages.getString("EQUIPES"), JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JScrollPane createTeamListPanel() {
        teamList = new JList<>(getTeamNames());
        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(teamList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addTeamButton = new JButton(messages.getString("AJ_EQ"));
        validateTeamsButton = new JButton(messages.getString("VAL_EQ"));

        addTeamButton.addActionListener(e -> handleAddTeam());
        validateTeamsButton.addActionListener(e -> handleValidateTeams());

        buttonPanel.add(addTeamButton);
        buttonPanel.add(validateTeamsButton);
        return buttonPanel;
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
            
            frame.navigateTo("MATCH_MANAGEMENT"); // Navigate after the dialog is closed
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
        teamList.setListData(getTeamNames());
        if(AppContext.getCurrentTournament().getStatus()==0){
          // System.out.println("in team managm "+ AppContext.getCurrentTournament());
           addTeamButton.setEnabled(true);
           validateTeamsButton.setEnabled(true);
        }
        else {
            addTeamButton.setEnabled(false);
            validateTeamsButton.setEnabled(false);
        }
    }

    private String[] getTeamNames() {
        EquipeDAO dao = FactoryDAO.getEquipeDAO();
        List<Equipe> teams = dao.getByTournoi(AppContext.getCurrentTournament().getId());
        return teams.stream().map(Equipe::getPlayers).toArray(String[]::new);
    }

    @Override
    public void update() {
        refreshTeamList();
    }
}
