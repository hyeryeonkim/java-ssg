package com;

import java.util.HashMap;
import java.util.Map;

public class App {
	private Map<String, Controller> controllers;
	
	App() {
		initControllers();
		Factory.getBoardService().makeBoard("공지사항", "notice");
		Factory.getBoardService().makeBoard("자유게시판", "free");
		Factory.getMemberService().join("admin", "admin", "관리자");
		Factory.getSession().setCurrentBoard(Factory.getBoardService().getBoard(1));
	}
	
	void initControllers() {
		controllers = new HashMap<>();
		controllers.put("build", new BuildController());
		controllers.put("board", new BoardController());
		controllers.put("article", new ArticleController());
		controllers.put("member", new MemberController());
	}
	
	void start() {
		while(true) {
			System.out.printf("명령어 : ");
			String command = Factory.getScanner().nextLine().trim();
			
			if (command.length() == 0) {
				continue;
			} else if (command.equals("exit")) {
				break;
			}
			
			Request request = new Request(command);
			
			if (request.isValidRequest() == false) {
				continue;
			}
			
			if (controllers.containsKey(request.getControllerName()) == false) {
				continue;
			}
			controllers.get(request.getControllerName()).doAction(request);
		}
		Factory.getScanner().close();
	}
}