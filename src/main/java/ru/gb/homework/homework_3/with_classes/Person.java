package ru.gb.homework.homework_3.with_classes;

public class Person {
    private long id;
    private String name;
    private int age;
    private boolean active;
    private long departmentId;
    public Person(long id, String name, int age, boolean active, long departmentId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.active = active;
        this.departmentId = departmentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getDepartment() {
        return departmentId;
    }

    public void setDepartment(long department) {
        this.departmentId = department;
    }

    @Override
    public String toString() {
        return "Person => " +
                " id = " + id +
                ", name = " + name +
                ", age = " + age +
                ", departmentId = " + departmentId;
    }
}