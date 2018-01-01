package com.bcg.dv.repositories;

import com.bcg.dv.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Integer> {

  List<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

  List<User> findByLastNameIgnoreCase(String lastName);

  User findByEmail(String email);
}
