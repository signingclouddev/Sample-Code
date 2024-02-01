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
import java.time.LocalDate;

@ManagedBean(name="displayRecentlyBudget")
@SessionScoped
public class DisplayRecentlyBudget implements Serializable {

    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                LocalDate today = LocalDate.now();
                String mySql = "SELECT employee.employee_id, employee.employee_fname, employee.employee_lname, budget_transaction.transaction_id, \n" +
                        "\tbudget_info.budget_id, budget_info.budget_type, budget_info.budget_date, budget_info.budget_amount, budget_info.budget_status \n" +
                        "FROM employee \n" +
                        "JOIN budget_transaction ON employee.employee_id = budget_transaction.employee_id \n" +
                        "JOIN budget_info ON budget_transaction.budget_id = budget_info.budget_id \n" +
                        "ORDER BY budget_transaction.transaction_id DESC LIMIT 3;";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet != null) {
                    while (resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setEmployee_fname(resultSet.getString(2));
                        displayData.setEmployee_lname(resultSet.getString(3));
                        displayData.setBudget_id(resultSet.getInt(5));
                        displayData.setBudget_type(resultSet.getString(6));
                        displayData.setBudget_date(resultSet.getDate(7));
                        displayData.setBudget_amount(resultSet.getDouble(8));
                        displayData.setBudget_status(resultSet.getString(9));

                        array.add(displayData);
                    }
                    return array;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurs while processing Today's Applied Budget");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }
}
