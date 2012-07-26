package se.flashcards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class NewCardActivity extends SherlockFragmentActivity implements PickCardFragment.OnContentChangedListener
{
	public static final String ANSWER_EXTRA = "answer";
	public static final String QUESTION_EXTRA = "question";
	
	private PickCardFragment question;
	private PickCardFragment answer;
	
	private Button confirmButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.new_card);
    	setTheme(R.style.Theme_Sherlock);
    	
    	answer = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.answer);
    	question = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.question);
    	
    	confirmButton = (Button)findViewById(R.id.confirm_button);
    }

	@Override
	public void onContentChanged(CardContent newContent) {
		if (!question.isContentDefault() && !answer.isContentDefault()) {
			confirmButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void onConfirmClicked(View view) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(ANSWER_EXTRA, answer.getCardContent());
		resultIntent.putExtra(QUESTION_EXTRA, question.getCardContent());
		setResult(SherlockActivity.RESULT_OK, resultIntent);
		finish();
	}
}
