package DAOs;


import java.util.List;
import java.util.Vector;

import Models.Equipe;
import Models.Match;
import Models.Tournoi;

public class TournoiService {

	private String nomTournoi;
	private int statut;
	private int id_tournoi;
	private int nb_match;

	private List<Equipe> dataeq ;
	public List<Match> datam ;

	public static TournoiDAO tournoiDao = FactoryDAO.getTournoiDAO();
	public static EquipeDAO equipeDao = FactoryDAO.getEquipeDAO();
	public static MatchDAO matchDao= FactoryDAO.getMatchDAO();

	public int getIdTournoi() {return id_tournoi;}
	public void setIdTournoi(int newId) {
		id_tournoi = newId;
	}


	public int getNbMatch() {return nb_match;}
	public void setNbMatch(int newNbMatch) {
		nb_match = newNbMatch;
	}

	public String getNomTournoi() {return nomTournoi;}
	public void setNomTournoi(String newNomTournoi) {
		nomTournoi = newNomTournoi;
	}

	public int getStatut() {return statut;}
	public void setStatut(int newStatut) {
		statut = newStatut;
	}



	public TournoiService(int id, int nbMatches, String nom, int statut) {
		this.nomTournoi = nom;
		this.statut = statut;
		this.id_tournoi = id;
		this.nb_match = nbMatches;
	}



	public TournoiService(String tournoiName){

		Tournoi trn = tournoiDao.getByName(tournoiName); // gets tournoi object by name
		if(trn == null){
			return;
		}
		this.id_tournoi = trn.getId();
		this.nomTournoi = trn.getNom();
		this.statut = trn.getStatus();
		this.nb_match = trn.getNumberMatch();
		majEquipes();
		majMatchs();

	}

	public void majEquipes(){
			dataeq = equipeDao.getByTournoi(id_tournoi);
	}

	public void majMatchs(){
		datam = matchDao.getMatchsByTournoi(id_tournoi);
	}

	public Match getMatch(int index){
		if(datam == null) majMatchs();
		return datam.get(index);
	}
	public int getNbMatchs(){
		if(datam == null) majMatchs();
		return datam.size();
	}
	public Equipe getEquipe(int index){
		if(dataeq == null)
			majEquipes();
		return dataeq.get(index);

	}
	public int getNbEquipes(){
		if(dataeq == null)
			majEquipes();
		return dataeq.size();
	}

	public int getNbTours(){
		return matchDao.getNbTours(this.id_tournoi);
	}
	public void genererMatchs(){
		int nbt = 1;
		System.out.println("Nombre d'equipes : " + getNbEquipes());
		System.out.println("Nombre de tours  : " + nbt);
		Vector<Vector<Match>> matchSets = this.getMatchsToDo(getNbEquipes(), nbt);
		matchDao.insertMatchs(matchSets, this.id_tournoi);

	}

	public boolean ajouterTour() {
		// Step 1: Check if more tours can be added
		int idTournoi = this.id_tournoi;
		int nbEquipes = equipeDao.getByTournoi(idTournoi).size();
		int nbTours = tournoiDao.getNbTours(idTournoi);
		if (nbTours >= nbEquipes - 1) {
			return false; // No more tours possible
		}
	
		// Step 2: Get the current number of tours and generate matches
		MatchDAO matchDao = FactoryDAO.getMatchDAO();
		if (nbTours == 0) {
			Vector<Vector<Match>> schedule = generateMatchsToDo(nbEquipes, nbTours + 1, idTournoi);
			matchDao.insertMatchs(schedule, idTournoi);
		} else {
			List<Integer> orderedEquipes = equipeDao.getOrderedEquipes(idTournoi);
			while (orderedEquipes.size() > 1) {
				int i = 1;
				while (i < orderedEquipes.size()) {
					if (matchDao.isMatchPlayed(idTournoi, orderedEquipes.get(0), orderedEquipes.get(i))) {
						i++;
					} else {
						Match newMatch = new Match(
							0, idTournoi, nbTours + 1, orderedEquipes.get(0), orderedEquipes.get(i), 0, 0, false
						);
						matchDao.add(newMatch);
						orderedEquipes.remove(i);
						orderedEquipes.remove(0);
					}
				}
			}
		}
		return true; // Tour successfully added
	}

	private Vector<Vector<Match>> generateMatchsToDo(int numPlayers, int numRounds, int idTournoi) {
		Vector<Vector<Match>> schedule = new Vector<>();
	
		int[] playerIds = new int[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			playerIds[i] = i + 1; // Dummy IDs for simplicity
		}
	
		for (int round = 1; round <= numRounds; round++) {
			Vector<Match> roundMatches = new Vector<>();
			for (int i = 0; i < numPlayers / 2; i++) {
				int id1 = playerIds[i];
				int id2 = playerIds[numPlayers - 1 - i];
				roundMatches.add(new Match(0, idTournoi, round, id1, id2, 0, 0, false));
			}
			schedule.add(roundMatches);
		}
		return schedule;
	}
	
	
	public void supprimerTour() {
		int nbtoursav = matchDao.getNbTours(id_tournoi);
		if (nbtoursav == 0) {
			return;
		}

		matchDao.deleteByTourAndTournament(nbtoursav, nbtoursav);
	}

