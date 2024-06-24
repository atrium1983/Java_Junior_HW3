package ru.gb.homework.homework_3.with_classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainForClasses {
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")){
            Service service = new Service();
            service.createDepartmentTable(connection);
            service.insertDepartmentData(connection);
            service.createPersonTable(connection);
            service.insertPersonData(connection);
            service.updatePearsonData(connection);
            service.selectPersonData(connection);

            long personId = 6L;
            System.out.println("Department of Person 6 = " + service.getDepartmentByPersonId(connection, personId));

            service.printMap(service.getPersonDepartments(connection));
            service.printMapWithList(service.getDepartmentPersons(connection));
        } catch (SQLException e) {
            System.err.println("Во время подлючения произошла ошибка: " + e.getMessage());
        }
    }
}