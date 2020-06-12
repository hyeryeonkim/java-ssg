import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Main {
	public static void main(String[] args) {
		App app = new App();
		app.start();
	}
}

// Session
// 현재 사용자가 이용중인 정보
// 이 안의 정보는 사용자가 프로그램을 사용할 때 동안은 계속 유지된다.
class Session {
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

	public boolean isLogined() {
		return loginedMember != null;
	}
}

// Factory
// 프로그램 전체에서 공유되는 객체 리모콘을 보관하는 클래스
class Factory {
	private static Session session;
	private static DB db;
	private static ArticleService articleService;
	private static ArticleDao articleDao;
	private static MemberService memberService;
	private static MemberDao memberDao;
	private static BuildService buildService;
	private static Scanner scanner;

	public static Session getSession() {
		if (session == null) {
			session = new Session();
		}

		return session;
	}

	public static Scanner getScanner() {
		if (scanner == null) {
			scanner = new Scanner(System.in);
		}

		return scanner;
	}

	public static DB getDB() {
		if (db == null) {
			db = new DB();
		}

		return db;
	}

	public static ArticleService getArticleService() {
		if (articleService == null) {
			articleService = new ArticleService();
		}

		return articleService;
	}

	public static ArticleDao getArticleDao() {
		if (articleDao == null) {
			articleDao = new ArticleDao();
		}
		return articleDao;

	}

	public static MemberService getMemberService() {
		if (memberService == null) {
			memberService = new MemberService();
		}
		return memberService;
	}

	public static MemberDao getMemberDao() {
		if (memberDao == null) {
			memberDao = new MemberDao();
		}

		return memberDao;
	}

	public static BuildService getBuildService() {
		if ( buildService == null ) {
			buildService = new BuildService();
		}
		return buildService;
	}
}

// App
class App {
	private Map<String, Controller> controllers;

	void initControllers() {
		controllers = new HashMap<>();
		controllers.put("build", new BuildController());
		controllers.put("article", new ArticleController());
		controllers.put("member", new MemberController());
	}

	void currentBoardName() {
		System.out.printf("[ 접속 게시판 : %s ]\n", Factory.getSession().getCurrentBoard().getName());
	}
	//구현
	public void showHelp() {
		
	}

	public App() {
		showHelp();
		// 컨트롤러 등록
		initControllers();

		// 관리자 회원 생성
		Factory.getMemberService().join("admin", "admin", "관리자");
		// 공지사항 게시판 생성
		Factory.getArticleService().makeBoard("공지사항", "notice");
		// 자유 게시판 생성
		Factory.getArticleService().makeBoard("자유게시판", "free");

		// 현재 게시판을 1번 게시판으로 선택
		Factory.getSession().setCurrentBoard(Factory.getArticleService().getBoard(1));
		// 임시 : 현재 로그인 된 회원은 1번 회원으로 지정, 이건 나중에 회원가입, 로그인 추가되면 제거해야함
		Factory.getSession().setLoginedMember(Factory.getMemberService().getMember(1));
	}

