package se.flashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class WriteTextDialogFragment extends SherlockDialogFragment {
	
	//containing fragments or activities must implement this
	public interface OnTextMadeListener {
		public void onTextMade(CharSequence text);
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.make_new_dialog, null);
        final TextView textView = (TextView)textEntryView.findViewById(R.id.text);
        textView.requestFocus();
        Dialog dialog = new AlertDialog.Builder(getActivity())
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle("Make new entry")
            .setView(textEntryView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            	@Override
                public void onClick(DialogInterface dialog, int whichButton) {
                	Fragment target = getTargetFragment();
                	if (target != null) {
                		((OnTextMadeListener)target).onTextMade(textView.getText());
                	} else {
                		((OnTextMadeListener)getActivity()).onTextMade(textView.getText());
                	}
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
    }
}
