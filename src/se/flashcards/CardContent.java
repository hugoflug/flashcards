package se.flashcards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

public class CardContent {
	private Bitmap bitmap;
	private Uri bitmapUri;
	private String string;
	private Context c;
	private boolean isBitmap;
	
	public CardContent(Context c, String string) {
		this.string = string;
		this.c = c;
		isBitmap = false;
	}
	
	public CardContent(Bitmap bitmap, Uri bitmapUri) {
		this.bitmap = bitmap;
		this.bitmapUri = bitmapUri;
		isBitmap = true;
	}
	
	public void save() {
		
	}
	
	public void load() {
		
	}
	
	public View getView() {
		ImageView i = new ImageView(c);
		i.setImageBitmap(bitmap);
        i.setScaleType(ImageView.ScaleType.FIT_START);
        return i;
	}
}
