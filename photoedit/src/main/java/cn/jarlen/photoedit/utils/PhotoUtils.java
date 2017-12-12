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
package cn.jarlen.photoedit.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Picture basic operation
 * @author jarlen
 *
 */

public class PhotoUtils
{

	/**
	 * Image Rotation * @param int * Rotate Original Image * * @param degrees * Rotation Degrees * * @return * Image after rotation
	 * 
	 */
	public static Bitmap rotateImage(Bitmap bit, int degrees)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
				bit.getHeight(), matrix, true);
		return tempBitmap;
	}
	
	/**
	 *Flip the image
	 *
	 * @param bit
	 * Flip the original image
	 *
	 * @param x
	 * Flip the X axis
	 *
	 * @param y
	 * Turn the Y axis
	 *
	 * @return
	 * Flip the image afterwards
	 *
	 * Description:
	 * (1, -1) to flip up and down
	 * (-1,1) Flip left and right
	 * 
	 */
	public static Bitmap reverseImage(Bitmap bit,int x,int y)
	{
		Matrix matrix = new Matrix();
		matrix.postScale(x, y);
		
		Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
				bit.getHeight(), matrix, true);
		return tempBitmap;
	}
}
