package se.flashcards;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import se.flashcards.WriteTextDialogFragment.OnTextMadeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
//import com.actionbarsherlock.view.ActionMode;
//import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(11)
public class FlashcardsActivity extends SherlockListActivity implements OnTextMadeListener {
	
	private static final int DIALOG_MAKE_NEW = 0;
	private static final int OPEN_CARDSLIST = 1;
	
	public static final String CARD_LIST_NAME = "card_list_name";
	public static final String CARD_LIST_ID = "card_list_id";

	private List<CardList> cardLists;
	private CardsListListAdapter cardListsAdapter;
	private InfoSaver infoSaver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setTheme(R.style.Theme_Sherlock);
        
        infoSaver = InfoSaver.getInfoSaver(this);
        
        //TEMP, should be set through XML
        ActionBar bar = getSupportActionBar();
        Util.customizeActionBar(getResources(), bar);
        
        cardLists = infoSaver.getCardLists();
        cardListsAdapter = new CardsListListAdapter(this, cardLists);
        setListAdapter(cardListsAdapter);
        //
        //3.0+ ONLY code!!!
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); //_MODAL
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
        	private int selectedItems = 0;
        	
			@Override
			public boolean onActionItemClicked(ActionMode mode, android.view.MenuItem item) {
				switch (item.getItemId()) {
		            case R.id.menu_delete:
		                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();             
		                int removed = 0;
		                for (int i = 0; i < checkedItems.size(); i++) {
		                	removeCardList(checkedItems.keyAt(i) - removed);
		                	removed++;
		                }
		                mode.finish();
		                return true;
		            default:
		                return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.layout.list_item_longpress_menu, menu);
				
				mode.setTitle(selectedItems + " selected");
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) 
			{
				selectedItems = 0;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int pos, long id, boolean checked) {
				if (checked) {
					selectedItems++;
				} else {
					selectedItems--;
				}
				mode.setTitle(selectedItems + " selected");
			}
        });
       
        
//        final ListView listView = getListView();
//        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//				FlashcardsActivity.this.startActionMode(new ActionMode.Callback() {
//					@Override
//					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//						MenuInflater inflater = mode.getMenuInflater();
//						inflater.inflate(R.layout.list_item_longpress_menu, menu);
//						return true;
//					}
//
//					@Override
//					public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//						return false;
//					}
//
//					@Override
//					public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//						return true;
//					}
//
//					@Override
//					public void onDestroyActionMode(ActionMode mode) {
//					}
//				});
//				view.setSelected(true);
//				return true;
//			}
//		}); 	
    }
    
    private void removeCardList(int nr) {
    	infoSaver.removeCardList(cardListsAdapter.getItem(nr).getID());
    	cardLists.remove(cardListsAdapter.getItem(nr));
    	cardListsAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	validateListNames();
    }
    
    private void validateListNames() {
//    	for (CardList cl : cardLists) {
//    		String oldName = cl.getName();
//    		String newName = infoSaver.nameFromId(cl.getID());
//    		if (!oldName.equals(newName)) {
//    			cl.rename(newName);
//    			cardListsAdapter.notifyDataSetChanged();
//    		}
//    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.layout.actionbar_menu, menu);
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.menu_make_new:
    	//		DialogFragment dialogFragment = new WriteTextDialogFragment();
    	//		dialogFragment.show(getFragmentManager(), "make_new_list");
    			showDialog(DIALOG_MAKE_NEW);
    			break;
    	}
        return true;
    }
    
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
    	Intent intent = new Intent(this, CardsListActivity.class);
    	intent.putExtra(CARD_LIST_NAME, cardLists.get(position).getName());
       	intent.putExtra(CARD_LIST_ID, cardLists.get(position).getID());
    	startActivityForResult(intent, OPEN_CARDSLIST);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
	    super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
		    switch (requestCode) {
		    	case OPEN_CARDSLIST: {
		    		long id = intent.getLongExtra(CARD_LIST_ID, -1);
		    		boolean shouldBeRemoved = intent.getBooleanExtra(CardsListActivity.SHOULD_BE_REMOVED, false);
		    		if (shouldBeRemoved) {
		    			removeId(id);
		    		} else {
			    		String newName = intent.getStringExtra(CARD_LIST_NAME);
			    		if (newName != null) {
			    			renameId(id, newName);
			    		}
			    		
			    		int newAmount = intent.getIntExtra(CardsListActivity.NUMBER_OF_CARDS, -1);
			    		Log.v("flashcards", "new: " + newAmount);
			    		if (newAmount != -1) {
			    			changeAmountOnId(id, newAmount);
			    		}
		    		}
		    	}
		    }
		}
	}
	
	private void changeAmountOnId(long id, int newAmount) {
		for (CardList cl : cardLists) {
			if (cl.getID() == id) {
				cl.setNumberOfCards(newAmount);
				cardListsAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void removeId(long id) {
		Iterator<CardList> it = cardLists.iterator();
		while (it.hasNext()) {
			CardList cl = it.next();
			if (cl.getID() == id) {
				it.remove();
				infoSaver.removeCardList(id);
				cardListsAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void renameId(long id, String newName) {
    	for (CardList cl : cardLists) {
    		if (cl.getID() == id) {
    			cl.rename(newName);
    			cardListsAdapter.notifyDataSetChanged();
    		}
    	}
	}
    
//    public void tryToAddNewList(String name) {
//    	boolean match = false;
//    	for (String listName : cardLists) {
//    		if (name.equals(listName)) {
//    			match = true;
//    		}
//    	}
//    	if (!match) {
//    		cardListsAdapter.add(name);
//    	} else {
//    		//show dialog saying there is already a list with that name
//    	}
//    }
	
	@TargetApi(11)
	protected Dialog onCreateDialog(int id) {
    	switch (id) {
    		case DIALOG_MAKE_NEW:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.make_new_dialog, null);
                final TextView textView = (TextView)textEntryView.findViewById(R.id.text);
                textView.setHint("Title");
                textView.requestFocus();
                Dialog dialog = new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Create new list")
                    .setView(textEntryView)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	String text = textView.getText().toString();
                        	if (!text.equals("")) {
                        		cardLists.add(new CardList(text));
                        		cardListsAdapter.notifyDataSetChanged();
                        	}
                        	textView.setText("");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	textView.setText("");
                        }
                    })
                    .create();
                dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                return dialog;
    		default:
    			return null;
    	}
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		infoSaver.saveCardLists(cardLists);
	}

	@Override
	public void onTextMade(String tag, CharSequence text) {
		if (tag.equals("make_new_list")) {
			if (!text.toString().equals("")) {
	    		cardLists.add(new CardList(text.toString()));
	    		cardListsAdapter.notifyDataSetChanged();
	    	}
		}
	}
}