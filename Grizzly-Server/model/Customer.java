package model;

import java.io.Serializable;

public class Customer implements Serializable {
	private String cust_id;
	private String cust_firstName;
	private String cust_lastName;
	private String cust_password;
	private double account;
	
	public Customer() {
	    this.setCust_id("");
	    this.setCust_firstName("");
	    this.setCust_lastName("");
	    this.setCust_password("");
	    this.account=0.00;
	}

	
	public Customer(String cust_id, String cust_password, String cust_firstName, String cust_lastName, double account ) {
		this.setCust_id(cust_id);
		this.setCust_password(cust_password);
		this.setCust_firstName(cust_firstName);
		this.setCust_lastName(cust_lastName) ;
		this.account=account;
		
	}


	public String getCust_id() {
		return cust_id;
	}


	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}


	public String getCust_firstName() {
		return cust_firstName;
	}


	public void setCust_firstName(String cust_firstName) {
		this.cust_firstName = cust_firstName;
	}


	public String getCust_lastName() {
		return cust_lastName;
	}


	public void setCust_lastName(String cust_lastName) {
		this.cust_lastName = cust_lastName;
	}


	public String getCust_password() {
		return cust_password;
	}


	public void setCust_password(String cust_password) {
		this.cust_password = cust_password;
	}


	public double getAccount() {
		return account;
	}


	public void setAccount(double account) {
		this.account = account;
	}
	public interface CustomerService {
	    void signupCustomer(Customer customer);
	    void loginCustomer(String cust_id, String cust_password);
	    
	}
	
}