package Utils;

import Models.Tournoi;
import Observer.IObserver;

import java.util.ArrayList;
import java.util.List;
public class AppContext  {
    private static String language;
    private static Tournoi currentTournament;
    private static final List<IObserver> observers = new ArrayList<>();

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        if (language.equals("eng")) {
            setCurrentTournament(new Tournoi(-1, 0, "No selected Tournament", 0));
        } else {
            setCurrentTournament(new Tournoi(-1, 0, "aucune tournois est choisi", 0));
        }
        AppContext.language = language;
    }

    public static Tournoi getCurrentTournament() {
        return currentTournament;
    }

    public static void setCurrentTournament(Tournoi tournament) {
        currentTournament = tournament;
        System.out.println("in AppContext " +tournament.getStatus());
        notifyObservers();
    }

    public static void addObserver(IObserver observer) {
        observers.add(observer);
    }

    public static void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    public static void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }
}
