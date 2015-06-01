package com.wjch.vo;

/*
 * User Value Object
 */
public class User {
	private int id;
	private String username;
	private String password;
	private String email;
	private String userpic;
	private String favid;// love song list;

	
	public User(int id, String username, String password, String email,
			String userpic, String favid) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.userpic = userpic;
		this.favid = favid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public String getFavid() {
		return favid;
	}

	public void setFavid(String favid) {
		this.favid = favid;
	}

}
