package DAOs;

import Persistence.EquipeMapper;
import Persistence.TournoiMapper;
import Utils.AppContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import Models.Equipe;
import Models.Match;
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
    public List<Equipe> getEquipes(int tournoiId){
        String query = String.format("SELECT * FROM equipes WHERE id_tournoi = %d", tournoiId);
        return jdbcTemplate.Query(query, new EquipeMapper());

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

    public int getTotalMatchesForRound(int tournoiId, int round) {
        String query = String.format("SELECT COUNT(*) FROM matchs WHERE id_tournoi = %d AND num_tour = %d", 
            tournoiId, round);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() ? 0 : result.get(0);
    }

    public int getPlayedMatchesForTournament(int tournoiId, int round) {
        String query = String.format("SELECT COUNT(*) FROM matchs WHERE id_tournoi = %d AND termine = 1 AND num_tour = %d", 
            tournoiId, round);
        List<Integer> result = jdbcTemplate.Query(query, (rs, rowNum) -> rs.getInt(1));
        return result.isEmpty() ? 0 : result.get(0);
    }

    public List<int[]> getRoundData(int tournoiId) {
        String roundQuery = String.format(
            "SELECT DISTINCT num_tour FROM matchs WHERE id_tournoi = %d ORDER BY num_tour",
            tournoiId
        );
    
        List<Integer> rounds = jdbcTemplate.Query(roundQuery, (rs, rowNum) -> rs.getInt("num_tour"));
        List<int[]> roundDataList = new ArrayList<>();
        
        for (Integer round : rounds) {
            int totalMatches = getTotalMatchesForRound(tournoiId, round);
            int playedMatches = getPlayedMatchesForTournament(tournoiId, round);
    
            int[] roundData = new int[3];
            roundData[0] = round;           // Round number
            roundData[1] = totalMatches;    // Total matches in the round
            roundData[2] = playedMatches;   // Played matches in the round
            roundDataList.add(roundData);
        }
    
        return roundDataList;
    }

    public boolean isAllMatchesPlayed(int tournoiId) {
        String query = String.format(
            "SELECT " +
            "COUNT(*) as total_matches, " +
            "SUM(CASE WHEN termine = 1 THEN 1 ELSE 0 END) as played_matches " +
            "FROM matchs WHERE id_tournoi = %d", 
            tournoiId
        );
        
        List<Object[]> result = jdbcTemplate.Query(query, (rs, rowNum) -> new Object[] {
            rs.getInt("total_matches"),
            rs.getInt("played_matches")
        });
        
        if (result.isEmpty() || result.get(0)[0] == null) {
            return false;
        }
        
        int totalMatches = (int) result.get(0)[0];
        int playedMatches = (int) result.get(0)[1];
        
        return totalMatches > 0 && totalMatches == playedMatches;
    }

    public int getNbEquipes(int trnId) {
        return this.getEquipesFromTournoi(trnId).length;
    }

    public void ajouterTour(int id, int round){
        if(round ==0 ) return;
        EquipeDAO equipeDao = FactoryDAO.getEquipeDAO() ; 
        List<Equipe> teams = equipeDao.getByTournoi(id);
    
        MatchDAO matchDAO =  FactoryDAO.getMatchDAO();
        Vector<Match> matchs = generateMatches(teams).get(round-1);
        for (Match m: matchs)
            matchDAO.add(m);
        Tournoi currTournoi = AppContext.getCurrentTournament();
        if(round ==1)
            currTournoi.setStatus(1);

        currTournoi.setNumberMatch(currTournoi.getNumberMatch()+ matchs.size());

        this.update(currTournoi);
        System.out.println("=======");
        System.out.println("Updated tournament : "+currTournoi);
        System.out.println("generated matches for round  "+round);
        for( Match m : matchs)
            System.out.println(m);
        System.out.println("=======");
    
    }

        
public Vector<Vector<Match>> generateMatches(List<Equipe> teams) {
    Vector<Vector<Match>> matchRounds = new Vector<>();
    int numTeams = teams.size();

    // Create a list of indices to rotate (excluding the first team)
    List<Integer> indices = new ArrayList<>();
    for (int i = 1; i < numTeams; i++) {
        indices.add(i);
    }

    for (int round = 0; round < numTeams - 1; round++) {
        Vector<Match> roundMatches = new Vector<>();

        // First match involves the first team and a rotated team
        Match firstMatch = new Match();
        firstMatch.setTournoi(AppContext.getCurrentTournament().getId());
        firstMatch.setEq1(teams.get(0).getId());
        firstMatch.setEq2(teams.get(indices.get(0)).getId());
        firstMatch.setNumTour(round + 1);
        firstMatch.setScore1(-1);
        firstMatch.setScore2(-1);
        firstMatch.setTermine(false);
        roundMatches.add(firstMatch);

        // Generate remaining matches for this round
        for (int j = 1; j < numTeams / 2; j++) {
            Match match = new Match();
            match.setTournoi(AppContext.getCurrentTournament().getId());
            match.setEq1(teams.get(indices.get(j)).getId());
            match.setEq2(teams.get(indices.get(numTeams - j - 1)).getId());
            match.setNumTour(round + 1);
            match.setScore1(-1);
            match.setScore2(-1);
            match.setTermine(false);
            roundMatches.add(match);
        }

        matchRounds.add(roundMatches);

        // Rotate the indices for the next round (fixed start, rotating rest)
        Collections.rotate(indices, -1);
    }

    return matchRounds;
}

}