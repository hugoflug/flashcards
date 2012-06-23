package se.flashcards;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Gallery;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardsListActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        setContentView(R.layout.cards_list);
        setTheme(R.style.Theme_Sherlock);
 
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Gallery g = (Gallery) findViewById(R.id.gallery1);
        Intent intent = getIntent();
        String name = intent.getStringExtra("temp");
        actionBar.setTitle(name);
        
       
        int[] imageIds = {R.drawable.raybans, R.drawable.raybans, R.drawable.raybans};
        g.setAdapter(new ImageAdapter(this, imageIds));
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
    	}
        return true;
    }
}
