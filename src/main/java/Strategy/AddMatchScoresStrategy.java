package Strategy;

import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import Models.Match;

import javax.swing.JOptionPane;
import Utils.Validator;


public class AddMatchScoresStrategy implements IButtonStrategy {
    private final Match match;
    private final int score1;
    private final int score2;

    public AddMatchScoresStrategy(Match match, int score1, int score2) {
        this.match = match;
        this.score1 = score1;
        this.score2 = score2;
    }

    @Override
    public Boolean execute() {
        if (Validator.validatePositiveInteger(String.valueOf(score1), "Score 1") &&
            Validator.validatePositiveInteger(String.valueOf(score2), "Score 2")) {

            match.setScore1(score1);
            match.setScore2(score2);
            match.setTermine(true);

            MatchDAO matchDAO = FactoryDAO.getMatchDAO();
            matchDAO.update(match);

            JOptionPane.showMessageDialog(null, "Scores updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true ;
        }
        return false ;
    }
}
