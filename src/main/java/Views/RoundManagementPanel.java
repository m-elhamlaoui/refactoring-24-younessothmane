package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import DAOs.TournoiDAO;
import Models.Match;
import Models.Tournoi;
import Observer.IObserver;
import Utils.AppContext;

public class RoundManagementPanel extends JPanel implements IObserver {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JTable roundTable;
    private JButton addRoundButton;
    private JButton deleteRoundButton;
    private final Color HEADER_BACKGROUND = new Color(51, 51, 51);
    private final Color HEADER_FOREGROUND = Color.WHITE;
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Color BUTTON_BACKGROUND = new Color(70, 130, 180);
    private final Color BUTTON_HOVER = new Color(51, 101, 138);
    private final int PADDING = 15;

    public RoundManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        AppContext.addObserver(this);
    }

    @Override
    public void update() {
        refreshRoundsTable();
        updateButtonStates();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(PADDING, PADDING));
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel(messages.getString("TOURS"), JLabel.CENTER);
        header.setFont(HEADER_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        roundTable = new JTable(new DefaultTableModel(getRoundData(), getRoundColumns())) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table styling
        roundTable.setFont(TABLE_FONT);
        roundTable.setRowHeight(30);
        roundTable.setIntercellSpacing(new Dimension(10, 5));
        roundTable.setShowGrid(true);
        roundTable.setGridColor(new Color(230, 230, 230));
        
        // Header styling
        JTableHeader header = roundTable.getTableHeader();
        header.setBackground(HEADER_BACKGROUND);
        header.setForeground(HEADER_FOREGROUND);
        header.setFont(HEADER_FONT);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
        JScrollPane scrollPane = new JScrollPane(roundTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING, 0));
        
        addRoundButton = createStyledButton(messages.getString("AJOUTER_TOUR"));
        deleteRoundButton = createStyledButton(messages.getString("SUPP_TOUR"));
        
        addRoundButton.addActionListener(e -> handleAddRound());
       // deleteRoundButton.addActionListener(e -> handleDeleteRound());
        
        buttonPanel.add(addRoundButton);
        buttonPanel.add(deleteRoundButton);
        
        return buttonPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BACKGROUND);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_BACKGROUND);
            }
        });
        
        return button;
    }

    private void handleAddRound() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament != null) {
            TournoiDAO tournoiDAO = FactoryDAO.getTournoiDAO();
            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            int trnId = currentTournament.getId();
            
            int totalRounds = tournoiDAO.getNbTours(trnId);
            int[] res = matchDAO.getMatchStatusByTournoi(trnId);
            boolean allMatchesCompleted = res[0] == res[1];
            
            if (allMatchesCompleted && totalRounds < tournoiDAO.getNbEquipes(trnId)) {
                tournoiDAO.ajouterTour(trnId, ++totalRounds);
                AppContext.notifyObservers();
            } else {
                showError(messages.getString("IMPAIR"));
            }
        } else {
            showError(messages.getString("TOURNOI_NON_SELECTIONNE"));
        }
    }

 

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            messages.getString("ERROR_TITLE"),
            JOptionPane.ERROR_MESSAGE);
    }

    private void updateButtonStates() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        boolean tournamentSelected = currentTournament != null && currentTournament.getId() != -1;
        addRoundButton.setEnabled(tournamentSelected);
        deleteRoundButton.setEnabled(tournamentSelected && roundTable.getSelectedRow() != -1);
    }

    private void refreshRoundsTable() {
        DefaultTableModel model = new DefaultTableModel(getRoundData(), getRoundColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roundTable.setModel(model);
    }

    private Object[][] getRoundData() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament == null || currentTournament.getId() == -1) {
            return new Object[0][];
        }
        
        MatchDAO matchDAO = FactoryDAO.getMatchDAO();
        List<Match> matches = matchDAO.getMatchsByTournoi(currentTournament.getId());
        
        return matches.stream()
            .collect(Collectors.groupingBy(Match::getNumTour))
            .entrySet()
            .stream()
            .map(entry -> {
                int round = entry.getKey();
                List<Match> matchList = entry.getValue();
                int totalMatches = matchList.size();
                int completedMatches = (int) matchList.stream()
                    .filter(Match::isTermine)
                    .count();
                return new Object[]{round, totalMatches, completedMatches};
            })
            .toArray(Object[][]::new);
    }

    private String[] getRoundColumns() {
        return new String[]{
            messages.getString("TOURS"),
            messages.getString("MATCHS"),
            messages.getString("MATCH_JOUEES")
        };
    }
}