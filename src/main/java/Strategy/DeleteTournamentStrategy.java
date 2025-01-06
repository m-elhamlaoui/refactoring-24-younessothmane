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
    public void execute() {
        if (Validator.validateString(tournamentName, "Tournament Name")) {
            TournoiDAO tournoiDAO = FactoryDAO.getTournoiDAO();
            Tournoi tournamentToDelete = tournoiDAO.getByName(tournamentName);

            if (tournamentToDelete != null) {
                tournoiDAO.delete(tournamentToDelete.getId());
                JOptionPane.showMessageDialog(null, "Tournament deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Tournament not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
