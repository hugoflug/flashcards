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
import android.widget.ImageView;

public class CardPagerAdapter extends PagerAdapter {
	private Context context;
    private List<Card> cards;
    
    public CardPagerAdapter(Context c, List<Card> cards) {
        context = c;
        this.cards = cards;
    }

	@Override
	public int getCount() {
        return cards.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
//		ImageView i = new ImageView(context);
//		i.setImageBitmap(cards.get(position).getQuestion());
//        i.setScaleType(ImageView.ScaleType.FIT_START);
//		collection.addView(i);
		
		CardContentView i = new CardContentView(context);
		i.setCardContent(cards.get(position).getQuestion());
		//i.setScaleType(ImageView.ScaleType.FIT_START);
		collection.addView(i);
		
		return i;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
    }
}
