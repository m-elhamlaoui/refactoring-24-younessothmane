package Persistence;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.Scanner;

public class Connector {
	
    String db ;
    String user ;
    String pwd;
    String url;

    Statement statement;
    
    private static Connection connection = null;

    private Connector() {
      try {
        Class.forName("org.hsqldb.jdbcDriver").newInstance();

        String dos = System.getenv("APPDATA") + "\\jBelote";
        System.out.println("Dossier de stockage:" + dos);
        System.out.println(dos);
        if( !new File(dos).isDirectory() ){
          new File(dos).mkdir();
        }
        connection = DriverManager
                .getConnection("jdbc:hsqldb:file:" + dos + "\\belote","sa","");
        statement = connection.createStatement();

        importSQL(connection, new File("create.sql"));


      }catch(SQLException e){

        JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de donn�e. Vérifier qu'une autre instance du logiciel n'est pas déjà ouverte.");
        System.out.println(e.getMessage());
        System.exit(0);
      }catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du logiciel. Vérifiez votre installation Java et vos droits d'acc�s sur le dossier AppData.");
        System.out.println(e.getMessage());
        System.exit(0);
      }

    }

    public static Connection getConnection() {
        if (connection == null)
            new Connector();
        return connection;
    }


  public static void importSQL(Connection conn, File in) throws SQLException, FileNotFoundException{
    Scanner s = new Scanner(in);
    s.useDelimiter("(;(\r)?\n)|(--\n)");
    Statement st = null;
    try
    {
      st = conn.createStatement();
      while (s.hasNext())
      {
        String line = s.next();
        if (line.startsWith("/*!") && line.endsWith("*/"))
        {
          int i = line.indexOf(' ');
          line = line.substring(i + 1, line.length() - " */".length());
        }

        if (line.trim().length() > 0)
        {
          st.execute(line);
        }
      }
    }
    finally
    {
      if (st != null) st.close();
    }
  }
}