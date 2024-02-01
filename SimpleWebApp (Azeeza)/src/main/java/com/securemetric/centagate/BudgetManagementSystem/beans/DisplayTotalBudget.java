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
import java.util.ArrayList;

@ManagedBean(name="displayTotalBudget")
@SessionScoped
public class DisplayTotalBudget implements Serializable {
    private Integer employeeId;
    private Double totalBudget;
    public DisplayTotalBudget() {
        Object employeeIdObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("employee_id");
        if (employeeIdObject != null) {
            this.employeeId = Integer.parseInt(employeeIdObject.toString());
        } else {
            // Handle the case when the employee_id is not found in the session map
            System.out.println("Employee ID is not found");
        }
    }

    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }
    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList();
        try {
            Connection connection = DatabaseConnection.getConnection();

            if(connection != null) {
                String mySql = "SELECT\n" +
                        "  employee.employee_id,\n" +
                        "  budget_transaction.transaction_id,\n" +
                        "  budget_info.budget_id,\n" +
                        "  budget_info.budget_amount,\n" +
                        "  budget_info.budget_totalAmount,\n" +
                        "  (SELECT SUM(budget_info.budget_amount) \n" +
                        "   FROM budget_transaction \n" +
                        "   JOIN budget_info ON budget_transaction.budget_id = budget_info.budget_id \n" +
                        "   WHERE budget_transaction.employee_id = ? AND budget_info.budget_status = ? \n" +
                        "  ) AS budget_totalAmount\n" +
                        "FROM\n" +
                        "  employee\n" +
                        "JOIN\n" +
                        "  budget_transaction ON employee.employee_id = budget_transaction.employee_id\n" +
                        "JOIN\n" +
                        "  budget_info ON budget_transaction.budget_id = budget_info.budget_id\n" +
                        "WHERE\n" +
                        "  employee.employee_id = ? AND budget_info.budget_status = ? \n" +
                        "ORDER BY \n" +
                        "  budget_transaction.transaction_id DESC;";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);
                preparedStatement.setInt(1, employeeId);
                preparedStatement.setString(2, "Approved");
                preparedStatement.setInt(3, employeeId);
                preparedStatement.setString(4, "Approved");
                ResultSet resultSet = preparedStatement.executeQuery();


                if(resultSet != null) {
                    while(resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setBudget_amount(resultSet.getDouble(4));
                        displayData.setBudget_totalAmount(resultSet.getDouble(6));

                        array.add(displayData);
                        //System.out.println("Leave Balance: " + leaveBalance);
                        System.out.println("Leave Total: " + totalBudget);

                    }
                    return array;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while recording your leave request.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }
}
