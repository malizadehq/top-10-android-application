package org.mathematica.pro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CopyOfMyFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_layout_two, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		initControls();
	}

	private void initControls() {
	}
}
