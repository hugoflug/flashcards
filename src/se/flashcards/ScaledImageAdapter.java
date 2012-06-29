package se.flashcards;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ScaledImageAdapter extends BaseAdapter {
    private int galleryItemBackground, width, height;
    private Context context;
    

    private List<Bitmap> images;
    
    public ScaledImageAdapter(Context c, int width, int height) {
        context = c;
        images = new ArrayList<Bitmap>();
        this.width = width;
        this.height = height;
    }
    
    private Bitmap loadBitmap(Uri uri) throws IOException {
		return General.decodeSampledBitmapFromUri(context, uri, width, height);
    }
    
    public void addUri(Uri uri) throws IOException {
    	images.add(loadBitmap(uri));
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
        return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position); 
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        ImageView i = new ImageView(context);

        i.setImageBitmap(images.get(position));
        i.setLayoutParams(new Gallery.LayoutParams(width, height));
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        Log.v("Flashcards", "size: " + images.size());
        
        return i;
	}

}
