package main.code;

import main.code.Dorm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Building implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Dorm> dorms;

    public Building(String name) {
        this.name = name;
        this.dorms = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Dorm> getDorms() {
        return dorms;
    }

    public void addDorm(Dorm dorm) {
        dorms.add(dorm);
    }

    @Override
    public String toString() {
        return name;
    }
}
