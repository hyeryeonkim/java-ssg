package com;

import java.util.List;

public class BoardController extends Controller {
	BoardService boardService;

	BoardController() {
		boardService = Factory.getBoardService();
	}

	@Override
	void doAction(Request request) {
		if (request.getActionName().equals("create")) {
			actionCreate(request);
		} else if (request.getActionName().equals("delete")) {
			actionDelete(request);
		} else if (request.getActionName().equals("list")) {
			actionList(request);
		} else if (request.getActionName().equals("change")) {
			actionChangeBoard(request);
		}
	}

	private void actionCreate(Request request) {
		if (Factory.getSession().getLoginedMember().getId() != 1) {
			System.out.printf("권한이 없습니다.\n");
		} else {
			System.out.printf("이름 : ");
			String name = Factory.getScanner().nextLine().trim();
			System.out.printf("code : ");
			String code = Factory.getScanner().nextLine().trim();
			int result = boardService.makeBoard(name, code);

			if (result == -1) {
			} else {
				System.out.printf("게시판 생성 성공\n");
			}
		}
	}

	private void actionDelete(Request request) {
		if (Factory.getSession().getLoginedMember().getId() != 1) {
			System.out.printf("권한이 없습니다.\n");
		} else {
			String code = request.getArg1();
			if (code != null) {
				Board board = boardService.getBoardByCode(code);
				if (board != null) {
					boardService.deleteBoard(board);
					System.out.println("게시판 삭제 성공");
				} else {
					System.out.printf("존재하지 않는 게시판 입니다.\n");
				}
			} else {
				System.out.printf("코드 입력\n");
			}
		}
	}

	private void actionList(Request request) {
		if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			int pageId = 1;
			pageId = Integer.parseInt(request.getArg1());
			List<Board> boards = boardService.getBoards();
			if (boards.size() > 0) {
				int newPageId = (boards.size() - 1) / 10 + 1;
				
				if (newPageId < pageId) {
					System.out.printf("존재하지 않는 페이지\n");
				} else {
					System.out.printf("%s|%s|%s|%s\n", "번호", "게시판 명", "게시판 코드명", "생성일");
					for (int i = boards.size() - 1 - (pageId - 1) * 10; i >= boards.size() - 10 - (pageId - 1) * 10; i--) {
						if (i >= 0) {
							Board board = boards.get(i);
							System.out.printf("%s|%s|%s|%s\n", board.getId(), board.getName(), board.getCode(), board.getRegDate());
						}
					}
					System.out.println();
					for (int first = 1; first <= newPageId; first++) {
						if (first == pageId) {
							System.out.printf("[%d] ", first);
						} else {
							System.out.printf("%d ", first);
						}
					}
					System.out.println();
				}
			} else if (boards.size() == 0) {
				System.out.println("게시판이 존재하지 않습니다.\n");
			}
		}
	}
	
	private void actionChangeBoard(Request request) {
		if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			String code = request.getArg1();
			if (code != null) {
				int boardId = boardService.getBoardIdByCode(code);
				if (boardId != -1) {
					boardService.changeBoard(boardId);
				}
			} 
			else {
				System.out.printf("코드 입력\n");
			}
		}
	}
}