package se.hugo.flashcards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.hugo.flashcards.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

@TargetApi(8)
public class PickCardFragment extends SherlockFragment implements WriteTextDialogFragment.OnTextMadeListener {
	
	//must be implemented by container Activity
    public interface OnContentChangedListener {
        public void onContentChanged(CardContent newContent);
    }
	
	private CardContentView contentView;
	private Uri newPhotoUri;
	private BitmapDownsampler downSampler;
	private CardContent cardContent;
	private boolean contentIsDefault = true;
	private String newTextTitle;
	private String newTextHint;
	
	private static final int SELECT_IMAGE = 0;
	private static final int TAKE_PHOTO = 1;
	
	public static final String ANSWER_EXTRA = "answer";
	public static final String QUESTION_EXTRA = "question";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        downSampler = new BitmapDownsampler(getActivity(), display.getWidth(), display.getHeight()/2); //500, 1000
        
        newTextTitle = getString(R.string.write_new_text);
        newTextHint = getString(R.string.text);
    }
    
    //set the content without changing default flag or removing buttons
    public void setContentRaw(CardContent content) {
    	cardContent = content;
    	contentView.setCardContent(content);
    }
    
    //set the content, changing the default flag to false and remove add buttons
	public void setContent(CardContent c) {
		setContentRaw(c);
		contentIsDefault = false;
		
		setButtonsVisibility(View.GONE);
		
		ImageButton optionsButton = (ImageButton)getView().findViewById(R.id.options);
		optionsButton.setVisibility(View.VISIBLE);	
		
		((OnContentChangedListener)getActivity()).onContentChanged(cardContent);
	}
    
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pick_card_fragment_layout, container, false);
    	contentView = (CardContentView)view.findViewById(R.id.content);
    	
    	
    	final ImageButton pickImageButton = (ImageButton)view.findViewById(R.id.pick_image_button);
    	pickImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onPickImageClicked(v);
			}
    	});
    	final ImageButton makeTextButton = (ImageButton)view.findViewById(R.id.make_text_button);
    	makeTextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onMakeTextClicked(v);
			}
    	});   	
    	final ImageButton takeImageButton = (ImageButton)view.findViewById(R.id.take_image_button);
    	
    	if (isTakeImageAvailable()) {
	    	takeImageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
			    	onTakeImageClicked(v);
				}
	    	});
    	} else {
    		takeImageButton.setVisibility(View.GONE);
    	}
    	
    	ImageButton optionsButton = (ImageButton)view.findViewById(R.id.options);
    	optionsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onOptionsClicked(v);
			}
    	});
    	
    	//clicking anywhere on the ContentView brings up the options menu
    	contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!contentIsDefault) {
					onOptionsClicked(v);
				} else {
					//set button "backgrounds" to the transition
//					TransitionDrawable transition = (TransitionDrawable) makeTextButton.getBackground();
//					transition.startTransition(200);
					Animation highlight = AnimationUtils.loadAnimation(getActivity(), R.anim.highlight_cards);
					makeTextButton.startAnimation(highlight);
					takeImageButton.startAnimation(highlight);
					pickImageButton.startAnimation(highlight);
				}
			}
    	});
    	
    	return view;
	}
	
	private boolean isTakeImageAvailable() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		newPhotoUri = createNewImageUri();
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri);
		return getActivity().getPackageManager().resolveActivity(takePictureIntent, 0) != null;
	}
    
	//state saving/reloading in these two methods
	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putParcelable("content", cardContent);
		outState.putBoolean("contentIsDefault", contentIsDefault);
		outState.putBoolean("isButtonsVisible", isButtonsVisible());
		outState.putParcelable("new_photo_uri", newPhotoUri);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState != null) {			
			contentIsDefault = savedInstanceState.getBoolean("contentIsDefault");
			
			if (!contentIsDefault) {
				ImageButton optionsButton = (ImageButton)getView().findViewById(R.id.options);
				optionsButton.setVisibility(View.VISIBLE);
				
				//ugly hack?
				((OnContentChangedListener)getActivity()).onContentChanged(cardContent);
			}
			
			cardContent = savedInstanceState.getParcelable("content");
			try {
				cardContent.reloadBitmap(downSampler);
				setContentRaw(cardContent);
			} catch (IOException e) {
				Log.v("flashcards", "Couldn't load bitmap");
			}
			
			boolean buttonsVisible = savedInstanceState.getBoolean("isButtonsVisible");		
			if (buttonsVisible) {
				setButtonsVisibility(View.VISIBLE);
			} else {
				setButtonsVisibility(View.GONE);
			}
			
			Uri photoUri = (Uri)savedInstanceState.getParcelable("new_photo_uri");
			if (photoUri != null) {
				newPhotoUri = photoUri;
			}
			
		}
	}

	
	public boolean isContentDefault() {
		return contentIsDefault;
	}
	
	public CardContent getCardContent() {
		return cardContent;
	}
	
	public void setNewTextTitle(String title) {
		newTextTitle = title;
	}
	
	public void setNewTextHint(String hint) {
		newTextHint = hint;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) { 
	    super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
		    	case TAKE_PHOTO:
					try {
						CardContent content = new CardContent(downSampler.decode(newPhotoUri), newPhotoUri);
						setContent(content);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
					}
		    	break;
		    	case SELECT_IMAGE:
		    		Uri uri = intent.getData();
					try {
						CardContent content = new CardContent(downSampler.decode(uri), uri);
						setContent(content);
					} catch (IOException e) {
						Log.v("flashcards", "Couldn't load image");
					}
		    	break;
			}
		}
	}
	
    public void onPickImageClicked(View view) {
    	Intent pickImageIntent = new Intent(Intent.ACTION_PICK, 
    			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	pickImageIntent.setType("image/*"); //necessary??
    	startActivityForResult(pickImageIntent, SELECT_IMAGE);
    }
    
    public void onMakeTextClicked(View view) {
    	String text = "";
    	if (!contentIsDefault && !cardContent.isBitmap()) {
    		text = cardContent.getString();
    	}
    	
        DialogFragment dialogFragment = WriteTextDialogFragment.newInstance(newTextTitle, newTextHint, text, getString(R.string.ok), getString(R.string.cancel));
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "text_content");
    }
    
    @Override
    public void onTextMade(String tag, CharSequence text) {
    	if (tag.equals("text_content")) {
    		setContent(new CardContent(text.toString()));
    	}
    }
    
    public void onTakeImageClicked(View view) {
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		newPhotoUri = createNewImageUri();
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri);
	    startActivityForResult(takePictureIntent, TAKE_PHOTO);
    }
	
    public void onOptionsClicked(View view) {
    	boolean isVisible = isButtonsVisible();
    	
    	if (isVisible) {
    		setButtonsVisibility(View.GONE);
    	} else {
    		setButtonsVisibility(View.VISIBLE);
    	}
    }

	private Uri createNewImageUri() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = timeStamp + ".jpg";
		
		File dir;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		} else {
			String packageName = getActivity().getPackageName();
			File externalPath = Environment.getExternalStorageDirectory();
			dir = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName + "/files");		
		}
		
		File file = new File(
			    dir,
			    imageFileName
			);	
		return Uri.fromFile(file);
	}
	
	private boolean isButtonsVisible() {
		ImageButton pickImageButton = (ImageButton)getView().findViewById(R.id.pick_image_button);
		return pickImageButton.getVisibility() == View.VISIBLE;
	}
	
	public void setButtonsVisibility(int visibility) {
		ImageButton pickImageButton = (ImageButton)getView().findViewById(R.id.pick_image_button);
		ImageButton pickTextButton = (ImageButton)getView().findViewById(R.id.make_text_button);
		ImageButton takeImageButton = (ImageButton)getView().findViewById(R.id.take_image_button);
		pickImageButton.setVisibility(visibility);
		pickTextButton.setVisibility(visibility);
		takeImageButton.setVisibility(visibility);
	}
}
