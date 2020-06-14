package com;

import java.util.List;

import java.util.List;

public class BoardService {
	private BoardDao boardDao;

	BoardService() {
		boardDao = Factory.getBoardDao();
	}
	
	// App 현재 게시판 설정
	public Board getBoard(int id) {
		return boardDao.getBoard(id);
	}
	
	public int makeBoard(String name, String code) {
		Board oldBoard = boardDao.getBoardByNameAndCode(name, code);

		if (oldBoard != null) {
			return -1;
		}

		Board board = new Board(name, code);
		return boardDao.save(board);
	}

	public Board getBoardByCode(String code) {
		return boardDao.getBoardByCode(code);
	}

	public void deleteBoard(Board board) {
		boardDao.delete(board);
	}

	public List<Board> getBoards() {
		return boardDao.getBoards();
	}

	public int getBoardIdByCode(String code) {
		return boardDao.getBoardIdByCode(code);
	}

	public void changeBoard(int boardId) {
		boardDao.change(boardId);
	}
}