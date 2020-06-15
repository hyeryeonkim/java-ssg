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
			String html = "<div class=\"boardName\">" + board.getName() + "</div>";

			List<Article> articles = Factory.getDB().getArticlesByBoardCode(board.getCode());
			
			html += Util.getFileContents("site_template/article/list.html");
			html += "<table class=\"list\" border=\"1\">";
			
			html += "<thead>";
			html += "<tr>";
			html += "<th>번호</th>";
			html += "<th>제목</th>";
			html += "<th>작성자</th>";
			html += "<th>작성일</th>";
			html += "<th>조회</th>";
			html += "</thead>";
			
			for (Article article : articles) {		
				html += "<tbody>";
				html += "<tr>";
				html += "<td>" + article.getId() + "</td>";
				html += "<td><a href=\"" + article.getId() + ".html\">" + article.getTitle() + "</a></td>";
				html += "<td>" + article.getMemberName() + "</td>";
				html += "<td>" + article.getRegDate() + "</td>";
				html += "<td>" + article.getHit() + "</td>"; // 조회수로 바꾸기
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
		// home파일 생성(index.html 대신 사용)
		String home = Util.getFileContents("site_template/article/homeImage.html"); // 혜련 추가
		String homeFileName = "home.html";
		String homeHtml = "";
		homeHtml = head + homeHtml + home + foot;
		Util.writeFileContents("site_template/part/" + homeFileName, homeHtml);
		
		//통계
//		List<Board> allBoard = Factory.getDB().getBoards();
//		
//		for (int i = 0; i <= allBoard.size(); i++) {
//			 
//		}
		List<Member> members = Factory.getDB().getMembers();
		List<Article> articles2 = Factory.getDB().getArticles();
		List<Article> frees = Factory.getDB().getArticlesByBoardCode("free");
		List<Article> notice = Factory.getDB().getArticlesByBoardCode("notice");
		String fileName3 = "stat.html";
		String template3 = Util.getFileContents("site_template/article/stat.html");
		
		String stat = "";
		stat += template3;
		stat += " <div class=\"boardName\">통계</div>";
		stat += "<table border=\"1\">";
		stat += "<thead>";
		
		stat += "<tr>";
		stat += "<th>회원수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>전체 게시물 수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>자유게시판 게시물 수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>공지사항 게시물 수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>전체 게시물 조회 수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>자유게시판 게시물 조회수</th>";
		stat += "</tr>";
		
		stat += "<tr>";
		stat += "<th>공지사항 게시물 조회수</th>";
		stat += "</tr>";
		stat += "</thead>";
		
		stat += "<tbody>";
		stat += "<tr>";
		stat += "<td>" + members.size() + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + articles2.size() + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + frees.size() + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + notice.size() + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + "미구현" + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + "미구현" + "</td>";
		stat += "</tr>";
		stat += "<tr>";
		stat += "<td>" + "미구현" + "</td>";
		stat += "</tr>";
		stat += "</tbody>";

		stat = head + stat + foot;

		Util.writeFileContents("site/article/" + fileName3, stat);
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