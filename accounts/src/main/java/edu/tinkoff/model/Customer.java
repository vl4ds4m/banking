package edu.tinkoff.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private int id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    public Customer() {
    }

    public Customer(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
