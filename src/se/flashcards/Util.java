package se.flashcards;

import java.util.Random;

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
}
