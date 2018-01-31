package com.bcg.dv.entities;

import com.bcg.dv.api.bindings.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Accounts")
public class Account {

  private Integer id;
  private BigDecimal balance;
  private User user;

  @Id
  @Column(name = "id")
  @JsonView(value = Views.Public.class)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Column(name = "balance")
  @JsonView(value = Views.Public.class)
  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id")
  @JsonIgnoreProperties(value = "account")
  @JsonView(value = Views.Protected.class)
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
