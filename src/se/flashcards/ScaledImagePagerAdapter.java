package se.flashcards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

public class ScaledImagePagerAdapter extends PagerAdapter {
    private int width, height;
    private Context context;
    
    private List<Bitmap> images;
    
    public ScaledImagePagerAdapter(Context c, int width, int height) {
        context = c;
        images = new ArrayList<Bitmap>();
        this.width = width;
        this.height = height;
    }
	
    private Bitmap loadBitmap(Uri uri) throws IOException {
    	BitmapDownsampler downSampler = new BitmapDownsampler(context, width, height);
		return downSampler.decode(uri);
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
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		ImageView i = new ImageView(context);
		i.setImageBitmap(images.get(position));
        i.setScaleType(ImageView.ScaleType.FIT_START);
		collection.addView(i);
		
		return i;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
    }

}
