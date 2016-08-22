package com.bookkos.bircle;

// 図書の一括登録モードで表示されるリストビューのItemのクラス
public class BookListViewItem {
	
	private String isbn;
	private int id;
	
	public BookListViewItem (String isbn){
		this.isbn = isbn;
	}
	
	public String getIsbn(){
		return this.isbn;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}