package se.hugo.flashcards;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.hugo.flashcards.R;

import com.actionbarsherlock.app.ActionBar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;

public class Util {
	public static int fromDips(Context context, double dips) {
		double scale = context.getResources().getDisplayMetrics().density;
		return (int)(dips * scale + 0.5f);
	}
	
	public static long generateUniqueId() {
		Random random = new Random();
		return random.nextLong();
	}
	
	//TEMP, should be done in XML instead
	public static void customizeActionBar(Resources resources, ActionBar bar) {
		bar.setBackgroundDrawable(new ColorDrawable(resources.getColor(R.color.action_bar_bg)));
	}
	
	public static String until(String str, String regex) {
		return str.split(regex)[0];
	}
	
	public static boolean isImageFile(String filename) {
		Pattern p = Pattern.compile("\\.(png|PNG|bmp|BMP|gif|GIF|jpg|JPG|jpeg|JPEG)$");
		Matcher m = p.matcher(filename); //
		return m.find(); //false; //TEMP
	}
}
