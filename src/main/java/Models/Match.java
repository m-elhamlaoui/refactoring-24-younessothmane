package Models;

public class Match {
	
    private int match, tournoi;
    private int numTour;
    private int score1, score2; 

    private int eq1, eq2;
    private boolean termine;

    public Match() {

    }

    public Match(int match, int tournoi, int equipe1, int equipe2, int score1, int score2, int numTour, boolean termine) {
        this.match = match;
        this.tournoi = tournoi;
        this.eq1 = equipe1;
        this.eq2 = equipe2;
        this.score1 = score1;
        this.score2 = score2;
        this.numTour = numTour;
        this.termine = termine;
    }

    public int getTournoi() {
        return tournoi;
    }

    public void setTournoi(int tournoi) {
    	this.tournoi = tournoi;
    }
    
    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
    	this.match = match;
    }
    public int getScore1() {
        return score1;
    }
    public void setScore1(int score) {
    	score1 = score;	
    }
    public int getScore2() {
        return score2;
    }
    public void setScore2(int score) {
    	score2 = score;
    }

    public int getNumTour() {
        return numTour;
    }
    public void setNumTour(int nTour) {
    	numTour = nTour;
    }

    public boolean isTermine() {
        return termine;
    }

    public void setTermine(boolean state) {
    	termine = state;
    }
    public int getEq1() {
        return eq1;
    }
    public void setEq1(int eq) {
    	eq1 = eq;
    }
    public int getEq2() {
        return eq2;
    }
    public void setEq2(int eq) {
    	eq2 = eq;
    }
    
    @Override
    public String toString() {
        return (eq1 < eq2) ? "  " + eq1 + " contre " + eq2 : "  " + eq2 + " contre " + eq1;
    }

}
