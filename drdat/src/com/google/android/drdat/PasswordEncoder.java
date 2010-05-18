package com.google.android.drdat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility to make md5 hashes with a string input. 
 * Note the original is in error and sometimes returned words that are missing leading zeros.
 * This bug has been fixed.
 * 
 * @see {@linkplain http://workbench.cadenhead.org/news/1428/creating-md5-hashed-passwords-java}
 * 
 * @author ??? 
 *
 */
public class PasswordEncoder {
	public static final int MD5_LEN = 32;
	
	public static String encode(String password) {
		String hashword = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(password.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			hashword = hash.toString(16);
			while (hashword.length() < MD5_LEN) hashword = "0" + hashword;
		} catch (NoSuchAlgorithmException nsae) {
			// ignore
		}
		return hashword;
	}
	public static SharedPreferences getSharedPrefs(Context context) {
			return context.getSharedPreferences(
					context.getString(R.string.UpdateLoginFile), 
					Context.MODE_PRIVATE
				);
	}
	
	public static String getEmail(Context context) {
		return getSharedPrefs(context).getString("email", "");
	}
	
	public static String getPasswordMD5(Context context) {
		return encode(getSharedPrefs(context).getString("password", ""));
	}
}
