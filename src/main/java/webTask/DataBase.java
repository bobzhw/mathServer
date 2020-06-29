package webTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DataBase {
    private Connection con;
    public DataBase(String database){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://192.168.1.9:3306/"+database+"?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT";
            String username = "root" ;
            String password = "woaizxl" ;
            con = DriverManager.getConnection(url , username , password );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
//    public static  DataBase getInstance(){
//        return dataBase;
//    }

    public ResultSet query(String sql) throws Exception{
        return con.createStatement().executeQuery(sql);
    }

    public void execute(String sql) throws Exception{
        con.createStatement().execute(sql);
    }

//    public static void main(String[] args) {
//        DataBase dataBase = DataBase.getInstance();
//    }
}
