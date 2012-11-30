package se.hugo.flashcards;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import se.hugo.flashcards.R;
import se.hugo.flashcards.ConfirmDialogFragment.OnConfirmedListener;
import se.hugo.flashcards.WriteTextDialogFragment.OnTextMadeListener;

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
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.os.AsyncTask;

@TargetApi(11)
public class FlashcardsActivity extends SherlockListActivity implements OnTextMadeListener, OnConfirmedListener {
	
	private static final int DIALOG_MAKE_NEW = 0;
	private static final int OPEN_CARDSLIST = 1;
	private static final int DIALOG_RENAME = 2;
	private static final int DIALOG_CONFIRM = 3;
	private static final int PICK_CSV = 4;
	private static final int DIALOG_INVALID_CSV = 5;
	private static final int DIALOG_CSV_INFO = 6;
	private static final int DIALOG_NO_SUCH_ACTIVITY = 7;
	private static final int DIALOG_IMPORTING = 8;
	public static final int DIALOG_EXPORTING = 9;
	
	public static final String CARD_LIST_NAME = "card_list_name";
	public static final String CARD_LIST_ID = "card_list_id";
	
	public static final String SAVE_TO_DISK = "se.hugo.flashcards.SaveToDisk";

	private ArrayList<CardList> cardLists;
	private CardsListListAdapter cardListsAdapter;
	private InfoSaver infoSaver;
	private int itemToRename = 0;
	private ActionMode modeToFinish; //3.0+ ONLY 
	private SparseBooleanArray itemsToRemove;
	private BitmapDownsampler downSampler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        
        Display display = getWindowManager().getDefaultDisplay();
        downSampler = new BitmapDownsampler(this, display.getWidth(), display.getHeight()/2); //600, 1000

        
        infoSaver = InfoSaver.getInfoSaver(this);
        
        //TEMP, should be set through XML
        ActionBar bar = getSupportActionBar();
        Util.customizeActionBar(getResources(), bar);     
        
        cardLists = infoSaver.getCardLists();
        cardListsAdapter = new CardsListListAdapter(this, cardLists);
        setListAdapter(cardListsAdapter);
        
        Intent intent = getIntent();     
        String srcPath = intent.getStringExtra(SAVE_TO_DISK);
        
        if (srcPath != null) {
        	File srcFile = new File(srcPath);
        	File destFile = new File(Util.getExportPath(this) + srcFile.getName());
    		try {
				Util.copyFile(new File(srcPath), destFile);
	        	Toast.makeText(this, getString(R.string.list_saved) + " " + destFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
	        	Toast.makeText(this, R.string.export_failed, Toast.LENGTH_SHORT).show();
			}
        }
        
        //
        //3.0+ ONLY code!!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        final ListView listView = getListView();
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); //_MODAL
	        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
	        	private int selectedItems = 0;
	        	private int listsWithZeroSelected = 0;
	        	private android.view.MenuItem renameItem = null;
	        	private android.view.MenuItem exportItem = null;
	        	
				@Override
				public boolean onActionItemClicked(ActionMode mode, android.view.MenuItem item) {
					switch (item.getItemId()) {
			            case R.id.menu_delete: {
	            	//		DialogFragment dialogFragment = new ConfirmDialogFragment();
	            	//		dialogFragment.show(getFragmentManager(), "delete_list");
	            			//TEMP, do through fragments instead
			            	modeToFinish = mode;			            	
			            	itemsToRemove = listView.getCheckedItemPositions();
			            	showDialog(DIALOG_CONFIRM);
			            	
//			                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();             
//			                int removed = 0;
//			                for (int i = 0; i < checkedItems.size(); i++) {
//			                	int key = checkedItems.keyAt(i);
//			                	if (checkedItems.get(key)) {
//				                	removeCardList(key - removed);
//				                	removed++;
//			                	}
//			                }
//			                mode.finish();
			                return true;
			            }
			            case R.id.rename_item: {
			            	SparseBooleanArray checkedItems = listView.getCheckedItemPositions();   
			            	for (int i = 0; i < checkedItems.size(); i++) {
			            		int key = checkedItems.keyAt(i);
			            		if (checkedItems.get(key)) {
					            	itemToRename = key;
		       		            	modeToFinish = mode;
		       		    	        // DialogFragment dialogFragment = WriteTextDialogFragment.newInstance("Rename", "Name", "");
		       		    	        // dialogFragment.show(FlashcardsActivity.this.getSupportFragmentManager(), "rename_list");
		       		            	//TEMP, do through fragments instead
		       		            	showDialog(DIALOG_RENAME);
			            		}
			            	}
	
			            	modeToFinish = mode;
			            	return true;
			            }
			            case R.id.export_as_csv: {
			            	SparseBooleanArray checkedItems = listView.getCheckedItemPositions();  
			            	modeToFinish = mode;
			            	for (int i = 0; i < checkedItems.size(); i++) {
			            		int key = checkedItems.keyAt(i);
			            		if (checkedItems.get(key)) {
			            			long exportId = cardLists.get(key).getID();
			            			exportAsCsv(exportId); 
			            		}
			            	}
			            	return true;
			            }
			            default:
			                return false;
					}
				}
	
