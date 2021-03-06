package se.hugo.flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import se.hugo.flashcards.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CardsListActivity extends SherlockFragmentActivity implements WriteTextDialogFragment.OnTextMadeListener, 
																		   ConfirmDialogFragment.OnConfirmedListener {
	private static final int MAKE_NEW_CARD = 1;
	private static final int EDIT_CARD = 2;
	public static final String SHOULD_BE_REMOVED = "should_be_removed";
	public static final String NUMBER_OF_CARDS = "number_of_cards";
	
	private CardPagerAdapter cardAdapter;
	private List<Card> cardList;
	private long listId;
	private ViewPager viewPager;
	private CardContentView answerView;
	private BitmapDownsampler downSampler;
	private WrappingSlidingDrawer drawer;
	private String name;
	private InfoSaver infoSaver;
	private int currentPosition;
	private LoadCardsTask loadCards;
	private boolean hasLoaded;
	private MenuItem deleteCardMenuItem;
	private MenuItem editCardMenuItem;
	private MenuItem exportMenuItem;
	private MenuItem shuffleMenuItem;
	private MenuItem makeNewMenuItem;
	private ActionBar actionBar;
	private int loadedCurrentItem;
	private boolean cardsListChanged;
	private Intent result;
	private boolean optionsMenuCreated = false;
	private boolean enableMenuItems = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        
        setContentView(R.layout.cards_list);
        
        hasLoaded = false;
 
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        //TEMP, should be set through XML
        ActionBar bar = getSupportActionBar();
        Util.customizeActionBar(getResources(), bar);

        Intent intent = getIntent();
        name = intent.getStringExtra(FlashcardsActivity.CARD_LIST_NAME);
        listId = intent.getLongExtra(FlashcardsActivity.CARD_LIST_ID, 0);
        actionBar.setTitle(name);
        
        result = new Intent();
        result.putExtra(FlashcardsActivity.CARD_LIST_ID, listId);
        
        Display display = getWindowManager().getDefaultDisplay();
        downSampler = new BitmapDownsampler(this, display.getWidth(), display.getHeight()/2); //600, 1000
        
        answerView = (CardContentView)findViewById(R.id.content);
        drawer = (WrappingSlidingDrawer)findViewById(R.id.drawer);
        drawer.lock();
        
        infoSaver = InfoSaver.getInfoSaver(this);
        
        cardsListChanged = false;

    	cardList = new ArrayList<Card>();
        cardAdapter = new CardPagerAdapter(this, cardList);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoShuffle = prefs.getBoolean(SettingsActivity.AUTO_SHUFFLE, false);
        
        //TODO: should not be set until all cards loaded
        if (autoShuffle) {
   //     	cardsListChanged = true; //should be commented away, TEMP uncommented
        }
        
    	loadCards = new LoadCardsTask(this, listId, downSampler, autoShuffle) {
    		private boolean answerImageSet = false;
    		
    		@Override
    		protected void onProgressUpdate (Card... values) {
    			addCard(values[0]);
    			
    			if (!answerImageSet) {
    				answerView.setCardContent(values[0].getAnswer());
    				answerImageSet = true;
    			}
    			
    		}   
			@Override
			protected void onPostExecute(Void v) {
				doAfterLoads();
			}
    	};

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(cardAdapter);    
        
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				drawer.close();
				answerView.setCardContent(cardList.get(position).getAnswer());
				currentPosition = position;
			}
        });
        
        if (savedInstanceState != null) {
	        int currentCard = savedInstanceState.getInt("current_card", 0);
	        loadedCurrentItem = currentCard;
	        
	        viewPager.setCurrentItem(currentCard);
	        boolean drawerOpen = savedInstanceState.getBoolean("drawer_open", false);
	        if (drawerOpen) {
	        	drawer.open();
	        } else {
	        	drawer.close();
	        }
        }
        
        setResult(SherlockActivity.RESULT_OK, result);
    }

    
	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putInt("current_card", currentPosition);
		outState.putBoolean("drawer_open", drawer.isOpened());
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (!hasLoaded) {
    		loadCards.execute();
    		hasLoaded = true;
    	}
    }
    
    private void removeCard(int pos) {   		
    	cardList.remove(pos);
    	cardAdapter.notifyDataSetChanged(); 
		cardsListChanged = true;
		
		if (cardList.size() == 0) {
			onEmptyList();
		}
    }
    
    private void renameList(String newName) {
    	infoSaver.renameCardList(listId, newName);
    	name = newName;
    	actionBar.setTitle(newName);
    	result.putExtra(FlashcardsActivity.CARD_LIST_NAME, newName);
        setResult(SherlockActivity.RESULT_OK, result);
    }
    
    private void onEmptyList() {
		deleteCardMenuItem.setVisible(false);
		editCardMenuItem.setVisible(false);
		shuffleMenuItem.setVisible(false);
    }
    
    private void onNonEmptyList() {
    	if (editCardMenuItem != null) {
			deleteCardMenuItem.setVisible(true);
			editCardMenuItem.setVisible(true);
			shuffleMenuItem.setVisible(true);
    	}
    }
    
    private void addCard(Card card) {
	    cardList.add(card);
		cardAdapter.notifyDataSetChanged();
		 
		onNonEmptyList();
		drawer.unlock();
    }
    
    private void shuffleCards() {
    	Collections.shuffle(cardList);
    	cardAdapter.notifyDataSetChanged();
    	cardsListChanged = true;
    }
    
    private void changeAmountOfCards(int newAmount) {
    	infoSaver.changeAmountCardList(listId, newAmount);
    	result.putExtra(NUMBER_OF_CARDS, newAmount);
    	setResult(SherlockActivity.RESULT_OK, result);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	optionsMenuCreated  = true;
    	
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.layout.cardslist_actionbar_menu, menu);
        
        deleteCardMenuItem = menu.findItem(R.id.menu_delete_card);
        editCardMenuItem = menu.findItem(R.id.menu_edit_card);
		shuffleMenuItem = menu.findItem(R.id.menu_shuffle_cards);
		makeNewMenuItem = menu.findItem(R.id.menu_make_new);
		
		
		if (enableMenuItems) {
	        deleteCardMenuItem.setEnabled(true);
	        editCardMenuItem.setEnabled(true);
			shuffleMenuItem.setEnabled(true);
			makeNewMenuItem.setEnabled(true);
		}
		
		if (cardList.size() != 0) {
			onNonEmptyList();
		}
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    			if (getCallingActivity().getClassName().equals(FlashcardsActivity.class.getName())) {
    				finish();
    			} else {
	    	    	Intent intent = new Intent(this, FlashcardsActivity.class);
	    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	    	startActivity(intent);
    			}
    		break;
    		case R.id.menu_make_new:
    			Intent makeNew = new Intent(this, NewCardActivity.class);
    			startActivityForResult(makeNew, MAKE_NEW_CARD);
    		break;
    		case R.id.menu_edit_card:
    			Intent edit = new Intent(this, NewCardActivity.class);
    			edit.putExtra("question_content", cardList.get(currentPosition).getQuestion());
    			edit.putExtra("answer_content", cardList.get(currentPosition).getAnswer());
    			startActivityForResult(edit, EDIT_CARD);
    		break;
    		case R.id.menu_delete_card: {
    	        DialogFragment dialogFragment = ConfirmDialogFragment.newInstance("", getString(R.string.card_will_be_deleted), getString(R.string.delete), getString(R.string.cancel));
    	        dialogFragment.show(getSupportFragmentManager(), "delete_card");
    		}
    		break;
    		case R.id.rename_list: {
    	        DialogFragment dialogFragment = WriteTextDialogFragment.newInstance(getString(R.string.rename), getString(R.string.name), "", getString(R.string.rename), getString(R.string.cancel));
    	        dialogFragment.show(getSupportFragmentManager(), "rename_list");
    		}		    	
    		case R.id.menu_shuffle_cards: {
    			shuffleCards();
    		}
    		break;
    		case R.id.delete_list: {
    			DialogFragment dialogFragment = ConfirmDialogFragment.newInstance("", getString(R.string.list_will_be_deleted), getString(R.string.delete), getString(R.string.cancel));
    	        dialogFragment.show(getSupportFragmentManager(), "delete_list");
    		}
    		break;
    	}
    	return true;
    }
	
	private void fixCardContent(CardContent content) throws IOException {
		if (content.isBitmap()) {
			Uri uri = content.getUri();
			if (uri.getScheme() == "media") {			
				File srcFile = new File(Util.getMediaPath(this, uri));
				File destFile = Util.getInternalStorage(this, srcFile.getName());
				Util.copyFile(srcFile, destFile);
				content.setBitmapUri(Uri.fromFile(destFile));
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
	    super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
		    switch (requestCode) {
		    	case MAKE_NEW_CARD: {
		    		CardContent question = intent.getParcelableExtra(NewCardActivity.QUESTION_EXTRA);
		    		CardContent answer = intent.getParcelableExtra(NewCardActivity.ANSWER_EXTRA);
					try {
			    		fixCardContent(question);
			    		fixCardContent(answer);
						question.reloadBitmap(downSampler);
				    	answer.reloadBitmap(downSampler);
					} catch (IOException e) {
						Log.v("flashcards", "Copying file to internal storage failed: ", e);
					}
					
					changeAmountOfCards(cardList.size() + 1);
					
					if (!hasLoaded) {
						loadCards.addLastLater(new Card(question, answer));
					} else {
						Card card = new Card(question, answer);
		    			addCard(card);
		    			
		    			if (cardList.size() == 1) {
		    				answerView.setCardContent(card.getAnswer());
		    			}
					}
		    		cardsListChanged = true;
		    	}
		    	break;
		    	case EDIT_CARD: {
		    		CardContent question = intent.getParcelableExtra(NewCardActivity.QUESTION_EXTRA);
		    		CardContent answer = intent.getParcelableExtra(NewCardActivity.ANSWER_EXTRA);
					try {
			    		fixCardContent(question);
			    		fixCardContent(answer);
						question.reloadBitmap(downSampler);
			    		answer.reloadBitmap(downSampler);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image.");
					}
					
					if (!hasLoaded) {
						loadCards.replaceLater(loadedCurrentItem, new 
								Card(question, answer));
					} else {
						cardList.remove(currentPosition);
						Card card = new Card(question, answer);
						cardList.add(currentPosition, card);
						cardAdapter.notifyDataSetChanged();
						answerView.setCardContent(card.getAnswer());
					}
		    		cardsListChanged = true;
		    	}
		    	break;
		    }
		}
	}
	
	private void doAfterLoads() {
		if (optionsMenuCreated) {		
	        deleteCardMenuItem.setEnabled(true);
	        editCardMenuItem.setEnabled(true);
			shuffleMenuItem.setEnabled(true);
			makeNewMenuItem.setEnabled(true);
		} else {
			enableMenuItems  = true;
		}
	}
	
	boolean isSaving = false;
	
	@Override
	protected void onPause() {
		super.onPause();
		if (cardsListChanged && !isSaving) {
           new Thread(new Runnable() {
				@Override
				public void run() {
					isSaving = true;
					InfoSaver is = InfoSaver.getInfoSaver(CardsListActivity.this);
					is.saveCards(listId, cardList);
					isSaving = false;
				}
            }).start();
		}
	}

	@Override
	public void onTextMade(String tag, CharSequence text) {
		if (tag.equals("rename_list")) {
			renameList(text.toString());
		}
	} 

	@Override
	public void onConfirmed(String tag) {
		if (tag.equals("delete_card")) {
			removeCard(currentPosition);
			changeAmountOfCards(cardList.size());
		} else if (tag.equals("delete_list")) {
			result.putExtra(SHOULD_BE_REMOVED, true);
	        setResult(SherlockActivity.RESULT_OK, result);
	        finish();
		}
	}
}
