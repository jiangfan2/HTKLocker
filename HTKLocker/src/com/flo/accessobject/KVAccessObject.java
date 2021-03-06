package com.flo.accessobject ;

import android.content.Context;

import com.flo.model.KeyValue;
import com.flo.util.MD5;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class KVAccessObject  {
	DbUtils db;

	
	static KVAccessObject singleton=null;

	public static KVAccessObject getInstance(Context context) {
		if (singleton == null) {
			singleton = new KVAccessObject(context);
			return singleton;
		} else
			return singleton;
	}
	private KVAccessObject(Context context){
		db = DbUtils.create(context);
	}

	public boolean validateUser(String password) {
		KeyValue keyValue = new KeyValue();
		try {
			keyValue = db.findFirst(Selector.from(KeyValue.class).where("KEY",
					"=", "PASSWORD"));
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (MD5.encrypt(password).equals(keyValue.getValue())) {
			return true;
		} else {
			return false;
		}
	}

	public String getOldPassword() {
		KeyValue keyValue=null;
		try {
			keyValue = db.findFirst(Selector.from(KeyValue.class).where("KEY",
					"=", "PASSWORD"));
			if (keyValue == null) {
				return "FIRSTLOGIN";
			} else
				return keyValue.getValue();
		} catch (DbException e) {
			return "FIRSTLOGIN";
		}
	}



	
	public boolean isFirstLogin() {
		if (getOldPassword().equals("FIRSTLOGIN")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setPassword( String password) {
		KeyValue keyValue = new KeyValue();
		keyValue.setKey("PASSWORD");
		keyValue.setValue(MD5.encrypt(password));

		try {
			db.delete(KeyValue.class, WhereBuilder.b("KEY", "=", "PASSWORD"));
			db.save(keyValue);
			return true;
		} catch (DbException e) {
			return false;
		}
	}

}
