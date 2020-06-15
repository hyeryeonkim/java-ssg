package com;

public class MemberService {
	private MemberDao memberDao;
	
	MemberService() {
		memberDao = Factory.getMemberDao();
	}
	
	// # 로그인 아이디 중복 
	public boolean isUsedLoginId(String loginId) {
		Member member = memberDao.getMemberByLoginId(loginId);
		
		if (member == null) {
			return false;
		}
		return true;
	}
	
	// Join
	public void join(String loginId, String loginPw, String name) {
		Member oldMember = memberDao.getMemberByLoginId(loginId);
		if (oldMember == null) {
			Member member = new Member(loginId, loginPw, name);
			memberDao.save(member);
		}
		return;
	}

	// Login
	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return memberDao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}
}