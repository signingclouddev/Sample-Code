package com.securemetric.centagate.simpleapp.beans;

import com.securemetric.centagate.simpleapp.database.DataConnect;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name="viewProfile")
@SessionScoped
public class ViewProfile implements Serializable {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String userEmail;

    public ViewProfile() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<ViewProfile> getCurrentUser() throws SQLException {
        List<ViewProfile> view = new ArrayList<>();

        String selectQuery = "SELECT * FROM voters WHERE username = username";
        try  {

            Connection connection = DataConnect.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ViewProfile user = new ViewProfile();
                user.setId(resultSet.getString("id"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setUsername(resultSet.getString("username"));
                user.setUserEmail(resultSet.getString("userEmail"));
                view.add(user);
            }

            for (ViewProfile user : view) {
                this.firstName = user.getFirstName();
                this.lastName = user.getLastName();
                this.userEmail = user.getUserEmail();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }

        return view;
    }
}