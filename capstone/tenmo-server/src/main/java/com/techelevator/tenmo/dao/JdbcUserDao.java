package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//Allows us to use a generic form of userDao in our controller
@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public User[] findAll() {
        //Query database and add all users to a list
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            user.setPassword("REDACTED");
            users.add(user);
        }
        User[] allUsers = new User[users.size()];
        for(int i = 0; i < allUsers.length; i++){
            allUsers[i] = users.get(i);
        }

        return allUsers;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public int findAccountIdByUsername(String username){
        String sql = "SELECT account_id from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    //take a BigDecimal from the balance column
    @Override
    public BigDecimal getBalance(String userName){
        String sql = "SELECT balance FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userName);
        results.next();

        return results.getBigDecimal("balance");
    }

    @Override
    public boolean send(String userName, BigDecimal amount) {
        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ?;";
        int rowsUpdated = jdbcTemplate.update(sql, amount, findIdByUsername(userName));
        if(rowsUpdated > 0){
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean receive(String userName, BigDecimal amount) {
        String sql = "UPDATE account SET balance = balance + ? WHERE user_id = ?;";
        int rowsUpdated = jdbcTemplate.update(sql, amount, findIdByUsername(userName));
        if(rowsUpdated > 0){
            return true;
        }
        else {
            return false;
        }
    }



    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
