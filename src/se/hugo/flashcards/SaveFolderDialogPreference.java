package se.hugo.flashcards;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SaveFolderDialogPreference extends DialogPreference{
	private Context context;
	private EditText editText;
	
	public SaveFolderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		
		setDialogLayoutResource(R.layout.save_folder_dialog_layout);
	}
	
	private String fixPath(String path) {
		if (!path.matches(".*/$")) {
			path = path + "/";
		}
		return path;
	}
	
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			String fixedPath = fixPath(editText.getText().toString());
			if (fixedPath == null) {
				Toast.makeText(context, context.getString(R.string.invalid_path), Toast.LENGTH_SHORT).show(); //TEMP
			} else {
				persistString(fixedPath);
			}
		}
	}
	
	protected View onCreateDialogView() {
		View root = super.onCreateDialogView();
		editText = (EditText)root.findViewById(R.id.save_folder_dialog_text);
		
		final String defaultString = context.getString(R.string.default_save_folder);
		editText.setText(getPersistedString(defaultString));
		
		Button button = (Button)root.findViewById(R.id.save_folder_default_button);
		button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				editText.setText(defaultString);
			}
		});
		
		return root;
	}

}
