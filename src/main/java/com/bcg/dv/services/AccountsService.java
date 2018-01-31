package com.bcg.dv.services;

import com.bcg.dv.api.bindings.Transfer;
import com.bcg.dv.entities.Account;
import com.bcg.dv.repositories.AccountsRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AccountsService {

  private AccountsRepository accountsRepository;

  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public Optional<Account> getDetails(Integer id) {
    return this.accountsRepository.findById(id);
  }

  public BigDecimal getBalance(Integer id) {
    return this.accountsRepository.findOne(id).getBalance();
  }

  public Status transfer(Transfer transDetails) {
    Account payer = this.accountsRepository.findOne(transDetails.getPayerId());
    Account payee = this.accountsRepository.findOne(transDetails.getPayeeId());
    BigDecimal curPayerBal = payer.getBalance();

    if (curPayerBal.compareTo(transDetails.getTransAmt()) < 0) {
      return Status.INSUFFICIENT_FUNDS;
    }

    BigDecimal tmpPayerBal = curPayerBal.subtract(transDetails.getTransAmt());
    BigDecimal tmpPayeeBal = payee.getBalance().add(transDetails.getTransAmt());

    payer.setBalance(tmpPayerBal);
    payee.setBalance(tmpPayeeBal);

    List<Account> accounts = Arrays.asList(payer, payee);
    this.accountsRepository.save(accounts);

    return Status.SUCCESS;
  }
}
