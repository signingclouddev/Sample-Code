package com.securemetric.centagate.BudgetManagementSystem.beans;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="displayData")
@SessionScoped
public class DisplayData implements Serializable {
    //Employee Table
    private Integer employee_id;
    private String employee_fname;
    private String employee_lname;
    private String employee_username;
    private String employee_email;
    private String employee_group;
    private String employee_role;
    private String employee_uniqueId;
    private String employee_clientId;
    private String employee_appId;

    //Budget Info Table
    private Integer budget_id;
    private String budget_type;
    private Date budget_date;
    private Double budget_amount;
    private String budget_remarks;
    private String budget_status;
    private Double budget_totalAmount;

    public DisplayData() {
    }

    public DisplayData(Integer employee_id, String employee_fname, String employee_lname, String employee_username,
                       String employee_email, String employee_group, String employee_role, String employee_uniqueId,
                       String employee_clientId, String employee_appId,
                       Integer budget_id, String budget_type, Date budget_date, Double budget_amount,
                       String budget_status, String budget_remarks, Double budget_totalAmount) {
        this.employee_id = employee_id;
        this.employee_fname = employee_fname;
        this.employee_lname = employee_lname;
        this.employee_username = employee_username;
        this.employee_email = employee_email;
        this.employee_group = employee_group;
        this.employee_role = employee_role;
        this.employee_uniqueId = employee_uniqueId;
        this.employee_clientId = employee_clientId;
        this.employee_appId = employee_appId;

        this.budget_id = budget_id;
        this.budget_type = budget_type;
        this.budget_date = budget_date;
        this.budget_amount = budget_amount;
        this.budget_status = budget_status;
        this.budget_remarks = budget_remarks;
        this.budget_totalAmount = budget_totalAmount;

    }

    public Integer getEmployee_id() {
        return employee_id;
    }
    public void setEmployee_id(Integer employee_id) {
        this.employee_id = employee_id;
    }

    public String getEmployee_fname() {
        return employee_fname;
    }
    public void setEmployee_fname(String employee_fname) {
        this.employee_fname = employee_fname;
    }

    public String getEmployee_lname() {
        return employee_lname;
    }
    public void setEmployee_lname(String employee_lname) {
        this.employee_lname = employee_lname;
    }

    public String getEmployee_username() {
        return employee_username;
    }
    public void setEmployee_username(String employee_username) {
        this.employee_username = employee_username;
    }

    public String getEmployee_email() {
        return employee_email;
    }
    public void setEmployee_email(String employee_email) {
        this.employee_email = employee_email;
    }

    public String getEmployee_group() {
        return employee_group;
    }
    public void setEmployee_group(String employee_group) {
        this.employee_group = employee_group;
    }

    public String getEmployee_role() {
        return employee_role;
    }
    public void setEmployee_role(String employee_role) {
        this.employee_role = employee_role;
    }

    public String getEmployee_uniqueId() {
        return employee_uniqueId;
    }
    public void setEmployee_uniqueId(String employee_uniqueId) {
        this.employee_uniqueId = employee_uniqueId;
    }

    public String getEmployee_clientId() {
        return employee_clientId;
    }
    public void setEmployee_clientId(String employee_clientId) {
        this.employee_clientId = employee_clientId;
    }

    public String getEmployee_appId() {
        return employee_appId;
    }
    public void setEmployee_appId(String appId) {
        this.employee_appId = appId;
    }

    public Integer getBudget_id() {
        return budget_id;
    }
    public void setBudget_id(Integer budget_id) {
        this.budget_id = budget_id;
    }

    public String getBudget_type() {
        return budget_type;
    }
    public void setBudget_type(String budget_type) {
        this.budget_type = budget_type;
    }

    public Date getBudget_date() {
        return budget_date;
    }
    public void setBudget_date(Date budget_date) {
        this.budget_date = budget_date;
    }

    public Double getBudget_amount() {
        return budget_amount;
    }
    public void setBudget_amount(Double budget_amount) {
        this.budget_amount = budget_amount;
    }

    public String getBudget_remarks() {
        return budget_remarks;
    }
    public void setBudget_remarks(String budget_remarks) {
        this.budget_remarks = budget_remarks;
    }

    public String getBudget_status() {
        return budget_status;
    }
    public void setBudget_status(String budget_status) {
        this.budget_status = budget_status;
    }

    public Double getBudget_totalAmount() {
        return budget_totalAmount;
    }
    public void setBudget_totalAmount(Double budget_totalAmount) {
        this.budget_totalAmount = budget_totalAmount;
    }
}
