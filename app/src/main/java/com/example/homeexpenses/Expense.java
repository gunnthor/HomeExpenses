package com.example.homeexpenses;


import java.math.BigDecimal;
import java.util.Date;

public class Expense {
    private Person person;
    private Long amount;
    private Date createdDateTime;
    private String createdByUser;
    private String description;
    private String id;

    public Expense() {
        // Required empty constructor for Firebase Realtime Database
    }

    public Expense(Person person, Long amount, String createdByUser, String description ) { // This function is called when a new expense is created.
        this.person = person;
        this.amount = amount;
        this.description = description;
        this.createdDateTime = new Date();
        this.createdByUser = createdByUser;
    }

    public Person getPerson() { // A public function to return the person.
        return person;
    }

    public Long getAmount() { // A public function to return the amount.
        return amount;
    }

    public Date getCreatedDateTime() { // A public function to return the amount.
        return createdDateTime;
    }

    public String getCreatedByUser() {return createdByUser;}

    public String getDescription() {return description;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
