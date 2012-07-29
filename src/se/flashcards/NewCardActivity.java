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
    	
    	question.setContentRaw(new CardContent("Pick a question"));
    	answer.setContentRaw(new CardContent("Pick an answer"));
    	
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
}