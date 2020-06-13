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
	private Board currentBoard;
	private Member loginedMember;
	
	public Board getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(Board currentBoard) {
		this.currentBoard = currentBoard;
	}
	
	public Member getLoginedMember() {
		return loginedMember;
	}

	public void setLoginedMember(Member loginedMember) {
		this.loginedMember = loginedMember;
	}

	public boolean isLogined() {
		return loginedMember != null;
	}
}

// Factory
// 프로그램 전체에서 공유되는 객체 리모콘을 보관하는 클래스
class Factory {
	private static Scanner scanner;
	private static Session session;
	private static BuildService buildService;
	private static BoardService boardService;
	private static ArticleService articleService;
	private static MemberService memberService;
	private static BoardDao boardDao;
	private static ArticleDao articleDao;
	private static MemberDao memberDao;
	private static DB db;

	public static Scanner getScanner() {
		if (scanner == null) {
			scanner = new Scanner(System.in);
		}
		return scanner;
	}

	public static Session getSession() {
		if (session == null) {
			session = new Session();
		}
		return session;
	}
	
	public static BuildService getBuildService() {
		if (buildService == null) {
			buildService = new BuildService();
		}
		return buildService;
	}

	public static BoardService getBoardService() {
		if (boardService == null) {
			boardService = new BoardService();
		}
		return boardService;
	}

	public static ArticleService getArticleService() {
		if (articleService == null) {
			articleService = new ArticleService();
		}
		return articleService;
	}

	public static MemberService getMemberService() {
		if (memberService == null) {
			memberService = new MemberService();
		}
		return memberService;
	}

	public static BoardDao getBoardDao() {
		if (boardDao == null) {
			boardDao = new BoardDao();
		}
		return boardDao;
	}

	public static ArticleDao getArticleDao() {
		if (articleDao == null) {
			articleDao = new ArticleDao();
		}
		return articleDao;
	}

	public static MemberDao getMemberDao() {
		if (memberDao == null) {
			memberDao = new MemberDao();
		}
		return memberDao;
	}

	public static DB getDB() {
		if (db == null) {
			db = new DB();
		}
		return db;
	}
}

// App
class App {
	private Map<String, Controller> controllers;

	void initControllers() {
		controllers = new HashMap<>();
		controllers.put("build", new BuildController());
		controllers.put("board", new BoardController());
		controllers.put("article", new ArticleController());
		controllers.put("member", new MemberController());
	}

	public App() {
		initControllers();
		
		Factory.getBoardService().makeBoard("공지시항", "notice");
		Factory.getBoardService().makeBoard("자유게시판", "free");
		Factory.getMemberService().join("admin", "admin", "관리자");
		
		Factory.getSession().setCurrentBoard(Factory.getBoardService().getBoard(1));
	}

