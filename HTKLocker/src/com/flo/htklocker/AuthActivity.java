package com.flo.htklocker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flo.adapter.AuthListViewAdapter;
import com.flo.htklocker.R;
import com.flo.model.User;
import com.flo.service.LoginService;
import com.flo.service.UserService;
import com.flo.util.AudioRecordFunc;
import com.flo.service.*;
import com.flo.util.ToastUtil;
import com.flo.util.TrainTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AuthActivity extends Activity {
	LoginService loginService;
	boolean isSoundMode = true;
	RelativeLayout relativeLayout_MainPanel;
	TextView textView_Time;
	TextView textView_Date;
	TextView textView_Info;

	EditText editText_Password;
	ProgressBar progressBar;
	Button button_ChangeMode;
	Button button_Reset;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	Button button8;
	Button button9;
	Button button0;

	GridLayout gridLayout_NumberPanel;
	String inputPassword;
	String oldPassword;

	ListView listView_User;
	List<Map<String, Object>> userMapList;
	// SimpleAdapter adapter;
	AuthListViewAdapter adapter;
	DecimalFormat decimalFormat;
	AlertDialog alertDialog;
	String wavPath;
	String wavString = "test_1.wav";
	String rawString = "test_1.raw";
	String wavlist;
	AudioRecordFunc audioRecordFunc;
	FileService fileService;
	UserService userService;

	static String[] weekDaysName;

	private List<Map<String, Object>> list2Map(List<User> userList) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (userList == null) {
			return result;
		}
		for (User u : userList) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (u.getIsTrained()) {
				map.put("textView_UserName", u.getName());
				map.put("USERID", u.getNameId());
			} else {
			}
			result.add(map);
		}
		return result;
	}

	private void bindView() {
		relativeLayout_MainPanel = (RelativeLayout) findViewById(R.id.relativeLayout_MainPanel);
		textView_Time = (TextView) findViewById(R.id.textView_Time);
		textView_Date = (TextView) findViewById(R.id.textView_Date);
		textView_Info = (TextView) findViewById(R.id.textView_Info);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		listView_User = (ListView) findViewById(R.id.listView_User);
		button_Reset = (Button) findViewById(R.id.button_Reset);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);
		button7 = (Button) findViewById(R.id.button7);
		button8 = (Button) findViewById(R.id.button8);
		button9 = (Button) findViewById(R.id.button9);
		button0 = (Button) findViewById(R.id.button0);
		gridLayout_NumberPanel = (GridLayout) findViewById(R.id.gridLayout_NumberPanel);
		button_ChangeMode = (Button) findViewById(R.id.button_ChangeMode);
		editText_Password = (EditText) findViewById(R.id.editText_Password);
		relativeLayout_MainPanel.setBackground(getWallpaper());
		editText_Password.setCursorVisible(false);
		editText_Password.setFocusable(false);
		editText_Password.setFocusableInTouchMode(false);
		progressBar.setVisibility(View.INVISIBLE);

		if (isSoundMode) {
			editText_Password.setVisibility(View.INVISIBLE);
			gridLayout_NumberPanel.setVisibility(View.INVISIBLE);
			listView_User.setVisibility(View.VISIBLE);
			button_ChangeMode.setText(R.string.switch_numerical_password);

		} else {
			listView_User.setVisibility(View.INVISIBLE);
			editText_Password.setVisibility(View.VISIBLE);
			gridLayout_NumberPanel.setVisibility(View.VISIBLE);
			button_ChangeMode.setText(R.string.switch_sound_password);

		}
		userMapList = list2Map(userService.getTrainedUserList());
		if (userMapList.size() < 3) {
			changeMode();
		}
		// adapter = new SimpleAdapter(this, userMapList, R.layout.item_auth,
		// new String[] { "textView_UserName" },
		// new int[] { R.id.textView_UserName });
		adapter = new AuthListViewAdapter(this, userMapList,
				R.layout.item_auth, new String[] { "textView_UserName",
						"imageButton_UnLock", "USERID" }, new int[] {
						R.id.textView_UserName, R.id.imageButton_UnLock });

		listView_User.setAdapter(adapter);
	}

	class NumberButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.button0:
				inputPassword = inputPassword + "0";
				break;
			case R.id.button1:
				inputPassword = inputPassword + "1";
				break;
			case R.id.button2:
				inputPassword = inputPassword + "2";
				break;
			case R.id.button3:
				inputPassword = inputPassword + "3";
				break;
			case R.id.button4:
				inputPassword = inputPassword + "4";
				break;
			case R.id.button5:
				inputPassword = inputPassword + "5";
				break;
			case R.id.button6:
				inputPassword = inputPassword + "6";
				break;
			case R.id.button7:
				inputPassword = inputPassword + "7";
				break;
			case R.id.button8:
				inputPassword = inputPassword + "8";
				break;
			case R.id.button9:
				inputPassword = inputPassword + "9";
				break;
			default:
				break;
			}
			editText_Password.setText(inputPassword);
			if (loginService.validateUser(editText_Password.getText()
					.toString())) {
				unLock();
			}
		}
	}

	private void bindListener() {
		NumberButtonClickListener numberButtonClickListener = new NumberButtonClickListener();
		button1.setOnClickListener(numberButtonClickListener);
		button2.setOnClickListener(numberButtonClickListener);
		button3.setOnClickListener(numberButtonClickListener);
		button4.setOnClickListener(numberButtonClickListener);
		button5.setOnClickListener(numberButtonClickListener);
		button6.setOnClickListener(numberButtonClickListener);
		button7.setOnClickListener(numberButtonClickListener);
		button8.setOnClickListener(numberButtonClickListener);
		button9.setOnClickListener(numberButtonClickListener);
		button0.setOnClickListener(numberButtonClickListener);
		button_Reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resetPassword(false);
			}
		});
		button_Reset.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				resetPassword(true);
				return false;
			}
		});

		button_ChangeMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (userMapList.size() < 3) {
					ToastUtil.show(getApplicationContext(), getResources()
							.getString(R.string.trained_user_not_enough));
				} else {
					changeMode();
				}
			}
		});
	}

	protected void changeMode() {
		if (isSoundMode) {
			isSoundMode = false;
			listView_User.setVisibility(View.INVISIBLE);
			gridLayout_NumberPanel.setVisibility(View.VISIBLE);
			editText_Password.setVisibility(View.VISIBLE);
			button_ChangeMode.setText(R.string.switch_sound_password);
		} else {
			isSoundMode = true;
			gridLayout_NumberPanel.setVisibility(View.INVISIBLE);
			listView_User.setVisibility(View.VISIBLE);
			editText_Password.setVisibility(View.INVISIBLE);
			button_ChangeMode.setText(R.string.switch_numerical_password);
		}
	}

	public void unLock() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	protected void resetPassword(boolean isAll) {
		if (isAll) {
			inputPassword = "";
			editText_Password.setText(inputPassword);
		} else {
			if (inputPassword.length() > 0) {
				inputPassword = inputPassword.substring(0,
						inputPassword.length() - 1);
				editText_Password.setText(inputPassword);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		fileService = new FileService(getApplicationContext());
		loginService = new LoginService(getApplicationContext());
		weekDaysName = getResources().getStringArray(R.array.weekDays);
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
		win.setAttributes(winParams);
		win.setFlags(0x80000000, 0x80000000);
		userService = new UserService(getApplicationContext());

		decimalFormat = new DecimalFormat("00");
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindView();
		bindListener();
		resetPassword(true);
		Calendar c = Calendar.getInstance();
		int weekIndex = c.get(Calendar.DAY_OF_WEEK) - 1;
		textView_Time.setText(""
				+ decimalFormat.format(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ decimalFormat.format(c.get(Calendar.MINUTE)));
		textView_Date.setText("" + decimalFormat.format(c.get(Calendar.YEAR))
				+ "/" + decimalFormat.format(c.get(Calendar.MONTH) + 1) + "/"
				+ decimalFormat.format(c.get(Calendar.DAY_OF_MONTH)) + " "
				+ weekDaysName[weekIndex]);

		// listView_User.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// wavPath = fileService.getTestWavPath();
		// if (wavPath==null) {
		// ToastUtil.show(AuthActivity.this,
		// R.string.audio_error_no_sdcard);
		// } else {
		// textView_Info.setText(R.string.record_start);
		// progressBar.setVisibility(View.VISIBLE);
		// listView_User.setVisibility(View.INVISIBLE);
		// button_ChangeMode.setVisibility(View.INVISIBLE);
		// startRecord();
		// }
		// }
		// });
	}

	protected void startRecord() {
		audioRecordFunc = AudioRecordFunc.getInstance();
		int result = audioRecordFunc.startRecordAndFile(wavPath, wavString,
				rawString);
		if (result == 1) {
			ToastUtil.show(getApplicationContext(),
					R.string.audio_error_unknown);
			return;
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				stopRecord();
			}
		}, 3000);
	}

	protected void stopRecord() {
		audioRecordFunc.stopRecordAndFile();

		TrainTest.createMFCC(fileService, wavPath, "test", false);
		textView_Info.setText("");
		listView_User.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
		button_ChangeMode.setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
