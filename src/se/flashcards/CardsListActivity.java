package se.flashcards;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
	private CardPagerAdapter cardAdapter;
	private List<Card> cardList;
	private ViewPager viewPager;
	private ImageView answerImage;
	private BitmapDownsampler downSampler;
	private Bitmap tempQuestionImage;
	private WrappingSlidingDrawer drawer;
	
	private String name;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   

        setTheme(R.style.Theme_Sherlock);
        
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
        
        cardList = new ArrayList<Card>();
        cardAdapter = new CardPagerAdapter(this, cardList);

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
        inflater.inflate(R.layout.actionbar_menu, menu);
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
    			pickImageIntent.setType("image/*"); //necessary??
    			startActivityForResult(pickImageIntent, SELECT_ANSWER_IMAGE);	//temp
    			startActivityForResult(pickImageIntent, SELECT_QUESTION_IMAGE);
    			break;
    	}
        return true;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (resultCode == RESULT_OK) {
		    switch (requestCode) {
		    	case SELECT_QUESTION_IMAGE:
	    			Uri image = imageReturnedIntent.getData();
	    			try {
	    				Bitmap bmp = downSampler.decode(image);
	    				tempQuestionImage = bmp;
	//	    			cardList.add(new Card(bmp, bmp));
	//	    			cardAdapter.notifyDataSetChanged();
					} catch (IOException e) {
						//TODO: write error message to user
						Log.v("Flashcards", "File could not be opened");
					}
		    	break;
		    	case SELECT_ANSWER_IMAGE:
	    			image = imageReturnedIntent.getData();
	    			try {
	    				Bitmap bmp = downSampler.decode(image);
	    				cardList.add(new Card(tempQuestionImage, bmp));
	    				cardAdapter.notifyDataSetChanged();
	    				answerImage.setImageBitmap(bmp);
					} catch (IOException e) {
						//TODO: write error message to user
						Log.v("Flashcards", "File could not be opened");
					}
		    	break;
		    }
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
