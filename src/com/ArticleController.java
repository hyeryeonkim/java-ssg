package com;

import java.util.List;

public class ArticleController extends Controller {
	private ArticleService articleService;
	int hit = 0;

	ArticleController() {
		articleService = Factory.getArticleService();
	}

	@Override
	void doAction(Request request) {
		if (request.getActionName().equals("write")) {
			actionWrite(request);
		} else if (request.getActionName().equals("modify")) {
			actionModify(request);
		} else if (request.getActionName().equals("delete")) {
			actionDelete(request);
		} else if (request.getActionName().equals("list")) {
			actionList(request);
		} else if (request.getActionName().equals("detail")) {
			actionListDetail(request);
		}
	}

	private void actionWrite(Request request) {
		if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			System.out.printf("제목 : ");
			String title = Factory.getScanner().nextLine();
			System.out.printf("내용 : ");
			String body = Factory.getScanner().nextLine();

			int boardId = Factory.getSession().getCurrentBoard().getId();
			int memberId = Factory.getSession().getLoginedMember().getId();
			String memberName = Factory.getSession().getLoginedMember().getName();
			int newId = articleService.write(boardId, memberId, memberName, title, body);

			System.out.printf("%d번 글 생성 - 작성자 : %s\n", newId, memberName);
		}
	}

	private void actionModify(Request request) {
		if (request.getArg1() == null) {
			System.out.printf("번호 입력\n");
		} else if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			int articleId = Integer.parseInt(request.getArg1());
			Article article = articleService.getArticleById(articleId);
			int loginedId = Factory.getSession().getLoginedMember().getId();
			String loginedName = Factory.getSession().getLoginedMember().getName();

			if (article == null) {
				System.out.printf("존재하지 않는 게시물\n");
			} else if (article.getMemberId() != loginedId) {
				System.out.printf("게시물 작성자 : %s, 현재 회원 : %s\n", article.getMemberName(), loginedName);
			} else {
				int boardId = Factory.getSession().getCurrentBoard().getId();
				int memberId = Factory.getSession().getLoginedMember().getId();

				System.out.printf("제목 : ");
				String title = Factory.getScanner().nextLine();
				System.out.printf("내용 : ");
				String body = Factory.getScanner().nextLine();
				articleService.modify(boardId, articleId, memberId, title, body);

				System.out.printf("%d번 게시물 수정 완료\n", articleId);
			}
		}
	}

	private void actionDelete(Request request) {
		if (request.getArg1() == null) {
			System.out.printf("번호 입력\n");
		} else if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			int articleId = Integer.parseInt(request.getArg1());
			Article article = articleService.getArticleById(articleId);
			int loginedId = Factory.getSession().getLoginedMember().getId();
			String loginedName = Factory.getSession().getLoginedMember().getName();

			if (articleService.delete(articleId) == false) {
				System.out.printf("존재하지 않는 게시물\n");
			} else if (article.getMemberId() != loginedId) {
				System.out.printf("게시물 작성자 : %s, 현재 회원 : %s\n", article.getMemberName(), loginedName);
			} else {
				System.out.printf("게시물 삭제 완료\n");
			}
		}
		
	}

	private void actionList(Request request) {
		if (request.getArg1() == null) {
			System.out.printf("번호 입력\n");
		} else if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			int pageId = 1;
			pageId = Integer.parseInt(request.getArg1());
			String keyword = "";
			try {
				if (request.getArg2().trim().length() > 0) {
					keyword = request.getArg2();
				}
			} catch (Exception e) {
			}

			List<Article> articles = articleService.getArticlesByPageIdAndKeyword(pageId, keyword);
			if (articles.size() > 0) {
				int newPageId = (articles.size() - 1) / 10 + 1;

				if (newPageId < pageId) {
					System.out.printf("존재하지 않는 페이지\n");
				} else {
					System.out.printf("%s|%s|%s|%s\n", "번호", "제목", "작성일", "작성자");
					for (int i = articles.size() - 1 - (pageId - 1) * 10; i >= articles.size() - 10
							- (pageId - 1) * 10; i--) {
						if (i >= 0) {
							Article article = articles.get(i);
							System.out.printf("%s|%s|%s|%s\n", article.getId(), article.getTitle(),
									article.getRegDate(), article.getMemberName());
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
			} else if (articles.size() == 0) {
				System.out.println("존재하지 않는 게시물\n");
			}
		}
	}

	private void actionListDetail(Request request) {
		if (request.getArg1() == null) {
			System.out.printf("번호 입력\n");
		} else if (Factory.getSession().getLoginedMember() == null) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			int ArticleId = Integer.parseInt(request.getArg1());
			Article article = articleService.getArticleDetailById(ArticleId);
			if (article != null) {
				++hit;
				article.setHit(hit);
				
				System.out.println(article.getId() + "번 게시물 상세보기");
				System.out.printf("제목 : %s\n" , article.getTitle());
				System.out.printf("내용 : %s\n" , article.getBody());
				System.out.printf("작성자 : %s\n" , article.getMemberName());
				System.out.printf("작성일 : %s\n" , article.getRegDate());
				System.out.printf("조회수 : %d\n" , article.getHit());
			} else {
				System.out.println("존재하지 않는 게시물입니다.");
			}
		}
	}
}