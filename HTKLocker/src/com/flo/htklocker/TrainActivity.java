package com.flo.htklocker;

import com.flo.service.UserService;
import com.flo.util.AudioRecordFunc;
import com.flo.util.FileHelper;
import com.flo.util.HTK;
import com.flo.util.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TrainActivity extends Activity {
	UserService userService;
	AudioRecordFunc audioRecordFunc;
	Button button_Train;
	TextView textView_TrainInfo;
	String wavPath;
	String wavlist;
	String labPath;
	String userid;
	String username;
	AlertDialog alertDialog;
	FileHelper fileHelper;

	private void bindControl() {
		button_Train = (Button) findViewById(R.id.button_Train);
		textView_TrainInfo = (TextView) findViewById(R.id.textView_TrainInfo);
		textView_TrainInfo.setText(getResources().getString(R.string.user_name)
				+ ":" + username);
	}

	private void controlBindListener() {

		button_Train.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				wavPath = fileHelper.getTrainWavPath();
				if (wavPath == null) {
					ToastUtil.ShowResString(TrainActivity.this,
							R.string.audio_error_no_sdcard);
				} else {
					button_Train.setText(R.string.start_record);
					final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(
							TrainActivity.this);
					View view1 = View.inflate(TrainActivity.this,
							R.layout.dialog_record, null);
					dialogBuilder1.setView(view1);
					alertDialog = dialogBuilder1.create();
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.setCancelable(false);
					alertDialog.show();
					startRecord();
				}
			}
		});
	}

	protected void startRecord() {
		audioRecordFunc = AudioRecordFunc.getInstance();
		int result = audioRecordFunc.startRecordAndFile(wavPath, userid
				+ "-1.wav", userid + "1.raw");
		if (result == 1) {
			ToastUtil.ShowResString(getApplicationContext(),
					R.string.audio_error_unknown);
			alertDialog.cancel();
			return;
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				stopRecord();
				alertDialog.cancel();

			}
		}, 3000);
	}

	protected void stopRecord() {
		audioRecordFunc.stopRecordAndFile();
		ToastUtil.ShowResString(getApplicationContext(),
				R.string.start_handling);
		userService.trainUser(Integer.valueOf(userid));
		ToastUtil.ShowResString(this, R.string.train_end);
		button_Train.setText(R.string.train);
		createMFCCnTrain();

	}

	private void createMFCCnTrain() {
		fileHelper.createLab(userid);
		wavlist=fileHelper.createWavList(userid);
		fileHelper.createProto(userid);
		HTK.mfcc(fileHelper.getConfigFilePath(), wavlist);
		fileHelper.copyMfcc(userid);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		userService = new UserService(getApplicationContext());
		fileHelper = new FileHelper(getApplicationContext());
		username = getIntent().getStringExtra("USERNAME");
		userid = getIntent().getStringExtra("USERID");
		bindControl();
		controlBindListener();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
