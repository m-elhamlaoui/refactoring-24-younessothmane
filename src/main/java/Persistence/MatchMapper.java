package Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Match;

public class MatchMapper implements IRowMapper<Match> {
    public Match mapRow(ResultSet rs, int rowNum) throws SQLException {
        Match m = new Match();

        m.setMatch(rs.getInt("id_match"));
        m.setTournoi(rs.getInt("id_tournoi"));
        m.setNumTour(rs.getInt("num_tour"));

        m.setEq1(rs.getInt("equipe1"));
        m.setEq2(rs.getInt("equipe2"));

        m.setScore1(rs.getInt("score1"));
        m.setScore2(rs.getInt("score2"));

        m.setTermine(rs.getBoolean("termine"));
        
        return m;
    }

}