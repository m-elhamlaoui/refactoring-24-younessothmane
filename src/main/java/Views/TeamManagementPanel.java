package Views;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

import DAOs.EquipeDAO;
import Utils.AppContext;
import DAOs.FactoryDAO;
import Models.Equipe;
import Strategy.*;

public class TeamManagementPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;

    public TeamManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel(messages.getString("EQUIPES"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Team list
        JList<String> teamList = new JList<>(getTeamNames());
        JScrollPane scrollPane = new JScrollPane(teamList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addTeamButton = new JButton(messages.getString("AJ_EQ"));
        JButton validateTeamsButton = new JButton(messages.getString("VAL_EQ"));

        buttonPanel.add(addTeamButton);
        buttonPanel.add(validateTeamsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions 
        addTeamButton.addActionListener(e -> new AddTeamStrategy("Player1", "Player2", AppContext.getCurrentTournament().getId()).execute());
        validateTeamsButton.addActionListener(e -> new ValidateTeamsStrategy(AppContext.getCurrentTournament().getId()).execute());
    }

    private String[] getTeamNames() {
        EquipeDAO dao = FactoryDAO.getEquipeDAO();
        List<Equipe> teams = dao.getByTournoi(AppContext.getCurrentTournament().getId());
        return teams.stream().map(Equipe::getJoueur1).toArray(String[]::new); 
    }
}
