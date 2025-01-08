package Strategy;

import DAOs.TournoiDAO;
import Models.Tournoi;

import javax.swing.JOptionPane;

import DAOs.FactoryDAO;
import Utils.Validator;


public class CreateTournamentStrategy implements IButtonStrategy {
    private final String tournamentName;

    public CreateTournamentStrategy(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    @Override
    public Boolean execute() {
        if (Validator.validateString(tournamentName, "Tournament Name")) {
            TournoiDAO tournoiDAO = FactoryDAO.getTournoiDAO();
            Tournoi newTournament = new Tournoi();
            newTournament.setNom(tournamentName);
            newTournament.setStatus(0); 
            newTournament.setNumberMatch(0);

            tournoiDAO.add(newTournament);
            return true ; 
        }
        return false ;
    }
}
