package com.bookkos.bircle;

import java.util.EventListener;

public interface DialogListener extends EventListener{
	public void doPositiveClick();
	public void doNeutralClick();
	public void doNegativeClick();
	public void doUnregisterClick(String isbn, String book_title);
	public void doOKClick(int borrow_return_flag, String isbn, String book_title, String shelf_name);
	public void doDecideClick(String isbn, String shelf_id, String shelf_name);
}
