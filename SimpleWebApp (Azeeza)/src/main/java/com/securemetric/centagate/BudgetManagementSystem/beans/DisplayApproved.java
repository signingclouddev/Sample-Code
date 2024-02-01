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
import java.util.ArrayList;

@ManagedBean(name="displayApproved")
@SessionScoped
public class DisplayApproved implements Serializable {
    private String message;
    private Integer selectedEmployeeId;
    private Integer selectedBudgetId;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSelectedEmployeeId() {
        return selectedEmployeeId;
    }
    public void setSelectedEmployeeId(Integer selectedEmployeeId) {
        this.selectedEmployeeId = selectedEmployeeId;
    }

    public Integer getSelectedBudgetId() {
        return selectedBudgetId;
    }
    public void setSelectedBudgetId(Integer selectedBudgetId) {
        this.selectedBudgetId = selectedBudgetId;
    }

    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if(connection!=null) {
                String mySql = "SELECT\n" +
                        "  employee.employee_id, employee.employee_fname, employee.employee_lname, employee.employee_username, \n" +
                        "  budget_transaction.transaction_id,\n" +
                        "  budget_info.budget_id, budget_info.budget_type, budget_info.budget_date, budget_info.budget_amount, budget_info.budget_remarks, budget_info.budget_status\n" +
                        "FROM\n" +
                        "  employee\n" +
                        "JOIN\n" +
                        "  budget_transaction ON employee.employee_id = budget_transaction.employee_id\n" +
                        "JOIN\n" +
                        "  budget_info ON budget_transaction.budget_id = budget_info.budget_id\n" +
                        "WHERE\n" +
                        "  budget_info.budget_status = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(mySql);
                preparedStatement.setString(1, "Approved");
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet!=null) {
                    while (resultSet.next()) {

                        Integer employeeId = resultSet.getInt("employee_id");
                        Integer budgetId = resultSet.getInt("budget_id");

                        DisplayData displayData = new DisplayData();
                        displayData.setEmployee_id(resultSet.getInt(1));
                        displayData.setBudget_id(resultSet.getInt(6));
                        displayData.setBudget_type(resultSet.getString(7));
                        displayData.setBudget_date(resultSet.getDate(8));
                        displayData.setBudget_amount(resultSet.getDouble(9));
                        displayData.setBudget_remarks(resultSet.getString(10));
                        displayData.setBudget_status(resultSet.getString(11));

                        array.add(displayData);

                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("employee_id", employeeId);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("budget_id", budgetId);
                    }

                    return array;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occured while recording your budget request.");
            FacesContext.getCurrentInstance().addMessage(null,mssg);
        }
        return null;
    }

    public void selectedBudget(Integer employeeId, Integer budgetId) {
        selectedEmployeeId = employeeId;
        selectedBudgetId = budgetId;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selected_employee_id", employeeId);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selected_budget_id", budgetId);

        // Use the selectedEmployeeId and selectedBudgetId as needed
        System.out.println("Selected Employee ID: " + selectedEmployeeId);
        System.out.println("Selected Budget ID: " + selectedBudgetId);
    }

//    public String yes() {
//        HttpSession session = SessionUtils.getSession();
//        return "authTransactionSigningCrOtp.xhtml?faces-redirect=true";
//    }

    public String no() {
        HttpSession session = SessionUtils.getSession();
        return "statusApproved.xhtml?faces-redirect=true";
    }

}
