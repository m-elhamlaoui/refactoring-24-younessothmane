package Strategy;

import DAOs.EquipeDAO;
import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import DAOs.TournoiDAO;
import Models.Equipe;
import Models.Match;
import Models.Tournoi;
import Utils.AppContext;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Vector;

public class ValidateTeamsStrategy implements IButtonStrategy {
    private final int tournamentId;
    
    public ValidateTeamsStrategy(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public Boolean execute() {
        EquipeDAO equipeDAO = FactoryDAO.getEquipeDAO();
        List<Equipe> teams = equipeDAO.getByTournoi(tournamentId);
    
        if (teams.size()!=0 && teams.size() % 2 == 0) {
            TournoiDAO tournoiDAO =  FactoryDAO.getTournoiDAO();

            
            Tournoi currTournoi = AppContext.getCurrentTournament();
            tournoiDAO.ajouterTour(currTournoi.getId(), 1);

            

            AppContext.notifyObservers();   

            JOptionPane.showMessageDialog(null, "Teams validated and matches generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true ;
        } else {
            JOptionPane.showMessageDialog(null, "Number of teams must be even!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false ;
        }
    }

    
 


}
