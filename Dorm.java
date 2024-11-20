  package main.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Dorm implements Serializable {
    private static final long serialVersionUID = 1L;
    private int maxTenants;
    private String name;
    private List<Tenant> tenants;
    private List<Chore> chores;

    public Dorm(int maxTenants, String name) {
        this.maxTenants = maxTenants;
        this.name = name;
        this.tenants = new ArrayList<>();
        this.chores = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addTenant(Tenant tenant) {
        if (tenants.size() < maxTenants) {
            tenants.add(tenant);
        } else {
            throw new IllegalStateException("Dorm is full!");
        }
    }

    public void addChore(Chore chore) {
        chores.add(chore);
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public List<Chore> getChores() {
        return chores;
    }

    @Override
    public String toString() {
        return name + " with " + tenants.size() + " tenants";
    }
}
