package se.hugo.flashcards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.hugo.flashcards.R;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
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
    	setTheme(R.style.Theme_Sherlock);
    	
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    	
    	setContentView(R.layout.new_card);
    	
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); 
        
        //TEMP, should be set through XML
        ActionBar bar = getSupportActionBar();
        Util.customizeActionBar(getResources(), bar);
        bar.setTitle(R.string.menu_add_new);
    	
    	confirmButton = (Button)findViewById(R.id.confirm_button);
 
    	question = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.question);
    	answer = (PickCardFragment)getSupportFragmentManager().findFragmentById(R.id.answer);
    	
        Intent intent = getIntent();
        CardContent questionContent = intent.getParcelableExtra("question_content");
        CardContent answerContent = intent.getParcelableExtra("answer_content");
        
        if (questionContent != null || answerContent != null) {
        	bar.setTitle(R.string.menu_edit_card);
        }
        
        Display display = getWindowManager().getDefaultDisplay();
        BitmapDownsampler sampler = new BitmapDownsampler(this, display.getWidth(), display.getHeight()/2); //600, 1000
        
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
        	question.setContentRaw(new CardContent(getString(R.string.pick_a_question)));
        }
        
        if (answerContent != null) {
        	answer.setContent(answerContent);
        	answer.setButtonsVisibility(View.VISIBLE);
        } else {
        	answer.setContentRaw(new CardContent(getString(R.string.pick_an_answer)));
        }
    	
    	question.setNewTextTitle(getString(R.string.write_new_question));
    	answer.setNewTextTitle(getString(R.string.write_new_answer));
    	
    	question.setNewTextHint(getString(R.string.question));
    	answer.setNewTextHint(getString(R.string.answer));
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