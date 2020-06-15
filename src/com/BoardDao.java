package com;

import java.util.List;

public class BoardDao {
	DB db;

	BoardDao() {
		db = Factory.getDB();
	}
	
	// App 현재 게시판 설정
	public Board getBoard(int id) {
		return db.getBoard(id);
	}
	
	public Board getBoardByNameAndCode(String name, String code) {
		return db.getBoardByNameAndCode(name, code);
	}

	public int save(Board board) {
		return db.save(board);
	}

	public Board getBoardByCode(String code) {
		return db.getBoardByCode(code);
	}

	public void delete(Board board) {
		db.delete(board);
	}

	public List<Board> getBoards() {
		return db.getBoards();
	}

	public int getBoardIdByCode(String code) {
		return db.getBoardIdByCode(code);
	}

	public void change(int id) {
		db.change(id);
	}
}