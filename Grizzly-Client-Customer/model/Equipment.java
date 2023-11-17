package model;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.*;

@Entity
@Table(name = "equipment")
public class Equipment implements Serializable {
    @Id
    @Column(name = "equipment_id")
    private String equipment_id;

    @Column(name = "equipment_name")
    private String equipment_name;

    @Column(name = "equipment_status")
    private String equipment_status;

    @Column(name = "equipment_category")
    private String equipment_category;

    @Column(name = "cost")
    private double cost;

    // Default constructor
    public Equipment() {
    }

    // Parameterized constructor
    public Equipment(String equipment_id, String equipment_name, String equipment_status, String equipment_category, double cost) {
        this.equipment_id = equipment_id;
        this.equipment_name = equipment_name;
        this.equipment_status = equipment_status;
        this.equipment_category = equipment_category;
        this.cost = cost;
    }

    // Getters and setters

    public String getEquipment_id() {
        return equipment_id;
    }

    public void setEquipment_id(String equipment_id) {
        this.equipment_id = equipment_id;
    }

    public String getEquipment_name() {
        return equipment_name;
    }

    public void setEquipment_name(String equipment_name) {
        this.equipment_name = equipment_name;
    }

    public String getEquipment_status() {
        return equipment_status;
    }

    public void setEquipment_status(String equipment_status) {
        this.equipment_status = equipment_status;
    }

    public String getEquipment_category() {
        return equipment_category;
    }

    public void setEquipment_category(String equipment_category) {
        this.equipment_category = equipment_category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}