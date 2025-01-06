package Models;

public class Equipe {

    private int tournoi;
    private int id;
    private int number;
    private String joueur1, joueur2;

    
    public Equipe() {

    }

    public Equipe(int id, int number, int tournoi, String joueur1, String joueur2) {
        this.id = id;
        this.number = number;
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.tournoi = tournoi;
    }

    public int getTournoi() {
        return tournoi;
    }

    public void setTournoi(int tournoi) {
        this.tournoi = tournoi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getJoueur1() {
        return joueur1;
    }

    public void setJoueur1(String j1) {
        this.joueur1 = j1;
    }

    public String getJoueur2() {
        return joueur2;
    }

    public void setJoueur2(String j2) {
        this.joueur2 = j2;
    }

    @Override
    public String toString() {
        return "Equipe[" +
                "id=" + id +
                ", number=" + number +
                ", joueur1='" + joueur1 + '\'' +
                ", joueur2='" + joueur2 + '\'' +
        ']';
    }
}
