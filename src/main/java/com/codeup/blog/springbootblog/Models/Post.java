package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by RyanHarper on 11/3/17.
 */

@Entity // annotation saying "will be a table", an entity in the database
@Table(name = "posts") // name of database table
public class Post {

    @Id // primary key annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment annotation
    private Long id;

    @Column(nullable = false, length = 100) // column on table, cannot be null (not-null) annotation
    @NotBlank(message = "Title cannot be empty.")
    @Length(min = 5, max = 100, message="Title must be between 5-100 characters.")
    private String title;

    @Column(name = "header_image", nullable = true)
    private String image;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Subtitle cannot be empty.")
    @Length(min = 5, max = 200, message="Subtitle must be between 5-200 characters.")
    private String subtitle;

    @Column(columnDefinition = "TEXT", length = 5000, nullable = false) // column, text for more, not-null
    @NotBlank(message = "Description cannot be empty.")
    @Length(min = 5, max = 5000, message="Description must be between 5-5000 characters.")
//    @Size.List({
//            @Size(min = 5, message = "Description must be at least 5 characters."),
//            @Size(max = 5000, message = "Description is too long. Must be 5000 characters or less.")
//    })
    private String description;

    @Column(name = "created_date")
//    @JsonFormat
//            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime date;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean disabled = false;

    //=============================== RELATIONSHIPS =========================================
    //=======================================================================================

    @ManyToOne //annotation: many posts can belong to one user.
//    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    // will define the foreign key. This Post class represents the posts table and we need a reference to the user
    @JsonManagedReference //allows Jackson to better handle the relation.
    // Is the forward part of reference – the one that gets serialized normally.
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL) // One post will have many comments
    @JsonBackReference //is the back part of reference – it will be omitted from serialization.
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    , orphanRemoval = true
    private List<PostVote> votes; // one post can have many votes, if a post is deleted, the votes disappear too.

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "posts_categories",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    @OrderBy("name ASC")
    @NotEmpty(message = "Categories cannot be empty")
    private List<Category> categories;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore
    private HitCount hitCount;

    //=============================== CONSTRUCTORS ==========================================
    //=======================================================================================

    //use for Spring magic.
    public Post() {}

    //use for form model binding
    public Post(String title, List<Category> categories, String image, String subtitle, String description) {
        this.title = title;
        this.categories = categories;
        this.image = image;
        this.subtitle = subtitle;
        this.description = description;
    }

    //use when the post is retrieved from the database.
    //gives me access to properties from User, user.getUsername(), or any other class inside constructor
    public Post(Long id, String title, String description, User user,
                LocalDateTime date,
                List<Comment> comments,
                List<PostVote> votes,
                String image,
                String subtitle,
                List<Category> categories,
                HitCount hitCount,
                boolean disabled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user;
        this.date = date;
        this.comments = comments;
        this.votes = votes;
        this.image = image;
        this.subtitle = subtitle;
        this.categories = categories;
        this.hitCount = hitCount;
        this.disabled = disabled;
    }

    //================================  GETTERS AND SETTERS =================================
    //=======================================================================================

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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<PostVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PostVote> votes) {
        this.votes = votes;
    }

    public HitCount getHitCount() {
        return hitCount;
    }

    public void setHitCount(HitCount hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    //==================================== METHODS ==========================================
    //=======================================================================================

//    public boolean disable() {
//        this.disabled = true;
//    }

    public boolean disable() {
        return this.disabled = true;
    }

    public boolean enable() {
        return this.disabled = false;
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

    // MARKDOWN PARSING FOR VIEW ==============================================================

    public String getHtmlTitle() {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(title));
    }

    public String getHtmlImage() {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(image));
    }

    public String getHtmlSubtitle() {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(subtitle));
    }

    public String getHtmlDescription() {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(description));
    }

    // TITLE STRING MANIPULATION ==============================================================

    public String titleToUppercase(String title) {

        StringBuffer sb = new StringBuffer();

        String[] sentence = title.split(" ");

//        String dontcap =
//                "a, an, the, and, as, as if, as long as, at, is, but, by, even if, for, from, if, if only, in, into, like, near, now, nor, of, off, on, on top of, once, onto, or, out of, over, past, so, than, that, till, to, up, upon, with, when, yet";
//
//        String[] wordsNotCapitalized = dontcap.split(",");
//
//        for(String wrd : wordsNotCapitalized) {
//            System.out.println(wrd);
//        }

        for (String word : sentence) {

            char[] letters = word.toCharArray(); // no need to trim()

            for (int i = 0; i < letters.length; i++) {
                if (letters[i] != '*' || letters[i] != '\"') {
                    // Capitalize the first non-asterisk (even if that doesn't change it)
                    letters[i] = Character.toUpperCase(letters[i]);
                    // No need to look any further
                    break;
                }

            }
            // That's it for capitalizing!
            word = new String(letters);
            sb.append(word).append(" ");
        }
        return sb.toString().trim();
    }

    // CREATING A SLUG FOR URL, this-is-a-slug-in-url ==================================================================

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String makeSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}


//    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
//    @JsonIgnore //annotation to simply ignore one of the sides of the relationship, thus breaking the chain.
//    private List<PostImage> images;

//    public List<PostImage> getImages() {
//        return images;
//    }
//
//    public void setImages(List<PostImage> images) {
//        this.images = images;
//    }