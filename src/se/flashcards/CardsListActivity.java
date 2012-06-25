package se.flashcards;

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
	private UriImageAdapter imageAdapter;
	private Gallery gallery;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        setContentView(R.layout.cards_list);
        setTheme(R.style.Theme_Sherlock);
 
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        gallery = (Gallery) findViewById(R.id.gallery1);
        Intent intent = getIntent();
        String name = intent.getStringExtra("temp");
        actionBar.setTitle(name);
        
        imageAdapter = new UriImageAdapter(this);
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
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	    	startActivity(intent);
    			break;
    		case R.id.menu_make_new:
    			Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
	    			imageAdapter.addUri(image);
	    		}
	    		break;
	    }
	}

}
