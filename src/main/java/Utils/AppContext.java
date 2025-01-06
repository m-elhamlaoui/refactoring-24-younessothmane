package Utils;

import Models.Tournoi;

public class AppContext {
    public static String language = "en";
    public static Tournoi currentTournament = new Tournoi(0, 0, language, 0);

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        AppContext.language = language;
    }    

    public static Tournoi getCurrentTournament() {
        return AppContext.currentTournament;
    }

    public static void setCurrentTournament(Tournoi currentTournament) {
        AppContext.currentTournament = currentTournament;
    }
}
