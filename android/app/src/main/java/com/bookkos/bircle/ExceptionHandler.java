package com.bookkos.bircle;

import static com.bookkos.bircle.CaptureActivity._activity;
import static com.bookkos.bircle.CaptureActivity._context;
import static com.bookkos.bircle.CaptureActivity.groupId;
import static com.bookkos.bircle.CaptureActivity.userId;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutionException;

import org.acra.ACRA;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
 
public class ExceptionHandler implements UncaughtExceptionHandler {
	 
    private Context _context;
    
    private String bookCode = "empty";
    private String shelfId = "empty";
    
    private UncaughtExceptionHandler defaultUncaughtExceptionHandler;
 
    public ExceptionHandler(Context context) {
        _context = context;
        // デフォルト例外ハンドラを保持する。
        defaultUncaughtExceptionHandler = Thread
                .getDefaultUncaughtExceptionHandler();
    }
 
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // スタックトレースを文字列にします。
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String stack_trace = stringWriter.toString();
        stack_trace = stack_trace.replaceAll("=", "＝");
        stack_trace = stack_trace.replaceAll(";", "；");
        stack_trace = stack_trace.replaceAll(":", "：");
        stack_trace = stack_trace.replaceAll(" ", "");
        stack_trace = stack_trace.replaceAll("　", "");
        stack_trace = stack_trace.replaceAll("&", "＆");
        stack_trace = stack_trace.replaceAll("/", "・");
        stack_trace = stack_trace.replaceAll("\n", "__");
        stack_trace = stack_trace.replaceAll("\t", "[]");
        
        sendStackTrace(stack_trace);
        
        // デフォルト例外ハンドラを実行し、強制終了します。
        defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
    }
    
    private void sendStackTrace(String stack_trace) {
    	String request_url = "https://bms-dev.herokuapp.com/android_log?user_id=" + userId + "&group_id=" + groupId + "&book_code=" + bookCode + "&shlef_id=" + shelfId + "&message=" + stack_trace;
    	HttpConnectPostReturnFlag send_stack_trace = new HttpConnectPostReturnFlag(_activity, request_url);
		send_stack_trace.execute();
		
		int result_flag = 0;
		
		try {
			result_flag = send_stack_trace.get();

		} catch (InterruptedException | ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }
    
    public void setBookCode(String book_code) {
    	bookCode = book_code;
    }
    
    public void setShelfId(String shelf_id) {
    	shelfId = shelf_id;
    }
}