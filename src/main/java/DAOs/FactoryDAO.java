package DAOs;

public final class FactoryDAO{

    private FactoryDAO() {
        //prevent instantiation
    }

    public static MatchDAO getMatchDAO() {
        return MatchDAOHolder.INSTANCE;
    }

    public static EquipeDAO getEquipeDAO() {
        return EquipeDAOHolder.INSTANCE;
    }

    public static TournoiDAO getTournoiDAO() {
        return TournoiDAOHolder.INSTANCE;
    }

    private static class MatchDAOHolder {
        private static final MatchDAO INSTANCE = new MatchDAO();
    }

    private static class EquipeDAOHolder {
        private static final EquipeDAO INSTANCE = new EquipeDAO();
    }

    private static class TournoiDAOHolder {
        private static final TournoiDAO INSTANCE = new TournoiDAO();
    }

}