	public static int deleteTournoi(String nomTournoi) {
		int idTournoi = -1;

		try {
			// Retrieve tournament ID
			Tournoi tournoi = tournoiDao.getByName(nomTournoi);
			idTournoi = tournoi.getId();

			if (idTournoi == -1) {
				System.out.println("models.Tournoi not found");
				return -1;
			}

			tournoiDao.delete(idTournoi);
		} catch (Exception e) {
			System.out.println("Erreur inconnue: " + e.getMessage());
			return -1;
		}

		return 0; // Success
	}
    public static int creerTournoi(String nom, int nbMatchs, int statut) {
        Tournoi newTournoi = new Tournoi(0, nbMatchs, nom, statut);
        tournoiDao.add(newTournoi);
        return 0; // Assume success
    }

	public void ajouterEquipe() {
		int a_aj = this.dataeq.size() + 1;

		Equipe newEquipe = new Equipe(0, a_aj, id_tournoi, "Joueur 1", "Joueur 2");
		equipeDao.add(newEquipe);
	}


	public void majEquipe(int index) {
		Equipe equipe = getEquipe(index);
		equipe.setJoueur1(equipe.getJoueur1());
		equipe.setJoueur2(equipe.getJoueur2());
		
		equipeDao.update(equipe);
	}

	public void majMatch(int index) {
		Match match = getMatch(index);
		matchDao.update(match);
	}

	public void supprimerEquipe(int ideq) {
		equipeDao.delete(ideq); // Delete from DB
		dataeq.removeIf(equipe -> equipe.getId() == ideq); // Remove from the local vector
	}

	public boolean areAllMatchesTerminated() {
		for (Match match : datam) {
			if (!match.isTermine()) {  // Assuming isTermine() returns true if the match is finished
				return false;
			}
		}
		return true;
	}
	public static String mysql_real_escape_string( String str)
            throws Exception
      {
          if (str == null) {
              return null;
          }

          if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
              return str;
          }

          String clean_string = str;
          clean_string = clean_string.replaceAll("\\n","\\\\n");
          clean_string = clean_string.replaceAll("\\r", "\\\\r");
          clean_string = clean_string.replaceAll("\\t", "\\\\t");
          clean_string = clean_string.replaceAll("\\00", "\\\\0");
          clean_string = clean_string.replaceAll("'", "''");
          return clean_string;

      }

	public Vector<Vector<Match>> getMatchsToDo(int numPlayers, int numRounds){
		if (numRounds >= numPlayers) {
			throw new IllegalArgumentException("Number of rounds must be less than number of players.");
		}

		// Adjust for odd number of players
		boolean isOdd = numPlayers % 2 != 0;
		int[] playerIds = new int[isOdd ? numPlayers + 1 : numPlayers];
		int[] equipeIds = tournoiDao.getEquipesFromTournoi(id_tournoi);
		for (int i = 0; i < numPlayers; i++) {
			playerIds[i] = equipeIds[i];
		}
		if (isOdd) {
			playerIds[numPlayers] = -1; // Fictitious player for odd case
		}

		Vector<Vector<Match>> schedule = new Vector<>();
		for (int round = 1; round <= numRounds; round++) {
			rotatePlayers(playerIds, round > 1);
			schedule.add(generateRoundMatches(playerIds, round));
		}

		return schedule;
	}

	private void rotatePlayers(int[] playerIds, boolean shouldRotate) {
		if (shouldRotate) {
			int lastPlayer = playerIds[playerIds.length - 2];
			System.arraycopy(playerIds, 0, playerIds, 1, playerIds.length - 2);
			playerIds[0] = lastPlayer;
		}
	}


	private Vector<Match> generateRoundMatches(int[] playerIds, int roundNumber) {
		Vector<Match> matches = new Vector<>();
		int numPlayers = playerIds.length;
		for (int i = 0; i < numPlayers / 2; i++) {
			int id1 = playerIds[i];
			int id2 = playerIds[numPlayers - 1 - i];

			if (id1 != -1 && id2 != -1) {
				// Assuming that models.Equipe can be created or retrieved using an integer ID
				Equipe equipe1 = equipeDao.getOne(id1);
				Equipe equipe2 = equipeDao.getOne(id2);

				System.out.println(equipe1.toString());
				System.out.println(equipe2.toString());

				// Assuming default scores and termine status (e.g., 0 for scores, false for termine)
				matches.add(new Match(0, id_tournoi, equipe1.getId(), equipe2.getId(), 0, 0, roundNumber, false));
			}
		}

		return matches;
	}

}



