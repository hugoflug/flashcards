package se.flashcards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
		CardContentView i = new CardContentView(context);
		i.setScaleType(ImageView.ScaleType.FIT_START);
		Log.v("flashcards", "card: " + cards.get(position)); //card: null
		i.setCardContent(cards.get(position).getQuestion());
		collection.addView(i);
		
		return i;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
    }
}
