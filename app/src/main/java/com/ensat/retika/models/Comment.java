/**
 * Model class representing a Comment.
 * Contains comment information such as ID, user ID, text, and timestamp.
 */
package com.ensat.retika.models;

public class Comment {
    private String commentId;
    private String userId;
    private String commentText;
    private long timestamp;

    /**
     * Default constructor for Firestore.
     */
    public Comment() {}

    /**
     * Constructor to initialize a Comment object.
     *
     * @param commentId   Unique identifier for the comment.
     * @param userId      ID of the user who made the comment.
     * @param commentText Text of the comment.
     * @param timestamp   Timestamp indicating when the comment was made.
     */
    public Comment(String commentId, String userId, String commentText, long timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    /**
     * Gets the unique identifier of the comment.
     *
     * @return Comment ID.
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * Sets the unique identifier of the comment.
     *
     * @param commentId Comment ID.
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     * Gets the ID of the user who made the comment.
     *
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who made the comment.
     *
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the text of the comment.
     *
     * @return Comment text.
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the text of the comment.
     *
     * @param commentText Comment text.
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Gets the timestamp indicating when the comment was made.
     *
     * @return Timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp indicating when the comment was made.
     *
     * @param timestamp Timestamp.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
