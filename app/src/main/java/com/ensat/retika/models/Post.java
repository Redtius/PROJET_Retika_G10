/**
 * Model class representing a Post.
 * Contains post information such as ID, user ID, image URL, caption, timestamp, likes, and comments.
 */
package com.ensat.retika.models;

import java.util.List;

public class Post {
    private String postId;
    private String userId;
    private String imageUrl;
    private String caption;
    private long timestamp;
    private List<String> likedBy; // List of user IDs who liked this post
    private List<Comment> comments; // List of comments

    /**
     * Default constructor for Firestore.
     */
    public Post() {}

    /**
     * Constructor to initialize a Post object.
     *
     * @param postId    Unique identifier for the post.
     * @param userId    ID of the user who created the post.
     * @param imageUrl  URL of the image associated with the post.
     * @param caption   Caption of the post.
     * @param timestamp Timestamp indicating when the post was created.
     */
    public Post(String postId, String userId, String imageUrl, String caption, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    /**
     * Gets the unique identifier of the post.
     *
     * @return Post ID.
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the unique identifier of the post.
     *
     * @param postId Post ID.
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the ID of the user who created the post.
     *
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who created the post.
     *
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the URL of the image associated with the post.
     *
     * @return Image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image associated with the post.
     *
     * @param imageUrl Image URL.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the caption of the post.
     *
     * @return Caption.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the post.
     *
     * @param caption Caption.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the timestamp indicating when the post was created.
     *
     * @return Timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp indicating when the post was created.
     *
     * @param timestamp Timestamp.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the list of user IDs who liked this post.
     *
     * @return List of user IDs.
     */
    public List<String> getLikedBy() {
        return likedBy;
    }

    /**
     * Sets the list of user IDs who liked this post.
     *
     * @param likedBy List of user IDs.
     */
    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    /**
     * Gets the list of comments associated with this post.
     *
     * @return List of comments.
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Sets the list of comments associated with this post.
     *
     * @param comments List of comments.
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}