package se.flashcards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class NewCardActivity extends SherlockActivity
{
	private CardContentView answerContentView;
	private CardContentView questionContentView;
	private Uri newQuestionPhotoUri;
	private Uri newAnswerPhotoUri;
	private BitmapDownsampler downSampler;
	
	private static final int SELECT_QUESTION_IMAGE = 0;
	private static final int SELECT_ANSWER_IMAGE = 1;
	private static final int TAKE_QUESTION_PHOTO = 2;
	private static final int TAKE_ANSWER_PHOTO = 3;
	
	private CardContent question;
	private CardContent answer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.new_card);
    	setTheme(R.style.Theme_Sherlock);
    	
    	answerContentView = (CardContentView)findViewById(R.id.answer_content);
    	questionContentView = (CardContentView)findViewById(R.id.question_content);
    	
    	answerContentView.setCardContent(getDefaultAnswerContent());
    	questionContentView.setCardContent(getDefaultQuestionContent());
    	
        downSampler = new BitmapDownsampler(this, 600, 1000); //600, 1000
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
	    super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
		    switch (requestCode) {
		    	case TAKE_QUESTION_PHOTO:
					try {
						CardContent questionContent = new CardContent(downSampler.decode(newQuestionPhotoUri), newQuestionPhotoUri);
						setQuestion(questionContent);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
					}
		    	break;
		    	case TAKE_ANSWER_PHOTO:
			    	try {
						CardContent answerContent = new CardContent(downSampler.decode(newAnswerPhotoUri), newAnswerPhotoUri);
						setAnswer(answerContent);
			    	} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
			    	}
		    	case SELECT_ANSWER_IMAGE:
		    		Uri answerUri = intent.getData();
					try {
						CardContent answerContent = new CardContent(downSampler.decode(answerUri), answerUri);
						setAnswer(answerContent);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
					}
		    	break;
		    	case SELECT_QUESTION_IMAGE:
		    		Uri questionUri = intent.getData();
					try {
						CardContent questionContent = new CardContent(downSampler.decode(questionUri), questionUri);
						setQuestion(questionContent);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
					}
		    	break;
		    }
		}
	}
    
    public void pickQuestionImage(View view) {
    	Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	pickImageIntent.setType("image/*"); //necessary??
    	startActivityForResult(pickImageIntent, SELECT_QUESTION_IMAGE);
    }
    
    public void pickQuestionText(View view) {
        
    }
    
    public void takeQuestionImage(View view) {
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		newQuestionPhotoUri = createNewImageUri("q");
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newQuestionPhotoUri);
	    startActivityForResult(takePictureIntent, TAKE_QUESTION_PHOTO);
    }
    
    public void pickAnswerImage(View view) {
    	Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	pickImageIntent.setType("image/*"); //necessary??
    	startActivityForResult(pickImageIntent, SELECT_ANSWER_IMAGE);
    }
    
    public void pickAnswerText(View view) {
        
    }
    
    public void takeAnswerImage(View view) {
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		newQuestionPhotoUri = createNewImageUri("a");
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newAnswerPhotoUri);
	    startActivityForResult(takePictureIntent, TAKE_ANSWER_PHOTO);
    }
    
	private Uri createNewImageUri(String addition) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = timeStamp + "_" + addition + ".jpg";
		File file = new File(
			    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 
			    imageFileName
			);	
		return Uri.fromFile(file);
	}
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getSupportMenuInflater();
//        //TEMP
//        inflater.inflate(R.layout.cardslist_actionbar_menu, menu);
//        return true;
//    }
	
	private void setQuestion(CardContent q) {
		question = q;
		questionContentView.setCardContent(q);
	}
	
	private void setAnswer(CardContent a) {
		answer = a;
		answerContentView.setCardContent(a);
	}
    
    private CardContent getDefaultQuestionContent() {
    	return new CardContent("Pick a question");
    }
    
    private CardContent getDefaultAnswerContent() {
    	return new CardContent("Pick an answer");
    }
}
