package edu.cupk.simple_library_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    @JsonProperty("userid")
    private Integer userId;

    @Column(name = "userName", nullable = false, unique = true)
    @JsonProperty("username")
    private String userName;

    @Column(name = "userPassword", nullable = false)
    @JsonProperty("userpassword")
    private String userPassword;

    @Column(name = "isAdmin", nullable = false)
    @JsonProperty("isadmin")
    private Byte isAdmin;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Byte getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Byte isAdmin) {
        this.isAdmin = isAdmin;
    }
}
