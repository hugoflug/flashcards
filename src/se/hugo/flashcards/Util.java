package se.hugo.flashcards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.hugo.flashcards.R;

import com.actionbarsherlock.app.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

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
		Pattern p = Pattern.compile("\\.(png|bmp|gif|jpg|jpeg)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(filename); //
		return m.find(); 
	}
	
	public static boolean isFile(String filename, String extension) {
		Pattern p = Pattern.compile("\\." + extension + "$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(filename); //
		return m.find();		
	}
	
	public static void saveBitmapToFile(Context context, Bitmap bmp, String filename) throws FileNotFoundException {
		FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
	}
	
	public static LinkedList<Integer> ascendingList(int length) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < length; i++) {
			list.add(i);
		}
		return list;
	}
	
	public static File getStorageDirectory() {
		return null;
	}
	
	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();

	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size - count)) < size);
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}	
}
