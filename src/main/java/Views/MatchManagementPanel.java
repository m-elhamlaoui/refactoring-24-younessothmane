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

        // Match table with custom model
        DefaultTableModel tableModel = new DefaultTableModel(getMatchData(), getMatchColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only score columns (3 and 4) editable
                return column == 3 || column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return Integer class for score columns
                return (columnIndex == 3 || columnIndex == 4) ? Integer.class : Object.class;
            }
        };

        matchTable = new JTable(tableModel);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add cell editing listener
        matchTable.getModel().addTableModelListener(e -> {
            if (e.getType() == e.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column == 3 || column == 4) { // Score columns
                    try {
                        Match match = getMatchByRow(row);
                        if (match != null) {
                            int score1 = (column == 3) ? 
                                (Integer) matchTable.getValueAt(row, 3) : match.getScore1();
                            int score2 = (column == 4) ? 
                                (Integer) matchTable.getValueAt(row, 4) : match.getScore2();

                            // Validate scores
                            if (score1 < 0 || score2 < 0) {
                                throw new IllegalArgumentException(messages.getString("INVALID_SCORE"));
                            }

                            // Update match scores using strategy
                            new AddMatchScoresStrategy(match, score1, score2).execute();
                            
                            // Update tournament status
                            int tournamentId = AppContext.getCurrentTournament().getId();
                            AppContext.getCurrentTournament().setStatus(2); // In progress
                            
                            // Check if all matches are played
                            if (tournoiDAO.isAllMatchesPlayed(tournamentId)) {
                                AppContext.getCurrentTournament().setStatus(3); // Completed

                            }

                            
                            
                            // Update tournament in database
                            tournoiDAO.update(AppContext.getCurrentTournament());
                            AppContext.notifyObservers();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            messages.getString("SCORE_UPDATE_ERROR") + ": " + ex.getMessage(),
                            messages.getString("ERROR"),
                            JOptionPane.ERROR_MESSAGE);
                        refresh(); // Revert changes on error
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(matchTable);
        add(scrollPane, BorderLayout.CENTER);

        // Remove the update scores button since editing is now inline
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Register this panel as an observer
        AppContext.addObserver(this);
    }

    @Override
    public void update() {
        refresh();
    }

    private void refresh() {
        DefaultTableModel model = (DefaultTableModel) matchTable.getModel();
        model.setDataVector(getMatchData(), getMatchColumns());
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