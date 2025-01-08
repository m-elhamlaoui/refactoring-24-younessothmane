package Strategy;

import DAOs.FactoryDAO;
import DAOs.TournoiDAO;
import Models.Tournoi;

import javax.swing.JOptionPane;

import Utils.Validator;

public class DeleteTournamentStrategy implements IButtonStrategy {
    private final String tournamentName;

    public DeleteTournamentStrategy(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    @Override
    public Boolean execute() {
        if (Validator.validateString(tournamentName, "Tournament Name")) {
            TournoiDAO tournoiDAO = FactoryDAO.getTournoiDAO();
            
            Tournoi tournamentToDelete = tournoiDAO.getByName(tournamentName);

            if (tournamentToDelete != null) {
                tournoiDAO.delete(tournamentToDelete.getId());
                return true ;
            } else {
                return false ;
            }
        }
        return false;
    }
}
