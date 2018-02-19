package com.bcg.dv.api.controllers;

import com.bcg.dv.api.bindings.Transfer;
import com.bcg.dv.api.bindings.Views;
import com.bcg.dv.entities.Account;
import com.bcg.dv.services.AccountsService;
import com.bcg.dv.services.Status;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/accounts")
public class AccountsController {

  private AccountsService accountsService;

  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @RequestMapping(path = "/details/{acctId}", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public ResponseEntity<?> getDetails(@PathVariable("acctId") Integer acctId) {
    Optional<Account> optAcct = this.accountsService.getDetails(acctId);

    return optAcct.map(acct -> new ResponseEntity<Account>(acct, HttpStatus.OK))
        .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
  }

  @RequestMapping(path = "/balance/{acctId}", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public BigDecimal getBalance(@PathVariable("acctId") Integer acctId) {
    return this.accountsService.getBalance(acctId);
  }

  @RequestMapping(path = "/transfer", method = RequestMethod.POST)
  @JsonView(value = Views.Protected.class)
  public ResponseEntity<?> transfer(@RequestBody Transfer transDetails) {
    Status stat = this.accountsService.transfer(transDetails);

    if (stat == Status.SUCCESS) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
