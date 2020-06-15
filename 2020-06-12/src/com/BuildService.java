package com;

import java.util.List;

class BuildService {
	ArticleService articleService;
	private static boolean workStarted;

	static {
		workStarted = false;
	}

	BuildService() {
		articleService = Factory.getArticleService();
	}

	// 각 게시판 별 게시물리스트 페이지 생성
	public void buildSite() {
		Util.makeDir("site");
		Util.makeDir("site/article");

		String head = Util.getFileContents("site_template/part/head.html");
		String foot = Util.getFileContents("site_template/part/foot.html");

		// 각 게시판 별 게시물리스트 페이지 생성
		List<Board> boards = Factory.getBoardService().getBoards();

		for (Board board : boards) {
			
			String fileName = board.getCode() + "-list-1.html";

			String html = "";

			List<Article> articles = Factory.getDB().getArticlesByBoardCode(board.getCode());
			
//			String template = Util.getFileContents("site_template/article/list.html");
			
			html += "<table border=\"1\" width=\"600px\">";
			html += "<caption>" + board.getCode() + " 게시물 리스트</caption>";
		    html += "<colgroup>";
		    html += "<col width=\"7%\">";
		    html += "<col width=\"+%\">";
		    html += "<col width=\"10%\">";
		    html += "<col width=\"28%\">";
		    html += "<col width=\"13%\">";
		    html += "</colgroup>";
			
			html += "<thead>";
			html += "<tr>";
			html += "<th>번호</th>";
			html += "<th>제목</th>";
			html += "<th>작성자</th>";
			html += "<th>작성일</th>";
			html += "<th>상세보기</th>";
			html += "</thead>";
			
			for (Article article : articles) {
				html += "<tbody>";
				html += "<tr>";
				html += "<td>" + article.getId() + "</td>";
				html += "<td>" + article.getTitle() + "</td>";
				html += "<td>" + article.getMemberName() + "</td>";
				html += "<td>" + article.getRegDate() + "</td>";
				html += "<td><a href=\"" + article.getId() + ".html\">" + article.getTitle() + "</a></td>";
				html += "</tr>";
				html += "</tbody>";
			}
			html += "</table>";

//			html = template.replace("${TR}", html);

			html = head + html + foot;

			Util.writeFileContents("site/article/" + fileName, html);
		}
		
		// 게시물 별 파일 생성
		List<Article> articles = Factory.getDB().getArticles();

		for (Article article : articles) {
			String html = "";
			html += "<div>번호 : " + article.getId() + "</div>";
			html += "<div>제목 : " + article.getTitle() + "</div>";
			html += "<div>내용 : " + article.getBody() + "</div>";
			html += "<div>작성자 : " + article.getMemberName() + "</div>";
			html += "<div>작성일 : " + article.getRegDate() + "</div>";
			html += "<div>조회수 : " + article.getHit() + "</div>";
//			if (article.getId() - 1 == 0) {
//				
//			}
			html += "<div><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></div>";
			html += "<div><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></div>";

			html = head + html + foot;

			Util.writeFileContents("site/article/" + article.getId() + ".html", html);
		}
	}

	public void buildStartAutoSite() {
		workStarted = true;
		
		new Thread(() -> {
			while (workStarted) {
				buildSite();
				System.out.println("생성");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void buildStropAutoSite() {
		try {
			System.out.println("10초 후 종료");
			Thread.sleep(10000);
			System.out.println("자동 생성 종료");
		} catch (InterruptedException e) {
		}
		workStarted = false;
	}
}