package net.cstong.android.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "user")
public class User {
	public User() {
		username = new String();
		password = new String();
		email = new String();
		mobile = new String();
		avatar_s = new String();
		avatar_m = new String();
		avatar = new String();
		lastvisit = new String();
		lastloginip = new String();
		lastpost = new String();
		cookie = new String();
	}

	public User(final User user) {
		uid = user.uid;
		username = new String(user.username);
		password = new String(user.password);
		age = user.age;
		sex = user.sex;
		email = new String(user.email);
		mobile = new String(user.mobile);
		avatar_s = new String(user.avatar_s);
		avatar_m = new String(user.avatar_m);
		avatar = new String(user.avatar);
		lastvisit = new String(user.lastvisit);
		lastloginip = new String(user.lastloginip);
		lastpost = new String(user.lastpost);
		cookie = new String(user.cookie);
	}

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;

	@Column(name = "uid")
	public String uid;

	// 登录用户名 length=20数据字段的长度是20
	@Column(name = "username", length = 20)
	public String username;

	// 用户密码
	@Column(name = "password")
	public String password;

	// 年龄一般是数值,用type = "INTEGER"规范一下.
	@Column(name = "age", type = "INTEGER")
	public int age;

	// 用户性别
	@Column(name = "sex")
	public String sex;

	// 用户邮箱
	// 假设您开始时没有此属性,程序开发中才想到此属性,也不用卸载程序.
	@Column(name = "email")
	public String email;

	// 用户电话
	// 假设您开始时没有此属性,程序开发中才想到此属性,也不用卸载程序.
	@Column(name = "mobile")
	public String mobile;

	// 头像地址
	@Column(name = "avatar_s")
	public String avatar_s;

	// 头像地址
	@Column(name = "avatar_m")
	public String avatar_m;

	// 头像地址
	@Column(name = "avatar")
	public String avatar;

	// 访问时间
	@Column(name = "lastvisit")
	public String lastvisit;

	// 访问时间
	@Column(name = "lastloginip")
	public String lastloginip;

	// 访问时间
	@Column(name = "lastpost")
	public String lastpost;

	// 城市
	@Column(name = "city")
	public String city;

	// 用户问题
	@Column(name = "question")
	public String question;

	// 用户答案
	@Column(name = "answer")
	public String answer;

	// 登录次数
	@Column(name = "onlinetime")
	public int onlinetime;

	// 登录授权
	@Column(name = "cookie")
	public String cookie;
}
