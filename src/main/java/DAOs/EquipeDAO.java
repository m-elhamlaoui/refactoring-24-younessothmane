package DAOs;

import Persistence.EquipeMapper;
import Persistence.MatchMapper;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import Models.Equipe;
import Models.Match;

public class EquipeDAO extends AbstractDAO<Equipe> {

    @Override
    public void add(Equipe obj) {
        if (obj == null) {
            System.out.println("Cannot add a null object");
            return;
        }

        String query = String.format(
                "INSERT INTO equipes(num_equipe, id_tournoi, nom_j1, nom_j2) VALUES (%d, %d, '%s', '%s')",
                obj.getNumber(), obj.getTournoi(), obj.getJoueur1(), obj.getJoueur2()
        );

        jdbcTemplate.Query(query);
    }

    @Override
    public void delete(int id) {
        String query = String.format("DELETE FROM equipes WHERE id_equipe = %d", id);
        jdbcTemplate.Query(query);
    }

    @Override
    public List<Equipe> getAll() {
        String query = "SELECT * FROM equipes";
        return jdbcTemplate.Query(query, new EquipeMapper());
    }

    @Override
    public void update(Equipe obj) {
        if (obj == null) {
            System.out.println("Cannot update a null object");
            return;
        }

        String query = String.format(
                "UPDATE equipes SET num_equipe = %d, id_tournoi = %d, nom_j1 = '%s', nom_j2 = '%s' WHERE id_equipe = %d",
                obj.getNumber(), obj.getTournoi(), obj.getJoueur1(), obj.getJoueur2(), obj.getId()
        );

        jdbcTemplate.Query(query);
    }

    @Override
    public Equipe getOne(int idEquipe) {
        String query = String.format("SELECT * FROM equipes WHERE id_equipe = %d", idEquipe);
        List<Equipe> result = jdbcTemplate.Query(query, new EquipeMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Equipe> getByTournoi(int tournamentID) {
        String query = String.format("SELECT * FROM equipes WHERE id_tournoi = %d ORDER BY num_equipe", tournamentID);
        return jdbcTemplate.Query(query, new EquipeMapper());
    }

    public void deleteByTournoi(int idTournoi) {
        String query = String.format("DELETE FROM equipes WHERE id_tournoi = %d", idTournoi);
        jdbcTemplate.Query(query);
    }

    public List<Integer> getOrderedEquipes(int idTournoi) {
        String query = String.format(
            "SELECT equipe FROM (" +
                "SELECT equipe1 AS equipe FROM matchs WHERE id_tournoi = %d " +
                "UNION " +
                "SELECT equipe2 AS equipe FROM matchs WHERE id_tournoi = %d" +
            ") ORDER BY equipe",
            idTournoi, idTournoi
        );
        return jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
    }

    public Vector<Integer> getTeamsNumbersByMatch(int matchId) {
        String query = String.format(
            "SELECT e.* " +
            "FROM matchs m " +
            "JOIN equipes e ON e.id_equipe = m.equipe1 OR e.id_equipe = m.equipe2 " +
            "WHERE m.id_match = %d " +
            "ORDER BY CASE " +
            "    WHEN e.id_equipe = m.equipe1 THEN 1 " +
            "    WHEN e.id_equipe = m.equipe2 THEN 2 " +
            "END"
        ,matchId
        );
        
    
        List<Equipe> teams = jdbcTemplate.Query(query, new EquipeMapper());
        
        Vector<Integer> teamNumbers = new Vector<>();
        for (Equipe team : teams) {
            teamNumbers.add(team.getNumber());
        }
        
        return teamNumbers;
    }


    
}
