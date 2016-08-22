/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bookkos.bircle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import com.bookkos.bircle.R;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
final class BircleBeepManager implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

	private static final String TAG = BeepManager.class.getSimpleName();

	private static final float BEEP_VOLUME = 0.20f;
	private static final long VIBRATE_DURATION = 250L;

	private final Activity activity;
	private MediaPlayer failureMediaPlayer;
	private MediaPlayer registMediaPlayer;
	private MediaPlayer registMediaPlayer2;
	private MediaPlayer borrowMediaPlayer;
	private MediaPlayer returnMediaPlayer;
	private boolean playBeep;
	private boolean vibrate;

	BircleBeepManager(Activity activity) {
		this.activity = activity;
		this.failureMediaPlayer = null;
		this.registMediaPlayer = null;
		this.registMediaPlayer2 = null;
		this.borrowMediaPlayer = null;
		this.returnMediaPlayer = null;
		updatePrefs();
	}

	synchronized void updatePrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		playBeep = shouldBeep(prefs, activity);
		vibrate = prefs.getBoolean(PreferencesActivity.KEY_VIBRATE, false);
		if (playBeep && failureMediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			failureMediaPlayer = buildMediaPlayer(activity, 0);
		}
		if (playBeep && registMediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			registMediaPlayer = buildMediaPlayer(activity, 1);
		}
		if (playBeep && registMediaPlayer2 == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			registMediaPlayer2 = buildMediaPlayer(activity, 2);
		}
		if (playBeep && borrowMediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			borrowMediaPlayer = buildMediaPlayer(activity, 3);
		}
		if (playBeep && returnMediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
			// so we now play on the music stream.
			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			returnMediaPlayer = buildMediaPlayer(activity, 4);
		}
		
	}

	/* playBeepSoundAndVibrate(int flag)について
	 * flag == 0 失敗のbeep音
	 * flag == 1 登録のbeep音
	 * flag == 2 一括登録の時のISBNを読み取るbeep音
	 * flag == 3 借用のbeep音
	 * flag == 4 返却のbeep音
	 */
	
	synchronized void playBeepSoundAndVibrate(int flag) {
		if (flag == 0 && playBeep && failureMediaPlayer != null) {
			failureMediaPlayer.start();
		}
		else if(flag == 1 && playBeep && registMediaPlayer != null) {
			registMediaPlayer.start();
		}
		else if(flag == 2 && playBeep && registMediaPlayer2 != null) {
			registMediaPlayer2.start();
		}
		else if(flag == 3 && playBeep && borrowMediaPlayer != null) {
			borrowMediaPlayer.start();
		}
		else if(flag == 4 && playBeep && returnMediaPlayer != null) {
			returnMediaPlayer.start();
		}
		
		if (vibrate) {
			Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private static boolean shouldBeep(SharedPreferences prefs, Context activity) {
		boolean shouldPlayBeep = prefs.getBoolean(PreferencesActivity.KEY_PLAY_BEEP, true);
		if (shouldPlayBeep) {
			// See if sound settings overrides this
			AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				shouldPlayBeep = false;
			}
		}
		return shouldPlayBeep;
	}

	private MediaPlayer buildMediaPlayer(Context activity, int flag) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);

		AssetFileDescriptor failure_beep_file = activity.getResources().openRawResourceFd(R.raw.beep_failure);
		AssetFileDescriptor regist_beep_file = activity.getResources().openRawResourceFd(R.raw.beep_regist);
		AssetFileDescriptor regist_beep_file2 = activity.getResources().openRawResourceFd(R.raw.beep_regist2);
		AssetFileDescriptor borrow_beep_file = activity.getResources().openRawResourceFd(R.raw.beep_borrow);
		AssetFileDescriptor return_beep_file = activity.getResources().openRawResourceFd(R.raw.beep_return);
		try {
			if(flag == 0) {
				mediaPlayer.setDataSource(failure_beep_file.getFileDescriptor(), failure_beep_file.getStartOffset(), failure_beep_file.getLength());
				failure_beep_file.close();
			}
			else if(flag == 1) {
				mediaPlayer.setDataSource(regist_beep_file.getFileDescriptor(), regist_beep_file.getStartOffset(), regist_beep_file.getLength());
				regist_beep_file.close();
			}
			else if(flag == 2) {
				mediaPlayer.setDataSource(regist_beep_file2.getFileDescriptor(), regist_beep_file2.getStartOffset(), regist_beep_file2.getLength());
				regist_beep_file2.close();
			}
			else if(flag == 3) {
				mediaPlayer.setDataSource(borrow_beep_file.getFileDescriptor(), borrow_beep_file.getStartOffset(), borrow_beep_file.getLength());
				borrow_beep_file.close();
			}
			else if(flag == 4) {
				mediaPlayer.setDataSource(return_beep_file.getFileDescriptor(), return_beep_file.getStartOffset(), return_beep_file.getLength());
				return_beep_file.close();
			}
			
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			mediaPlayer = null;
		}
		return mediaPlayer;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// When the beep has finished playing, rewind to queue up another one.      
		mp.seekTo(0);
	}

	@Override
	public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			// we are finished, so put up an appropriate error toast if required and finish
			activity.finish();
		} else {
			// possibly media player error, so release and recreate
			mp.release();
			failureMediaPlayer = null;
			registMediaPlayer = null;
			registMediaPlayer2 = null;
			borrowMediaPlayer = null;
			returnMediaPlayer = null;
			updatePrefs();
		}
		return true;
	}

}