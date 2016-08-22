package com.bookkos.bircle;

import com.bookkos.bircle.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BookListViewAdapter extends ArrayAdapter<BookListViewItem> {
	
	private LayoutInflater inflater;
	private int layoutId;
	
	private RemoveIsbnListener _removeIsbnListener;
	
	public BookListViewAdapter(Context context, int resource, RemoveIsbnListener removeIsbnListener) {
		super(context, resource);
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutId = resource;
		_removeIsbnListener = removeIsbnListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null){
			view = inflater.inflate(layoutId, parent, false);
		}
		
		TextView isbn_text_view = (TextView)view.findViewById(R.id.isbn_text_view);
		Button book_delete_button = (Button)view.findViewById(R.id.book_delete_button);
		
		BookListViewItem item = (BookListViewItem) getItem(position);
		item.setId(position);
		book_delete_button.setTag(item);
		
		book_delete_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 削除処理
				BookListViewItem item = (BookListViewItem)v.getTag();
				remove(item);
				_removeIsbnListener.removeIsbn(item.getId(), item.getIsbn());
			}
		});
		
		isbn_text_view.setText((item.getId() + 1) + "冊目: " + item.getIsbn());
		
		return view;
	}
}