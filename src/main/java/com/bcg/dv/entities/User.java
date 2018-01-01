package com.bcg.dv.entities;

import com.bcg.dv.api.json.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

  private Integer id;
  private String firstName;
  private String middleName;
  private String lastName;
  private String email;
  private String password;
  private Account account;

  @Id
  @Column(name = "id")
  @JsonView(value = Views.Public.class)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Column(name = "first_name")
  @JsonView(value = Views.Public.class)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Column(name = "middle_name")
  @JsonView(value = Views.Public.class)
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  @Column(name = "last_name")
  @JsonView(value = Views.Public.class)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Column(name = "email")
  @JsonView(value = Views.Public.class)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "password")
  @JsonView(value = Views.Private.class)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
  @JsonIgnoreProperties(value = "user")
  @JsonView(value = Views.Public.class)
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
