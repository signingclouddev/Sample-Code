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

@ManagedBean(name="displayEmployeeDetails")
@SessionScoped
public class DisplayEmployeeDetails implements Serializable {
    private Integer leaveId;

    public DisplayEmployeeDetails() {
        Object leaveIdObject = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("leave_id");
        if (leaveIdObject != null) {
            this.leaveId = Integer.parseInt(leaveIdObject.toString());
        } else {
            // Handle the case when the employee_id is not found in the session map
            System.out.println("Leave ID is not found");
        }
    }

    public Integer getLeaveId() {
        return leaveId;
    }
    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if(connection != null ) {
                String mySql = "SELECT employee.employee_id, employee.employee_fname, employee.employee_lname, employee.employee_username, employee.employee_email, employee.employee_group, \n" +
                        "\tleave_transaction.transaction_id, \n" +
                        "\tleave_info.leave_id, leave_info.leave_type, leave_from, leave_to, leave_status \n" +
                        "FROM employee \n" +
                        "JOIN leave_transaction ON employee.employee_id = leave_transaction.employee_id \n" +
                        "JOIN leave_info ON leave_transaction.leave_id = leave_info.leave_id \n" +
                        "WHERE leave_info.leave_id = ?;";
                PreparedStatement selectStatement = connection.prepareStatement(mySql);
                selectStatement.setInt(1, leaveId);
                ResultSet resultSet = selectStatement.executeQuery();

                if(resultSet != null) {
                    while(resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setEmployee_id(resultSet.getInt(1));
                        displayData.setEmployee_fname(resultSet.getString(2));
                        displayData.setEmployee_lname(resultSet.getString(3));
                        displayData.setEmployee_username(resultSet.getString(4));
                        displayData.setEmployee_email(resultSet.getString(5));
                        displayData.setEmployee_group(resultSet.getString(6));

                        array.add(displayData);
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
