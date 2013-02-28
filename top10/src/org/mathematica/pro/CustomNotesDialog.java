package org.mathematica.pro;

import org.mathematica.logic.RetainedChanges;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("ParserError")
public class CustomNotesDialog extends Dialog {

	private static String content;
	private static EditText textBox;
	private static Button clearNotesButton;

	public CustomNotesDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomNotesDialog(Context context) {
		super(context);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		RetainedChanges.saveNote(textBox.getText().toString());
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder implements android.view.View.OnClickListener {

		private Context context;

		private View layout;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomNotesDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomNotesDialog dialog = new CustomNotesDialog(context,
					R.style.Dialog);
			content = RetainedChanges.getStoredNote();
			layout = inflater.inflate(R.layout.notes_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			textBox = (EditText) layout.findViewById(R.id.notes_board);
			clearNotesButton = (Button) layout
					.findViewById(R.id.clear_notes_button);
			clearNotesButton.setOnClickListener(this);
			textBox.setText(content);

			dialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

			return dialog;
		}

		public void onClick(View v) {
			textBox.setText("");
		}

	}

}