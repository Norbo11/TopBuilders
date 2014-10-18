package com.github.norbo11.builderscentral.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.norbo11.builderscentral.models.enums.UserType;
import com.github.norbo11.builderscentral.models.exceptions.PasswordException;
import com.github.norbo11.builderscentral.models.exceptions.UsernameException;
import com.github.norbo11.builderscentral.util.Database;
import com.github.norbo11.builderscentral.util.Log;

public class User {
    public static final String DB_TABLE_NAME = "users";
    public static User currentUser = null;
    private String username;
    private String password;
    private UserType type;
    
    public User(String username, String password, UserType type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "[USER] " + username + " - " + password + " - " + type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return type;
    }
    
    public void save() {
        
    }
    
    public static User login(String inputUser, String inputPassword) throws UsernameException, PasswordException {
        try {
            ResultSet result = Database.executeQuery("SELECT * FROM " + DB_TABLE_NAME  + " WHERE username = ? COLLATE NOCASE", inputUser);

            if (result.next())
            {
                String username = result.getString("username");
                String password = result.getString("password");
                
                if (password.equalsIgnoreCase(inputPassword)) {
                    String type = result.getString("type");
                    return new User(username, password, UserType.getUserType(type));
                } else throw new PasswordException();
            } else throw new UsernameException();
        } catch (SQLException e) {
            Log.error(e);
        }
        
        return null;
    }

    public static void setCurrentUser(User user) {
        User.currentUser = user;
    }
    
    public static User getCurrentUser() {
        return User.currentUser;
    }
}
