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

@ManagedBean(name="displayRecently")
@SessionScoped
public class DisplayRecently implements Serializable {
    private String displayRecentlyQuery;
    private Integer employeeId;

    public DisplayRecently() {
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

    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                //int limit = 3;
                String mySql = "SELECT employee.employee_id, budget_transaction.transaction_id, budget_info.budget_id, \n" +
                        "\t budget_info.budget_type, budget_info.budget_date, budget_info.budget_amount, budget_info.budget_remarks, budget_info.budget_status \n" +
                        "FROM employee \n" +
                        "JOIN budget_transaction ON employee.employee_id = budget_transaction.employee_id \n" +
                        "JOIN budget_info ON budget_transaction.budget_id = budget_info.budget_id \n" +
                        "WHERE employee.employee_id =?\n" +
                        "ORDER BY budget_transaction.transaction_id DESC LIMIT 3;";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet != null) {
                    while (resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setBudget_id(resultSet.getInt(3));
                        displayData.setBudget_type(resultSet.getString(4));
                        displayData.setBudget_date(resultSet.getDate(5));
                        displayData.setBudget_amount(resultSet.getDouble(6));
                        displayData.setBudget_remarks(resultSet.getString(7));
                        displayData.setBudget_status(resultSet.getString(8));

                        array.add(displayData);
                        System.out.println(employeeId);
                    }
                    return array;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while recording your leave request.");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }
}
