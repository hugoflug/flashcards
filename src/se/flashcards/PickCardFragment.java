package se.flashcards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class PickCardFragment extends Fragment implements WriteTextDialogFragment.OnTextMadeListener {
	
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
        downSampler = new BitmapDownsampler(getActivity(), 1000, 1000);
        newTextTitle = "Write new text";
        newTextHint = "Text";
    }
    
    public void setContentRaw(CardContent content) {
    	cardContent = content;
    	contentView.setCardContent(content);
    }
    
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pick_card_fragment_layout, container, false);
    	contentView = (CardContentView)view.findViewById(R.id.content);
    	
    	
    	ImageButton pickImageButton = (ImageButton)view.findViewById(R.id.pick_image_button);
    	pickImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onPickImageClicked(v);
			}
    	});
    	ImageButton makeTextButton = (ImageButton)view.findViewById(R.id.make_text_button);
    	makeTextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onMakeTextClicked(v);
			}
    	});   	
    	ImageButton takeImageButton = (ImageButton)view.findViewById(R.id.take_image_button);
    	takeImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onTakeImageClicked(v);
			}
    	});
    	
    	ImageButton optionsButton = (ImageButton)view.findViewById(R.id.options);
    	optionsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onOptionsClicked(v);
			}
    	});
    	
    	return view;
	}
    
	//state saving/reloading in these two methods
	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putParcelable("content", cardContent);
		outState.putBoolean("contentIsDefault", contentIsDefault);
		outState.putBoolean("isButtonsVisible", isButtonsVisible());
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
        DialogFragment dialogFragment = WriteTextDialogFragment.newInstance(newTextTitle, newTextHint);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "dialog");
    }
    
    public void onTextMade(CharSequence text) {
    	setContent(new CardContent(text.toString()));
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
	
	public void setContent(CardContent c) {
		cardContent = c;
		contentIsDefault = false;
		contentView.setCardContent(c);
		
		setButtonsVisibility(View.GONE);
		
		ImageButton optionsButton = (ImageButton)getView().findViewById(R.id.options);
		optionsButton.setVisibility(View.VISIBLE);	
		
		((OnContentChangedListener)getActivity()).onContentChanged(cardContent);
	}
	
	private Uri createNewImageUri() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = timeStamp + ".jpg";
		File file = new File(
			    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 
			    imageFileName
			);	
		return Uri.fromFile(file);
	}
	
	private boolean isButtonsVisible() {
		ImageButton pickImageButton = (ImageButton)getView().findViewById(R.id.pick_image_button);
		return pickImageButton.getVisibility() == View.VISIBLE;
	}
	
	private void setButtonsVisibility(int visibility) {
		ImageButton pickImageButton = (ImageButton)getView().findViewById(R.id.pick_image_button);
		ImageButton pickTextButton = (ImageButton)getView().findViewById(R.id.make_text_button);
		ImageButton takeImageButton = (ImageButton)getView().findViewById(R.id.take_image_button);
		pickImageButton.setVisibility(visibility);
		pickTextButton.setVisibility(visibility);
		takeImageButton.setVisibility(visibility);
	}
}
