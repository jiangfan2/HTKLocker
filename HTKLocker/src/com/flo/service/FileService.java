package com.flo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.flo.util.FileUtil;

import android.content.Context;
import android.os.Environment;

public class FileService {
	/**
	 * File structure ./- |-proto- hmmperson.mmf... |-wav- person1.wav
	 * person2.wav person3.wav... |-mfcc- person1.mfc person2.mfc person3.mfc...
	 * |-lab- |-person person1.lab person2.lab person3.lab... |-hmm0 |-hmm1
	 * |-hmm2 -persontrainlist.txt -dict.txt -net.slf -wavlist.txt
	 * 
	 */
	String appRoot;
	String hmm0Path;
	String hmm1Path;
	String hmm2Path;
	String protoPath;
	String mfccPath;
	String labPath;
	String labUserPath;
	String trainWavPath;
	String testWavPath;
	Context context;

	public String getAppRoot() {
		return appRoot;
	}

	public String getHmm0Path() {
		return hmm0Path;
	}

	public String getHmm1Path() {
		return hmm1Path;
	}

	public String getHmm2Path() {
		return hmm2Path;
	}

	public String getProtoPath() {
		return protoPath;
	}

	public String getMfccPath() {
		return mfccPath;
	}

	public String getLabPath(String userid) {
		File labUser = new File(labPath + "/" + userid);
		if (!labUser.exists()) {
			labUser.mkdirs();
		}
		labUserPath = labUser.getAbsolutePath();
		return labUserPath;
	}

	public String getTrainWavPath() {
		return trainWavPath;
	}

	public String getTestWavPath() {
		return testWavPath;
	}

	
	public String getConfigFilePath() {
		return 	appRoot + "/config";
	}
	
	public FileService(Context context) {
		this.context = context;
		File sdDir;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = context.getExternalFilesDir(null);
			appRoot = sdDir.getAbsolutePath();
			InputStream inputStream;
			try {
				inputStream = context.getAssets().open("config");
				FileUtil.copyFile(inputStream, appRoot + "/config");
			} catch (IOException e) {
			}

			File hmm0 = new File(appRoot + "/hmm0");
			if (!hmm0.exists()) {
				hmm0.mkdirs();
			}
			hmm0Path = hmm0.getAbsolutePath();

			File hmm1 = new File(appRoot + "/hmm1");
			if (!hmm1.exists()) {
				hmm1.mkdirs();
			}
			hmm1Path = hmm1.getAbsolutePath();

			File hmm2 = new File(appRoot + "/hmm2");
			if (!hmm2.exists()) {
				hmm2.mkdirs();
			}
			hmm2Path = hmm2.getAbsolutePath();

			File proto = new File(appRoot + "/proto");
			if (!proto.exists()) {
				proto.mkdirs();
			}
			protoPath = proto.getAbsolutePath();

			File mfcc = new File(appRoot + "/mfcc");
			if (!mfcc.exists()) {
				mfcc.mkdirs();
			}
			mfccPath = mfcc.getAbsolutePath();

			File lab = new File(appRoot + "/lab");
			if (!lab.exists()) {
				lab.mkdirs();
			}
			labPath = lab.getAbsolutePath();

			File trainWav = new File(appRoot + "/trainwav");
			if (!trainWav.exists()) {
				trainWav.mkdirs();
			}
			trainWavPath = trainWav.getAbsolutePath();

			File testWav = new File(appRoot + "/testwav");
			if (!testWav.exists()) {
				testWav.mkdirs();
			}
			testWavPath = testWav.getAbsolutePath();

		} else {
			appRoot = null;
		}
	}
	
	public String copyMfcc(String userid) {
		FileUtil.copyFile(mfccPath + "/" + userid + "_1.mfc", mfccPath
				+ "/" + userid + "_2.mfc");
		FileUtil.copyFile(mfccPath + "/" + userid + "_1.mfc", mfccPath
				+ "/" + userid + "_3.mfc");
		FileUtil.copyFile(mfccPath + "/" + userid + "_1.mfc", mfccPath
				+ "/" + userid + "_4.mfc");
		return mfccPath;
	}

	public void clearWav(String userid) {
		FileUtil.deleteFile(trainWavPath + "/" + userid + "_2.wav");
		FileUtil.deleteFile(trainWavPath + "/" + userid + "_3.wav");
		FileUtil.deleteFile(trainWavPath + "/" + userid + "_4.wav");

	}

	public String createLab(String userid) {
		getLabPath(userid);
		FileOutputStream fs = null;
		String textString = "0 20000000 " + userid;
		try {
			fs = new FileOutputStream(labUserPath + "/" + userid + "_1.lab");
			fs.write(textString.getBytes());
			fs.close();
			fs = new FileOutputStream(labUserPath + "/" + userid + "_2.lab");
			fs.write(textString.getBytes());
			fs.close();
			fs = new FileOutputStream(labUserPath + "/" + userid + "_3.lab");
			fs.write(textString.getBytes());
			fs.close();
			fs = new FileOutputStream(labUserPath + "/" + userid + "_4.lab");
			fs.write(textString.getBytes());
			fs.close();
			return labUserPath;
		} catch (Exception e) {
			return null;
		}
	}

	public String createWavList(String wavPath,String userid) {
		getLabPath(userid);
		FileOutputStream fs = null;
		String textString1 = wavPath + "/" + userid + "_1.wav " + mfccPath
				+ "/" + userid + "_1.mfc \n";
		try {
			fs = new FileOutputStream(appRoot + "/wavlist.txt");
			fs.write(textString1.getBytes());
			fs.close();
			return appRoot + "/wavlist.txt";
		} catch (Exception e) {
			return null;
		}
	}

	public String createProto(String userid) {
		try {
			InputStream inputStream = context.getAssets().open("proto");
			OutputStream fosto = new FileOutputStream(protoPath + "/hmm_" + userid);
			byte btHeader[] = new byte[34];
			inputStream.read(btHeader);
			fosto.write(btHeader);
			fosto.write(userid.getBytes());
			int c;
			byte bt[] = new byte[1024];
			while ((c = inputStream.read(bt)) > 0) {
			fosto.write(bt, 0, c);
			}
			fosto.close();
			return protoPath + "/hmm_" + userid;
		} catch (IOException e) {
			return null;
		}
	}
	public String createTrainList(String userid) {
		FileOutputStream fs = null;
		String textString1 = mfccPath + "/" + userid + "_1.mfc \n";
		String textString2 = mfccPath + "/" + userid + "_2.mfc \n";
		String textString3 = mfccPath + "/" + userid + "_3.mfc \n";
		String textString4 = mfccPath + "/" + userid + "_4.mfc \n";

		try {
			fs = new FileOutputStream(appRoot + "/trainlist.txt");
			fs.write(textString1.getBytes());
			fs.write(textString2.getBytes());
			fs.write(textString3.getBytes());
			fs.write(textString4.getBytes());
			fs.close();
			return appRoot + "/trainlist.txt";
		} catch (Exception e) {
			return null;
		}
	}
}