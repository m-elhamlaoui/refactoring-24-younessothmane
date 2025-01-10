package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelEvent;
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
    
    // Design constants
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TABLE_HEADER_COLOR = new Color(41, 128, 185);
    private static final Color EDITABLE_CELL_COLOR = new Color(236, 240, 241);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int PADDING = 15;
    private static final int ROW_HEIGHT = 35;

    public MatchManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        this.tournoiDAO = FactoryDAO.getTournoiDAO();
        this.matchDAO = FactoryDAO.getMatchDAO();
        initializeUI();
        AppContext.addObserver(this);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(PADDING, PADDING));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMatchTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel header = new JLabel(messages.getString("MATCHS_TOURNOI"), JLabel.CENTER);
        header.setFont(HEADER_FONT);
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING, 0));
        
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JScrollPane createMatchTablePanel() {
        DefaultTableModel tableModel = createTableModel();
        matchTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (isCellEditable(row, column)) {
                    component.setBackground(EDITABLE_CELL_COLOR);
                } else {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : BACKGROUND_COLOR);
                }
                return component;
            }
        };

        styleTable();
        setupTableModelListener();

        JScrollPane scrollPane = new JScrollPane(matchTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        return scrollPane;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(getMatchData(), getMatchColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                Object score1 = getValueAt(row, 3);
                Object score2 = getValueAt(row, 4);
                return (column == 3 || column == 4) && ("-".equals(score1) || "-".equals(score2));
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
    }

    private void styleTable() {
        matchTable.setFont(TABLE_FONT);
        matchTable.setRowHeight(ROW_HEIGHT);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchTable.setShowGrid(true);
        matchTable.setGridColor(new Color(189, 195, 199));
        matchTable.setIntercellSpacing(new Dimension(10, 5));
        
        // Style header
        JTableHeader header = matchTable.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(TABLE_FONT.deriveFont(Font.BOLD));
        header.setBorder(BorderFactory.createLineBorder(TABLE_HEADER_COLOR));
    }

    private void setupTableModelListener() {
        matchTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                handleScoreUpdate(e.getFirstRow(), e.getColumn());
            }
        });
    }

    private void handleScoreUpdate(int row, int column) {
        if (column != 3 && column != 4) return;

        try {
            Object score1Obj = matchTable.getValueAt(row, 3);
            Object score2Obj = matchTable.getValueAt(row, 4);

            if ("-".equals(score1Obj) || "-".equals(score2Obj)) {
                return;
            }

            validateAndUpdateScores(row, score1Obj, score2Obj);
            
        } catch (Exception ex) {
            showError(messages.getString("SCORE_UPDATE_ERROR") + ": " + ex.getMessage());
            refresh();
        }
    }

    private void validateAndUpdateScores(int row, Object score1Obj, Object score2Obj) {
        try {
            int score1 = Integer.parseInt(score1Obj.toString());
            int score2 = Integer.parseInt(score2Obj.toString());

            if (score1 < 0 || score2 < 0) {
                throw new IllegalArgumentException(messages.getString("INVALID_SCORE"));
            }

            updateMatchScores(row, score1, score2);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(messages.getString("INVALID_SCORE_FORMAT"));
        }
    }

    private void updateMatchScores(int row, int score1, int score2) {
        Match match = getMatchByRow(row);
        if (match != null) {
            new AddMatchScoresStrategy(match, score1, score2).execute();
            updateTournamentStatus();
            AppContext.notifyObservers();
        }
    }

    private void updateTournamentStatus() {
        int tournamentId = AppContext.getCurrentTournament().getId();
        AppContext.getCurrentTournament().setStatus(2);

        if (tournoiDAO.isAllMatchesPlayed(tournamentId)) {
            AppContext.getCurrentTournament().setStatus(3);
        }

        tournoiDAO.update(AppContext.getCurrentTournament());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            messages.getString("ERROR"),
            JOptionPane.ERROR_MESSAGE
        );
    }

    private Object[][] getMatchData() {
        if (AppContext.getCurrentTournament().getId() == -1) {
            return new Object[0][];
        }
        
        int round = tournoiDAO.getNbTours(AppContext.getCurrentTournament().getId());
        List<Match> matches = matchDAO.getMatchesByRound(AppContext.getCurrentTournament().getId(), round);
        
        return matches.stream()
                .map(m -> new Object[]{
                    m.getNumTour(),
                    m.getEq1(),
                    m.getEq2(),
                    m.getScore1() == -1 ? "-" : m.getScore1(),
                    m.getScore2() == -1 ? "-" : m.getScore2()
                })
                .toArray(Object[][]::new);
    }

    private String[] getMatchColumns() {
        return new String[]{
            "Round",
            messages.getString("EQ1"),
            messages.getString("EQ2"),
            messages.getString("S_EQ1"),
            messages.getString("S_EQ2")
        };
    }

    private Match getMatchByRow(int rowIndex) {
        int tournamentId = AppContext.getCurrentTournament().getId();
        int round = tournoiDAO.getNbTours(tournamentId);
        List<Match> matches = matchDAO.getMatchesByRound(tournamentId, round);
        return (rowIndex >= 0 && rowIndex < matches.size()) ? matches.get(rowIndex) : null;
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
}