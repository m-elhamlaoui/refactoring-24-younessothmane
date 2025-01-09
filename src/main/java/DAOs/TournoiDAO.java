package DAOs;

import Persistence.TournoiMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Models.Tournoi;

public class TournoiDAO extends AbstractDAO<Tournoi> {

    @Override
    public void add(Tournoi obj) {
        if (obj == null) {
            System.out.println("Cannot add a null tournament");
            return;
        }

        String query = String.format(
                "INSERT INTO tournois (nb_matchs, nom_tournoi, statut_ ) VALUES (%d, '%s', %d)",
                obj.getNumberMatch(), obj.getNom(), obj.getStatus()
        );

        jdbcTemplate.Query(query);
    }

    public int[] getEquipesFromTournoi(int tournoiId) {
        String query = String.format("SELECT id_equipe FROM equipes WHERE id_tournoi = %d", tournoiId);

        List<Integer> equipeIds = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt("id_equipe"));

        return equipeIds.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public void delete(int id) {
        String query = String.format("DELETE FROM tournois WHERE id_tournoi = %d", id);
        jdbcTemplate.Query(query);
    }

    @Override
    public Tournoi getOne(int id) {
        String query = String.format("SELECT * FROM tournois WHERE id_tournoi = %d", id);
        List<Tournoi> result = jdbcTemplate.Query(query, new TournoiMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Tournoi> getAll() {
        String query = "SELECT * FROM tournois";
        return jdbcTemplate.Query(query, new TournoiMapper());
    }

    @Override
    public void update(Tournoi obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Invalid Tournoi object provided for update.");
        }

        String query = String.format(
                "UPDATE tournois SET nom_tournoi = '%s', statut_ = %d, nb_matchs = %d WHERE id_tournoi = %d",
                obj.getNom(), obj.getStatus(), obj.getNumberMatch(), obj.getId()
        );

        jdbcTemplate.Query(query);
    }

    public Tournoi getByName(String tournoiName) {


        String query = String.format("SELECT * FROM tournois WHERE nom_tournoi = '%s'", tournoiName);
        List<Tournoi> result = jdbcTemplate.Query(query, new TournoiMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    public Vector<String> getTournoiNames() {
        String query = "SELECT nom_tournoi FROM tournois";
        List<String> tournoiNames = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getString("nom_tournoi"));
        return new Vector<>(tournoiNames);
    }

    public int getNbTours(int tournoiId) {
        String query = String.format("SELECT MAX(num_tour) FROM matchs WHERE id_tournoi = %d", tournoiId);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() || result.get(0) == null ? 0 : result.get(0);
    }

    public int getTotalMatchesForTournament(int tournoiId) {
        String query = String.format("SELECT COUNT(*) FROM matchs WHERE id_tournoi = %d", tournoiId);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() ? 0 : result.get(0);
    }

    // Method to calculate the number of played matches in a tournament
    public int getPlayedMatchesForTournament(int tournoiId) {
        String query = String.format("SELECT COUNT(*) FROM matchs WHERE id_tournoi = %d AND termine = 1", tournoiId);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() ? 0 : result.get(0);
    }

public List<int[]> getRoundData(int tournoiId) {
    String query = String.format("SELECT num_tour, COUNT(*) AS total_matches, SUM(CASE WHEN termine = 1 THEN 1 ELSE 0 END) AS played_games FROM matchs WHERE id_tournoi = %d GROUP BY num_tour", tournoiId);

    // Use a List to hold the round data as int arrays
    List<int[]> roundDataList = new ArrayList<>();

    // Execute the query and populate the list with int arrays
    List<Object[]> result = jdbcTemplate.Query(query, (rs, rowNum) -> new Object[] {
        rs.getInt("num_tour"),
        rs.getInt("total_matches"),
        rs.getInt("played_games")
    });

    // Convert the result into a List<int[]> where each entry is an array of ints
    for (Object[] row : result) {
        int[] roundData = new int[3];  // Array to store round data (round number, total matches, played games)
        roundData[0] = (int) row[0];   // Round number
        roundData[1] = (int) row[1];   // Total matches
        roundData[2] = (int) row[2];   // Played games
        roundDataList.add(roundData);
    }

    return roundDataList;
}

    public boolean isAllMatchesPlayed(int tournoiId) {
        int totalMatches = getTotalMatchesForTournament(tournoiId);
        int playedMatches = getPlayedMatchesForTournament(tournoiId);
        
        // Check if there are any matches in the tournament
        if (totalMatches == 0) {
            return false;
        }
        
        return totalMatches == playedMatches;
    }

    
}
