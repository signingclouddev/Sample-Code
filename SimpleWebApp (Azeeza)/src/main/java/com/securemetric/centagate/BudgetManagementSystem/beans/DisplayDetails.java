package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ManagedBean(name="displayDetails")
@SessionScoped
public class DisplayDetails implements Serializable {
    private Integer budgetId;
    private Integer employeeId;
    private DisplayData displayData;

    public DisplayDetails() {
        if (budgetId == null) {
            Object budgetIdObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("budget_id");
            if (budgetIdObject != null) {
                this.budgetId = Integer.parseInt(budgetIdObject.toString());
            } else {
                // Handle the case when the budget_id is not found in the session map
                System.out.println("Budget ID is not found");
            }
        }
        if (employeeId == null) {
            Object employeeIdObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("employee_id");
            if (employeeIdObject != null) {
                this.employeeId = Integer.parseInt(employeeIdObject.toString());
            } else {
                // Handle the case when the employee_id is not found in the session map
                System.out.println("Employee ID is not found");
            }
        }
    }

    public Integer getBudgetId() {
        return (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("budget_id");
    }
    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }

    public Integer getEmployeeId() {
        return (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("employee_id");
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public DisplayData getDisplayData() {
        return displayData;
    }
    public void setDisplayData(DisplayData displayData) {
        this.displayData = displayData;
    }

    public String displayDataDetails() throws Exception {
        Integer selectedEmployeeId = (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_employee_id");
        Integer selectedBudgetId = (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selected_budget_id");

        Connection connection = DatabaseConnection.getConnection();

        try {
            if(connection != null ) {
                String mySql = "SELECT employee.employee_id, employee.employee_fname, employee.employee_lname, employee.employee_username, employee.employee_email, employee.employee_group, \n" +
                        "\tbudget_transaction.transaction_id, \n" +
                        "\tbudget_info.budget_id, budget_info.budget_type, budget_date, budget_amount, budget_remarks, budget_status \n" +
                        "FROM employee \n" +
                        "JOIN budget_transaction ON employee.employee_id = budget_transaction.employee_id \n" +
                        "JOIN budget_info ON budget_transaction.budget_id = budget_info.budget_id \n" +
                        "WHERE budget_transaction.budget_id = ? AND budget_transaction.employee_id = ?;";
                PreparedStatement selectStatement = connection.prepareStatement(mySql);
                selectStatement.setInt(1, selectedBudgetId);
                selectStatement.setInt(2, selectedEmployeeId);
                ResultSet resultSet = selectStatement.executeQuery();

                if(resultSet.next()) {
                    displayData = new DisplayData();
                    displayData.setEmployee_id(resultSet.getInt(1));
                    displayData.setEmployee_fname(resultSet.getString(2));
                    displayData.setEmployee_lname(resultSet.getString(3));
                    displayData.setEmployee_username(resultSet.getString(4));
                    displayData.setEmployee_email(resultSet.getString(5));
                    displayData.setEmployee_group(resultSet.getString(6));

                    displayData.setBudget_id(resultSet.getInt(8));
                    displayData.setBudget_type(resultSet.getString(9));
                    displayData.setBudget_date(resultSet.getDate(10));
                    displayData.setBudget_amount(resultSet.getDouble(11));
                    displayData.setBudget_remarks(resultSet.getString(12));
                    displayData.setBudget_status(resultSet.getString(13));

                    //FacesContext.getCurrentInstance().getExternalContext().redirect("viewDetails.xhtml");
                    System.out.println("Selected Employee ID: " + selectedEmployeeId);
                    System.out.println("Selected Budget ID: " + selectedBudgetId);
                    return "success";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while recording your budget request.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
    return null;
    }
}
