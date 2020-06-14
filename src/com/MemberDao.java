package com;

public class MemberDao {
	private DB db;
	
	MemberDao() {
		db = Factory.getDB();
	}
	
	// # 로그인 아이디 중복
	public Member getMemberByLoginId(String loginId) {
		return db.getMemberByLoginId(loginId);
	}

	// Join
	public void save(Member member) {
		db.save(member);
	}
	
	// Login
	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return db.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}
}
