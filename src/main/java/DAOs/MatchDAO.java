package DAOs;


import Persistence.MatchMapper;

import java.util.List;
import java.util.Vector;

import Models.Match;

public class MatchDAO extends AbstractDAO<Match> {

    @Override
    public void add(Match match) {
        if (match == null) {
            System.out.println("Cannot add a null match");
            return;
        }

        String query = String.format(
                "INSERT INTO matchs (id_tournoi, num_tour, equipe1, equipe2, score1, score2, termine) " +
                        "VALUES (%d, %d, %d, %d, %d, %d, %d)",
                match.getTournoi(),
                match.getNumTour(),
                match.getEq1(),
                match.getEq2(),
                match.getScore1(),
                match.getScore2(),
                match.isTermine() ? 1 : 0
        );

        jdbcTemplate.Query(query);
    }

    public void insertMatchs(Vector<Vector<Match>> matchSets, int id_tournoi) {
      for (int roundNumber = 1; roundNumber <= matchSets.size(); roundNumber++) {
          for (Match match : matchSets.get(roundNumber - 1)) {
              add(new Match(0, id_tournoi, roundNumber, match.getEq1(), match.getEq2(), 0, 0, false));
          }
      }
  }

    @Override
    public Match getOne(int id) {
        String query = String.format("SELECT * FROM matchs WHERE id_match = %d", id);
        List<Match> result = jdbcTemplate.Query(query, new MatchMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void delete(int id) {
        String query = String.format("DELETE FROM matchs WHERE id_match = %d", id);
        jdbcTemplate.Query(query);
    }

    @Override
    public void update(Match match) {
        if (match == null) {
            System.out.println("Cannot update a null match");
            return;
        }

        String query = String.format(
                "UPDATE matchs SET id_tournoi = %d, num_tour = %d, equipe1 = %d, equipe2 = %d, score1 = %d, score2 = %d, termine = %d " +
                        "WHERE id_match = %d",
                match.getTournoi(),
                match.getNumTour(),
                match.getEq1(),
                match.getEq2(),
                match.getScore1(),
                match.getScore2(),
                match.isTermine() ? 1 : 0,
                match.getMatch()
        );

        jdbcTemplate.Query(query);
    }

    @Override
    public List<Match> getAll() {
        String query = "SELECT * FROM matchs";
        return jdbcTemplate.Query(query, new MatchMapper());
    }

    public List<Match> getMatchsByTournoi(int tournoi) {
        String query = String.format("SELECT * FROM matchs WHERE id_tournoi = %d", tournoi);
        return jdbcTemplate.Query(query, new MatchMapper());
    }

    public int getNbTours(int tournoi) {
        String query = String.format("SELECT MAX(num_tour) FROM matchs WHERE id_tournoi = %d", tournoi);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() ? -1 : result.get(0);
    }

    public int[] getMatchStatusByTournoi(int tournoiId) {
        String query = String.format(
                "SELECT COUNT(*) AS total, " +
                        "(SELECT COUNT(*) FROM matchs m2 WHERE m2.id_tournoi = %d AND m2.termine = 1) AS termines " +
                        "FROM matchs m WHERE m.id_tournoi = %d GROUP BY id_tournoi",
                tournoiId, tournoiId
        );

        List<int[]> result = jdbcTemplate.Query(query, (rs, numRow) -> new int[]{rs.getInt("total"), rs.getInt("termines")});
        return result.isEmpty() ? new int[]{0, 0} : result.get(0);
    }

    public boolean isMatchPlayed(int idTournoi, int equipe1, int equipe2) {
        String query = String.format(
            "SELECT COUNT(*) FROM matchs WHERE id_tournoi = %d " +
            "AND ((equipe1 = %d AND equipe2 = %d) OR (equipe1 = %d AND equipe2 = %d))",
            idTournoi, equipe1, equipe2, equipe2, equipe1
        );
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return !result.isEmpty() && result.get(0) > 0;
    }
    
    public void deleteByTourAndTournament(int idTournoi, int nbtoursav) {
        jdbcTemplate.Query(String.format("DELETE FROM matchs WHERE id_tournoi = %d AND num_tour = %d", idTournoi, nbtoursav));
    }
}
