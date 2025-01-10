package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import DAOs.EquipeDAO;
import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import Models.Match;
import Observer.IObserver;
import Models.Equipe;
import Utils.AppContext;

public class ResultManagementPanel extends JPanel implements IObserver {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public ResultManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        AppContext.addObserver(this);
        initializeUI();
    }

    @Override
    public void update() {
        // Refresh the table data when the observable notifies this observer
        Object[][] newData = getResultData();
        tableModel.setDataVector(newData, getResultColumns());
        resultTable.revalidate();
        resultTable.repaint();
    }

      private static final Color WINNER_BACKGROUND = new Color(50, 247, 50);  // Light green
    private static final Font WINNER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("RESULTATS"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(getResultData(), getResultColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (row == 0) {  // Top row (winner)
                    c.setBackground(WINNER_BACKGROUND);
                    c.setFont(WINNER_FONT);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setFont(getFont());
                }
                
                return c;
            }
        };

        // Improve table appearance
        resultTable.setRowHeight(25);  // Increase row height for better readability
        resultTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
    }


private Object[][] getResultData() {
    if (AppContext.getCurrentTournament().getId() == -1) return new Object[0][];

    EquipeDAO equipeDao = FactoryDAO.getEquipeDAO();
    List<Equipe> teams = equipeDao.getByTournoi(AppContext.getCurrentTournament().getId());

    // Create a list of team data
    List<Object[]> resultData = teams.stream()
            .map(team -> new Object[]{
                team.getNumber(),
                team.getJoueur1(),
                team.getJoueur2(),
                calculateTotalScore(team.getId()),
                calculateMatchesWon(team.getId()),
                calculateMatchesPlayed(team.getId())
            })
            .collect(Collectors.toList());

    // Sort the resultData by number of matches won and then by total score
    resultData.sort((data1, data2) -> {
        int matchesWonComparison = Integer.compare((int) data2[4], (int) data1[4]); // Compare matches won (index 4)
        if (matchesWonComparison != 0) {
            return matchesWonComparison; // If matches won are different, sort by that
        } else {
            // If matches won are the same, sort by total score (index 3)
            return Integer.compare((int) data2[3], (int) data1[3]);
        }
    });

    // Convert the list back to a 2D array
    return resultData.toArray(new Object[0][]);
}


    private String[] getResultColumns() {
        return new String[]{
            "TEAM_NUMBER",// "Numéro d'équipe"
            "PLAYER1_NAME", // "Nom joueur 1"
            "PLAYER2_NAME", // "Nom joueur 2"
            "SCORE", // "Score"
            "MATCHES_WON", // "Matchs gagnés"
           "MATCHES_PLAYED" // "Matchs joués"
        };
    }

    private int calculateTotalScore(int teamId) {
        MatchDAO matchDao = FactoryDAO.getMatchDAO();
        List<Match> matches = matchDao.getMatchsByTournoi(AppContext.getCurrentTournament().getId());

        return matches.stream()
                .filter(match -> match.isTermine() && (match.getEq1() == teamId || match.getEq2() == teamId))
                .mapToInt(match -> {
                    if (match.getEq1() == teamId) {
                        return match.getScore1();
                    } else if (match.getEq2() == teamId) {
                        return match.getScore2();
                    }
                    return 0;
                })
                .sum();
    }

    private int calculateMatchesWon(int teamId) {
        MatchDAO matchDao = FactoryDAO.getMatchDAO();
        List<Match> matches = matchDao.getMatchsByTournoi(AppContext.getCurrentTournament().getId());

        return (int) matches.stream()
                .filter(match -> match.isTermine() && (
                        (match.getEq1() == teamId && match.getScore1() > match.getScore2()) ||
                        (match.getEq2() == teamId && match.getScore2() > match.getScore1())
                ))
                .count();
    }

    private int calculateMatchesPlayed(int teamId) {
        MatchDAO matchDao = FactoryDAO.getMatchDAO();
        List<Match> matches = matchDao.getMatchsByTournoi(AppContext.getCurrentTournament().getId());

        return (int) matches.stream()
                .filter(match -> match.isTermine() && (match.getEq1() == teamId || match.getEq2() == teamId))
                .count();
    }
}
