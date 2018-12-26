package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Created by RyanHarper on 11/7/17.
 */

//JPA specification defines an object-relational mapping between tables in a relational database and a set of Java classes.
@Entity //is a POJO with mapping information. It's now a jpa entity object. Attributes then get automatically mapped to database columns with the same name
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "(?=^.{3,20}$)^[a-zA-Z][a-zA-Z0-9 ]*[._-]?[a-zA-Z0-9 ]+$", message = "Username must be alphanumeric only.")
    @NotBlank(message="Please enter a username.")
    @Length(min = 2, max = 20, message="Your username must be between 2-20 characters.")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Please enter an email address.")
    @Email(message = "That email is not a valid email address.")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Your password cannot be empty.")
    @Length(min = 8, max = 100, message="Your password must be between 8-100 characters.") // BCrypt PasswordEncoder hashes passwords with 60 random characters. Make sure the max is >= 60
    @JsonIgnore //password is hidden from the client
    private String password;

    @Column(name = "JOINED_DATE")
    private LocalDateTime date;

    @Column
    private String profilePicture;

    @Column
    private String bio;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean banned = false;

    //=============================== RELATIONSHIPS =========================================
    //=======================================================================================

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore // need to ignore for when uploading a profile picture
    private List<PasswordToken> passwordToken;
    // @OneToOne(cascade = CascadeType.ALL, mappedBy = "user") //Before @OneToMany, users couldn't request more than one token.
    // private PasswordToken passwordToken;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private HitCount hitCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flagger")
    @JsonIgnore // can put on getter as well
    private List<CommentFlag> commentFlags;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sender")
    @JsonIgnore
    private List<Message> messagesSent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver")
    @JsonIgnore
    private List<Message> messagesReceived;

    //=============================== CONSTRUCTORS ==========================================
    //=======================================================================================

    public User(){}

    public User(Long id, String username, String email) {
        this.id=id;
        this.username = username;
        this.email = email;
    }

    // security files will need the next constructor. Only for properties needed to login It makes clones/copies. Why?
    // Spring dependencies require a copy of all the properties in the User object
    // because it's the only one that requires authentication:
    public User(User copy) {
        this.id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        this.email = copy.email;
        this.username = copy.username;
        this.password = copy.password;
        this.date = copy.date;
        this.bio = copy.bio;
    }

    public User(String username, String email, String bio) {
        this.username = username;
        this.email = email;
        this.bio = bio;
    }

    //================================  GETTERS AND SETTERS =================================
    //=======================================================================================

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

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<PasswordToken> getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(List<PasswordToken> passwordToken) {
        this.passwordToken = passwordToken;
    }

    public HitCount getHitCount() {
        return hitCount;
    }

    public void setHitCount(HitCount hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public List<CommentFlag> getCommentFlags() {
        return commentFlags;
    }

    public void setCommentFlags(List<CommentFlag> commentFlags) {
        this.commentFlags = commentFlags;
    }

    //==================================== METHODS ==========================================
    //=======================================================================================

    @Override
    public boolean equals(Object anotherUser) {
        if (anotherUser != null)
            if (anotherUser instanceof User)
                if (id != null && id.equals(((User) anotherUser).id)) return true;
        return false;
    }

    public String profilePicturePath() {
        return profilePicture == null ? String.format("%s.png", username) : profilePicture;
    }

    public void updateProfilePicture() {
        if (profilePicture == null) {
            profilePicture = profilePicturePath();
        }
    }

    public boolean ban() {
        return this.banned = true;
    }

    public boolean unban() {
        return this.banned = false;
    }

}

//    Difference between @JsonIgnore and @JsonBackReference, @JsonManagedReference

//    "Used to solve the Infinite recursion (StackOverflowError)"
//
// @JsonIgnore is not designed to solve the Infinite Recursion problem, it just ignores the annotated property from
// being serialized or deserialized. But if there was a two-way linkage between fields, since @JsonIgnore ignores
// the annotated property, you may avoid the infinite recursion.
//
// On the other hand, @JsonManagedReference and @JsonBackReference are designed to handle this
// two-way linkage between fields, one for Parent role, the other for Child role, respectively:
//
// For avoiding the problem, linkage is handled
//  such that the property annotated with @JsonManagedReference
// annotation is handled normally (serialized normally, no special handling for deserialization) and the property
// annotated with @JsonBackReference annotation is not serialized; and during deserialization, its value is set to
// instance that has the "managed" (forward) link.
//
// To recap, if you don't need those properties in the serialization or deserialization process,
// you can use @JsonIgnore. Otherwise, using the @JsonManagedReference /@JsonBackReference pair is the way to go.