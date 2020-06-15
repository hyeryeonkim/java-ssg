package com;

import java.util.Scanner;

public class Factory {
	private static Scanner scanner;
	private static Session session;
	private static BuildService buildService;
	private static BoardService boardService;
	private static ArticleService articleService;
	private static MemberService memberService;
	private static BoardDao boardDao;
	private static ArticleDao articleDao;
	private static MemberDao memberDao;
	private static DB db;
	
	public static Scanner getScanner() {
		if (scanner == null) {
			scanner = new Scanner(System.in);
		}
		return scanner;
	}
	public static Session getSession() {
		if (session == null) {
			session = new Session();
		}
		return session;
	}
	public static BuildService getBuildService() {
		if (buildService == null) {
			buildService = new BuildService();
		}
		return buildService;
	}
	public static BoardService getBoardService() {
		if (boardService == null) {
			boardService = new BoardService();
		}
		return boardService;
	}
	public static ArticleService getArticleService() {
		if (articleService == null) {
			articleService = new ArticleService();
		}
		return articleService;
	}
	public static MemberService getMemberService() {
		if (memberService == null) {
			memberService = new MemberService();
		}
		return memberService;
	}
	public static BoardDao getBoardDao() {
		if (boardDao == null) {
			boardDao = new BoardDao();
		}
		return boardDao;
	}
	public static ArticleDao getArticleDao() {
		if (articleDao == null) {
			articleDao = new ArticleDao();
		}
		return articleDao;
	}
	public static MemberDao getMemberDao() {
		if (memberDao == null) {
			memberDao = new MemberDao();
		}
		return memberDao;
	}
	public static DB getDB() {
		if (db == null) {
			db = new DB();
		}
		return db;
	}
}