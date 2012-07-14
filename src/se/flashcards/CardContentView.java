package se.flashcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CardContentView extends FrameLayout {
	private Context context;
	
	
	public CardContentView(Context context) {
		super(context);
		this.context = context;
	}
	
	public CardContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	public CardContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
	
	public void setCardContent(CardContent content) {
		removeAllViews();
		if (content.isBitmap()) {
			ImageView i = new ImageView(context);
			i.setImageBitmap(content.getBitmap());
	//      i.setScaleType(ImageView.ScaleType.FIT_START);
			addView(i);
		} else {
			TextView t = new TextView(context);
			t.setText(content.getString());
			addView(t);
		}
	}

	
}
