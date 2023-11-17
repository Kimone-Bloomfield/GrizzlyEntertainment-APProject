package model;

import java.io.Serializable;

public class Staff implements Serializable {
	private String emp_id;
	private String emp_firstName;
	private String emp_lastName;
	private String emp_password;
	
	
	public Staff() {
		this.setEmp_id("employee_id");
		this.setEmp_firstName("employee_name");
		this.setEmp_lastName("");
		this.setEmp_password("employee_password");
	}
	
	public Staff(String emp_id,String emp_password, String emp_firstName,String emp_lastName ) {
		this.setEmp_id(emp_id);
		this.setEmp_password(emp_password);
		this.setEmp_firstName(emp_firstName);
		this.setEmp_lastName(emp_lastName);
		
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmp_firstName() {
		return emp_firstName;
	}

	public void setEmp_firstName(String emp_firstName) {
		this.emp_firstName = emp_firstName;
	}

	public String getEmp_lastName() {
		return emp_lastName;
	}

	public void setEmp_lastName(String emp_lastName) {
		this.emp_lastName = emp_lastName;
	}

	public String getEmp_password() {
		return emp_password;
	}

	public void setEmp_password(String emp_password) {
		this.emp_password = emp_password;
	}
	
}

