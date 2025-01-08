package DAOs;

import Persistence.TournoiMapper;

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


    
}
