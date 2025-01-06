package Views;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import Models.Match;
import Utils.AppContext;
import Strategy.*;

public class MatchManagementPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;

    public MatchManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("MATCHS_TOURNOI"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Match table
        JTable matchTable = new JTable(getMatchData(), getMatchColumns());
        JScrollPane scrollPane = new JScrollPane(matchTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton updateScoresButton = new JButton(messages.getString("SCORE"));
        buttonPanel.add(updateScoresButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        updateScoresButton.addActionListener(e -> {
            int selectedRow = matchTable.getSelectedRow();
            if (selectedRow >= 0) {
                Match selectedMatch = getMatchByRow(selectedRow);
                new AddMatchScoresStrategy(selectedMatch, 2, 1).execute(); // Example scores
            }
        });
    }

    private Object[][] getMatchData() {
        MatchDAO dao = FactoryDAO.getMatchDAO();
        return dao.getMatchsByTournoi(AppContext.getCurrentTournament().getId())
                  .stream()
                  .map(m -> new Object[]{
                      m.getNumTour(),
                      m.getEq1(),
                      m.getEq2(),
                      m.getScore1(),
                      m.getScore2()
                  })
                  .toArray(Object[][]::new);
    }

    private String[] getMatchColumns() {
        return new String[]{
            messages.getString("TOURS"),
            messages.getString("EQ1"),
            messages.getString("EQ2"),
            messages.getString("S_EQ1"),
            messages.getString("S_EQ2")
        };
    }

    private Match getMatchByRow(int rowIndex) {
        MatchDAO dao = FactoryDAO.getMatchDAO();
        return dao.getMatchsByTournoi(AppContext.getCurrentTournament().getId()).get(rowIndex);
    }
}
