package se.flashcards;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Gallery;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardsListActivity extends SherlockActivity {
	private static final int SELECT_IMAGE = 0;
	private ScaledImageAdapter imageAdapter;
	private Gallery gallery;
	private String name;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        setContentView(R.layout.cards_list);
        setTheme(R.style.Theme_Sherlock);
 
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        gallery = (Gallery) findViewById(R.id.gallery1);
        Intent intent = getIntent();
        name = intent.getStringExtra(FlashcardsActivity.CARD_LIST_NAME);
        actionBar.setTitle(name);
        
        imageAdapter = new ScaledImageAdapter(this, 600, 1000); //temp
        gallery.setAdapter(imageAdapter);
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
    			break;
    		case R.id.menu_make_new:
    			Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    			pickImageIntent.setType("image/*"); //necessary??
    			startActivityForResult(pickImageIntent, SELECT_IMAGE);
    			break;
    	}
        return true;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
	    switch (requestCode) {
	    	case SELECT_IMAGE:
	    		if (resultCode == RESULT_OK) {
	    			Uri image = imageReturnedIntent.getData();
	    			try {
						imageAdapter.addUri(image);
					} catch (IOException e) {
						//TODO: write error message to user
						Log.v("Flashcards", "File could not be opened");
					}
	    		}
	    	break;
	    }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
