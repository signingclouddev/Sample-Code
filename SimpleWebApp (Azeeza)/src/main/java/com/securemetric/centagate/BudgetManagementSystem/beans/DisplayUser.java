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

@ManagedBean(name="displayUser")
@SessionScoped
public class DisplayUser implements Serializable {

    private String searchQuery;
    private Integer selectedEmployeeId;
    private String selectedEmployeeUsername;

    public ArrayList<DisplayData> find() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                String mySql = "SELECT * FROM employee WHERE employee_role = '3' \n";
                PreparedStatement preparedStatement;

                if (searchQuery != null && !searchQuery.isEmpty()) {
                    mySql += " AND employee_username = ?";
                    preparedStatement = connection.prepareStatement(mySql);
                    preparedStatement.setString(1, searchQuery);
                } else {
                    preparedStatement = connection.prepareStatement(mySql);
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet != null) {
                    while (resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setEmployee_id(resultSet.getInt(1));
                        displayData.setEmployee_username(resultSet.getString(2));
                        displayData.setEmployee_fname(resultSet.getString(3));
                        displayData.setEmployee_lname(resultSet.getString(4));
                        displayData.setEmployee_group(resultSet.getString(6));
                        displayData.setEmployee_email(resultSet.getString(10));

                        array.add(displayData);
                    }
                    return array;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurs while displaying user list.");
            FacesContext.getCurrentInstance().addMessage(null,mssg);
        }
        return null;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getSelectedEmployeeId() {
        return selectedEmployeeId;
    }
    public void setSelectedEmployeeId(Integer selectedEmployeeId) {
        this.selectedEmployeeId = selectedEmployeeId;
    }

    public String getSelectedEmployeeUsername() {
        return selectedEmployeeUsername;
    }
    public void setSelectedEmployeeUsername(String selectedEmployeeUsername) {
        this.selectedEmployeeUsername = selectedEmployeeUsername;
    }

    public void selectedEmployee(Integer employeeId, String employeeUsername) {
        selectedEmployeeId = employeeId;
        selectedEmployeeUsername = employeeUsername;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selected_employee_username", employeeUsername);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selected_employee_id", employeeId);

        System.out.println("Selected Employee ID: " + selectedEmployeeId);
        System.out.println("Selected Employee Username: " + selectedEmployeeUsername);
    }
}