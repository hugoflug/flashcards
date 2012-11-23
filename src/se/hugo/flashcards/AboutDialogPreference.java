package se.hugo.flashcards;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.util.Linkify;
import android.util.AttributeSet;

public class AboutDialogPreference extends DialogPreference {

	public AboutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogTitle(R.string.about_title);
		setDialogMessage(R.string.about_text);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
        setDialogIcon(null);
	}

}
