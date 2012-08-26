package se.flashcards;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.TextView;

public class CardListListItem extends TextView implements Checkable {

	private boolean isChecked = false;
	
	public CardListListItem(Context context) {
		super(context);
	}
	
	public CardListListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        	backgroundId = android.R.color.holo_blue_dark; //blue??
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

