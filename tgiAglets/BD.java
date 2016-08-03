package tgiAglets;

/**
 * Created on 10/07/2009
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BD {
    public static Connection connection = null;

    public static Statement statement = null;

    public static ResultSet resultSet = null;

    public static final String DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";

    public static final String URL = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=c:/meubd/ip.mdb";

     //método que faz conexão com o banco de dados retorna true se houve
     //sucesso, ou false em caso negativo
    
    public static boolean getConnection() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL);
            statement = connection
                    .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            System.out.println("Conexão OK");
            return true;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

     //Fecha ResultSet, Statement e Connection

    public static void close() {
        closeResultSet();
        closeStatement();
        closeConnection();
    }

    private static void closeConnection() {
        try {
            connection.close();
            System.out.println("Desconexão OK");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private static void closeStatement() {
        try {
            statement.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private static void closeResultSet() {
        try {
            resultSet.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


     //Carrega o resultSet com o resultado do script SQL

    public static void setResultSet(String sql) {
        try {
            resultSet = statement.executeQuery(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

     // Executa um script SQL de atualização retorna um valor inteiro contendo a
     // quantidade de linhas afetadas

    public static int runSQL(String sql) {
        int quant = 0;
        try {
            quant = statement.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return quant;
    }
}