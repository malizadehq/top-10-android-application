package org.mathematica.pro;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class CustomTextDialog extends Dialog {

	public CustomTextDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomTextDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder implements View.OnClickListener {

		private Button n0;
		private Button n1;
		private Button n2;
		private Button n3;
		private Button n4;
		private Button n5;
		private Button n6;
		private Button n7;
		private Button n8;
		private Button n9;

		CustomTextDialog dialog;

		private String value;

		private DialogInterface.OnClickListener closeListener;

		private Context context;

		private View layout;

		public Builder(Context context) {
			this.context = context;
		}

		public String getValue() {
			return this.value;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomTextDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dialog = new CustomTextDialog(context, R.style.Dialog);
			layout = inflater.inflate(R.layout.dialpad, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			n0 = (Button) layout.findViewById(R.id.n0);
			n1 = (Button) layout.findViewById(R.id.n1);
			n2 = (Button) layout.findViewById(R.id.n2);
			n3 = (Button) layout.findViewById(R.id.n3);
			n4 = (Button) layout.findViewById(R.id.n4);
			n5 = (Button) layout.findViewById(R.id.n5);
			n6 = (Button) layout.findViewById(R.id.n6);
			n7 = (Button) layout.findViewById(R.id.n7);
			n8 = (Button) layout.findViewById(R.id.n8);
			n9 = (Button) layout.findViewById(R.id.n9);

			n0.setOnClickListener(this);
			n1.setOnClickListener(this);
			n2.setOnClickListener(this);
			n3.setOnClickListener(this);
			n4.setOnClickListener(this);
			n5.setOnClickListener(this);
			n6.setOnClickListener(this);
			n7.setOnClickListener(this);
			n8.setOnClickListener(this);
			n9.setOnClickListener(this);

			return dialog;
		}

		public Builder setCloseListener(
				android.content.DialogInterface.OnClickListener onClickListener) {
			this.closeListener = onClickListener;
			return this;
		}

		public void onClick(View v) {
			value = ((Button) v).getText().toString();
			closeListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
		}

	}

}