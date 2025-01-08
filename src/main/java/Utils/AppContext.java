package Utils;

import Models.Tournoi;

public class AppContext {
    public static String language ;
    public static Tournoi currentTournament ;;

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        if(language.equals("eng"))
            AppContext.setCurrentTournament( new Tournoi(-1, 0, "No selected Tournament", 0));
        else 
            AppContext.setCurrentTournament( new Tournoi(-1, 0, "aucune tournois est choisi", 0));
          
        AppContext.language = language;
    }    

    public static Tournoi getCurrentTournament() {
        return AppContext.currentTournament;
    }

    public static void setCurrentTournament(Tournoi currentTournament) {
        AppContext.currentTournament = currentTournament;
    }
}
