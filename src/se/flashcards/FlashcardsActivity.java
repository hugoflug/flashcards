package se.flashcards;


import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FlashcardsActivity extends ListActivity {

	static final String[] STUFF = new String[] { "Hipster", "Hopster", "Hapster" };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, STUFF));
    }
}