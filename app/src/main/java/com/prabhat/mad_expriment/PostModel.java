package com.prabhat.mad_expriment;

public class PostModel {

    private final int id;
    private final String title;
    private final String body;
    private final String category;
    private final String author;
    private final String dateOrReadTime;
    private final String articleUrl;

    public PostModel(int id, String title, String body, String category, String author, String dateOrReadTime, String articleUrl) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.author = author;
        this.dateOrReadTime = dateOrReadTime;
        this.articleUrl = articleUrl;
    }

    public PostModel(int id, int userId, String title, String body) {
        this(id, title, body, "General Tech", "User " + userId, "REST API", "");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getDateOrReadTime() {
        return dateOrReadTime;
    }

    public String getArticleUrl() {
        return articleUrl;
    }
}