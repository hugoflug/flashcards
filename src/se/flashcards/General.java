package se.flashcards;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class General {
	public static Bitmap decodeSampledBitmapFromUri(Context context, Uri resUri,
	        int reqWidth, int reqHeight) throws IOException {

		InputStream input = context.getContentResolver().openInputStream(resUri);
		
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(input, null, options);
	    
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
		
		input.close();
		input = context.getContentResolver().openInputStream(resUri);
		
		return BitmapFactory.decodeStream(input, null, options);
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    Log.v("Flashcards", "iss " + inSampleSize);
	    
	    return inSampleSize;
	}
}
