package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberController extends Controller {
	private MemberService memberService;

	MemberController() {
		memberService = Factory.getMemberService();
	}

	@Override
	void doAction(Request request) {
		if (request.getActionName().equals("join")) {
			actionJoin();
		} else if (request.getActionName().equals("login")) {
			actionLogin();
		} else if (request.getActionName().equals("logout")) {
			actionLogout();
		} else if (request.getActionName().equals("whoami")) {
			actionWhoami();
		}
	}

	private void actionJoin() {
		String loginId;
		String loginPw;
		String loginPwConfirm;
		String name;
		
		while(true) {
			System.out.printf("아이디 : ");
			loginId = Factory.getScanner().nextLine().trim();
			
			if (loginId.length() == 0) {
				System.out.printf("아이디를 입력해주세요.\n");
				continue;
			} else if (loginId.length() < 4) {
				System.out.printf("아이디를 4자 이상 입력해주세요.\n");
				continue;
			} else if (memberService.isUsedLoginId(loginId)) {
				System.out.printf("현재 사용중인 아이디 입니다.\n");
				continue;
			}
			break;
		}
			
		while(true) {
			boolean loginPwValid = true;
			
			String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,12}$";
			String pwPattern2 = "(.)\\1\\1\\1";
			
			while(true) {
				System.out.printf("비밀번호 : ");
				loginPw = Factory.getScanner().nextLine().trim();
				Matcher matcher = Pattern.compile(pwPattern).matcher(loginPw);
				Matcher matcher2 = Pattern.compile(pwPattern2).matcher(loginPw);

				if (loginPw.length() == 0) {
					System.out.printf("비밀번호를 입력해주세요.\n");
					continue;
				} 
				if (!matcher.matches()) {
					System.out.printf("비밀번호는 영문, 숫자, 특수문자 포함, 9~12자리 입니다.\n");
					continue;
				} 
				if (matcher2.find()) {
					System.out.printf("같은문자는 4개 이상 사용 불가\n");
					continue;
				}
				if(loginPw.contains(loginId)){
				    System.out.printf("아이디는 비밀번호에 사용할 수 없습니다.\n");
				}
				if (loginPw.contains(" ")) {
					System.out.printf("비밀번호 중간에 공백 사용 불가\n");
					continue;
				} 
				break;
			}
			while(true) {
				System.out.printf("비밀번호 확인 : ");
				loginPwConfirm = Factory.getScanner().nextLine().trim();
				Matcher matcher = Pattern.compile(pwPattern).matcher(loginPwConfirm);
				Matcher matcher2 = Pattern.compile(pwPattern2).matcher(loginPwConfirm);
				
				if (loginPwConfirm.length() == 0) {
					System.out.printf("비밀번호를 입력해주세요.\n");
					continue;
				} 
				if (loginPwConfirm.length() < 9 || matcher.matches() == false) {
					System.out.printf("비밀번호는 영문, 숫자, 특수문자 포함, 9~12자리 입니다.\n");
					continue;
				} 
				if (matcher2.find()) {
					System.out.printf("같은문자는 4개 이상 사용 불가\n");
					continue;
				}
				if(loginPw.contains(loginId)){
				    System.out.printf("아이디는 비밀번호에 사용할 수 없습니다.\n");
				}
				if (loginPwConfirm.contains(" ")) {
					System.out.printf("비밀번호 중간에 공백 사용 불가\n");
					continue;
				} 
				if (loginPwConfirm.equals(loginPw) == false) {
					System.out.printf("비밀번호와 일치하지 않습니다.\n");
					loginPwValid = false;
				}
				break;
			}
			if (loginPwValid) {
				break;
			}
		}
		while(true) {
			System.out.printf("이름 : ");
			name = Factory.getScanner().nextLine().trim();
			
			if (loginId.length() == 0) {
				System.out.printf("이름을 입력해주세요.\n");
				continue;
			} else if (loginId.length() < 2) {
				System.out.printf("이름을 2자 이상 입력해주세요.\n");
				continue;
			}
			break;
		}
		memberService.join(loginId, loginPw, name);
		System.out.printf("회원가입 성공\n");
	}

	private void actionLogin() {
		if (Factory.getSession().getLoginedMember() != null) {
			System.out.printf("현재 로그인 상태\n");
		} else {
			System.out.printf("로그인 아이디 : ");
			String loginId = Factory.getScanner().nextLine().trim();

			System.out.printf("로그인 비번 : ");
			String loginPw = Factory.getScanner().nextLine().trim();

			Member member = memberService.getMemberByLoginIdAndLoginPw(loginId, loginPw);

			if (member == null) {
				System.out.printf("일치하는 회원이 없습니다.\n");
			} else {
				System.out.printf(member.getName() + " 회원 : 로그인 성공\n");
				Factory.getSession().setLoginedMember(member);
			}
		}
	}

	private void actionLogout() {
		Member loginedMember = Factory.getSession().getLoginedMember();
		
		if (loginedMember != null) {
			System.out.printf("로그아웃 성공\n");
			Factory.getSession().setLoginedMember(null);
		} else {
			System.out.printf("로그아웃 상태\n");
		}
	}

	private void actionWhoami() {
		Member loginedMember = Factory.getSession().getLoginedMember();
		
		if (loginedMember != null) {
			System.out.printf("회원 : %s\n", loginedMember.getName());
		} else {
			System.out.printf("비 회원\n");
		}
	}
}