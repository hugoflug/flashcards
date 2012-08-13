package se.flashcards;

import se.flashcards.WriteTextDialogFragment.OnTextMadeListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ConfirmDialogFragment extends SherlockDialogFragment {
	//containing fragments or activities must implement this
	public interface OnConfirmedListener {
		public void onConfirmed();
	}
	
	public static ConfirmDialogFragment newInstance(String title, String question, String confirmText, String dismissText) {
		ConfirmDialogFragment fragment = new ConfirmDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("question", question);
		args.putString("confirm", confirmText);
		args.putString("dismiss", dismissText);
		fragment.setArguments(args);
		return fragment;
		
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		String question = getArguments().getString("question");
		String confirm = getArguments().getString("confirm");
		String dismiss = getArguments().getString("dismiss");
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (!title.equals("")) {
        	builder.setTitle(title);
        }
        builder.setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle(title)
            .setMessage(question)
            .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            	@Override
                public void onClick(DialogInterface dialog, int whichButton) {
                	Fragment target = getTargetFragment();
                	if (target != null) {
                		((OnConfirmedListener)target).onConfirmed();
                	} else {
                		((OnConfirmedListener)getActivity()).onConfirmed();
                	}
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
}
