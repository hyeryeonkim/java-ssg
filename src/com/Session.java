package com;

public class Session {
	private Member loginedMember;
	private Board currentBoard;

	public Member getLoginedMember() {
		return loginedMember;
	}
	
	public void setLoginedMember(Member loginedMember) {
		this.loginedMember = loginedMember;
	}
	
	public Board getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(Board currentBoard) {
		this.currentBoard = currentBoard;
	}
}
