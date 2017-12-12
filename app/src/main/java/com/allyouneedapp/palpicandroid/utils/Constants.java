/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.allyouneedapp.palpicandroid.utils;

import android.os.Environment;

public class Constants
{
	public static final String filePath = Environment.getExternalStorageDirectory() + "/PictureTest/";
	public static final String APPID = "901417566407:android:03302751eda336db";
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000;
	public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2000;
	public static final int MY_PERMISSIONS_REQUEST_CAMERA = 3000;
	public static final int MY_PERMISSON_ACCESS_CHECKIN_PROPERTIES = 4000;
	public static final int CAMERA_WITH_DATA = 3023;

	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHOTO = "photoUrl";
	public static final String KEY_UID = "uid";

	public static final int KEY_GOOGLE_SIGN_IN = 5000;

	public static final int REQUEST_FINISH_FILTER = 1030;

	public static final int THUMBNAIL_SIZE = 64;
	public static final int THUMBNAIL_SIZE_BIG = 128;
	public static final int THUMBNAIL_HEIGHT = 250;

	public static final String FACEBOOK_URL = "https://www.facebook.com/palpicapp";
}
