package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.List;

import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import DAOs.TournoiService;
import Models.Match;
import Models.Tournoi;
import Utils.AppContext;

public class RoundManagementPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JTable roundTable;

    public RoundManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
    
        JLabel header = new JLabel(messages.getString("TOURS"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);
    
        // Initialize the table
        roundTable = new JTable(new DefaultTableModel(getRoundData(), getRoundColumns()));
        JScrollPane scrollPane = new JScrollPane(roundTable);
        add(scrollPane, BorderLayout.CENTER);
    
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addRoundButton = new JButton(messages.getString("AJOUTER_TOUR"));
        JButton deleteRoundButton = new JButton(messages.getString("SUPP_TOUR"));
    
        buttonPanel.add(addRoundButton);
        buttonPanel.add(deleteRoundButton);
        add(buttonPanel, BorderLayout.SOUTH);
    
        // Button actions
        addRoundButton.addActionListener(e -> {
            // Add round logic
            Tournoi curr = AppContext.getCurrentTournament();

            TournoiService currentTournament = new TournoiService(
                curr.getId(),
                curr.getNumberMatch(),
                curr.getNom(),
                curr.getStatus()
            );
            
            if (currentTournament != null) {
                MatchDAO matchDAO = FactoryDAO.getMatchDAO();
                int totalRounds = currentTournament.getNbTours();
                boolean allMatchesCompleted = matchDAO.getMatchStatusByTournoi(curr.getId())[0] ==
                                              matchDAO.getMatchStatusByTournoi(curr.getId())[1];
    
                if (allMatchesCompleted && totalRounds < currentTournament.getNbEquipes() - 1) {
                    currentTournament.ajouterTour();
                    JOptionPane.showMessageDialog(this, messages.getString("AJ_TOUR_M"), messages.getString("RESULTATS"), JOptionPane.INFORMATION_MESSAGE);
                    refreshRoundsTable();
                } else {
                    JOptionPane.showMessageDialog(this, messages.getString("IMPAIR"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, messages.getString("TOURNOI_NON_SELECTIONNE"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        });
    
        deleteRoundButton.addActionListener(e -> {
            // Delete round logic
            Tournoi curr = AppContext.getCurrentTournament();

            TournoiService currentTournament = new TournoiService(
                curr.getId(),
                curr.getNumberMatch(),
                curr.getNom(),
                curr.getStatus()
            );

            if (currentTournament != null) {
                if (currentTournament.getNbTours() > 1) {
                    currentTournament.supprimerTour();
                    JOptionPane.showMessageDialog(this, messages.getString("SUPP_TOUR"), messages.getString("RESULTATS"), JOptionPane.INFORMATION_MESSAGE);
                    refreshRoundsTable();
                } else {
                    JOptionPane.showMessageDialog(this, messages.getString("NB_TOUR"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, messages.getString("TOURNOI_NON_SELECTIONNE"), messages.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void refreshRoundsTable() {
        DefaultTableModel tableModel = new DefaultTableModel(getRoundData(), getRoundColumns());
        roundTable.setModel(tableModel);
    }
    
    private Object[][] getRoundData() {
        Tournoi currentTournament = AppContext.getCurrentTournament();
        if (currentTournament == null) {
            return new Object[][]{};
        }

        MatchDAO matchDAO = FactoryDAO.getMatchDAO();
        Vector<Vector<Object>> roundData = new Vector<>();

        try {
            List<Match> matches = matchDAO.getMatchsByTournoi(currentTournament.getId());

            matches.stream()
                    .collect(Collectors.groupingBy(Match::getNumTour))
                    .forEach((round, matchList) -> {
                        int totalMatches = matchList.size();
                        int completedMatches = (int) matchList.stream().filter(Match::isTermine).count();

                        Vector<Object> roundInfo = new Vector<>();
                        roundInfo.add(round);
                        roundInfo.add(totalMatches);
                        roundInfo.add(completedMatches);
                        roundData.add(roundInfo);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roundData.stream().map(Vector::toArray).toArray(Object[][]::new);
    }


    private String[] getRoundColumns() {
        return new String[]{
            messages.getString("TOURS"),
            messages.getString("MATCHS"),
            messages.getString("MATCH_JOUEES")
        };
    }
}
