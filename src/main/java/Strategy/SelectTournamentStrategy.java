package Strategy;

import DAOs.FactoryDAO;
import DAOs.TournoiDAO;

import Views.MainFrame;

import Models.Tournoi;

import Utils.AppContext;
import Utils.Validator;

import javax.swing.JOptionPane;

public class SelectTournamentStrategy implements IButtonStrategy {
    private final MainFrame frame;
    private final String tournamentName;

    public SelectTournamentStrategy(MainFrame frame, String tournamentName) {
        this.frame = frame;
        this.tournamentName = tournamentName;
    }

    @Override
    public Boolean execute() {
        if (Validator.validateString(tournamentName, "Tournament Name")) {
            TournoiDAO tournoiDAO = FactoryDAO.getTournoiDAO();
            Tournoi selectedTournament = tournoiDAO.getByName(tournamentName);

            if (selectedTournament != null) {
                AppContext.setCurrentTournament(selectedTournament);
                System.out.println(selectedTournament);
                frame.navigateTo("TOURNAMENT_DETAILS");
                return true ;
            } else {
                return false  ; 
            }
        }
        return false ; 
    }
}
