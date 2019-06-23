package com.company;



import com.sun.media.jfxmedia.logging.Logger;

import java.sql.*;
import java.util.List;

public class Database {
    private static String manufacturer = "manufacturer";
    private static String screenSize = "screenSize";
    private static String resolution = "resolution";
    private static String screenCoating = "screenCoating";
    private static String touchPad = "touchPad";
    private static String cpu = "cpu";
    private static String coreCount = "coreCount";
    private static String clockSpeed = "clockSpeed";
    private static String ram = "ram";
    private static String driveCapacity = "driveCapacity";
    private static String driveType = "driveType";
    private static String gpu = "gpu";
    private static String gpuRam = "gpuRam";
    private static String os = "os";
    private static String opticalDrive = "opticalDrive";

    private static Connection connection;

    private static String DatabaseDriver = "com.mysql.jdbc.Driver";

    private static String DatabaseUrl = "jdbc:mysql://localhost/is";

    private static String DatabaseUser = "root";

    private static String DatabasePassword = "";

    public void createConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/is?serverTimezone=UTC", "root", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet getAllRecords()  {
        Statement query = null;
        ResultSet resultSet = null;
        try {
            query = connection.createStatement();
            resultSet = query.executeQuery("select * from laptopy");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public void insertRecord(String query){
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet prepareStatement(String sqlQuery) {
        ResultSet myDbResultSet = null;
        if (connection == null) {
            throw new IllegalStateException("Cannot prepare statement without CONNECTION to database");
        }
        try {
            Statement myDbStatement = connect().createStatement();
            myDbResultSet = myDbStatement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myDbResultSet;
    }

    public static Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DatabaseDriver);
                connection = DriverManager.getConnection(DatabaseUrl, DatabaseUser, DatabasePassword);
                Logger.logMsg(0, "Database is connected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public int countRecords(String nazwaProducenta) throws SQLException {
        ResultSet resultSet = null;
        try {
            Statement query = null;
            query = connection.createStatement();
            resultSet = query.executeQuery("CALL `countLaptop`('"+nazwaProducenta+"');");
            if(resultSet.next()){
                int liczba = resultSet.getInt(1);
                return liczba;
            }else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countRecordsRes(String rozdzcEkranuBox) throws SQLException {
        ResultSet resultSet = null;
        try {
            Statement query = null;
            query = connection.createStatement();
            resultSet = query.executeQuery("CALL `countResolution`('"+rozdzcEkranuBox+"');");
            if(resultSet.next()){
                int liczba = resultSet.getInt(1);
                return liczba;
            }else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
