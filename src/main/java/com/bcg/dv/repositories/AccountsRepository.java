package com.bcg.dv.repositories;

import com.bcg.dv.entities.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Account, Integer> {

  Optional<Account> findById(Integer id);
}
