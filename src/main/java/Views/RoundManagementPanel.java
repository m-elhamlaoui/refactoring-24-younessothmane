package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
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

    public RoundManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        AppContext.addObserver(this);
    }

    @Override
    public void update() {
        refreshRoundsTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("TOURS"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        roundTable = new JTable(new DefaultTableModel(getRoundData(), getRoundColumns()));
        add(new JScrollPane(roundTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addRoundButton = new JButton(messages.getString("AJOUTER_TOUR"));
        JButton deleteRoundButton = new JButton(messages.getString("SUPP_TOUR"));

        buttonPanel.add(addRoundButton);
        buttonPanel.add(deleteRoundButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addRoundButton.addActionListener(e -> handleAddRound());
        deleteRoundButton.addActionListener(e -> handleDeleteRound());
    }

    private void handleAddRound() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament != null) {
            TournoiDAO  tournoiDAO = FactoryDAO.getTournoiDAO();

            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            int trnId = AppContext.getCurrentTournament().getId();
            int totalRounds = tournoiDAO.getNbTours(trnId);
            boolean allMatchesCompleted = matchDAO.getMatchStatusByTournoi(currentTournament.getId())[0] ==
                                          matchDAO.getMatchStatusByTournoi(currentTournament.getId())[1];

            if (allMatchesCompleted && totalRounds < tournoiDAO.getNbEquipes(trnId) - 1) {
                tournoiDAO.ajouterTour(trnId);
                refreshRoundsTable();
            } else {
                JOptionPane.showMessageDialog(this, messages.getString("IMPAIR"), messages.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, messages.getString("TOURNOI_NON_SELECTIONNE"), messages.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteRound() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament != null) {
            TournoiDAO  tournoiDAO = FactoryDAO.getTournoiDAO();

            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            int trnId = AppContext.getCurrentTournament().getId();
            int totalRounds = tournoiDAO.getNbTours(trnId);
            boolean allMatchesCompleted = matchDAO.getMatchStatusByTournoi(currentTournament.getId())[0] ==
                                          matchDAO.getMatchStatusByTournoi(currentTournament.getId())[1];

            if (tournoiDAO.getNbTours(trnId) > 1) {
                //tournoiDAO.supprimerTour();
                JOptionPane.showMessageDialog(this, messages.getString("SUPP_TOUR"), messages.getString("RESULTATS"), JOptionPane.INFORMATION_MESSAGE);
                refreshRoundsTable();
            } else {
                JOptionPane.showMessageDialog(this, messages.getString("NB_TOUR"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, messages.getString("TOURNOI_NON_SELECTIONNE"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshRoundsTable() {
        roundTable.setModel(new DefaultTableModel(getRoundData(), getRoundColumns()));
    }

    private Object[][] getRoundData() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament == null) {
            return new Object[][]{};
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
                int completedMatches = (int) matchList.stream().filter(Match::isTermine).count();
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
