package main.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class DormManager implements Serializable {
    private static final long serialVersionUID = 1L; // Add serialVersionUID for Serializable
    private List<Building> buildings;

    public DormManager() {
        this.buildings = new ArrayList<>();
    }

    public void add(Building building) {
        buildings.add(building);
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
