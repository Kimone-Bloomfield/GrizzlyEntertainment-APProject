package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int message_id;

    @Column(name = "cust_id")
    private String cust_id;

    @Column(name = "custmessage")
    private String custmessage;

    @Column(name = "status")
    private String status;

    @Column(name = "empmessage")
    private String empmessage;

    public Message() {
        this.custmessage = "";
        this.status = "";
        this.empmessage = "";
    }

    public Message(String cust_id, String custmessage, String status, String empmessage) {
        this.cust_id = cust_id;
        this.custmessage = custmessage;
        this.status = status;
        this.empmessage = empmessage;
    }

    public int getMessageID() {
        return message_id;
    }

    public void setMessageID(int message_id) {
        this.message_id = message_id;
    }

    public String getCustID() {
        return cust_id;
    }

    public void setCustID(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCustMessage() {
        return custmessage;
    }

    public void setCustMessage(String custmessage) {
        this.custmessage = custmessage;
    }

    public String getEmpMessage() {
        return empmessage;
    }

    public void setEmpMessage(String empmessage) {
        this.empmessage = empmessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
