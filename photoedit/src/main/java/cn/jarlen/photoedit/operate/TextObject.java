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
package cn.jarlen.photoedit.operate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

/**
 * @author jarlen
 */
public class TextObject extends ImageObject
{

	private int textSize = 80;
	private int color = Color.BLACK;
//	private String typeface;
	private String text;
	private boolean bold = false;
	private boolean italic = false;
	private Context context;
	private Typeface typeface = Typeface.DEFAULT;

	Paint paint = new Paint();

	/**
	 * Construction method
	 * 
	 * @param context
	 *            Context
	 * @param text
	 *            input text
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param rotateBm
	 * 			Rotate the button's picture
	 * @param deleteBm
	 *            Delete the picture of the button
	 */
	public TextObject(Context context, String text, int x, int y,
			Bitmap rotateBm, Bitmap deleteBm)
	{
		super(text);
		this.context = context;
		this.text = text;
		mPoint.x = x;
		mPoint.y = y-120;
		this.rotateBm = rotateBm;
		this.deleteBm = deleteBm;
		regenerateBitmap();
	}

	public TextObject()
	{
	}

	/**
	 * Draw a font
	 */
	public void regenerateBitmap()
	{
		paint.setAntiAlias(true);
		paint.setTextSize(textSize);
		paint.setTypeface(typeface);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		paint.setDither(true);
		paint.setFlags(Paint.SUBPIXEL_TEXT_FLAG);
        String lines[] = text.split("\n");

		int textWidth = 0;
		for (String str : lines)
		{
			int temp = (int) paint.measureText(str);
			if (temp > textWidth)
				textWidth = temp;
		}
		if (textWidth < 1)
			textWidth = 1;
		if (srcBm != null)
			srcBm.recycle();
		srcBm = Bitmap.createBitmap(textWidth, textSize * (lines.length) + 30,// add bottom height for draw text object.
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(srcBm);
		canvas.drawARGB(0, 0, 0, 0);
		for (int i = 1; i <= lines.length; i++)
		{
			canvas.drawText(lines[i - 1], 0, i * textSize, paint);
		}
		setCenter();
	}

	/**
	 * Sets the font style
	 * 
	 * @return Typeface Default system font, set the property after the transformation of the font currently supports two local fonts by3500.ttf 、 bygf3500.ttf
	 */

	public Typeface getTypefaceObj()
	{
		Typeface tmptf = Typeface.DEFAULT;
		if (typeface != null)
		{
			tmptf = Typeface.createFromAsset(context.getAssets(), "fonts/" + typeface + ".ttf"); //changed by Mostofa
		}
		if (bold && !italic)
			tmptf = Typeface.create(tmptf, Typeface.BOLD);
		if (italic && !bold)
			tmptf = Typeface.create(tmptf, Typeface.ITALIC);
		if (italic && bold)
			tmptf = Typeface.create(tmptf, Typeface.BOLD_ITALIC);
		return tmptf;
	}

	/**
	 * After setting the property value, submit the method
	 */
	public void commit()
	{
		regenerateBitmap();
	}

	/**
	 *\	Public getter and setter methods
	 */
	public int getTextSize()
	{
		return textSize;
	}

	public void setTextSize(int textSize)
	{
		this.textSize = textSize;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public Typeface getTypeface()
	{
		return typeface;
	}

	public void setTypeface(Typeface typeface)
	{
		this.typeface = typeface;
	}

	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}

	public int getX()
	{
		return mPoint.x;
	}

	public void setX(int x)
	{
		this.mPoint.x = x;
	}

	public int getY()
	{
		return mPoint.y;
	}

	public void setY(int y)
	{
		this.mPoint.y = y;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

}
