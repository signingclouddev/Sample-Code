package com.securemetric.centagate.simpleapp.beans;

import com.securemetric.centagate.simpleapp.database.DataConnect;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name="candidates")
public class Candidates {

    private String id;
    private String name;
    private String faculty;
    private int votes;
    private String img;
    private String about;
    private String manifesto;
    private String candidateName;
    private String candidateId;
    private static String globalCandidateId;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty=faculty;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes=votes;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img=img;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getCandidateName() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("candidateName");
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateId() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("candidateId");
    }

    public void setCandidateId(String candidateId) {
        this.candidateId=candidateId;
    }

    public Candidates() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").toString();
    }

    public List<Candidates> getCandidates() throws SQLException {
        List<Candidates> candidates = new ArrayList<>();

        String selectQuery = "SELECT * FROM candidates";
        try  {
            Connection connection = DataConnect.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Candidates candidate = new Candidates();
                candidate.setId(resultSet.getString("id"));
                candidate.setName(resultSet.getString("name"));
                candidate.setFaculty(resultSet.getString("faculty"));
                candidate.setVotes(resultSet.getInt("votes"));
                candidate.setImg(resultSet.getString("img"));
                candidate.setAbout(resultSet.getString("about"));
                candidate.setManifesto(resultSet.getString("manifesto"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }
        return candidates;
    }

    public void selectedCandidate(String id) throws Exception {
        candidateId = retrieveCandidate(id, "id");
        candidateName = retrieveCandidate(id, "name");
        votes = Integer.parseInt(retrieveCandidate(id, "votes"));

        globalCandidateId = candidateId;

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("candidateName", candidateName);
        PrimeFaces.current().executeScript("PF('confirmationDialog').show()");
    }

    private String retrieveCandidate(String id, String columnName) {
        String candidateValue = null;

        String selectQuery = "SELECT " + columnName + " FROM candidates WHERE id = ?";
        try (Connection connection = DataConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                candidateValue = resultSet.getString(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }
        return candidateValue;
    }

    public void countVotes() {
        String updateQuery = "UPDATE candidates SET votes = votes + 1 WHERE id = ?";

        try (Connection connection = DataConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1,globalCandidateId);

            // Execute the update query
            statement.executeUpdate();

            Login login = new Login();
            login.hasVoted();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }
    }

    public boolean isHasVoted() {
        // Retrieve the status value from the users table based on the username
        String selectQuery = "SELECT status FROM voters WHERE username = ?";
        try (Connection connection = DataConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String status = resultSet.getString("status");
                return status.equals("Has Voted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related errors
        }
        return false; // Default to false if the status retrieval fails
    }
}
