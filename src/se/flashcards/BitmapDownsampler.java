package se.flashcards;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class BitmapDownsampler 
{
	private Context context;
	private int width, height;
	
	public BitmapDownsampler(Context c, int width, int height) {
		context = c;
		this.width = width;
		this.height = height;
	}
	
	public Bitmap decode(Uri uri) throws IOException {
		InputStream input = context.getContentResolver().openInputStream(uri);
		
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    options.inDither = false;
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    
	    BitmapFactory.decodeStream(input, null, options);
	    
	    options.inSampleSize = calculateInSampleSize(options);
	    
	    options.inJustDecodeBounds = false;
		
		input.close();
		input = context.getContentResolver().openInputStream(uri);
		
		return BitmapFactory.decodeStream(input, null, options);
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options) {
	    final int currentHeight = options.outHeight;
	    final int currentWidth = options.outWidth;
	    int inSampleSize = 1;
	    
	    if (currentHeight > height || currentWidth > width) {
	        if (currentHeight > currentWidth) {
	            inSampleSize = Math.round((float)currentHeight / (float)height);
	        } else {
	            inSampleSize = Math.round((float)currentWidth / (float)width);
	        }
	    }    
	    return inSampleSize;
	}
}
