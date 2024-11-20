package main.code;

import java.io.Serializable;

class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String gender;
    private int age;

    public Tenant(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + " (" + gender + ", " + age + ")";
    }
}
