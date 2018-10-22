package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "user_receiver_id")
    private User receiver;

    @Column
    private LocalDateTime created_on;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean unread = true;

    public Message(){}

    public Message(String body, User sender, User receiver, LocalDateTime created_on, boolean unread) {
        this.body = body;
        this.sender = sender;
        this.receiver = receiver;
        this.created_on = created_on;
        this.unread = unread;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getCreated_on() {
        return created_on;
    }

    public void setCreated_on(LocalDateTime created_on) {
        this.created_on = created_on;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
