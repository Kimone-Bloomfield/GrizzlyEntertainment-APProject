package model;

import java.io.Serializable;
import java.util.Date;

import java.io.Serializable;
import java.util.Date;

import java.io.Serializable;
import java.util.Date;

public class Rental extends Equipment implements Serializable {

   private String customer_id;
   private String customer_name;
   private String equipment_id;
   private String equipment_name;
   private Date date;
   private String equipment_category;
   private double cost;

   public Rental() {
	   super(); 
       this.customer_id = "";
       this.customer_name = "";
       this.date = new Date();
   }

   public Rental(String customer_id, String customer_name, String equipment_id, String equipment_name, Date date,
                  String equipment_category, double cost) {
       this.customer_id = customer_id;
       this.customer_name = customer_name;
       this.equipment_id = equipment_id;
       this.equipment_name = equipment_name;
       this.date = date;
       this.equipment_category = equipment_category;
       this.cost = cost;
   }

   public String getCustomer_id() {
       return customer_id;
   }

   public void setCustomer_id(String customer_id) {
       this.customer_id = customer_id;
   }

   public String getCustomer_name() {
       return customer_name;
   }

   public void setCustomer_name(String customer_name) {
       this.customer_name = customer_name;
   }

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

   public Date getDate() {
       return date;
   }

   public void setDate(Date date) {
       this.date = date;
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
   
   @Override
   public String toString() {
	    return "Rental{" +
	            "customer_id='" + customer_id + '\'' +
	            ", customer_name='" + customer_name + '\'' +
	            ", equipment_id='" + equipment_id + '\'' +
	            ", equipment_name='" + equipment_name + '\'' +
	            ", date=" + date +
	            ", equipment_category='" + equipment_category + '\'' +
	            ", cost=" + cost + 
	            '}';
	}


}
