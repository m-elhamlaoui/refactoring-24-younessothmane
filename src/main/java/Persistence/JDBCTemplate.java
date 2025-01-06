package Persistence;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class JDBCTemplate {

    private Connection connector;
    private static JDBCTemplate instance;

    private JDBCTemplate(){
        this.connector = Connector.getConnection();
    }

    public void Query(String query){
        try {
            connector.createStatement().execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> Query(String query, IRowMapper<T> rowMapper){
        ArrayList<T> list = new ArrayList<>();
        
        try {
            int i = 0;
            ResultSet rs = connector.createStatement().executeQuery(query);

            while(rs.next()){
                list.add(rowMapper.mapRow(rs, i));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static JDBCTemplate getInstance(){
        if(instance == null){
            instance = new JDBCTemplate();
        }
        return instance;
    }
    
}
