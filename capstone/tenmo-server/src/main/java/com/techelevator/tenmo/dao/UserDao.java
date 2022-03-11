package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    User[] findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    int findAccountIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal getBalance(String name);

    boolean send(String userName, BigDecimal amount);

    boolean receive(String userName, BigDecimal amount);

    }

