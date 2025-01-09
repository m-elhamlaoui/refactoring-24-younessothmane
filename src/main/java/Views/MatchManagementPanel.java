package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import DAOs.TournoiDAO;
import Models.Match;
import Observer.IObserver;
import Utils.AppContext;
import Strategy.*;

public class MatchManagementPanel extends JPanel implements IObserver {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JTable matchTable;
    private final TournoiDAO tournoiDAO;
    private final MatchDAO matchDAO;

    public MatchManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        this.tournoiDAO = FactoryDAO.getTournoiDAO();
        this.matchDAO = FactoryDAO.getMatchDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("MATCHS_TOURNOI"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Match table
        matchTable = new JTable(getMatchData(), getMatchColumns());
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(matchTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton updateScoresButton = new JButton(messages.getString("SCORE"));
        buttonPanel.add(updateScoresButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        updateScoresButton.addActionListener(e -> handleScoreUpdate());

        // Register this panel as an observer
        AppContext.addObserver(this);
    }

    private void handleScoreUpdate() {
        int selectedRow = matchTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                messages.getString("SELECT_MATCH"),
                messages.getString("ERROR"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Match selectedMatch = getMatchByRow(selectedRow);
        if (selectedMatch == null) {
            JOptionPane.showMessageDialog(this,
                messages.getString("MATCH_NOT_FOUND"),
                messages.getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create score input dialog
        JPanel scorePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JSpinner score1Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        JSpinner score2Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        
        scorePanel.add(new JLabel(messages.getString("EQ1") + ":"));
        scorePanel.add(score1Spinner);
        scorePanel.add(new JLabel(messages.getString("EQ2") + ":"));
        scorePanel.add(score2Spinner);

        int result = JOptionPane.showConfirmDialog(this,
            scorePanel,
            messages.getString("ENTER_SCORES"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int score1 = (Integer) score1Spinner.getValue();
            int score2 = (Integer) score2Spinner.getValue();

            try {
                // Update match scores using strategy
                new AddMatchScoresStrategy(selectedMatch, score1, score2).execute();
                
                // Update tournament status
                int tournamentId = AppContext.getCurrentTournament().getId();
                AppContext.getCurrentTournament().setStatus(2); // In progress
                
                // Check if all matches are played
                if (tournoiDAO.isAllMatchesPlayed(tournamentId)) {
                    AppContext.getCurrentTournament().setStatus(3); // Completed
                }
                
                // Update tournament in database
                tournoiDAO.update(AppContext.getCurrentTournament());
                
                // Refresh the view
                refresh();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    messages.getString("SCORE_UPDATE_ERROR") + ": " + ex.getMessage(),
                    messages.getString("ERROR"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void update() {
        refresh();
    }

    private void refresh() {
        DefaultTableModel model = new DefaultTableModel(getMatchData(), getMatchColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        matchTable.setModel(model);
        matchTable.repaint();
    }

    private Object[][] getMatchData() {
        List<Match> matches = matchDAO.getMatchsByTournoi(AppContext.getCurrentTournament().getId());
        return matches.stream()
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
        List<Match> matches = matchDAO.getMatchsByTournoi(AppContext.getCurrentTournament().getId());
        return (rowIndex >= 0 && rowIndex < matches.size()) ? matches.get(rowIndex) : null;
    }
}