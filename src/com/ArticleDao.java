package com;

import java.util.List;

public class ArticleDao {
	DB db;

	ArticleDao() {
		db = Factory.getDB();
	}

	public int save(Article article) {
		return db.save(article);
	}

	public Article getArticleById(int id) {
		return db.getArticleById(id);
	}

	public void modify(Article article, int articleId) {
		db.modify(article, articleId);
	}

	public boolean delete(int articleId) {
		return db.delete(articleId);
	}

	public List<Article> getArticlesByPageIdAndKeyword(int pageId, String keyword) {
		return db.getArticlesByPageIdAndKeyword(pageId, keyword);
	}

	public Article detail(int articleId) {
		return db.detail(articleId);
	}
}