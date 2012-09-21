package se.flashcards;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardListListItem extends LinearLayout implements Checkable {

	private boolean isChecked = false;
	private TextView titleView;
	private TextView amountView;
	
	public CardListListItem(Context context) {
		super(context);
		inflate();
	}
	
	public CardListListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
	}
	
	private void inflate() {
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View subView = inflater.inflate(R.layout.card_list_item, this);
		titleView = (TextView)subView.findViewById(R.id.card_list_title);
		amountView = (TextView)subView.findViewById(R.id.card_list_amount);
	}
	
	public void setTitle(CharSequence title) {
		titleView.setText(title);
	}
	
	public void setAmount(int amount) {
		String cardsText;
		if (amount == 1) {
			cardsText = getContext().getString(R.string.card);
		} else {
			cardsText = getContext().getString(R.string.cards);
		}
		
		amountView.setText(amount + " " + cardsText);
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;
		updateBackground();
	}

	@Override
	public void toggle() {
		setChecked(!isChecked());
	}
	
	private void updateBackground() {
		int backgroundId;
        if (isChecked()) {
        	backgroundId = R.color.checked_list_item;
        } else {
        	backgroundId = -1;
        }
        if (backgroundId == -1) {
        	setBackgroundDrawable(null);
        } else {
	        Drawable background = getResources().getDrawable(backgroundId);
	        setBackgroundDrawable(background);
        } 
	}
}
