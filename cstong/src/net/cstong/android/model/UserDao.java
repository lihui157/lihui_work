package net.cstong.android.model;

import net.cstong.android.util.DBSDHelper;
import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;

/**
 * 
 * © 2012 amsoft.cn
 * 名称：UserDao.java 
 * 描述：用户信息
 * @author 还如一梦中
 * @date：2013-7-31 下午4:12:36
 * @version v1.0
 */
public class UserDao extends AbDBDaoImpl<User> {
	public UserDao(final Context context) {
		super(new DBSDHelper(context), User.class);
	}
}
