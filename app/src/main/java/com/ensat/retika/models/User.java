/**
 * Model class representing a User.
 * Contains user information such as ID, username, email, profile picture, and relationships (followers and following).
 */
package com.ensat.retika.models;

import java.util.List;

public class User {
    private String userId;
    private String username;
    private String email;
    private String profilePictureUrl;
    private long subscribers; // Number of followers
    private List<String> followers; // List of user IDs who follow this user
    private List<String> following; // List of user IDs this user follows

    /**
     * Default constructor for Firestore.
     */
    public User() {}

    /**
     * Constructor to initialize a User object.
     *
     * @param userId             Unique identifier for the user.
     * @param username           The username of the user.
     * @param email              The email address of the user.
     * @param profilePictureUrl  URL of the user's profile picture.
     * @param subscribers        Number of followers (subscribers).
     */
    public User(String userId, String username, String email, String profilePictureUrl, long subscribers) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.subscribers = subscribers;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the username of the user.
     *
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username Username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of the user.
     *
     * @return Email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email Email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the URL of the user's profile picture.
     *
     * @return Profile picture URL.
     */
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * Sets the URL of the user's profile picture.
     *
     * @param profilePictureUrl Profile picture URL.
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Gets the number of subscribers (followers) of the user.
     *
     * @return Number of subscribers.
     */
    public long getSubscribers() {
        return subscribers;
    }

    /**
     * Sets the number of subscribers (followers) of the user.
     *
     * @param subscribers Number of subscribers.
     */
    public void setSubscribers(long subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Gets the list of user IDs who follow this user.
     *
     * @return List of follower user IDs.
     */
    public List<String> getFollowers() {
        return followers;
    }

    /**
     * Sets the list of user IDs who follow this user.
     *
     * @param followers List of follower user IDs.
     */
    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    /**
     * Gets the list of user IDs this user follows.
     *
     * @return List of following user IDs.
     */
    public List<String> getFollowing() {
        return following;
    }

    /**
     * Sets the list of user IDs this user follows.
     *
     * @param following List of following user IDs.
     */
    public void setFollowing(List<String> following) {
        this.following = following;
    }
}
