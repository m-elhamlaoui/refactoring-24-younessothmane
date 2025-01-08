package Strategy;

import DAOs.EquipeDAO;
import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import Models.Equipe;
import Models.Match;

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

        if (teams.size() % 2 == 0) {
            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            matchDAO.insertMatchs(generateMatches(teams), tournamentId);

            JOptionPane.showMessageDialog(null, "Teams validated and matches generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true ;
        } else {
            JOptionPane.showMessageDialog(null, "Number of teams must be even!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false ;
        }
    }

    private Vector<Vector<Match>> generateMatches(List<Equipe> teams) {
        Vector<Vector<Match>> matchRounds = new Vector<>();

        for (int i = 0; i < teams.size() - 1; i++) {
            Vector<Match> matches = new Vector<>();
            for (int j = 0; j < teams.size() / 2; j++) {
                Match match = new Match();
                match.setEq1(teams.get(j).getId());
                match.setEq2(teams.get(teams.size() - j - 1).getId());
                matches.add(match);
            }

            matchRounds.add(matches);

            Equipe lastTeam = teams.get(teams.size() - 1);
            teams.remove(teams.size() - 1);
            teams.add(1, lastTeam);
        }

        return matchRounds;
    }
}
