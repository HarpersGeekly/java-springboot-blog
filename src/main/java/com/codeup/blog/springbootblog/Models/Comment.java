package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by RyanHarper on 11/20/17.
 */

@Entity // annotation saying "will be a table".
@Table(name = "comments") // name of database table
public class Comment {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

//    @Column
//    private Long parent_comment_id;
    // all replies are comments, but with a parent_id of the comment they're replying to.

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message = "Comments cannot be empty.")
    @Size(min = 2, message = "Comments must be more than 2 characters.")
    private String body;

    @ManyToOne // many comments will belong to one Post
//    @JsonManagedReference
    @JsonIgnore
    private Post post; // this is the mappedBy "post"

    @ManyToOne // many comments will belong to one User
//    @JsonManagedReference
    @JsonIgnore
    private User user;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentVote> commentVotes;

    public Comment() {
    }

    public Comment(String body, Post post, User user, LocalDateTime date, List<CommentVote> commentVotes) {
        this.body = body;
        this.post = post;
        this.user = user;
        this.date = date;
        this.commentVotes = commentVotes;
    }

    public boolean isOwnedBy(long userId) {
        return getUser() != null && getUser().getId().equals(userId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<CommentVote> getCommentVotes() {
        return commentVotes;
    }

    public void setCommentVotes(List<CommentVote> commentVotes) {
        this.commentVotes = commentVotes;
    }

    public CommentVote getVoteFrom(User user) {
        if (commentVotes == null) return null;
        for (CommentVote vote : commentVotes) {
            if (vote.voteBelongsTo(user)) {
                return vote;
            }
        }
        return null;
    }

    public void addVote(CommentVote vote) {
        commentVotes.add(vote);
    }

    public void removeVote(CommentVote vote) {
        commentVotes.remove(vote);
    }

    @JsonGetter("voteCount") // saying that this method is only being used as an attribute in show.html
    public int voteCount() {
        return commentVotes.stream().mapToInt(CommentVote::getType).reduce(0, (sum, vote) -> (sum + vote));
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

}
