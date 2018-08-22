package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Pattern(regexp = "(?=^.{3,20}$)^[a-zA-Z][a-zA-Z0-9 ]*[._-]?[a-zA-Z0-9 ]+$", message = "")
    @NotBlank(message="Please enter a username.")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Please enter an email address.")
//    @Pattern(regexp = "/^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$/",
//            message="")
    @Email(message = "That email is not a valid email address.")
    @JsonIgnore
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Your password cannot be empty.")
    @Size(min = 8, message="Your password must be at least 8 characters.")
    @JsonIgnore
    private String password;

    @Column(name = "JOINED_DATE")
    private LocalDateTime date;

    @Column
    private String profilePicture;

    @Column
    private String bio;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private PasswordToken passwordToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") // one user can have many posts. When User is deleted, these delete too
    @JsonBackReference
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") // one user can have many comments. When User is deleted, these delete too
    @JsonBackReference
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<PostVote> votes; // one user can have many votes.

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<CommentVote> commentVotes;

    public User(){}

    // use this constructor for update profile form.
    public User(Long id, String username, String email, LocalDateTime date, String profilePicture, String bio) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.date = date;
        this.profilePicture = profilePicture;
        this.bio = bio;
    }

    // security files will need the next constructor. It makes clones/copies. Why?
    // Spring dependencies require a copy of all the properties in the User object
    // because it's the only one that requires authentication:
    public User(User copy) {
        this.id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        this.email = copy.email;
        this.username = copy.username;
        this.password = copy.password;
        this.date = copy.date;
        this.posts = copy.posts;
        this.comments = copy.comments;
        this.votes = copy.votes;
        this.commentVotes = copy.commentVotes;
        this.profilePicture = copy.profilePicture;
        this.bio = copy.bio;
        this.passwordToken = copy.passwordToken;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PostVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PostVote> votes) {
        this.votes = votes;
    }

    public List<CommentVote> getCommentVotes() {
        return commentVotes;
    }

    public void setCommentVotes(List<CommentVote> commentVotes) {
        this.commentVotes = commentVotes;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String profilePicturePath() {
        return profilePicture == null ? String.format("%s.png", username) : profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void updateProfilePicture() {
        if (profilePicture == null) {
            profilePicture = profilePicturePath();
        }
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public PasswordToken getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(PasswordToken passwordToken) {
        this.passwordToken = passwordToken;
    }
}


