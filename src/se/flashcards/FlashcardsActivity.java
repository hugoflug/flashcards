package se.flashcards;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FlashcardsActivity extends SherlockListActivity {

	static final String[] STUFF = new String[] { "Hipster", "Hopster", "Hapster" };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setTheme(R.style.Theme_Sherlock);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, STUFF));
        
        ActionBar bar = getSupportActionBar();
        bar.setTitle("RAYBAN");
        bar.setSubtitle("Only $999");
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
    		case R.id.menu_save:
    			Toast toast = Toast.makeText(getBaseContext(), "Saved!", Toast.LENGTH_SHORT);
    			toast.show();
    			break;
    	}
        return true;
    }
    
    public void onListItemClick(ListView listView, View view, int position, long id) {
    	switch (position) {
    		case 0:
    			Toast toast = Toast.makeText(getBaseContext(), "Hip!", Toast.LENGTH_SHORT);
    			toast.show();
    			break;
    		case 1:
    			Toast toast2 = Toast.makeText(getBaseContext(), "Hup!", Toast.LENGTH_SHORT);
    			toast2.show();
    			break;
    		case 2:
    			Toast toast3 = Toast.makeText(getBaseContext(), "Hap!", Toast.LENGTH_SHORT);
    			toast3.show();
    			break;
    	}
    }
}