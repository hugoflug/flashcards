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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
    	
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);     
    	
    	confirmButton = (Button)findViewById(R.id.confirm_button);
 
    	question = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.question);
    	answer = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.answer);
    	
        Intent intent = getIntent();
        CardContent questionContent = intent.getParcelableExtra("question_content");
        CardContent answerContent = intent.getParcelableExtra("answer_content");
        
        BitmapDownsampler sampler = new BitmapDownsampler(this, 600, 1000);
        
        if (questionContent != null) {
            try {
    			questionContent.reloadBitmap(sampler);
    			answerContent.reloadBitmap(sampler);
    		} catch (IOException e) {
    			Log.v("flashcards", "Couldn't load bitmap");
    		}
        	question.setContent(questionContent);
        	question.setButtonsVisibility(View.VISIBLE);
        } else {   	
        	question.setContentRaw(new CardContent("Pick a question"));
        }
        
        if (answerContent != null) {
        	answer.setContent(answerContent);
        	answer.setButtonsVisibility(View.VISIBLE);
        } else {
        	answer.setContentRaw(new CardContent("Pick an answer"));
        }
    	
    	question.setNewTextTitle("Write a new question");
    	answer.setNewTextTitle("Write a new answer");
    	
    	question.setNewTextHint("Question");
    	answer.setNewTextHint("Answer");
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    	    	Intent intent = new Intent(this, CardsListActivity.class);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

    	    	startActivity(intent);
    	    	finish(); 
    		break;
    	}
    	return true;
	}

	@Override
	public void onContentChanged(CardContent newContent) {
		updateConfirmButtonStatus();
	}
	
	private void updateConfirmButtonStatus() {
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