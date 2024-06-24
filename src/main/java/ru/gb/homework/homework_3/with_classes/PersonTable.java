package ru.gb.homework.homework_3.with_classes;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class PersonTable {
    public void createTable(Connection connection, String sqlRequest) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute(sqlRequest);
        } catch (SQLException e){
            System.err.println("Во время создания таблицы Person произошла ошибка: " + e.getMessage());
            throw e;
        }
    }
    public void insertData(Connection connection, String sqlRequest, ArrayList<Long> departmentIds, int minAge, int maxAge, int personNum) throws SQLException{
        try (Statement statement = connection.createStatement()){
            StringBuilder insertQuery = new StringBuilder(sqlRequest);
            for (int i = 1; i <= personNum; i++) {
                int age = ThreadLocalRandom.current().nextInt(minAge, maxAge);
                boolean active = ThreadLocalRandom.current().nextBoolean();
                long department = departmentIds.get(ThreadLocalRandom.current().nextInt(departmentIds.size()));
                insertQuery.append(String.format("(%s, '%s', %s, %s, '%s')", i, "Person #" + i, age, active, department));
                if(i != personNum){
                    insertQuery.append(",\n");
                }
            }
            int insertCount = statement.executeUpdate(insertQuery.toString());
            System.out.println("Добавлено строк в Person: " + insertCount);
        }
    }
    public void selectData(Connection connection, String sqlRequest) throws SQLException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sqlRequest);
            while(resultSet.next()){
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                long department_id = resultSet.getLong("department_id");
                System.out.println("Найдена строка: [id = " + id + ", name = " + name + ", age = " + age + ", department_id = " + department_id +"]");
            }
        }
    }
    public void updateData(Connection connection, String sqlRequest) throws SQLException{
        try (Statement statement = connection.createStatement()){
            int updateCount = statement.executeUpdate(sqlRequest);
            System.out.println("Обновлено строк: " + updateCount);
        }
    }
}