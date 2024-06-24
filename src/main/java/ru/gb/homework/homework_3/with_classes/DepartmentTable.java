package ru.gb.homework.homework_3.with_classes;

import java.sql.*;
import java.util.ArrayList;

public class DepartmentTable {
    public void createTable(Connection connection, String sqlRequest) throws SQLException{
        try (Statement statement = connection.createStatement()){
            statement.execute(sqlRequest);
        } catch (SQLException e){
            System.err.println("Во время создания таблицы Department произошла ошибка: " + e.getMessage());
            throw e;
        }
    }
    public void insertData(Connection connection, String sqlRequest, int depNum) throws SQLException{
        try (Statement statement = connection.createStatement()){
            StringBuilder insertQuery = new StringBuilder(sqlRequest);
            for (int i = 1; i <= depNum; i++) {
                insertQuery.append(String.format("(%s, '%s')", i, "Department #" + i));
                if(i != depNum){
                    insertQuery.append(",\n");
                }
            }
            int insertCount = statement.executeUpdate(insertQuery.toString());
            System.out.println("Добавлено строк в Department: " + insertCount);
        }
    }
    public ArrayList<Long> selectDepartmentIds(Connection connection, String sqlRequest) throws SQLException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sqlRequest);
            ArrayList<Long> departments = new ArrayList<>();
            while (resultSet.next()){
                long id = resultSet.getLong("id");
                departments.add(id);
            }
            return departments;
        }
    }
}
