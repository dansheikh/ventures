package com.bcg.dv.api.controllers;

import com.bcg.dv.api.json.Views;
import com.bcg.dv.entities.User;
import com.bcg.dv.services.UsersService;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UsersController {

  private UsersService usersService;

  public UsersController(UsersService usersService) {
    this.usersService = usersService;
  }

  @RequestMapping(path = "", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public List<User> getUsers() {
    return this.usersService.getUsers();
  }

  @RequestMapping(path = "/fullname/{firstName}/{lastName}", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public List<User> getUsersByFullName(@PathVariable("firstName") String firstName,
      @PathVariable("lastName") String lastName) {
    return this.usersService.getUsersByFullName(firstName, lastName);
  }

  @RequestMapping(path = "/lastname/{lastName}", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public List<User> getUsersByLastName(@PathVariable("lastName") String lastName) {
    return this.usersService.getUsersByLastName(lastName);
  }

  @RequestMapping(path = "/email/{email}", method = RequestMethod.GET)
  @JsonView(value = Views.Protected.class)
  public User getUser(@PathVariable("email") String email) {
    return this.usersService.getUserByEmail(email);
  }
}
