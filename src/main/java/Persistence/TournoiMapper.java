package Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Tournoi;

public class TournoiMapper implements IRowMapper<Tournoi> {
    public Tournoi mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tournoi t = new Tournoi();

        t.setId(rs.getInt("id_tournoi"));
        t.setNom(rs.getString("nom_tournoi"));
        t.setNumberMatch(rs.getInt("nb_matchs"));
        t.setStatus(rs.getInt("status"));
        
        return t;
    }

}