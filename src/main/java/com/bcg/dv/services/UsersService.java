package com.bcg.dv.services;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bcg.dv.entities.User;
import com.bcg.dv.repositories.UsersRepository;

@Service
@Transactional
public class UsersService {

  private UsersRepository usersRepository;

  public UsersService(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  public List<User> getUsers() {
    Sort sort = new Sort(Direction.ASC, "lastName");
    return this.usersRepository.findAll(sort);
  }

  public List<User> getUsersByFullName(String firstName, String lastName) {
    return this.usersRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
  }

  public List<User> getUsersByLastName(String lastName) {
    return this.usersRepository.findByLastNameIgnoreCase(lastName);
  }

  public User getUserByEmail(String email) {
    return this.usersRepository.findByEmail(email);
  }
}
