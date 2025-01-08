package Strategy;

import DAOs.EquipeDAO;
import Models.Equipe;
import DAOs.FactoryDAO;
import Utils.Validator;

import javax.swing.JOptionPane;


public class AddTeamStrategy implements IButtonStrategy {
    private final String teamName1;
    private final String teamName2;
    private final int tournamentId;

    public AddTeamStrategy(String teamName1, String teamName2, int tournamentId) {
        this.teamName1 = teamName1;
        this.teamName2 = teamName2;
        this.tournamentId = tournamentId;
    }

    @Override
    public Boolean execute() {
        if (Validator.validateString(teamName1, "Team Member 1") && Validator.validateString(teamName2, "Team Member 2")) {
            EquipeDAO equipeDAO = FactoryDAO.getEquipeDAO();
            Equipe newTeam = new Equipe();
            newTeam.setJoueur1(teamName1);
            newTeam.setJoueur2(teamName2);
            newTeam.setTournoi(tournamentId);

            equipeDAO.add(newTeam);
           // JOptionPane.showMessageDialog(null, "Team added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
           return true ;
        }
        return false ; 
    }
}
