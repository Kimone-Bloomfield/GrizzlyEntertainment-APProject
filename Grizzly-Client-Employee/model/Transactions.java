package model;

import java.io.Serializable;
import java.util.Date;

public class Transactions extends Rental implements Serializable{
    private int transactionId;
    private Date transactionDate;
    private String customerId;
    private String customerName;
    private String equipmentId;
    private String equipmentName;
    private Date date;
    private String equipmentCategory;
    private double cost;
    private double custAccount;

   
    public Transactions() {
    }


    public Transactions(int transactionId, Date transactionDate, String customerId, String customerName,
                       String equipmentId, String equipmentName, Date date,
                       String equipmentCategory, double cost, double custAccount) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.date = date;
        this.equipmentCategory = equipmentCategory;
        this.cost = cost;
        this.custAccount=custAccount;
    }


    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(String equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


	public double getCustAccount() {
		return custAccount;
	}


	public void setCustAccount(double custAccount) {
		this.custAccount = custAccount;
	}
}