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
package cn.jarlen.photoedit.filters;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 
 * @author jarlen
 * 
 *         使用：
 * 
 *         Filters filters = new Filters(bitmap,type); filters.process(factor);
 * 
 */

public class Filters
{

	private int filterType = 0;
	private int[] pixels;

	private NativeFilter nativeFilters;

	private int width, height;

	public Filters(Bitmap bitmap, int type)
	{
		this.filterType = type;
		nativeFilters = new NativeFilter();

		width = bitmap.getWidth();
		height = bitmap.getHeight();

		pixels = new int[width * height];

		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	}

	/**
	 * 执行滤镜操作
	 * 
	 * @param factor
	 *            (0 <= factor <= 1)
	 * @return
	 * 
	 *         Bitmap
	 */
	public Bitmap process(float factor)
	{
		int[] newPixels = null;

		switch (filterType)
		{
		/* Black and white effect */
			case FilterType.FILTER4BlackWhite :

				newPixels = nativeFilters.toBlackWhite(pixels, width, height,
						factor);
				break;
			/* Brown effect */
			case FilterType.FILTER4BROWN :

				newPixels = nativeFilters
						.brown(pixels, width, height, factor);
				break;
			/* Sculpture */
			case FilterType.FILTER4CARVING :
				newPixels = nativeFilters.ToCarving(pixels, width, height,
						factor);
				break;
			/* Comic effect */
			case FilterType.FILTER4COMICS :
				newPixels = nativeFilters.comics(pixels, width, height,
						factor);

				break;

			/* gray */
			case FilterType.FILTER4GRAY :
				newPixels = nativeFilters.gray(pixels, width, height, factor);
				break;
			/* LOMO effect */
			case FilterType.FILTER4LOMO :
				newPixels = nativeFilters.lomo(pixels, width, height, factor);
				break;

			/* Mosaic effect */
			case FilterType.FILTER4MOSATIC :
				int mosatic = (int) (factor * 30);
				newPixels = nativeFilters.mosatic(pixels, width, height,
						mosatic);
				break;

			/* Film effect */
			case FilterType.FILTER4NEGATIVE :
				newPixels = nativeFilters.negative(pixels, width, height,
						factor);

				break;

			/* Neon effect */
			case FilterType.FILTER4NiHong :
				newPixels = nativeFilters.niHong(pixels, width, height,
						factor);
				break;

			/* Nostalgic effect */
			case FilterType.FILTER4NOSTALGIC :
				newPixels = nativeFilters.nostalgic(pixels, width, height,
						factor);
				break;

			/* Overexposure */
			case FilterType.FILTER4OVEREXPOSURE :
				newPixels = nativeFilters.overExposure(pixels, width, height,
						factor);
				break;

			/* Relief effect */
			case FilterType.FILTER4RELIEF :
				newPixels = nativeFilters.ToRelief(pixels, width, height,
						factor);
				break;
			/* Sharpening effect */
			case FilterType.FILTER4RUIHUA :
				newPixels = nativeFilters.ToRuiHua(pixels, width, height,
						factor);
				break;
			/* sketch */
			case FilterType.FILTER4SKETCH :
				newPixels = nativeFilters.sketch(pixels, width, height,
						factor);
				break;
			/* pencil drawing */
			case FilterType.FILTER4SKETCH_PENCIL :
				newPixels = nativeFilters.sketchPencil(pixels, width, height,
						factor);
				break;

			/* Softening effect */
			case FilterType.FILTER4SOFTNESS :
				newPixels = nativeFilters.ToSoftness(pixels, width, height,
						factor);
				break;

			/* Bright white effect */
			case FilterType.FILTER4WHITELOG :
				newPixels = nativeFilters.ToWhiteLOG(pixels, width, height,
						FilterType.BeitaOfWhiteLOG, factor);
				break;

			default :
				newPixels = pixels;
				break;
		}

		Bitmap newBitmap = Bitmap.createBitmap(newPixels, width, height,
				Config.ARGB_8888);
		return newBitmap;
	}

}
