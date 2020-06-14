package com;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Table<T> {
	private Class<T> dataCls;
	private String tableName;
	private String tableDirPath;
	
	public Table(Class<T> dataCls, String dbDirPath) {
		this.dataCls = dataCls;
		this.tableName = Util.lcfirst(dataCls.getCanonicalName());
		this.tableDirPath = dbDirPath + "/" + this.tableName;

		Util.makeDir(tableDirPath);
	}
	
	// # 로그인 아이디 중복
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

	private String getRowFilePath(int id) {
		return tableDirPath + "/" + id + ".json";
	}
	// Join, Write
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
	}

	private void setLastId(int lastId) {
		String filePath = getLastIdFilePath();
		Util.writeFileContents(filePath, lastId);
	}
	
	// Modify
	public void saveRow(Article article, int articleId) {
		Dto dto = (Dto) article;

		if (dto.getId() == 0) {
			int lastId = getLastId();
			dto.setId(lastId);
			setLastId(lastId);
		}
		String FilePath = getRowFilePath(articleId);

		Util.writeJsonFile(FilePath, article);
	}
	
	// Article Delete
	public boolean delete(int articleId) {
		String filePath = getRowFilePath(articleId);

		if (Util.isFileExists(filePath) == false) {
			return false;
		}

		File removeFile = new File(filePath);
		removeFile.delete();
		return true;
	}
	
	// Board Delete
	public void delete(Board board) {
		String filePath = getRowFilePath(board.getId()); 
		
		for (Article article : Factory.getDB().getArticles()) {
			if (article.getBoardId() == board.getId()) {
				Factory.getDB().delete(article.getId());
			}
		}
		File deleteFile = new File(filePath);
		deleteFile.delete();
	}
}