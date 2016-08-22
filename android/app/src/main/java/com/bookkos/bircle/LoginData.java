package com.bookkos.bircle;

public class LoginData {
	int flag;
	int userId;
	int groupId;
	String groupName;
	
	LoginData(int user_id, int flag, int group_id, String group_name) {
		this.flag = flag;
		this.userId = user_id;
		this.groupId = group_id;
		this.groupName = group_name;
	}
}
