package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {
	private Map<String, Table> tables;

	public DB() {
		String dbDirPath = getDirPath();
		Util.makeDir(dbDirPath);
		
		tables = new HashMap<>();
		tables.put("member", new Table<Member>(Member.class, dbDirPath));
		tables.put("article", new Table<Article>(Article.class, dbDirPath));
		tables.put("board", new Table<Board>(Board.class, dbDirPath));
	}
	
	private String getDirPath() {
		return "db";
	}
	
	// # 로그인 아이디 중복
	public Member getMemberByLoginId(String loginId) {
		List<Member> members = getMembers();
		
		for (Member member : members) {
			if (member.getLoginId().equals(loginId)) {
				return member;
			}
		}
		return null;
	}
	
	private List<Member> getMembers() {
		return tables.get("member").getRows();
	}
	
	// Join
	public void save(Member member) {
		tables.get("member").saveRow(member);
	}
	
	// Login
	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		List<Member> members = getMembers();

		for (Member member : members) {
			if (member.getLoginId().equals(loginId) && member.getLoginPw().equals(loginPw)) {
				return member;
			}
		}
		return null;
	}
	
	// Write
	public int save(Article article) {
		return tables.get("article").saveRow(article);
	}
	
	// Modify
	public Article getArticleById(int id) {
		return (Article) tables.get("article").getRow(id);
	}
	
	public void modify(Article article, int articleId) {
		tables.get("article").saveRow(article, articleId);
	}
	
	// Article Delete
	public boolean delete(int articleId) {
		return tables.get("article").delete(articleId);
	}
	
	// Article Title List
	public List<Article> getArticlesByPageIdAndKeyword(int pageId, String keyword) {
		List<Article> articles = new ArrayList<>();
		for (Article article : getArticles()) {
			int articleBoardId = article.getBoardId();
			int currentBoardId = Factory.getSession().getCurrentBoard().getId();
			
			if (articleBoardId == currentBoardId) {
				String articleTitle = article.getTitle();
				String articleBody = article.getBody(); 
				if (articleTitle.contains(keyword) || articleBody.contains(keyword)) {
					articles.add(article);
				}
			}
		}
		return articles;
	}
	
	List<Article> getArticles() {
		return tables.get("article").getRows();
	}
	
	// Detail
	public Article detail(int articleId) {
		Article article = getArticleById(articleId);
		return article;
	}
	
	// App 현재 게시판 설정
	public Board getBoard(int id) {
		return (Board) tables.get("board").getRow(id);
	}
	
	// Create
	public Board getBoardByNameAndCode(String name, String code) {
		List<Board> boards = getBoards();

		for (Board board : boards) {
			if (board.getCode().equals(code) && board.getName().equals(name)) {
				return board;
			}
		}
		return null;
	}
	
	public List<Board> getBoards() {
		return tables.get("board").getRows();
	}
	
	public int save(Board board) {
		return tables.get("board").saveRow(board);
	}
	
	// Delete
	public Board getBoardByCode(String code) {
		List<Board> boards = getBoards();

		for (Board board : boards) {
			if (board.getCode().equals(code)) {
				return board;
			}
		}
		return null;
	}
	
	public void delete(Board board) {
		tables.get("board").delete(board);
	}
	
	// list
	public Member getMember(int id) {
		return (Member) tables.get("member").getRow(id);
	}
	
	public int getBoardIdByCode(String code) {
		for (Board board : getBoards()) {
			if (board.getCode().equals(code)) {
				return board.getId();
			}
		}
		return -1;
	}
	
	public void change(int id) {
		int currentBoardId = Factory.getSession().getCurrentBoard().getId();
		Board changeBoard = Factory.getBoardService().getBoard(id);
		
		if (id != currentBoardId) {
			Factory.getSession().setCurrentBoard(changeBoard);
			System.out.printf("%s 게시판으로 이동\n", changeBoard.getName());
		} 
	}
	
	public List<Article> getArticlesByBoardCode(String code) {
		List<Article> articles = new ArrayList<>();
		for (Article article : getArticles()) {
			if (getBoardByCode(code).getId() == article.getBoardId()) {
				articles.add(article);
			}
		}
		return articles;
	}
	
//	public void saveBoard(Board board) {
//		tables.get("board").saveRow(board);
//	}	
}