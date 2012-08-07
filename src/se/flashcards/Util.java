package se.flashcards;

import android.content.Context;

public class Util {
	public static int fromDips(Context context, double dips) {
		double scale = context.getResources().getDisplayMetrics().density;
		return (int)(dips * scale + 0.5f);
	}
}
