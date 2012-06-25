package se.flashcards;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
    int galleryItemBackground;
    private Context context;

    private int[] imageIds;

    public ImageAdapter(Context c, int[] imageIds) {
        context = c;
        this.imageIds = imageIds;

//        TypedArray a = c.obtainStyledAttributes(R.styleable.HelloGallery);
//        galleryItemBackground = a.getResourceId(
//                R.styleable.HelloGallery_android_galleryItemBackground, 0);
//        a.recycle();
    }

    public int getCount() {
        return imageIds.length;
    }

    public Object getItem(int position) {
        return imageIds[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(context);

        i.setImageResource(imageIds[position]);
        i.setLayoutParams(new Gallery.LayoutParams(500, 200));
        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
 //       i.setBackgroundResource(galleryItemBackground);

        return i;
    }
}