				@Override
				public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.layout.list_item_longpress_menu, menu);
					
					renameItem = menu.findItem(R.id.rename_item);
					exportItem = menu.findItem(R.id.export_as_csv);
					//TEMP
			//		renameItem.setVisible(false);
					
					mode.setTitle(selectedItems + " " + getString(R.string.selected));
					return true;
				}
	
				@Override
				public void onDestroyActionMode(ActionMode mode) 
				{
					selectedItems = 0;
					listsWithZeroSelected = 0;
				}
	
				@Override
				public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
					return false;
				}
	
				@TargetApi(11)
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int pos, long id, boolean checked) {
					if (checked) {
						selectedItems++;
					} else {
						selectedItems--;
					}
					if (selectedItems > 1) {
						renameItem.setVisible(false);
						exportItem.setVisible(false);
					} else if (selectedItems <= 1) {
						renameItem.setVisible(true);
						exportItem.setVisible(true);
					}
					
					if (cardLists.get(pos).getNumberOfCards() == 0) {
						if (checked == true) {
							listsWithZeroSelected++;
						} else {
							listsWithZeroSelected--;
						}
					}
					
					if (listsWithZeroSelected == 0 && selectedItems == 1) {
						exportItem.setVisible(true);
					} else {
						exportItem.setVisible(false);
					}
					
					mode.setTitle(selectedItems + " " + getString(R.string.selected));
				}
	        });	
        } else {
        	registerForContextMenu(getListView());
        }
      
       
        
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
    
    private void exportAsCsv(final long listId) {
		showDialog(DIALOG_EXPORTING);  
		
		currentTask = new AsyncTask<Void, Void, File>() {
			@Override
			protected File doInBackground(Void... params) {
				try {
					return Importer.exportAsCSV(FlashcardsActivity.this, listId, downSampler);
				} catch (IOException e) {
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(File result) {
		         if (result == null) {
		        	Toast.makeText(FlashcardsActivity.this, getString(R.string.export_failed), Toast.LENGTH_SHORT).show();
		        	dismissDialog(DIALOG_EXPORTING);
		         } else {
		     		File outFile = result;
		    		
		    		boolean isZip = outFile.getName().matches(".*\\.zip$"); 
		    		
		    		Intent i = new Intent();
		    		i.setAction(Intent.ACTION_SEND);
		    		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outFile));
		    		
		    		int nameRes;
		    		
		    		if (isZip) {
		    			i.setType("application/zip");
		    			nameRes = R.string.send_zip_to;
		    		} else {
		    			i.setType("text/csv");
		    			nameRes = R.string.send_csv_to;
		    		}
		    		
		            PackageManager manager = getPackageManager();
		            ResolveInfo info = manager.resolveActivity(i, 0);
		            
		    		Intent saveIntent = new Intent(FlashcardsActivity.this, FlashcardsActivity.class);
		    		saveIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    		saveIntent.putExtra(FlashcardsActivity.SAVE_TO_DISK, outFile.getAbsolutePath());
		            //
		            if (info == null) {
		            	startActivity(saveIntent);
		            } else {	
		    			Intent chooserIntent = Intent.createChooser(i, getResources().getText(nameRes));

		    			LabeledIntent labeledIntent = new LabeledIntent(saveIntent, "se.hugo.flashcards", R.string.save_to_disk, R.drawable.folder);		
		    			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] {labeledIntent});		
		    			dismissDialog(DIALOG_EXPORTING);
		    			startActivity(chooserIntent);
		            }    		         
		        }
		    }
			
			
			
		}.execute();
		
		if (modeToFinish != null) {
			modeToFinish.finish();
		}
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.list_item_longpress_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected (android.view.MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
	        case R.id.menu_delete: {
	        	removeCardList(info.position);
	        }
	        break;
	        case R.id.rename_item: {
            	itemToRename = info.position;
    	        // DialogFragment dialogFragment = WriteTextDialogFragment.newInstance("Rename", "Name", "");
    	        // dialogFragment.show(FlashcardsActivity.this.getSupportFragmentManager(), "rename_list");
            	//TEMP, do through fragments instead
            	showDialog(DIALOG_RENAME);
	        }
	        break;
	        case R.id.export_as_csv: {
    			long exportId = cardLists.get(info.position).getID();
    			exportAsCsv(exportId); 
	        }
	        break;
		}
    	return false;
    }
    
    //TODO: remove this method
    private void removeCardList(int nr) {
    	infoSaver.removeCardList(cardListsAdapter.getItem(nr).getID());
    	cardLists.remove(cardListsAdapter.getItem(nr));
    	cardListsAdapter.notifyDataSetChanged();
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
    			//TEMP, do through fragments instead
    			showDialog(DIALOG_MAKE_NEW);
    			break;
    		case R.id.menu_import_csv: {	
     		    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    	        intent.setType("text/csv,application/zip,file/*,*/*"); //file/*
    	        
    	        PackageManager manager = getPackageManager();
    	        ResolveInfo info = manager.resolveActivity(intent, 0);
    	        
    	        if (info == null) {
    	        	showDialog(DIALOG_NO_SUCH_ACTIVITY); //TEMP
    	        } else {
    	        	Intent chooserIntent = Intent.createChooser(intent, getString(R.string.import_from));
    	        	startActivityForResult(chooserIntent, PICK_CSV);
    	        }
    		    break;
    		}
    		case R.id.menu_csv_info: {
    			showDialog(DIALOG_CSV_INFO);
    			break;
    		}
    		case R.id.settings: {
    			Intent intent = new Intent(this, SettingsActivity.class);
    			startActivity(intent);
    		}
    		break;
    	}
        return false;
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
			    		if (newAmount != -1) {
			    			changeAmountOnId(id, newAmount);
			    		}
		    		}
		    	}
		    	break;
		    	case PICK_CSV: {
		    		handleImportCSVIntent(intent);
		    	}
		    	break;
		    }
		}
	}
	
	private void handleImportCSVIntent(Intent intent) {
		String pickedPath = intent.getData().getPath();
		
		String name = intent.getData().getLastPathSegment();
		name = Util.until(name, "\\.");
		
		//TODO: move file to local storage and send this location to importCSV
		
		importCSV(name, pickedPath);
	}
	
	private void finishImport(CardList cardList) {
		cardLists.add(cardList);
		cardListsAdapter.notifyDataSetChanged();
	}
	
	private AsyncTask currentTask;
	
	private void importCSV(final String listName, final String filename) {
		final CardList cardList = new CardList(listName);
		
		showDialog(DIALOG_IMPORTING);
		
		currentTask = new AsyncTask<Void, Void, List<Card>>() {
			@Override
			protected List<Card> doInBackground(Void... params) {
				try {
					List<Card> listOfCards = Importer.importCards(FlashcardsActivity.this, filename, downSampler);
					
					InfoSaver saver = InfoSaver.getInfoSaver(FlashcardsActivity.this);
					saver.saveCards(cardList.getID(), listOfCards);
					
					return listOfCards;
				} catch (IOException e) {
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(List<Card> result) {
		         if (result == null) {
		        	 dismissDialog(DIALOG_IMPORTING);
		        	 showDialog(DIALOG_INVALID_CSV);
		         } else {
		     		cardList.setNumberOfCards(result.size());
		    		cardLists.add(cardList);
		    		cardListsAdapter.notifyDataSetChanged();
		    		dismissDialog(DIALOG_IMPORTING);
		    		Toast.makeText(FlashcardsActivity.this, "List \"" + listName + "\" added", Toast.LENGTH_SHORT).show();
		         }
		    }
		}.execute();
	}
	
	private void changeAmountOnId(long id, int newAmount) {
		for (CardList cl : cardLists) {
			if (cl.getID() == id) {
				cl.setNumberOfCards(newAmount);
				cardListsAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void removeId(final long id) {
		Iterator<CardList> it = cardLists.iterator();
		while (it.hasNext()) {
			CardList cl = it.next();
			if (cl.getID() == id) {
				it.remove();
	            new Thread(new Runnable() {
					@Override
					public void run() {
						infoSaver.removeCardList(id);					}
	            }).start();
			}
		}
		cardListsAdapter.notifyDataSetChanged();
	}
	
	private void renameId(long id, String newName) {
    	for (CardList cl : cardLists) {
    		if (cl.getID() == id) {
    			cl.rename(newName);
    			cardListsAdapter.notifyDataSetChanged();
    		}
    	}
	}
	
	private void renameNumber(int number, String newName) {
		CardList cl = cardLists.get(number);
		cl.rename(newName);
		cardListsAdapter.notifyDataSetChanged();
		infoSaver.renameCardList(cl.getID(), newName);
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
	
	protected Dialog onCreateDialog(int id) {
    	switch (id) {
    		case DIALOG_MAKE_NEW: {
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.make_new_dialog, null);
                final TextView textView = (TextView)textEntryView.findViewById(R.id.text);
                textView.setHint(getString(R.string.title));
                textView.requestFocus();
                
                //TEMP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                	textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                
                Dialog dialog = new AlertDialog.Builder(this)
         //           .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.create_new_list)
                    .setView(textEntryView)
                    .setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	String text = textView.getText().toString();
                        	if (!text.equals("")) {
                        		cardLists.add(new CardList(text));
                        		cardListsAdapter.notifyDataSetChanged(); //
                        	}
                        	textView.setText("");
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	textView.setText("");
                        }
                    })
                    .create();
                dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                return dialog;
    		}
            //TEMP, do through fragments instead
    		case DIALOG_RENAME: {
    			String title = getString(R.string.rename_list);
    			String hint = getString(R.string.name);
    			String text = "";
    			String positiveText = getString(R.string.rename);
    			final String tag = "rename_list";
    			
    	        LayoutInflater factory = LayoutInflater.from(this);
    	        final View textEntryView = factory.inflate(R.layout.make_new_dialog, null);
    	        final EditText textView = (EditText)textEntryView.findViewById(R.id.text);
    	        
                //TEMP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                	textView.setTextColor(getResources().getColor(android.R.color.black));
                }
    	        
    	        textView.setHint(hint);
    	        textView.requestFocus();
    	        textView.setText(text);
    	        textView.setSelection(textView.getText().length());
    	        Dialog dialog = new AlertDialog.Builder(this)
 //   	            .setIconAttribute(android.R.attr.alertDialogIcon)
    	            .setTitle(title)
    	            .setView(textEntryView)
    	            .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
    	            	@Override
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	            		onTextMade(tag, textView.getText());
    	                }
    	            })
    	            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	textView.setText("");
    	                }
    	            })
    	            .create();
    	        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    	        return dialog;
    		}
    		case DIALOG_CONFIRM: {
    			String title = getString(R.string.delete_lists);
    			String question = getString(R.string.lists_will_be_deleted);
    			String confirm = getString(R.string.delete);
    			String dismiss = getString(R.string.cancel);
    			final String tag = "confirm_delete";
    			
    	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	        if (!title.equals("")) {
    	        	builder.setTitle(title);
    	        }
    	        builder.setTitle(title)
    	            .setMessage(question)
    	            .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
    	            	@Override
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	onConfirmed(tag);
    	                }
    	            })
    	            .setNegativeButton(dismiss, new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	
    	                }
    	            });

    	        Dialog dialog = builder.create();
    	        
    	        if (title.equals("")) {
    	        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	        }
    	        return dialog;
    		}
    		case DIALOG_INVALID_CSV: {
       			String title = getString(R.string.invalid_csv_title);
    			String text = getString(R.string.invalid_csv_text);
       			String dismiss = getString(R.string.cancel);
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle(title)
    				   .setMessage(text)
    				   .setNegativeButton("?", new DialogInterface.OnClickListener() {
						@Override
							public void onClick(DialogInterface dialog, int which) {
								showDialog(DIALOG_CSV_INFO);
							}				   
    				    })
    				   .setNeutralButton(dismiss, new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	
    	                }
    	            });
    			
    			Dialog dialog = builder.create();
    			
    	        if (title.equals("")) {
    	        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	        }
    			return dialog;
    		}
    		default:
    			return null;
    		case DIALOG_CSV_INFO: {
       			String title = getString(R.string.csv_info_title);
    			String text = getString(R.string.csv_info_text);
       			String dismiss = getString(R.string.cancel);
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle(title)
    				   .setMessage(text)
    				   .setNeutralButton(dismiss, new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	
    	                }
    	            });
    			
    			Dialog dialog = builder.create();
    			
    	        if (title.equals("")) {
    	        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	        }
    			return dialog;
    		}
       		case DIALOG_NO_SUCH_ACTIVITY: {
       			String title = getString(R.string.no_such_activity_title);
    			String text = getString(R.string.no_such_activity_text);
       			String dismiss = getString(R.string.cancel);
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle(title)
    				   .setMessage(text)
    				   .setNeutralButton(dismiss, new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	
    	                }
    	            });
    			
    			Dialog dialog = builder.create();
    			
    	        if (title.equals("")) {
    	        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	        }
    			return dialog;
    		}
       		case DIALOG_IMPORTING: {
       			String title = "";

    			LayoutInflater inflater = getLayoutInflater();
    			View dialogLayout = inflater.inflate(R.layout.processbar_dialog_layout, null);

    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setView(dialogLayout)
    				   .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
    					   public void onClick(DialogInterface dialog, int whichButton) {
    						   currentTask.cancel(true);
    					   }
    				   }).create();
    			
    			Dialog dialog = builder.create();
    			return dialog;
    		}
       		case DIALOG_EXPORTING: {
       			String title = "";

    			LayoutInflater inflater = getLayoutInflater();
    			View dialogLayout = inflater.inflate(R.layout.processbar_dialog_layout, null);

    			TextView textView = (TextView)dialogLayout.findViewById(R.id.about_dialog_text);
    			textView.setText(R.string.exporting);
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setView(dialogLayout) //??
    				   .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
    					   public void onClick(DialogInterface dialog, int whichButton) {
    						   currentTask.cancel(true);
    					   }
    				   }).create();;
    			
    			Dialog dialog = builder.create();
    			return dialog;
    		}
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
		} else if (tag.equals("rename_list")) {
			renameNumber(itemToRename, text.toString());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				modeToFinish.finish();
			}
		}
	}
	
	private void removeMarkedCardLists() {
		int removed = 0;
        for (int i = 0; i < itemsToRemove.size(); i++) {
        	int key = itemsToRemove.keyAt(i);
        	if (itemsToRemove.get(key)) {
        		cardLists.remove(cardListsAdapter.getItem(key - removed));
            	removed++;
        	}
        }	
        cardListsAdapter.notifyDataSetChanged();
	}
	
	
	//infoSaver.removeCardList(cardListsAdapter.getItem(nr).getID());
	
	@SuppressWarnings("unchecked")
	@Override
	public void onConfirmed(String tag) {
		if (tag.equals("confirm_delete")) {       
            
			final ArrayList<CardList> remCardLists = (ArrayList<CardList>)cardLists.clone();
			
            removeMarkedCardLists();
            
			final SparseBooleanArray removeItems = itemsToRemove.clone();
			
            new Thread(new Runnable() {
				@Override
				public void run() {
					int removed = 0;
		            for (int i = 0; i <  removeItems.size(); i++) {
		            	int key = removeItems.keyAt(i);
		            	if (removeItems.get(key)) {
		            		infoSaver.removeCardList(remCardLists.get(key - removed).getID());
		                	removed++;
		            	}
		            }
				}   	
            }).start();

            modeToFinish.finish();
		}
	}
	
//	@Override
//	public void onConfirmed(String tag) {
//		if (tag.equals("confirm_delete")) {       
//            int removed = 0;
//            
//            for (int i = 0; i <  itemsToRemove.size(); i++) {
//            	int key = itemsToRemove.keyAt(i);
//            	if (itemsToRemove.get(key)) {
//                	removeCardList(key - removed);
//                	removed++;
//            	}
//            }
//            modeToFinish.finish();
//		}
//	}
}