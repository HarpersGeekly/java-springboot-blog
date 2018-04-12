package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
//import java.util.Date;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;

/**
 * Created by RyanHarper on 11/3/17.
 */

@Entity // annotation saying "will be a table".
@Table(name = "posts") // name of database table
public class Post {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(nullable = false, length = 100) // column on table, not-null
    @NotBlank(message="Title cannot be empty")
    private String title;

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message="Description cannot be empty :/")
    @Size(min = 5, message="Description must be at least 5 characters.")
    private String description;

    @ManyToOne // many posts can belong to one user.
    // will define the foreign key. This class represents the post table and we need a reference to the user
//    @JsonManagedReference
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL) // One post will have many comments
//    @JsonBackReference
    private List<Comment> comments;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    , orphanRemoval = true
    private List<PostVote> votes; // one post can have many votes, if a post is deleted, the votes disappear too.

    //use when the post is retrieved from the database.
    public Post(Long id, String title, String description, User user, String image,
                LocalDateTime date,
                List<Comment> comments,
                List<PostVote> votes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user; // this gives me access to properties from user, user.getUsername()
        this.date = date;
        this.comments = comments;
//        this.image = image; // may not need this here.
        this.votes = votes;
    }

    //use on the create form action with Model binding.
    public Post(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //use for Spring magic.
    public Post() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getUsername();
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

    public void setComment(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PostVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PostVote> votes) {
        this.votes = votes;
    }

    public PostVote getVoteFrom(User user) {
        for (PostVote vote : votes) {
            if (vote.voteBelongsTo(user)) {
                return vote;
            }
        }
        return null;
    }

    public void addVote(PostVote vote) {
        votes.add(vote);
    }

    public void removeVote(PostVote vote) {
        votes.remove(vote);
    }

    // VOTING LOGIC =============================================================================
    @JsonGetter("voteCount") // saying that this method is only being used as an attribute in show.html
    public int voteCount() {
        return votes.stream().mapToInt(PostVote::getType).reduce(0, (sum, vote) -> sum + vote);
        // takes all the votes and adds 1 or -1 (getType). Needs more users in application to vote and see results.
        // http://www.baeldung.com/java-8-double-colon-operator (::)
        // stream(), mapToInt(), reduce()
        // https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
        // A stream represents a sequence of elements and supports different kinds of operations to perform computations upon those elements.
        // Streams can be created from various data sources, especially collections. Lists and Sets support new methods stream()
        // mapToInt() returns an IntStream consisting of the results of applying the given function to the elements of this stream.
        // PostVote::getType will evaluate to a function that invokes getType() directly without any delegation.
        // There’s a really tiny performance difference due to saving one level of delegation.
        // reduce() sums the values
    }

//    public String getImage() {
//        return image;
//    }

//    public void setImage(String image) {
//        this.image = image;
//    }
}
