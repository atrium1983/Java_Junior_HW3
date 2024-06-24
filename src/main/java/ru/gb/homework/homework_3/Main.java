package ru.gb.homework.homework_3;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    /**
     * С помощью JDBC, выполнить следующие пункты:
     * 1. Создать таблицу Person (скопировать код с семниара)
     * 2. Создать таблицу Department (id bigint primary key, name varchar(128) not null)
     * 3. Добавить в таблицу Person поле department_id типа bigint (внешний ключ)
     * 4. Написать метод, который загружает Имя department по Идентификатору person
     * 5. * Написать метод, который загружает Map<String, String>, в которой маппинг person.name -> department.name
     *   Пример: [{"person #1", "department #1"}, {"person #2", "department #3}]
     * 6. ** Написать метод, который загружает Map<String, List<String>>, в которой маппинг department.name -> <person.name>
     *   Пример:
     *   [
     *     {"department #1", ["person #1", "person #2"]},
     *     {"department #2", ["person #3", "person #4"]}
     *   ]
     *  7. *** Создать классы-обертки над таблицами, и в пунктах 4, 5, 6 возвращать объекты.
     */

    /**
     * Пункт 4
     */

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")){
            createDepartmentTable(connection);
            createPersonTable(connection);
            insertDepartmentData(connection);
            insertPersonData(connection);
            updateData(connection);
            selectData(connection);

            long personId = 6L;
            System.out.println("Department name of Person 6 = " + getPersonDepartmentName(connection, personId));

            printMap(getPersonDepartments(connection));
            printMapWithList(getDepartmentPersons(connection));
        } catch (SQLException e) {
            System.err.println("Во время подлючения произошла ошибка: " + e.getMessage());
        }
    }
    private static void createPersonTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute("""
                create table person (
                id bigint primary key,
                name varchar(256),
                age int,
                active boolean,
                department_id bigint,
                foreign key (department_id) references department(id)
                )
                """);
        } catch (SQLException e){
            System.err.println("Во время создания таблицы Person произошла ошибка: " + e.getMessage());
            throw e;
        }
    }
    private static void createDepartmentTable(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            statement.execute("""
                create table department (
                id bigint primary key,
                name varchar(128) not null
                )
                """);
        } catch (SQLException e){
            System.err.println("Во время создания таблицы Department произошла ошибка: " + e.getMessage());
            throw e;
        }
    }
    private static void insertDepartmentData(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            StringBuilder insertQuery = new StringBuilder("insert into department (id, name) values");
            for (int i = 1; i <= 3; i++) {
                insertQuery.append(String.format("(%s, '%s')", i, "Department #" + i));
                if(i != 3){
                    insertQuery.append(",\n");
                }
            }
            int insertCount = statement.executeUpdate(insertQuery.toString());
            System.out.println("Добавлено строк в Department: " + insertCount);
        }
    }
    private static void insertPersonData(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            StringBuilder insertQuery = new StringBuilder("insert into person (id, name, age, active, department_id) values");
            for (int i = 1; i <= 10; i++) {
                int age = ThreadLocalRandom.current().nextInt(20, 60);
                boolean active = ThreadLocalRandom.current().nextBoolean();
                ArrayList<Long> departmentIds = selectDepartmentIds(connection);
                long department = departmentIds.get(ThreadLocalRandom.current().nextInt(departmentIds.size()));
                insertQuery.append(String.format("(%s, '%s', %s, %s, '%s')", i, "Person #" + i, age, active, department));
                if(i != 10){
                    insertQuery.append(",\n");
                }
            }
            int insertCount = statement.executeUpdate(insertQuery.toString());
            System.out.println("Добавлено строк в Person: " + insertCount);
        }
    }
    private static ArrayList<Long> selectDepartmentIds(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
                    select id
                    from department
                    """);
            ArrayList<Long> departments = new ArrayList<>();
            while (resultSet.next()){
                long id = resultSet.getLong("id");
                departments.add(id);
            }
            return departments;
        }
    }
    private static void selectData(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select id, name, age, department_id
            from person
            where active is true""");
            while(resultSet.next()){
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                long department_id = resultSet.getLong("department_id");
                System.out.println("Найдена строка: [id = " + id + ", name = " + name + ", age = " + age + ", department_id = " + department_id +"]");
            }
        }
    }
    private static void updateData(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()){
            int updateCount = statement.executeUpdate("update person set active = true where id > 5");
            System.out.println("Обновлено строк: " + updateCount);
        }
    }
    /**
     * Пункт 4
     */
    private static String getPersonDepartmentName(Connection connection, long personId) throws SQLException, UnsupportedOperationException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select department.id, department.name
            from department
            join person
            on department.id = person.department_id
            where person.id =""" + personId);
            String departmentName = "";
            while(resultSet.next()){
                departmentName = resultSet.getString("department.name");
            }
            return departmentName;
        }
    }
    /**
     * Пункт 5
     */
    private static Map<String, String> getPersonDepartments(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select id, name
            from person
            """);
            Map<String,String> map = new HashMap<>();
            while (resultSet.next()){
                long id = resultSet.getLong("id");
                String personName = resultSet.getString("name");
                String departmentName = getPersonDepartmentName(connection, id);
                map.put(personName, departmentName);
            }
            return map;
        }
    }
    private static void printMap(Map<String, String> map){
        StringBuilder builder = new StringBuilder();
        map.forEach((k,v) -> builder.append(k).append(", ").append(v).append("\n"));
        System.out.println(builder);
    }
    /**
     * Пункт 6
     */
    private static Map<String, List<String>> getDepartmentPersons(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select id, name
            from person
            """);
            Map<String,List<String>> resultMap = new HashMap<>();
            while (resultSet.next()){
                long id = resultSet.getLong("id");
                String personName = resultSet.getString("person.name");
                String departmentName = getPersonDepartmentName(connection, id);
                if(resultMap.containsKey(departmentName)){
                    resultMap.get(departmentName).add(personName);
                } else {
                    ArrayList<String> persons = new ArrayList<>();
                    resultMap.put(departmentName, persons);
                    resultMap.get(departmentName).add(personName);
                }
            }
            return resultMap;
        }
    }
    private static void printMapWithList(Map<String, List<String>> map){
        StringBuilder builder = new StringBuilder();
        map.forEach((k,v) -> builder.append(k).append(", ").append(v).append("\n"));
        System.out.println(builder);
    }
}