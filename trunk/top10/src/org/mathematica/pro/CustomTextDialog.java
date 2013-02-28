package org.mathematica.pro;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("ParserError")
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
	public static class Builder implements android.view.View.OnClickListener {

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

		Typeface myTypeface;

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

		public Builder setTypeFace(Typeface tp) {
			myTypeface = tp;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomTextDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomTextDialog dialog = new CustomTextDialog(context,
					R.style.Dialog);
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

			n0.setTypeface(myTypeface);
			n1.setTypeface(myTypeface);
			n2.setTypeface(myTypeface);
			n3.setTypeface(myTypeface);
			n4.setTypeface(myTypeface);
			n5.setTypeface(myTypeface);
			n6.setTypeface(myTypeface);
			n7.setTypeface(myTypeface);
			n8.setTypeface(myTypeface);
			n9.setTypeface(myTypeface);

			n0.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "0";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "1";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "2";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "3";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "4";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n5.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "5";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n6.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "6";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n7.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "7";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n8.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "8";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			n9.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					value = "9";
					closeListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});

			return dialog;
		}

		public void onClick(View v) {
			Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

		}

		public Builder setCloseListener(
				android.content.DialogInterface.OnClickListener onClickListener) {
			this.closeListener = onClickListener;
			return this;
		}

	}

}