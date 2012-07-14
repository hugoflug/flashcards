package se.flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardsListActivity extends SherlockActivity {
	private static final int SELECT_QUESTION_IMAGE = 0;
	private static final int SELECT_ANSWER_IMAGE = 1;
	private static final int TAKE_QUESTION_PHOTO = 2;
	private static final int TAKE_ANSWER_PHOTO = 3;
	private CardPagerAdapter cardAdapter;
	private List<Card> cardList;
	private ViewPager viewPager;
	private ImageView answerImage;
	private BitmapDownsampler downSampler;
	private Uri tempQuestionUri;
	private Uri tempNewPhotoQuestionUri;
	private Uri tempNewPhotoAnswerUri;
	private WrappingSlidingDrawer drawer;
	
	private String name;
	private InfoSaver infoSaver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        setContentView(R.layout.cards_list);
        setTheme(R.style.Theme_Sherlock);
 
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Intent intent = getIntent();
        name = intent.getStringExtra(FlashcardsActivity.CARD_LIST_NAME);
        actionBar.setTitle(name);
        
        downSampler = new BitmapDownsampler(this, 600, 1000); //600, 1000
        
        answerImage = (ImageView)findViewById(R.id.content);
        drawer = (WrappingSlidingDrawer)findViewById(R.id.drawer);
        drawer.lock();
        
        infoSaver = InfoSaver.getInfoSaver(this);
        
        //new
    	cardList = new ArrayList<Card>();
        cardAdapter = new CardPagerAdapter(this, cardList);
    	LoadCardsTask loadCards = new LoadCardsTask(this, name, downSampler) {
    		private boolean answerImageSet = false;
    		
    		@Override
    		protected void onProgressUpdate (Card... values) {
    			 cardList.add(values[0]);
    			 cardAdapter.notifyDataSetChanged();
    			 
    			 if (!answerImageSet) {
    				 answerImage.setImageBitmap(values[0].getAnswer());
    				 answerImageSet = true;
    			 }
    			 drawer.unlock();
    		}
    	};
    	loadCards.execute();

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(cardAdapter);    
        
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				drawer.close();
				answerImage.setImageBitmap(cardList.get(position).getAnswer());
			}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.layout.cardslist_actionbar_menu, menu);
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    	    	Intent intent = new Intent(this, FlashcardsActivity.class);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
    	    	startActivity(intent);
    	    	finish(); //??
    		break;
    		case R.id.menu_make_new:
    			Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    			
    			//TEMP
//				non-standard!!!
//    			pickImageIntent.putExtra("crop", "true"); 
//    			pickImageIntent.putExtra("outputX", 200);
//    			pickImageIntent.putExtra("outputY", 200);
//    			pickImageIntent.putExtra("aspectX", 1);
//    			pickImageIntent.putExtra("aspectY", 1);
//    			pickImageIntent.putExtra("scale", true);
//    			pickImageIntent.putExtra("return-data", true);
    			
    			pickImageIntent.setType("image/*"); //necessary??
    			startActivityForResult(pickImageIntent, SELECT_QUESTION_IMAGE);
    		break;
    		case R.id.menu_take_photo:    			
    			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    			tempNewPhotoQuestionUri = createNewImageUri("q");
//    			tempNewPhotoAnswerUri = createNewImageUri("a");
//    			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempNewPhotoAnswerUri);
//    		    startActivityForResult(takePictureIntent, TAKE_ANSWER_PHOTO);	//temp
    			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempNewPhotoQuestionUri);
    		    startActivityForResult(takePictureIntent, TAKE_QUESTION_PHOTO);
    		break;
    	}
        return true;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
	    super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
		    switch (requestCode) {
		    	case SELECT_QUESTION_IMAGE:
	    			tempQuestionUri = intent.getData();
	    			
	    			Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    			startActivityForResult(pickImageIntent, SELECT_ANSWER_IMAGE);
		    	break;
		    	case SELECT_ANSWER_IMAGE:
	    			Uri answer = intent.getData();
	    			try {
	    				addCard(tempQuestionUri, answer);
					} catch (IOException e) {
						//TODO: write error message to user
						Log.v("Flashcards", "File could not be opened");
					}
		    	break;
		    	case TAKE_QUESTION_PHOTO:
		    		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    			tempNewPhotoAnswerUri = createNewImageUri("a");
	    			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempNewPhotoAnswerUri);
	    		    startActivityForResult(takePictureIntent, TAKE_ANSWER_PHOTO);	//temp
		    	break;
		    	case TAKE_ANSWER_PHOTO:
	    			try {
	    				addCard(tempNewPhotoQuestionUri, tempNewPhotoAnswerUri); //TEMP
					} catch (IOException e) {
						//TODO: write error message to user
						Log.v("Flashcards", "File could not be opened");
					}
		    	break;
		    }
		}
	}
	
	private void addCard(Uri question, Uri answer) throws IOException {
		Bitmap questionBmp = downSampler.decode(question);
		Bitmap answerBmp = downSampler.decode(answer);
		
		cardList.add(new Card(question, questionBmp, answer, answerBmp));
		cardAdapter.notifyDataSetChanged();
		answerImage.setImageBitmap(answerBmp);
		drawer.unlock();
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
	
	@Override
	protected void onPause() {
		super.onPause();
		infoSaver.saveCards(name, cardList);
	}

}
