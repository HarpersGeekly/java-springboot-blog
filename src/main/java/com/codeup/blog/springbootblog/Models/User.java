package com.codeup.blog.springbootblog.Models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by RyanHarper on 11/7/17.
 */

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message="Enter a username.")
    @Pattern(regexp = "(?=^.{3,20}$)^[a-zA-Z][a-zA-Z0-9]*[._-]?[a-zA-Z0-9]+$")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Please enter an email address.")
    @Email(message = "Enter a valid email address.")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Your password cannot be empty.")
    @Size(min = 8, message="Your password must be at least 8 characters.")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") // one user can have many posts.
    private List<Post> posts;

    @Column(name = "JOINED_DATE")
    private LocalDateTime date;

    public User(){}

    // use this constructor for update profile form.
    public User(Long id, String username, String email, LocalDateTime date) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.date = date;
    }

    // security files will need the next constructor. It makes clones/copies. Why?
    // Spring dependencies require a copy of all the properties in the User object
    // because it's the only one that requires authentication:
    public User(User copy) {
        this.id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        this.email = copy.email;
        this.username = copy.username;
        this.password = copy.password;
        this.posts = copy.posts;
        this.date = copy.date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object anotherUser) {
        if (anotherUser != null)
            if (anotherUser instanceof User)
                if (id != null && id.equals(((User) anotherUser).id)) return true;
        return false;
    }
}


