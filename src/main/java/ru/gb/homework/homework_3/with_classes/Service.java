package ru.gb.homework.homework_3.with_classes;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
    PersonTable personalTable;
    DepartmentTable departmentTable;

    public Service() {
        personalTable = new PersonTable();
        departmentTable = new DepartmentTable();
    }

    public void createPersonTable(Connection connection) throws SQLException {
        String request = """
                create table person (
                id bigint primary key,
                name varchar(256),
                age int,
                active boolean,
                department_id bigint,
                foreign key (department_id) references department(id)
                )
                """;
        personalTable.createTable(connection, request);
    }
    public void createDepartmentTable(Connection connection) throws SQLException {
        String request = """
                create table department (
                id bigint primary key,
                name varchar(128) not null
                )
                """;
        departmentTable.createTable(connection, request);

    }
    public void insertPersonData(Connection connection) throws SQLException {
        String request = """
                insert into person (id, name, age, active, department_id) values
                """;
        int personNum = 10;
        int minAge = 20;
        int maxAge = 60;
        personalTable.insertData(connection, request, selectDepartmentIds(connection), minAge, maxAge, personNum);
    }
    public void insertDepartmentData(Connection connection) throws SQLException {
        String request = """
                insert into department (id, name) values
                """;
        int departmentNum = 3;
        departmentTable.insertData(connection, request, departmentNum);
    }
    public ArrayList<Long> selectDepartmentIds(Connection connection) throws SQLException{
        String request = """
                    select id
                    from department
                    """;
        return departmentTable.selectDepartmentIds(connection, request);
    }

    public void selectPersonData(Connection connection) throws SQLException {
        String request = """
            select id, name, age, department_id
            from person
            where active is true
            """;
        personalTable.selectData(connection, request);
    }
    public void updatePearsonData(Connection connection) throws SQLException {
        String request = """
            update person 
            set active = true 
            where id > 5
            """;
        personalTable.updateData(connection, request);
    }
    public List<String> selectNamesByAge(Connection connection, String age) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement("select name from person where age =?")){
            statement.setInt(1, Integer.parseInt(age));
            ResultSet resultSet = statement.executeQuery();
            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
            return names;
        }
    }

    /**
     * Пункт 4
     */
    public Department getDepartmentByPersonId(Connection connection, long personId) throws SQLException{
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select department.id, department.name
            from department
            join person
            on department.id = person.department_id
            where person.id =""" + personId);
            long departmentId = -1;
            String departmentName = "";
            while(resultSet.next()){
                departmentName = resultSet.getString("department.name");
                departmentId = resultSet.getLong("department.id");
            }
            return new Department(departmentId, departmentName);
        }
    }
    /**
     * Пункт 5
     */
    public Map<Person, Department> getPersonDepartments(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select *
            from person
            """);
            Map<Person,Department> map = new HashMap<>();
            while (resultSet.next()){
                long personId = resultSet.getLong("id");
                Department department = getDepartmentByPersonId(connection, personId);
                map.put(createPerson(resultSet), department);
            }
            return map;
        }
    }

    public void printMap(Map<Person, Department> map){
        StringBuilder builder = new StringBuilder();
        map.forEach((k,v) -> builder.append(k).append(", ").append(v).append("\n"));
        System.out.println(builder);
    }
    /**
     * Пункт 6
     */
    public Map<Department, List<Person>> getDepartmentPersons(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
            select *
            from person
            """);
            Map<Department,List<Person>> resultMap = new HashMap<>();
            while (resultSet.next()){
                long id = resultSet.getLong("id");
                Department department = getDepartmentByPersonId(connection, id);
                if(resultMap.containsKey(department)){
                    resultMap.get(department).add(createPerson(resultSet));
                } else {
                    resultMap.put(department, new ArrayList<>());
                    resultMap.get(department).add(createPerson(resultSet));
                }
            }
            return resultMap;
        }
    }
    public void printMapWithList(Map<Department, List<Person>> map){
        StringBuilder builder = new StringBuilder();
        map.forEach((k,v) -> builder.append(k).append(", ").append(v).append("\n"));
        System.out.println(builder);
    }

    public Person createPerson(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getBoolean("active"),
                resultSet.getLong("department_id"));
    }
}
