package Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Equipe;

public class EquipeMapper implements IRowMapper<Equipe> {
    public Equipe mapRow(ResultSet rs, int rowNum) throws SQLException {
        Equipe e = new Equipe();

        e.setId(rs.getInt("id_equipe"));
        e.setNumber(rs.getInt("num_equipe"));
        e.setTournoi(rs.getInt("id_tournoi"));
        e.setJoueur1(rs.getString("nom_j1"));
        e.setJoueur2(rs.getString("nom_j2"));
        
        return e;
    }

}