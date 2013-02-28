package org.mathematica.pro;

import org.mathematica.logic.GameTutorial;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class TutorialActivity extends Activity implements OnClickListener {

	private Button _skipToMainMenuButton;
	private TextView _tutorialText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tutorial);

		_skipToMainMenuButton = (Button) findViewById(R.id.nextbutton);
		_tutorialText = (TextView) findViewById(R.id.tutorial_text);
		_tutorialText.setMovementMethod(new ScrollingMovementMethod());

		_tutorialText.setText(new GameTutorial().getTutorial());

		_skipToMainMenuButton.setOnClickListener(this);
	}

	public void onClick(View view) {
		if (view == _skipToMainMenuButton) {
			finish();
		}

	}

}
