package Models;

public class Tournoi {

    private int id;
    private int numberMatch;
    private String nom;
    private int status; 

    public Tournoi() {

    }

    public Tournoi(int id, int numberMatch, String nom, int status) {
        this.id = id;
        this.numberMatch = numberMatch;
        this.nom = nom;
        this.status = status;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
    	this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
    	this.id = id;
    }

    public int getNumberMatch() {
        return numberMatch;
    }

    public void setNumberMatch(int numberMatch) {
    	this.numberMatch = numberMatch;
    }

    public String getStatutName(){
		String statusNom = "Inconnu";
		switch(this.status){
			case 0:
                statusNom = "Inscription des joueurs";
				break;
			case 1:
                statusNom = "Génération des matchs";
				break;
			case 2:
                statusNom = "Matchs en cours";
				break;
			case 3:
                statusNom = "Terminé";
				break;
			default:
                statusNom = "Statut non reconnu";

		}
		return statusNom;
	}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
    	this.status = status;
    }

    @Override
    public String toString() {
        return "Tournoi [id=" + id + ", numberMatch=" + numberMatch + ", nom=" + nom + ", status=" + status + "]";
    }
    
}
