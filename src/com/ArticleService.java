package com;

import java.util.List;

public class ArticleService {
	private ArticleDao articleDao;

	ArticleService() {
		articleDao = Factory.getArticleDao();
	}
	
	public int write(int boardId, int memberId, String memberName, String title, String body) {
		Article article = new Article(boardId, memberId, memberName, title, body);
		return articleDao.save(article);
	}

	public Article getArticleById(int id) {
		return articleDao.getArticleById(id);
	}

	public void modify(int boardId, int articleId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		articleDao.modify(article, articleId);
	}

	public boolean delete(int articleId) {
		return articleDao.delete(articleId);
	}

	public List<Article> getArticlesByPageIdAndKeyword(int pageId, String keyword) {
		return articleDao.getArticlesByPageIdAndKeyword(pageId, keyword);
	}

	public Article getArticleDetailById(int articleId) {
		return articleDao.detail(articleId);
	}
}