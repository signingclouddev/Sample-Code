package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ManagedBean(name="displayProfile")
@SessionScoped
public class DisplayProfile implements Serializable {
    private Integer employeeId;
    private DisplayData displayData;

    public DisplayProfile() {
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

    public DisplayData getDisplayData() {
        return displayData;
    }
    public void setDisplayData(DisplayData displayData) {
        this.displayData = displayData;
    }

    public String displayProfileUser() throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        try {
            if (connection != null) {
                String mySql = "SELECT * FROM employee WHERE employee_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    displayData = new DisplayData();
                    displayData.setEmployee_username(resultSet.getString(2));
                    displayData.setEmployee_fname(resultSet.getString(3));
                    displayData.setEmployee_lname(resultSet.getString(4));
                    displayData.setEmployee_role(resultSet.getString(5));
                    displayData.setEmployee_group(resultSet.getString(6));
                    displayData.setEmployee_uniqueId(resultSet.getString(7));
                    displayData.setEmployee_clientId(resultSet.getString(8));
                    displayData.setEmployee_appId(resultSet.getString(9));
                    displayData.setEmployee_email(resultSet.getString(10));

                    return "success";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while retrieving user information");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }
        return null;
    }

    public String back() {
        HttpSession session = SessionUtils.getSession();
        return "welcome.xhtml?faces-redirect=true";
    }

    public String editProfile() {
        HttpSession session = SessionUtils.getSession();
        return "updateProfileAdmin.xhtml?faces-redirect=true";
    }
}
