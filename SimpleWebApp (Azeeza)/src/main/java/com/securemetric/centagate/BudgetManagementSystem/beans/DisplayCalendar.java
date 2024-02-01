package com.securemetric.centagate.BudgetManagementSystem.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean(name="displayCalender")
@ViewScoped
public class DisplayCalendar implements Serializable {
    private Date date;

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public void printDate() {
        System.out.println("Selected Date:" + date);
    }
}
