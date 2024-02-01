package com.securemetric.centagate.BudgetManagementSystem.beans;

import com.securemetric.centagate.BudgetManagementSystem.db.DatabaseConnection;
//import org.primefaces.model.chart.PieChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartOptions;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartDataSet;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


@ManagedBean(name="displayChart")
@SessionScoped
public class DisplayChart implements Serializable {
    private String searchQuery;
    private PieChartModel pieChartModel;
    private BarChartModel barChartModel;
    //private LineChartModel lineChartModel;

    @PostConstruct
    public void init() {
        pieChartModel = createPieChartModel();
        barChartModel = createBarChartModel();
    }

    public PieChartModel getPieChartModel() {
        return pieChartModel;
    }

    public BarChartModel getBarChartModel() {
        return barChartModel;
    }


    public ArrayList<DisplayData> display() {
        ArrayList array = new ArrayList();

        try {
            Connection connection = DatabaseConnection.getConnection();
            if(connection!=null) {
                String mySql = "SELECT employee.employee_id, employee.employee_username, budget_transaction.employee_id, budget_transaction.budget_id, \n" +
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
                        displayData.setEmployee_username(resultSet.getString(2));
                        displayData.setBudget_id(resultSet.getInt(5));
                        displayData.setBudget_type(resultSet.getString(6));
                        displayData.setBudget_date(resultSet.getDate(7));
                        displayData.setBudget_amount(resultSet.getDouble(8));
                        displayData.setBudget_remarks(resultSet.getString(9));
                        displayData.setBudget_status(resultSet.getString(10));

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

    private PieChartModel createPieChartModel() {
        PieChartModel chartModel = new PieChartModel();

        // Retrieve the data for the pie chart
        List<DisplayData> dataList = display();

        Map<String, Integer> typeCountMap = new HashMap<>();
        for (DisplayData displayData : dataList) {
            if (displayData.getBudget_status().equals("Approved")) { // Filter by budget_status
                String budgetType = displayData.getBudget_type();
                typeCountMap.put(budgetType, typeCountMap.getOrDefault(budgetType, 0) + 1);
            }
        }

        // Create chart data and add data points
        ChartData data = new ChartData();
        PieChartDataSet dataSet = new PieChartDataSet();

        List<Number> dataPoints = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> backgroundColors = new ArrayList<>();

        // Count the occurrences of each budget_type
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            String budgetType = entry.getKey();
            int count = entry.getValue();
            dataPoints.add(count);
            labels.add(budgetType);
            backgroundColors.add("#" + generateRandomColor()); // Generate random colors for each data point
        }

        dataSet.setData(dataPoints);
        dataSet.setBackgroundColor(backgroundColors);
        data.setLabels(labels);
        data.addChartDataSet(dataSet);

        chartModel.setData(data);

        return chartModel;
    }

    private BarChartModel createBarChartModel() {
        BarChartModel chartModel = new BarChartModel();

        // Retrieve the data for the bar chart
        List<DisplayData> dataList = display();

        // Create chart data and add data points
        ChartData data = new ChartData();

        // Create a dataset for the bar chart
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Total Applied Budget");

        List<Number> dataPoints = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> backgroundColors = new ArrayList<>();

        Map<String, Double> employeeTotalBudgetMap = new HashMap<>(); // Map to store total budget for each employee

        // Process the data and add it to the dataset
        for (DisplayData displayData : dataList) {
            if (displayData.getBudget_status().equals("Approved")) { // Filter by budget_status
                double budgetAmount = displayData.getBudget_amount();
                String employeeUsername = displayData.getEmployee_username();

                // Update total budget for the employee
                if (employeeTotalBudgetMap.containsKey(employeeUsername)) {
                    double currentTotalBudget = employeeTotalBudgetMap.get(employeeUsername);
                    employeeTotalBudgetMap.put(employeeUsername, currentTotalBudget + budgetAmount);
                } else {
                    employeeTotalBudgetMap.put(employeeUsername, budgetAmount);
                }
            }
        }

        // Add data points for each employee
        for (Map.Entry<String, Double> entry : employeeTotalBudgetMap.entrySet()) {
            String employeeUsername = entry.getKey();
            double totalBudget = entry.getValue();

            labels.add(employeeUsername);
            dataPoints.add(totalBudget);
            backgroundColors.add("#" + generateRandomColor()); // Generate random colors for each data point
        }

        dataSet.setData(dataPoints);
        dataSet.setBackgroundColor(backgroundColors);
        dataSet.setBorderColor("rgb(255, 255, 255)");
        dataSet.setBorderWidth(2);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        chartModel.setData(data);

        return chartModel;
    }

    // Helper method to generate a random color in hexadecimal format
    private String generateRandomColor() {
        Random random = new Random();
        int rgb = random.nextInt(0xFFFFFF + 1);
        return String.format("%06x", rgb);
    }

}


