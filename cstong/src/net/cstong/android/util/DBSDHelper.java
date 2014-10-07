package net.cstong.android.util;

import net.cstong.android.model.LocalUser;
import net.cstong.android.model.User;
import android.content.Context;

import com.ab.db.orm.AbSDDBHelper;

public class DBSDHelper extends AbSDDBHelper {
	// 数据库名
	private static final String DBNAME = "cstong.db";
	// 数据库 存放路径
	private static final String DBPATH = "cstong";

	// 当前数据库的版本
	private static final int DBVERSION = 1;
	// 要初始化的表
	private static final Class<?>[] clazz = { User.class, LocalUser.class /**, Stock.class, Friend.class, IMMessage.class **/
	};

	public DBSDHelper(final Context context) {
		super(context, DBPATH, DBNAME, null, DBVERSION, clazz);
	}

}
