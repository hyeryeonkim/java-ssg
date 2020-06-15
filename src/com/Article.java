package com;

public class Article extends Dto {
	private int boardId;
	private int memberId;
	private String memberName;
	private String title;
	private String body;
	private int hit;
	
	public Article() {
		
	}
	
	public Article(int boardId, int memberId, String title, String body) {
		this(boardId, memberId, "", title, body);
	}

	public Article(int boardId, int memberId, String memberName, String title, String body) {
		this.boardId = boardId;
		this.memberId = memberId;
		this.memberName = memberName; 
		this.title = title;
		this.body = body;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	@Override
	public String toString() {
		return "Article [boardId=" + boardId + ", memberId=" + memberId + ", title=" + title + ", body=" + body
				+ ", getId()=" + getId() + ", getRegDate()=" + getRegDate() + "]";
	}
}