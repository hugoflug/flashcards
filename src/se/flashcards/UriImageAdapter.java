package se.flashcards;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class UriImageAdapter extends BaseAdapter {
    int galleryItemBackground;
    private Context context;

    private List<Uri> imageUris;
    
    public UriImageAdapter(Context c) {
        context = c;
        imageUris = new ArrayList<Uri>();
    }
    
    public void addUri(Uri uri) {
    	imageUris.add(uri);
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
        return imageUris.size();
	}

	@Override
	public Object getItem(int position) {
		return imageUris.get(position); //return position?
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(context);

        i.setImageURI(imageUris.get(position));
        i.setLayoutParams(new Gallery.LayoutParams(600, 500));
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        return i;
	}

}
