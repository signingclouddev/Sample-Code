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
import org.primefaces.model.chart.PieChartModel;


@ManagedBean(name="searchbean")
@SessionScoped
public class Searchbean implements Serializable {

    private String searchQuery;
    private Integer selectedEmployeeId;
    private Integer selectedBudgetId;

    public ArrayList<DisplayData> find() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if(connection!=null) {
                String mySql = "SELECT employee.employee_id, budget_transaction.employee_id, budget_transaction.budget_id, \n" +
                        "\t budget_info.budget_id, budget_info.budget_type, budget_date, budget_amount, budget_remarks, budget_status \n" +
                        "FROM employee \n" +
                        "JOIN budget_transaction ON employee.employee_id = budget_transaction.employee_id \n" +
                        "JOIN budget_info ON budget_transaction.budget_id = budget_info.budget_id \n";
                PreparedStatement preparedStatement;

                if(searchQuery != null && !searchQuery.isEmpty()) {
                    mySql += " WHERE budget_type = ?";
                    preparedStatement = connection.prepareStatement(mySql);
                    preparedStatement.setString(1, searchQuery);
                }
                else {
                    preparedStatement = connection.prepareStatement(mySql);
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet!=null) {
                    while (resultSet.next()) {
                        DisplayData displayData = new DisplayData();
                        displayData.setEmployee_id(resultSet.getInt(1));
                        displayData.setBudget_id(resultSet.getInt(4));
                        displayData.setBudget_type(resultSet.getString(5));
                        displayData.setBudget_date(resultSet.getDate(6));
                        displayData.setBudget_amount(resultSet.getDouble(7));
                        displayData.setBudget_remarks(resultSet.getString(8));
                        displayData.setBudget_status(resultSet.getString(9));

                        array.add(displayData);
                    }
                    return array;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            FacesMessage mssg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurs while processing your budget request.");
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

    public Integer getSelectedBudgetId() {
        return selectedBudgetId;
    }
    public void setSelectedBudgetId(Integer selectedBudgetId) {
        this.selectedBudgetId = selectedBudgetId;
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
}
