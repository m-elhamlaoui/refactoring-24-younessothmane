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
        int numTeams = teams.size();
    
        if (teams.size()!=0 && teams.size() % 2 == 0) {
            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            Tournoi currTournoi = AppContext.getCurrentTournament();
            int round = currTournoi.getNumberMatch() / (numTeams / 2); 
            round ++; 
            matchDAO.insertMatchs(generateMatches(teams,round), tournamentId,round);
            
            AppContext.getCurrentTournament().setStatus(1);
            AppContext.getCurrentTournament().setNumberMatch((numTeams-1)*(numTeams/2));
            TournoiDAO dao = FactoryDAO.getTournoiDAO();
            dao.update(AppContext.getCurrentTournament());
        
        
            AppContext.notifyObservers();    
            JOptionPane.showMessageDialog(null, "Teams validated and matches generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true ;
        } else {
            JOptionPane.showMessageDialog(null, "Number of teams must be even!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false ;
        }
    }

    
    private Vector<Match> generateMatches(List<Equipe> teams,int round) {
        int numTeams = teams.size();
        Tournoi currTournoi = AppContext.getCurrentTournament();

        Vector<Match> roundMatches = new Vector<>();

        for (int j = 0; j < numTeams / 2; j++) {
            Match match = new Match();
            
            // Set teams
            match.setTournoi(AppContext.getCurrentTournament().getId());
            match.setEq1(teams.get(j).getId());
            match.setEq2(teams.get(numTeams - j - 1).getId());
            
            // Set round number (1-based indexing for display purposes)
            match.setNumTour(round + 1);
            
            // Initialize other match properties
            match.setScore1(0);
            match.setScore2(0);
            match.setTermine(false);
            
            roundMatches.add(match);

            System.out.println(match.getNumTour());
        }

        currTournoi.setNumberMatch(currTournoi.getNumberMatch()+numTeams/2);
        return roundMatches;

        }

        


}