	public void start() {
		while (true) {
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
	private String arg1;
	private String arg2;
	private String arg3;
	
	Request(String requestStr) {
		this.requestStr = requestStr;
		String[] requestStrBits = requestStr.split(" ");
		this.controllerName = requestStrBits[0];

		if (requestStrBits.length > 1) {
			this.actionName = requestStrBits[1];
		}

		if (requestStrBits.length > 2) {
			this.arg1 = requestStrBits[2];
		}

		if (requestStrBits.length > 3) {
			this.arg2 = requestStrBits[3];
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

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public String getArg3() {
		return arg3;
	}

	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}
	
	boolean isValidRequest() {
		return actionName != null;
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

	@Override
	void doAction(Request request) {
		if (request.getActionName().equals("site")) {
			actionSite(request);
		} else if (request.getActionName().equals("startAutoSite")) {
			actionStartAutoSite(request);
		} else if (request.getActionName().equals("stopAutoSite")) {
			actionStopAutoSite(request);
		}
	}

	private void actionSite(Request request) {
		buildService.buildSite();
		System.out.println("게시물 수동 생성");
	}
	
	private void actionStartAutoSite(Request request) {
		System.out.println("=========================== Auto ===========================");
		buildService.buildStartAutoSite();
	}

	private void actionStopAutoSite(Request request) {
		System.out.println("=========================== Manual ===========================");
		buildService.buildStropAutoSite();
	}
}

class ArticleController extends Controller {
	private ArticleService articleService;

	ArticleController() {
		articleService = Factory.getArticleService();
	}

	public void doAction(Request request) {
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
		if (Factory.getSession().isLogined() == false) {
			System.out.printf("로그인 후 사용 가능\n");
		} else {
			System.out.printf("제목 : ");
			String title = Factory.getScanner().nextLine();
			System.out.printf("내용 : ");
			String body = Factory.getScanner().nextLine();
	
			int boardId = Factory.getSession().getCurrentBoard().getId();
			int memberId = Factory.getSession().getLoginedMember().getId();
			String memberName = Factory.getSession().getLoginedMember().getName();
			int newId = articleService.write(boardId, memberId, title, body);
	
			System.out.printf("%d번 글 생성 - 작성자 : %s\n", newId, memberName);
		}
	}

	private void actionModify(Request request) {
		int articleId = Integer.parseInt(request.getArg1());
		Article article = articleService.getArticleById(articleId);
		int loginedId = Factory.getSession().getLoginedMember().getId(); 
		
		if (Factory.getSession().isLogined() == false) {
			System.out.printf("로그인 후 사용 가능\n");
		} else if (article.getMemberId() != loginedId) {
			System.out.printf("게시물 작성자 : %s, 현재 회원 : %s\n", article.getMemberId(), loginedId);
		} else if (article == null) {
			System.out.printf("존재하지 않는 게시물\n");
		} else {
			int boardId = Factory.getSession().getCurrentBoard().getId();
			int memberId = Factory.getSession().getLoginedMember().getId();

			System.out.printf("제목 : ");
			String title = Factory.getScanner().nextLine();
			System.out.printf("내용 : ");
			String body = Factory.getScanner().nextLine();
			articleService.modify(boardId, articleId, memberId, title, body);
			
			System.out.printf("%d번 게시물 수정 완료\n");
		}
	}

	private void actionDelete(Request request) {
		int boardId = Factory.getSession().getCurrentBoard().getId();
		int articleId = Integer.parseInt(request.getArg1());
		if (articleService.remove(articleId) == false) {
			System.out.printf("존재하지 않는 게시물\n");
		} else {
			System.out.printf("게시물 삭제 완료\n");
		}
	}

	private void actionList(Request request) {
		int pageId = 1;
		pageId = Integer.parseInt(request.getArg1());
		String title = "";
		try {
			if (request.getArg2().trim().length() > 0) {
				title = request.getArg2();
			}
		} catch (Exception e) {}
		
		List<Article> articles = articleService.getArticlesPageIdByTitle(pageId, title);
		if (articles.size() > 0) {
			int newPageId = (articles.size() - 1) / 10 + 1;
			
			if (newPageId < pageId) {
				System.out.printf("존재하지 않는 페이지\n");
			} else {
				System.out.printf("%s |%-8s|%-18s |%s\n", "번호", "제목", "작성일", "작성자");
				for (int i = articles.size() - 1 - (pageId - 1) * 10; i >= articles.size() - 10 - (pageId - 1) * 10; i--) {
					if (i >= 0) {
						Article article = articles.get(i);
						System.out.printf("%-5s|%-10s|%1s   |%s\n", article.getId(), article.getTitle(), article.getRegDate(),
								Factory.getMemberService().getMember(article.getMemberId()).getName());
					}
				}
			}
		} else if (articles.size() == 0) {
			System.out.println("존재하지 않는 게시글입니다.\n");
		}
	}

	private void actionListDetail(Request request) {
		if (Factory.getSession().isLogined() == false) {
			System.out.printf("로그인 후 사용 가능\n");
		}
		int ArticleId = Integer.parseInt(request.getArg1());
		Article article = articleService.getArticleDetailById(ArticleId);
	}
}

class MemberController extends Controller {
	private MemberService memberService;

	MemberController() {
		memberService = Factory.getMemberService();
	}

	void doAction(Request request) {
		if (request.getActionName().equals("join")) {
			actionJoin(request);
		} else if (request.getActionName().equals("login")) {
			actionLogin(request);
		} else if (request.getActionName().equals("logout")) {
			actionLogout(request);
		} else if (request.getActionName().equals("whoami")) {
			actionWhoami(request);
		}
	}

	private void actionJoin(Request request) {
		System.out.printf("아이디 : ");
		String joinId = Factory.getScanner().nextLine().trim();
		System.out.printf("비밀번호 : ");
		String joinPw = Factory.getScanner().nextLine().trim();
		System.out.printf("이름 : ");
		String joinName = Factory.getScanner().nextLine().trim();
		
		int id = memberService.join(joinId, joinPw, joinName);
		if (id == -1) {
			System.out.printf("회원가입 실패\n");
		}
		else {
			System.out.printf("회원가입 성공\n");
		}
	}

	private void actionLogin(Request request) {

		if (Factory.getSession().getLoginedMember() != null) {
			System.out.printf("현재 로그인 상태");
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

	private void actionLogout(Request request) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember != null) {
			Session session = Factory.getSession();
			System.out.printf("로그아웃 성공\n");
			session.setLoginedMember(null);
		}
		else {
			System.out.printf("현재 로그아웃 상태\n");
		}
	}

	private void actionWhoami(Request request) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember == null) {
			System.out.printf("비 회원\n");
		} else {
			System.out.printf("%d번 회원 : %s\n", loginedMember.getId(), loginedMember.getName());
		}
	}
}

// Service  ----> 여기서부터 
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

			List<Article> articles = articleService.getArticlesByBoardCode(board.getCode());
			
//			String template = Util.getFileContents("site_template/article/list.html");
			
			for (Article article : articles) {
				html += "<tr>";
				html += "<td>" + article.getId() + "</td>";
				html += "<td>" + article.getRegDate() + "</td>";
				html += "<td><a href=\"" + article.getId() + ".html\">" + article.getTitle() + "</a></td>";
				html += "</tr>";
			}

//			html = template.replace("${TR}", html);

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

class ArticleService {
	private ArticleDao articleDao;

	ArticleService() {
		articleDao = Factory.getArticleDao();
	}
	
	public int write(int boardId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		return articleDao.save(article);
	}
	
	public void modify(int boardId, int articleId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		articleDao.modify(article, articleId);
	}
	
	public Article getArticleById(int id) {
		return articleDao.getArticleById(id);
	}
	
	public boolean remove(int articleId) {
		return articleDao.remove(articleId);
	}
	
	public List<Article> getArticlesPageIdByTitle(int pageId, String title) {
		return articleDao.getArticlesPageIdByTitle(pageId, title);
	}

	public Article getArticleDetailById(int ArticleId) {
		return articleDao.getArticleDetailById(ArticleId);
	}
	
	public List<Article> getArticles() {
		return articleDao.getArticles();
	}
	
	public List<Article> getArticlesByBoardCode(String code) {
		return articleDao.getArticlesByBoardCode(code);
	}
}

class MemberService {
	private MemberDao memberDao;

	MemberService() {
		memberDao = Factory.getMemberDao();
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

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		
		return memberDao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}
}

// Dao
class ArticleDao {
	DB db;

	ArticleDao() {
		db = Factory.getDB();
	}

	public int save(Article article) {
		return db.saveArticle(article);
	}
	
	public void modify(Article article, int articleId) {
		db.modifyArticle(article, articleId);
	}
	
	public boolean remove(int articleId) {
		return db.removeArticle(articleId);
	}
	
	public List<Article> getArticlesPageIdByTitle(int pageId, String title) {
		return db.getArticlesPageIdByTitle(pageId, title);
	}
	
	public List<Article> getArticles() {
		return db.getArticles();
	}
	
	public Article getArticleById(int id) {
		return db.getArticleById(id);
	}

	public Article getArticleDetailById(int articleId) {
		return db.getArticleDetailById(articleId);
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return db.getArticlesByBoardCode(code);
	}
}

class MemberDao {
	DB db;

	MemberDao() {
		db = Factory.getDB();
	}

	public Member getMember(int id) {
		return db.getMember(id);
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return db.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}

	public Member getMemberByLoginId(String loginId) {
		return db.getMemberByLoginId(loginId);
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

	public Board getBoard(int id) {
		return (Board) tables.get("board").getRow(id);
	}

	public List<Board> getBoards() {
		return tables.get("board").getRows();
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

	public Board getBoardByNameAndCode(String name, String code) {
		List<Board> boards = getBoards();

		for (Board board : boards) {
			if (board.getCode().equals(code) && board.getName().equals(name)) {
				return board;
			}
		}
		return null;
	}

	public int saveBoard(Board board) {
		return tables.get("board").saveRow(board);
	}

	public int saveArticle(Article article) {
		return tables.get("article").saveRow(article);
	}
	
	public void deleteBoard(Board board) {
		tables.get("board").removeBoard(board);
	}
	
	public void listBoard() {
		System.out.printf("%s|%s|%s|%s\n", "번호", "이름", "코드", "생성일");
		for (Board board : getBoards()) {
			System.out.printf("%s|%s|%s|%s\n", board.getId(), board.getName(), board.getCode(), board.getRegDate());
		}
	}
	
	public void changeBoard(Board board) {
		if (board.getId() != Factory.getSession().getCurrentBoard().getId()) {
			Factory.getSession().setCurrentBoard(getBoard(board.getId()));
		} 
	}

	public void modifyArticle(Article article, int articleId) {
		tables.get("article").modifyArticle(article, articleId);
	}

	public boolean removeArticle(int articleId) {
		return tables.get("article").removeArticle(articleId);
	}

	public List<Article> getArticlesPageIdByTitle(int pageId, String title) {
		List<Article> articles = new ArrayList<>();
		for (Article article : getArticles()) {
			int articleBoardId = article.getBoardId();
			int currentBoardId = Factory.getSession().getCurrentBoard().getId();
			
			if (articleBoardId == currentBoardId) {
				String articleTitle = article.getTitle();
				
				if (articleTitle.contains(title)) {
					articles.add(article);
				}
			}
		}
		return articles;
	}

	public List<Article> getArticles() {
		return tables.get("article").getRows();
	}

	public Article getArticleById(int id) {
		return (Article) tables.get("article").getRow(id);
	}
	
	public Article getArticleDetailById(int articleId) {
		Article article = getArticleById(articleId);
		return article;
	}

	public Member getMember(int id) {
		return (Member) tables.get("member").getRow(id);
	}

	public List<Member> getMembers() {
		return tables.get("member").getRows();
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

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		List<Member> members = getMembers();

		for (Member member : members) {
			if (member.getLoginId().equals(loginId) && member.getLoginPw().equals(loginPw)) {
				return member;
			}
		}
		return null;
	}

	public int saveMember(Member member) {
		return tables.get("member").saveRow(member);
	}

	public String getDirPath() {
		return "db";
	}

	public void backup() {
		for (String tableName : tables.keySet()) {
			Table table = tables.get(tableName);
			table.backup();
		}
	}

	public int isExistBoard(String deleteCode) {
		for (Board board : getBoards()) {
			if (board.getCode().equals(deleteCode)) {
				return board.getId();
			}
		}
		return 0;
	}

	public void changeBoard(int boardId) {
		if (boardId != Factory.getSession().getCurrentBoard().getId()) {
			Factory.getSession().setCurrentBoard(Factory.getBoardService().getBoard(boardId));
			System.out.printf("%s 게시판으로 이동\n", getBoard(boardId).getName());
		} 
	}

	public List<Article> getArticlesByBoardCode(String code) {
		List<Article> articles = new ArrayList<>();
		for (Article article : getArticles()) {
			if (getBoardByCode(code).getId() == article.getBoardId()) {
				articles.add(article);
			}
		}
		return articles;
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
	
	public Boolean removeBoard(Board board) {
		String filePath = getRowFilePath(board.getId()); 
		if (Util.isFileExists(filePath) == false) {
			return false;
		}
		for (Article article : Factory.getDB().getArticles()) {
			if (article.getBoardId() == board.getId()) {
				Factory.getDB().removeArticle(article.getId());
			}
		}
		File removeFile = new File(filePath);
		removeFile.delete();
		return true;
	}

	public void modifyArticle(Article article, int articleId) {
		Dto dto = (Dto) article;

		if (dto.getId() == 0) {
			int lastId = getLastId();
			dto.setId(lastId);
			setLastId(lastId);
		}

		String FilePath = getRowFilePath(articleId);

		Util.writeJsonFile(FilePath, article);
	}

	public boolean removeArticle(int articleId) {
		String filePath = getRowFilePath(articleId);

		if (Util.isFileExists(filePath) == false) {
			return false;
		}

		File removeFile = new File(filePath);
		removeFile.delete();
		return true;
	}

	public T getRow(int id) {
		return (T) Util.getObjectFromJson(getRowFilePath(id), dataCls);
	}

	public List<T> getRows() {
		int lastId = getLastId();

		List<T> rows = new ArrayList<>();

		for (int id = 1; id <= lastId; id++) {
			T row = getRow(id);

			if (row != null) {
				rows.add(row);
			}
		}
		return rows;
	}

	public int saveRow(T data) {
		Dto dto = (Dto) data;

		if (dto.getId() == 0) {
			int lastId = getLastId();
			int newId = lastId + 1;
			dto.setId(newId);
			setLastId(newId);
		}
		String rowFilePath = getRowFilePath(dto.getId());

		Util.writeJsonFile(rowFilePath, data);

		return dto.getId();
	}
	
	private String getTableName() {
		return tableName;
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

	private void setLastId(int lastId) {
		String filePath = getLastIdFilePath();
		Util.writeFileContents(filePath, lastId);
	}

	private String getRowFilePath(int id) {
		return tableDirPath + "/" + id + ".json";
	}

	public void backup() {

	}

	void delete(int id) {
		/* 구현 */
	}
}

// DTO
abstract class Dto {
	private int id;
	private String regDate;
	
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
}

class BoardController extends Controller {
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
		String name = Factory.getScanner().nextLine().trim();
		String code = Factory.getScanner().nextLine().trim();
		int result = boardService.makeBoard(name, code);
		
		if (result == -1) {}
		else {
			System.out.printf("게시판 생성 성공\n");
		}
	}

	private void actionDelete(Request request) {
		String code = request.getArg1();
		if (code != null) {
			Board board = Factory.getBoardDao().getBoardByCode(code);
			if (board != null) {
				boardService.deleteBoard(board);
				System.out.println("게시판 삭제 성공");
			} else {
				System.out.printf("존재하지 않는 게시판 입니다.\n");
			}
		} 
//		else {
//			System.out.printf("코드 입력\n");
//		}
	}

	private void actionList(Request request) {
		boardService.listBoard();
	}

	private void actionChangeBoard(Request request) {
		String code = request.getArg1();
		if (code != null) {
			int boardId = boardService.isExistBoard(code);
			if (boardId > 0) {
				boardService.changeBoard(boardId);
			}
		} 
//		else {
//			System.out.printf("코드 입력\n");
//		}
	}
}

class BoardService {
	private BoardDao boardDao;

	BoardService() {
		boardDao = Factory.getBoardDao();
	}
	
	public int makeBoard(String name, String code) {
		Board oldBoard = boardDao.getBoardByNameAndCode(name, code);

		if (oldBoard != null) {
			return -1;
		}

		Board board = new Board(name, code);
		return boardDao.saveBoard(board);
	}
	
	public Board getBoard(int id) {
		return boardDao.getBoard(id);
	}
	
	public List<Board> getBoards() {
		return boardDao.getBoards();
	}

	public void deleteBoard(Board board) {
		boardDao.deleteBoard(board);
	}

	public void listBoard() {
		boardDao.listBoard();
	}

	public int isExistBoard(String deleteCode) {
		return boardDao.isExistBoard(deleteCode);
	}
	
	public void changeBoard(int boardId) {
		boardDao.changeBoard(boardId);
	}
}

class BoardDao {
	DB db;

	BoardDao() {
		db = Factory.getDB();
	}
	
	public Board getBoardByCode(String code) {
		return db.getBoardByCode(code);
	}
	
	public Board getBoardByNameAndCode(String name, String code) {
		return db.getBoardByNameAndCode(name, code);
	}

	public int saveBoard(Board board) {
		return db.saveBoard(board);
	}

	public Board getBoard(int id) {
		return db.getBoard(id);
	}

	public void deleteBoard(Board board) {
		db.deleteBoard(board);
	}

	public void listBoard() {
		db.listBoard();
	}

	public int isExistBoard(String deleteCode) {
		return db.isExistBoard(deleteCode);
	}

	public void changeBoard(int boardId) {
		db.changeBoard(boardId);
	}

	public List<Board> getBoards() {
		return db.getBoards();
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
}

class ArticleReply extends Dto {
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
	
	public static String getNowDateStr() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = Date.format(cal.getTime());
		return dateStr;
	}

	public static String lcfirst(String str) {
		String newStr = "";
		newStr += str.charAt(0);
		newStr = newStr.toLowerCase();

		return newStr + str.substring(1);
	}

	public static boolean isFileExists(String filePath) {
		File f = new File(filePath);
		
		if (f.isFile()) {
			return true;
		}
		return false;
	}
	
	public static String getFileContents(String filePath) {
		String rs = null;
		try {
			FileInputStream fileStream = null; 

			fileStream = new FileInputStream(filePath);

			byte[] readBuffer = new byte[fileStream.available()];
			while (fileStream.read(readBuffer) != -1) {
			}

			rs = new String(readBuffer);

			fileStream.close(); 
		} catch (Exception e) {
			e.getStackTrace();
		}
		return rs;
	}
	
	public static void writeFileContents(String filePath, int data) {
		writeFileContents(filePath, data + "");
	}

	public static void writeFileContents(String filePath, String contents) {
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(new FileOutputStream(filePath));
			bs.write(contents.getBytes()); 
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
	
	public static void writeJsonFile(String filePath, int boardId) {
		writeJsonFile(filePath, boardId);
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