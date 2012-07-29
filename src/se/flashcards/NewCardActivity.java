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
	private String listName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.new_card);
    	setTheme(R.style.Theme_Sherlock);
    	
    	Intent intent = getIntent();
    	listName = intent.getStringExtra("list_name");
    	
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
 
    	question = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.question);
    	answer = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.answer);
    	
    	question.setDefaultContent(new CardContent("Pick a question"));
    	answer.setDefaultContent(new CardContent("Pick an answer"));
    	
//    	InfoSaver infoSaver = InfoSaver.getInfoSaver(this);  	
//    	if (infoSaver.cardContentExists(listName + "_new_question")) {
//    		CardContent questionContent = infoSaver.loadCardContent(listName + "_new_question", new BitmapDownsampler(this, 1000, 1000));
//        	question.setContent(questionContent);
//    	}
//    	if (infoSaver.cardContentExists(listName + "_new_answer")) {
//    		CardContent answerContent = infoSaver.loadCardContent(listName + "_new_answer", new BitmapDownsampler(this, 1000, 1000));
//    		answer.setContent(answerContent);
//    	}
    	
    	question.setNewTextTitle("Write a new question");
    	answer.setNewTextTitle("Write a new answer");
    	
    	question.setNewTextHint("Question");
    	answer.setNewTextHint("Answer");
    	
    	confirmButton = (Button)findViewById(R.id.confirm_button);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    			//doesn't work as intended
    	    	Intent intent = new Intent(this, CardsListActivity.class);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

    	    	startActivity(intent);
    	    	finish(); //??
    		break;
    	}
    	return true;
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
	
	@Override
	protected void onPause() {
		super.onPause();
		InfoSaver saver = InfoSaver.getInfoSaver(this);
		saver.saveCardContent(listName + "_new_question", question.getCardContent());
		saver.saveCardContent(listName + "_new_answer", answer.getCardContent());
	}
}