	public void start() {
		// 접속 게시판 항시 표기
		while (true) {
			currentBoardName();
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

// Request
class Request {
	private String requestStr;
	private String controllerName;
	private String actionName;
	private String articleId;
	private String googling;
	private String arg3;

	boolean isValidRequest() {
		return actionName != null;
	}

	Request(String requestStr) {
		this.requestStr = requestStr;
		String[] requestStrBits = requestStr.split(" ");
		this.controllerName = requestStrBits[0];

		if (requestStrBits.length > 1) {
			this.actionName = requestStrBits[1];
		}

		if (requestStrBits.length > 2) {
			this.articleId = requestStrBits[2];
		}

		if (requestStrBits.length > 3) {
			this.googling = requestStrBits[3];
		}

		if (requestStrBits.length > 4) {
			this.arg3 = requestStrBits[4];
		}
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getGoogling() {
		return googling;
	}

	public void setGoogling(String googling) {
		this.googling = googling;
	}

	public String getArg3() {
		return arg3;
	}

	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}
}

// Controller
abstract class Controller {
	abstract void doAction(Request request);
}
class BuildController extends Controller {
	private BuildService buildService;
	BuildController() {
		buildService = Factory.getBuildService();
	}
	void doAction(Request request) {
		if ( request.getActionName().equals("site")) {
			actionSite();
		}
	}

	private void actionSite() {
		buildService.buildSite();
	}
}
class ArticleController extends Controller {
	private ArticleService articleService;

	ArticleController() {
		articleService = Factory.getArticleService();
	}
	/*
	 * 이 프로그램에서 사용하지 않아도 되는 메서드. 제거해야하는 메서드. public void
	 * setArticleService(ArticleService articleService) { this.articleService =
	 * articleService; }
	 */

	public void doAction(Request request) {
		if (request.getActionName().equals("list")) {
			actionList(request);
		} else if (request.getActionName().equals("write")) {
			actionWrite(request);
		} else if (request.getActionName().equals("free")) {
			actionFree(request);
		} else if (request.getActionName().equals("notice")) {
			actionNotice(request);
		} else if (request.getActionName().equals("listBoard")) {
			actionlistBoard(request);
		} else if (request.getActionName().equals("modify")) {
			actionModify(request);
		} else if (request.getActionName().equals("delete")) {
			actionDelete(request);
		} else if (request.getActionName().equals("detail")) {
			actionDetail(request);
		} else if (request.getActionName().equals("createBoard")) {
			actionCreateBoard(request);
		} else if (request.getActionName().equals("map")) {
			actionMap(request); 
		} else if (request.getActionName().equals("deleteBoard")) {
			actionDeleteBoard(request); 
		}
		
	}

	private void actionDeleteBoard(Request request) {
		System.out.println("== 게시판 삭제 ==");
		if ( Factory.getSession().getLoginedMember().getLoginId().equals("admin") == false ) {
			System.out.println("게시판 삭제는 관리자만 가능한 기능입니다.");
			return;
		}
		System.out.printf("삭제할 게시판 코드를 입력바랍니다 : ");
		String code = Factory.getScanner().nextLine().trim();
		Board board = articleService.getBoardByCode(code);
		
		if ( board == null ) {
			System.out.println("해당하는 게시판 코드는 존재하지 않습니다.");
			return;
		}
		
		
		articleService.setBoardDelete(board);
	}
	private void actionMap(Request request) {
		Board newBoard = articleService.getBoardByCode("map");
		Session session = Factory.getSession();
		session.setCurrentBoard(newBoard);
	}

	private void actionCreateBoard(Request request) {
		System.out.println("== 게시판 생성 ==");
		String name;
		String code;
		System.out.printf("게시판 이름 : ");
		name = Factory.getScanner().nextLine().trim();
		System.out.printf("게시판 코드 : ");
		code = Factory.getScanner().nextLine().trim();
		
		articleService.makeBoard(name, code);
		System.out.println(name + "이(가) 생성되었습니다.");
	}
	private void actionDetail(Request request) {
		int detail = Integer.parseInt(request.getArticleId());
		Article article = articleService.getThisArticle(detail);
		if ( article == null ) {
			System.out.println("해당 게시물은 존재하지 않습니다.");
			return;
		}
		articleService.getThisNumDetail(detail);
	}
	private void actionDelete(Request request) {
		System.out.println("== 게시물 삭제 ==");
		int deleteId = Integer.parseInt(request.getArticleId());
		Article article = articleService.getThisArticle(deleteId);
		if ( article == null ) {
			System.out.println("작성된 게시물이 존재하지 않습니다.");
			return;
		}
		if ( article.getMemberId() != Factory.getSession().getLoginedMember().getId() ) {
			System.out.println("게시물 삭제는 작성자 본인만 가능합니다.");
			return;
		}
		
		articleService.setArticleDelete(article);
	}

	private void actionModify(Request request) {
		int modifyId = Integer.parseInt(request.getArticleId());
		Article article = articleService.getThisArticle(modifyId);
		String title;
		String body;
		if (article == null) {
			System.out.println("작성된 게시물이 존재하지 않습니다.");
			return;
		}
		if (article.getMemberId() != Factory.getSession().getLoginedMember().getId()) {
			System.out.println("게시물은 작성자 본인만 수정할 수 있습니다.");
			return;
		}
		System.out.printf("수정 게시판 번호 : %d번\n", modifyId);

		System.out.printf("제목 : ");
		title = Factory.getScanner().nextLine().trim();
		System.out.printf("내용 : ");
		body = Factory.getScanner().nextLine().trim();
		article.setTitle(title);
		article.setBody(body);
		System.out.printf("%d번 게시물을 수정하였습니다.\n", article.getId());
		articleService.setArticleModify(article);

	}

	private void actionlistBoard(Request request) {
		articleService.getListBoard();
	}

	private void actionNotice(Request request) {
		Board newBoard = articleService.getBoardByCode("notice");
		Session session = Factory.getSession();
		session.setCurrentBoard(newBoard);
	}

	private void actionFree(Request request) {
		Board newBoard = articleService.getBoardByCode("free");
		Session session = Factory.getSession();
		session.setCurrentBoard(newBoard);
	}

	// List<Article> articles = articleService.getArticles(); 
	private void actionList(Request request) {
		String pageIdStr = request.getArticleId();
		String googling = request.getGoogling();
		if ( pageIdStr == null ) {
			System.out.println("게시물 페이지를 입력바랍니다.");
			return;
		}
		int pageId = Integer.parseInt(pageIdStr);
		
		
		// 검색어를 입력했을 때, 검색어를 포함한 리스트만 리스트업되어야 한다. 흠........ 입력받았을 때에만!
		if ( googling != null ) {
			articleService.getGooglingByCurrentBoardCode(Factory.getSession().getCurrentBoard().getId(), googling, pageId);
		}
		else {
			articleService.getArticleListByCurrentBoardCode(Factory.getSession().getCurrentBoard().getId(), pageId );
		}
		
	}

	private void actionWrite(Request request) {
		System.out.printf("제목 : ");
		String title = Factory.getScanner().nextLine();
		System.out.printf("내용 : ");
		String body = Factory.getScanner().nextLine();

		// 현재 게시판 id 가져오기
		int boardId = Factory.getSession().getCurrentBoard().getId();

		// 현재 로그인한 회원의 id 가져오기
		int memberId = Factory.getSession().getLoginedMember().getId();
		int newId = articleService.write(boardId, memberId, title, body);

		System.out.printf("%d번 글이 생성되었습니다.\n", newId);
	}
}

class MemberController extends Controller {
	private MemberService memberService;

	MemberController() {
		memberService = Factory.getMemberService();
	}

	void doAction(Request request) {
		if (request.getActionName().equals("logout")) {
			actionLogout();
		} else if (request.getActionName().equals("login")) {
			actionLogin();
		} else if (request.getActionName().equals("whoami")) {
			actionWhoami();
		} else if (request.getActionName().equals("join")) {
			actionJoin();
		} else if (request.getActionName().equals("list")) {
			actionList();
		}

	}

	private void actionList() {
		memberService.getMemberList();
	}

	private void actionJoin() {
		if (Factory.getSession().getLoginedMember() != null) {
			System.out.println("접속 상태로 회원가입을 진행할 수 없습니다.");
			return;
		}
		System.out.println("== 회원가입 시작 ==");
		String loginId;
		String loginPw;
		String loginPwConfirm;
		String name;
		while (true) {
			System.out.printf("이름 : ");
			name = Factory.getScanner().nextLine().trim();
			if (name.length() == 0) {
				System.out.println("이름을 입력바랍니다.");
				continue;
			}
			if (name.length() < 2) {
				System.out.println("이름을 2자 이상 입력바랍니다.");
				continue;
			}

			break;
		}
		while (true) {
			System.out.printf("로그인 아이디 : ");
			loginId = Factory.getScanner().nextLine().trim();
			if (loginId.length() == 0) {
				System.out.println("로그인 아이디를 입력바랍니다.");
				continue;
			}
			if (loginId.length() < 2) {
				System.out.println("로그인 아이디를 2자 이상 입력바랍니다.");
				continue;
			}
			if (memberService.isUsedLoginId(loginId)) {
				System.out.println("사용중인 아이디로 다시 입력바랍니다.");
				continue;
			}
			break;
		}
		while (true) {
			boolean loginPwValid = true;
			while (true) {
				System.out.printf("로그인 비밀번호 : ");
				loginPw = Factory.getScanner().nextLine().trim();
				if (loginPw.length() == 0) {
					System.out.println("로그인 비밀번호를 입력바랍니다.");
					continue;
				}
				if (loginPw.length() < 2) {
					System.out.println("로그인 비밀번호를 2자 이상 입력바랍니다.");
					continue;
				}

				break;
			}
			while (true) {
				System.out.printf("로그인 비밀번호 확인 : ");
				loginPwConfirm = Factory.getScanner().nextLine().trim();
				if (loginPwConfirm.length() == 0) {
					System.out.println("로그인 비밀번호 확인을 입력바랍니다.");
					continue;
				}
				if (loginPw.equals(loginPwConfirm) == false) {
					System.out.println("로그인 비밀번호와 비밀번호 확인이 일치하지 않습니다. 다시 입력바랍니다.");
					loginPwValid = false;
					break;
				}
				break;
			}
			if (loginPwValid) {
				break;
			}
		}

		memberService.join(loginId, loginPw, name);
		System.out.println(loginId + "님. 회원가입을 축하드립니다.");
	}

	private void actionWhoami() {
		Member member = Factory.getSession().getLoginedMember();
		if (member == null) {
			System.out.println("나그네");
			System.out.println("미접속 상태입니다.");
			return;
		}
		System.out.println(member.getName());
	}

	private void actionLogin() {
		if (Factory.getSession().getLoginedMember() != null) {
			System.out.println("현재 접속 상태입니다.");
			return;
		}
		System.out.printf("로그인 아이디 : ");
		String loginId = Factory.getScanner().nextLine().trim();
		System.out.printf("로그인 비밀번호 : ");
		String loginPw = Factory.getScanner().nextLine().trim();

		Member member = memberService.getMemberByLoginIdAndLoginPw(loginId, loginPw);
		if (member == null) {
			System.out.println("일치하는 회원정보가 존재하지 않습니다.");
			return;
		} else {
			Factory.getSession().setLoginedMember(member);
			System.out.println(member.getName() + "님 환영합니다.");
		}
		// System.out.println(Factory.getSession().getLoginedMember());
	}

	private void actionLogout() {
		Member member = Factory.getSession().getLoginedMember();
		if (member != null) {
			Session session = Factory.getSession();
			session.setLoginedMember(null);
			System.out.println("로그아웃 되셨습니다.");
		} else {
			System.out.println("현재 로그아웃 상태입니다.");
		}
	}
}

// Service  ----> 여기서부터 
class BuildService {
	ArticleService articleService;
	BuildService() {
		articleService = Factory.getArticleService();
	}
	public void buildSite() {
		Util.makeDir("site");
		Util.makeDir("site/article");
		Util.makeDir("site/board");
		Util.makeDir("site/member");
		
		String head = Util.getFileContents("site_template/part/head.html");
		String foot = Util.getFileContents("site_template/part/foot.html");
		
		// 각 게시판 별 게시물리스트 페이지 생성
		List<Board> boards = articleService.getBoards();
		for (Board board : boards) {
			String fileName = board.getCode() + "-list-1.html";

			String html = "";

			List<Article> articles = articleService.getArticlesByBoardCode(board.getCode());
			String template = Util.getFileContents("site_template/article/list.html");
			//System.out.println("되고 있니??");
			for (Article article : articles) {
				Member member = Factory.getMemberService().getMember(article.getMemberId());
				html += "<tr>";
				html += "<td>" + article.getId() + "</td>";
				html += "<td>" + article.getRegDate() + "</td>";
				html += "<td>" + member.getLoginId() + "</td>";  // 혜련 추가 내용
				html += "<td><a href=\"" + article.getId() + ".html\">" + article.getTitle() + "</a></td>";
				html += "</tr>";
				
		}
			html = template.replace("${TR}", html);

			html = head + html + foot;

			Util.writeFileContents("site/article/" + fileName, html);
		}
		// 게시물 별 파일 생성
		List<Article> articles = articleService.getArticles();

		for (Article article : articles) {
			String html = "";

			html += "<div>제목 : " + article.getTitle() + "</div>";	
			html += "<div>내용 : " + article.getBody() + "</div>";
			html += "<div><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></div>";
			html += "<div><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></div>";

			html = head + html + foot;
			Util.writeFileContents("site/article/" + article.getId() + ".html", html);
		}
		// 내가 하고 싶은거는??? 생성된 게시판 이력
		// 게시판들 리스트
		
		for (Board board : boards) {
			String fileName = board.getCode() + ".html";
			String html = "";
			String template = Util.getFileContents("site_template/article/boardList.html");
				html += "<tr>";
				html += "<td>" + board.getId() + "</td>";
				html += "<td>" + board.getName() + "</td>";
				html += "<td>" + board.getRegDate() + "</td>";  // 혜련 추가 내용
				html += "</tr>";
				html = template.replace("${TR}", html);
				html = head + html + foot;
				Util.writeFileContents("site/board/" + fileName, html);
		}
		//게시판 리스트
		String fileName = "boardList.html";
		String boardHtml = "";
		String template = Util.getFileContents("site_template/article/boardList.html");
		for (Board board : boards) {
			boardHtml += "<tr>";
			boardHtml += "<td>" + board.getId() + "</td>";
			boardHtml += "<td>" + board.getName() + "</td>";
			boardHtml += "<td>" + board.getRegDate() + "</td>";  // 혜련 추가 내용
			boardHtml += "</tr>";
			
		}
		boardHtml = template.replace("${TR}", boardHtml);
		boardHtml = head + boardHtml + foot;
			Util.writeFileContents("site/board/" + fileName, boardHtml);
		
	}		
	
}
class ArticleService {
	private ArticleDao articleDao;

	ArticleService() {
		articleDao = Factory.getArticleDao();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return articleDao.getArticlesByBoardCode(code);
	}

	public List<Board> getBoards() {
		return articleDao.getBoards();
	}

	public void setBoardDelete(Board board) {
		articleDao.setBoardDelete(board);
	}

	public void getThisNumDetail(int detail) {
		articleDao.getThisNumDetail(detail);
	}

	public void getGooglingByCurrentBoardCode(int BoardId, String googling, int pageId) {
		articleDao.getGooglingByCurrentBoardCode(BoardId, googling, pageId);
	}

	public void setArticleDelete(Article article) {
		articleDao.setArticleDelete(article);
	}

	public void setArticleModify(Article articleModify) {
		articleDao.setArticleModify(articleModify);
	}

	public Article getThisArticle(int modifyId) {
		return articleDao.getThisArticle(modifyId);
	}

	public void getArticleListByCurrentBoardCode(int boardId, int pageId) {
		articleDao.getArticleListByCurrentBoardCode(boardId, pageId);
	}

	public void getListBoard() {
		articleDao.getListBoard();
	}

	public Board getBoardByCode(String code) {
		return articleDao.getBoardByCode(code);
	}

	public int makeBoard(String name, String code) {
		Board oldBoard = articleDao.getBoardByCode(code);
		if (oldBoard != null) {
			return -1;
		}
		Board board = new Board(name, code);
		return articleDao.saveBoard(board);
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public int write(int boardId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		return articleDao.save(article);
	}

	// 지우기 현재 수정중....
	public List<Article> getArticles() {
		return articleDao.getArticles();
	}

}

class MemberService {
	private MemberDao memberDao;

	MemberService() {
		memberDao = Factory.getMemberDao();
	}

	public void getMemberList() {
		memberDao.getMemberList();
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return memberDao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}

	public boolean isUsedLoginId(String loginId) {
		Member member = memberDao.getMemberByLoginId(loginId);
		if (member == null) {
			return false;
		}
		return true;
	}

	public int join(String loginId, String loginPw, String name) {
		Member oldMember = memberDao.getMemberByLoginId(loginId);
		if (oldMember != null) {
			return -1;
		}
		Member member = new Member(loginId, loginPw, name);
		return memberDao.save(member);
	}

	public Member getMember(int id) {
		return memberDao.getMember(id);
	}
}

// Dao
class ArticleDao {
	DB db;

	ArticleDao() {
		db = Factory.getDB();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return db.getArticlesByBoardCode(code);
	}

	public List<Board> getBoards() {
		return db.getBoards();
	}

	public void setBoardDelete(Board board) {
		db.setBoardDelete(board);
	}

	public void getThisNumDetail(int detail) {
		db.getThisNumDetail(detail);
	}

	public void getGooglingByCurrentBoardCode(int boardId, String googling, int pageId) {
		db.getGooglingByCurrentBoardCode(boardId, googling, pageId);
	}

	public void setArticleDelete(Article article) {
		db.setArticleDelete(article);
	}

	public void setArticleModify(Article articleModify) {
		db.setArticleModify(articleModify);
	}

	public Article getThisArticle(int modifyId) {
		return db.getThisArticle(modifyId);
	}

	public void getArticleListByCurrentBoardCode(int boardId, int pageId) {
		db.getArticleListByCurrentBoardCode(boardId, pageId);
	}

	public void getListBoard() {
		db.getListBoard();
	}

	public Board getBoardByCode(String code) {
		return db.getBoardByCode(code);
	}

	public int saveBoard(Board board) {
		return db.saveBoard(board);
	}

	public int save(Article article) {
		return db.saveArticle(article);
	}

	public Board getBoard(int id) {
		return db.getBoard(id);
	}

	// 지우기... 현재 수정중...
	public List<Article> getArticles() {
		return db.getArticles();
	}

}

class MemberDao {
	DB db;

	MemberDao() {
		db = Factory.getDB();
	}

	public void getMemberList() {
		db.getMemberList();
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return db.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}

	public Member getMemberByLoginId(String loginId) {
		return db.getMemberByLoginId(loginId);
	}

	public Member getMember(int id) {
		return db.getMember(id);
	}

	public int save(Member member) {
		return db.saveMember(member);
	}
}

// DB
class DB {
	private Map<String, Table> tables;

	public DB() {
		String dbDirPath = getDirPath();
		Util.makeDir(dbDirPath);

		tables = new HashMap<>();

		tables.put("board", new Table<Board>(Board.class, dbDirPath));
		tables.put("article", new Table<Article>(Article.class, dbDirPath));
		tables.put("member", new Table<Member>(Member.class, dbDirPath));
	}

	public List<Article> getArticlesByBoardCode(String code) {
		Board board = getBoardByCode(code);
		
		List<Article> articles = getArticles();
		List<Article> newArticles = new ArrayList<>();
		for ( Article article : articles ) {
			if ( board.getId() == article.getBoardId()) {
				newArticles.add(article);
			}
		}
		return newArticles;
	}

	public void setBoardDelete(Board board) {
		tables.get("board").setBoardDelete(board);
	}

	public void getThisNumDetail(int detail) {
		List<Article> articles = getArticles();
		for ( Article article : articles ) {
			if ( article.getId() == detail ) {
				System.out.println(article);
			}
		}
	}

	public void getGooglingByCurrentBoardCode(int boardId, String googling, int pageId) {
		List<Article> articles = getArticles();
			
			int totalltems = articles.size();
			int itemsInAPage = 5;
			int totalPage = (int)Math.ceil((double)totalltems / itemsInAPage);
			int currentPage = pageId;
			if ( pageId > totalPage ) {
				System.out.println("존재하지 않는 페이지 입니다.");
				return;
			}
			int startIndex = (itemsInAPage*currentPage)-itemsInAPage;
			int until = (itemsInAPage*currentPage)-1;

			if ( totalltems < until ) {
				int num = until - totalltems;
				until = until - num-1;
			}
			for ( int i = startIndex; i <= until; i++ ) {
				if (articles.get(i).getBoardId() == boardId &&  articles.get(i).getTitle().contains(googling)) {
					System.out.println(articles.get(i).toStringList());	
				}
			}   
			for ( int i = 1; i <= totalPage; i++ ) {
					String a = "" + i;
					
					if ( i == currentPage) {
						a = "[" + a + "]";
					}
					if ( i < totalPage ) {
						System.out.printf("%s ", a);
					}
					if ( i == totalPage ) {
						System.out.printf("%s \n", a);
					}
			}
	}

	public void setArticleDelete(Article article) {
		tables.get("article").setArticleDelete(article);
	}

	public void setArticleModify(Article articleModify) {
		tables.get("article").setArticleModify(articleModify);

	}

	public Article getThisArticle(int modifyId) {
		List<Article> articles = getArticles();
		for (Article article : articles) {
			if (article.getId() == modifyId) {
				return article;
			}
		}
		return null;
	}

	public void getMemberList() {
		List<Member> members = getMembers();
		for (Member member : members) {
			System.out.println(member.toString());
		}
	}

	public void getArticleListByCurrentBoardCode(int boardId, int pageId) {
		List<Article> articles = getArticles();
		/*for (Article article : articles) {
			if (article == null) {
				System.out.println("작성된 게시물이 존재하지 않습니다.");
				return;
			}*/
			int totalltems = articles.size();
			int itemsInAPage = 5;
			int totalPage = (int)Math.ceil((double)totalltems / itemsInAPage);
			int currentPage = pageId;
			if ( pageId > totalPage ) {
				System.out.println("존재하지 않는 페이지 입니다.");
				return;
			}
			int startIndex = (itemsInAPage*currentPage)-itemsInAPage;
			int until = (itemsInAPage*currentPage)-1;
			//int start = currentPage;
			//int end = start + (itemsInAPage - currentPage);
			if ( totalltems < until ) {
				int num = until - totalltems;
				until = until - num-1;
			}
			for ( int i = startIndex; i <= until; i++ ) {
				if (articles.get(i).getBoardId() == boardId) {
					System.out.println(articles.get(i).toStringList());	
				}
			}
			for ( int i = 1; i <= totalPage; i++ ) {
					String a = "" + i;
					
					if ( i == currentPage) {
						a = "[" + a + "]";
					}
					if ( i < totalPage ) {
						System.out.printf("%s ", a);
					}
					if ( i == totalPage ) {
						System.out.printf("%s \n", a);
					}
			}
		}
			
	//}

	public void getListBoard() {
		List<Board> boards = getBoards();
		for (Board board : boards) {
			System.out.println(board.toString());
		}
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		List<Member> members = getMembers();
		for (Member member : members) {
			if (member.getLoginId().equals(loginId) && member.getLoginPw().equals(loginPw)) {
				return member;
			}
		}
		return null;
	}

	public Board getBoardByCode(String code) {
		List<Board> boards = getBoards();
		for (Board board : boards) {
			if (board.getCode().equals(code)) {
				return board;
			}
		}
		return null;
	}

	public List<Board> getBoards() {
		return tables.get("board").getRows();
	}

	public Member getMemberByLoginId(String loginId) {
		List<Member> members = getMembers();
		for (Member member : members) {
			if (member.getLoginId().equals(loginId)) {
				return member;
			}
		}
		return null;
	}

	public List<Member> getMembers() {
		return tables.get("member").getRows();
	}

	public Member getMember(int id) {
		return (Member) tables.get("member").getRow(id);
	}

	public int saveBoard(Board board) {
		return tables.get("board").saveRow(board);
	}

	public String getDirPath() {
		return "db";
	}

	public int saveMember(Member member) {
		return tables.get("member").saveRow(member);
	}

	public Board getBoard(int id) {
		return (Board) tables.get("board").getRow(id);
	}

	public List<Article> getArticles() {
		return tables.get("article").getRows();
	}

	public int saveArticle(Article article) {
		return tables.get("article").saveRow(article);
	}

	public void backup() {
		for (String tableName : tables.keySet()) {
			Table table = tables.get(tableName);
			table.backup();
		}
	}
}

// Table
class Table<T> {
	private Class<T> dataCls;
	private String tableName;
	private String tableDirPath;

	public Table(Class<T> dataCls, String dbDirPath) {
		this.dataCls = dataCls;
		this.tableName = Util.lcfirst(dataCls.getCanonicalName());
		this.tableDirPath = dbDirPath + "/" + this.tableName;

		Util.makeDir(tableDirPath);
	}

	public void setBoardDelete(Board board) {
		String filePath = getRowFilePath(board.getId());
		//return Util.deleteJsonFile(filePath, board);
	}

	public Article setArticleDelete(Article article) {
		String filePath = getRowFilePath(article.getId());
		return Util.deleteJsonFile(filePath, article);
	}

	public void setArticleModify(Article articleModify) {
		String filePath = getRowFilePath(articleModify.getId());
		Util.writeJsonFile(filePath, articleModify);
	}

	private String getTableName() {
		return tableName;
	}

	public int saveRow(T data) {
		Dto dto = (Dto) data;

		if (dto.getId() == 0) {
			int lastId = getLastId();
			int newId = lastId + 1;
			((Dto) data).setId(newId);
			setLastId(newId);
		}

		String rowFilePath = getRowFilePath(dto.getId());

		Util.writeJsonFile(rowFilePath, data);

		return dto.getId();
	};

	private String getRowFilePath(int id) {
		return tableDirPath + "/" + id + ".json";
	}

	private void setLastId(int lastId) {
		String filePath = getLastIdFilePath();
		Util.writeFileContents(filePath, lastId);
	}

	private int getLastId() {
		String filePath = getLastIdFilePath();

		if (Util.isFileExists(filePath) == false) {
			int lastId = 0;
			Util.writeFileContents(filePath, lastId);
			return lastId;
		}

		return Integer.parseInt(Util.getFileContents(filePath));
	}

	private String getLastIdFilePath() {
		return this.tableDirPath + "/lastId.txt";
	}

	public T getRow(int id) {
		return (T) Util.getObjectFromJson(getRowFilePath(id), dataCls);
	}

	public void backup() {

	}

	/*void delete(int id) {
		/* 구현  * ///
	} */

	List<T> getRows( /* 구현 */ ) {
		List<T> rows = new ArrayList<>();
		int lastId = getLastId();
		for (int id = 1; id <= lastId; id++) {
			T row = getRow(id);
			if ( row != null ) {
				rows.add(row);
			}
		}
		return rows;
	};
}

// DTO
abstract class Dto {
	private int id;
	private String regDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	Dto() {
		this(0);
	}

	Dto(int id) {
		this(id, Util.getNowDateStr());
	}

	Dto(int id, String regDate) {
		this.id = id;
		this.regDate = regDate;
	}
}

class Board extends Dto {
	private String name;
	private String code;

	public Board() {
	}

	public Board(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "Board [name=" + name + ", code=" + code + ", getId()=" + getId() + ", getRegDate()=" + getRegDate()
				+ "]";
	}

}

class Article extends Dto {
	private int boardId;
	private int memberId;
	private String title;
	private String body;

	public Article() {

	}



	public Article(int boardId, int memberId, String title, String body) {
		this.boardId = boardId;
		this.memberId = memberId;
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

	@Override
	public String toString() {
		return "Article [boardId=" + boardId + ", memberId=" + memberId + ", title=" + title + ", body=" + body
				+ ", getId()=" + getId() + ", getRegDate()=" + getRegDate() + "]";
	}
	public String toStringList() {
		return "Article [boardId=" + boardId + ", memberId=" + memberId + ", title=" + title + ", body=" + body
				+ ", getId()=" + getId() + ", getRegDate()=" + getRegDate() + "]";
	}
}

class ArticleReply extends Dto {
	private int id;
	private String regDate;
	private int articleId;
	private int memberId;
	private String body;

	ArticleReply() {

	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}

class Member extends Dto {
	private String loginId;
	private String loginPw;
	private String name;

	public Member() {

	}

	public Member(String loginId, String loginPw, String name) {
		this.loginId = loginId;
		this.loginPw = loginPw;
		this.name = name;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPw() {
		return loginPw;
	}

	public void setLoginPw(String loginPw) {
		this.loginPw = loginPw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Member [loginId=" + loginId + ", loginPw=" + loginPw + ", name=" + name + ", getId()=" + getId()
				+ ", getRegDate()=" + getRegDate() + "]";
	}

}

// Util
class Util {
	// 현재날짜문장
	public static String getNowDateStr() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = Date.format(cal.getTime());
		return dateStr;
	}

	// 파일에 내용쓰기
	public static void writeFileContents(String filePath, int data) {
		writeFileContents(filePath, data + "");
	}

	// 첫 문자 소문자화
	public static String lcfirst(String str) {
		String newStr = "";
		newStr += str.charAt(0);
		newStr = newStr.toLowerCase();

		return newStr + str.substring(1);
	}

	// 파일이 존재하는지
	public static boolean isFileExists(String filePath) {
		File f = new File(filePath);
		if (f.isFile()) {
			return true;
		}

		return false;
	}

	// 파일내용 읽어오기
	public static String getFileContents(String filePath) {
		String rs = null;
		try {
			// 바이트 단위로 파일읽기
			FileInputStream fileStream = null; // 파일 스트림

			fileStream = new FileInputStream(filePath);// 파일 스트림 생성
			// 버퍼 선언
			byte[] readBuffer = new byte[fileStream.available()];
			while (fileStream.read(readBuffer) != -1) {
			}

			rs = new String(readBuffer);

			fileStream.close(); // 스트림 닫기
		} catch (Exception e) {
			e.getStackTrace();
		}

		return rs;
	}

	// 파일 쓰기
	public static void writeFileContents(String filePath, String contents) {
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(new FileOutputStream(filePath));
			bs.write(contents.getBytes()); // Byte형으로만 넣을 수 있음
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			try {
				bs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Json안에 있는 내용을 가져오기
	public static Object getObjectFromJson(String filePath, Class cls) {
		ObjectMapper om = new ObjectMapper();
		Object obj = null;
		try {
			obj = om.readValue(new File(filePath), cls);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public static Article deleteJsonFile(String filePath, Article article) { 
	        boolean delYn = true;
	        File file = new File(filePath);
	        if(file.exists()) {
	            delYn = file.delete();
	            if(delYn){
	                System.out.println("게시물 삭제 성공!"); //성공
	            }
	        }else{
	            System.out.println("해당 게시물이 존재하지 않습니다."); //미존재
	        }
	        return article;
	}
	public static void writeJsonFile(String filePath, Object obj) {
		ObjectMapper om = new ObjectMapper();
		try {
			om.writeValue(new File(filePath), obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void makeDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}