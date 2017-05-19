package com.vikas.trillo.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vikas.trillo.R;
import com.vikas.trillo.listeners.AudioUploadListener;
import com.vikas.trillo.network.UploadAudioService;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by OFFICE on 5/17/2017.
 */

public class RecordFragment extends Fragment {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final int REQUEST_CODE = 101;
    private final String TAG = RecordFragment.class.getSimpleName();

    @BindView(R.id.tvRecordTiming)
    TextView tvRecordTiming;
    @BindView(R.id.ivRecordingMic)
    ImageView ivRecordingMic;
    @BindView(R.id.flRecording)
    FrameLayout flRecording;
    @BindView(R.id.tvRecordingMessage)
    TextView tvRecordingMessage;
    @BindView(R.id.ivPlayPause)
    ImageView ivPlayPause;
    @BindView(R.id.sbRecording)
    AppCompatSeekBar sbRecording;
    @BindView(R.id.tvPlayTime)
    TextView tvPlayTime;
    @BindView(R.id.btUploadRecording)
    AppCompatButton btUploadRecording;
    @BindView(R.id.llPlayPause)
    LinearLayout llPlayPause;
    Unbinder unbinder;
    private boolean isPlayingSound;
    private boolean isRecordingSound;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Handler mHandler = new Handler();
    private Runnable runnableRecording;
    private Runnable runnablePlaying;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private ProgressDialog progressDialog;

    public static RecordFragment getInstance() {
        return new RecordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkRecordingPremissions();
    }

    private void checkRecordingPremissions() {
        //here check if the user has granted the permission or not
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //here you have to request the permission for voice recording
            //before that check if the user has revoked permission earlier or not
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                //here you need to show some message to user that you need permission for recording
                makeToastPermission(getString(R.string.premission_msg));
            } else {
                //new permission
                requestPermission();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
    }

    private void makeToastPermission(String message) {
        final Snackbar finalSnackbar = Snackbar.make(flRecording, message, Snackbar.LENGTH_LONG);
        finalSnackbar.setAction("Give Permission", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalSnackbar.dismiss();
                requestPermission();
            }
        });
        finalSnackbar.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        initializeMembers();
        setListener();
        return view;
    }

    private void setListener() {
        sbRecording.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    try {
                        mPlayer.seekTo(progress * 1000);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "seekBar updated to" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initializeMembers() {
        try {
            mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
            mFileName += "/trillBit.3gp";
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(getString(R.string.upload_title));
            progressDialog.setMessage(getString(R.string.message_upload));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecorder = null;
        mPlayer = null;
        unbinder.unbind();
    }

    @OnClick({R.id.ivRecordingMic, R.id.ivPlayPause, R.id.btUploadRecording})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRecordingMic:
                if (isRecordingSound) {
                    //means we are recording sound right now and user clicked to stop
                    stopRecordingVoice();
                } else {
                    //user has initiated the recording
                    startRecordingVoice();
                }
                break;
            case R.id.ivPlayPause:
                if (isPlayingSound) {
                    //already playing sound now stop
                    stopPlayingSound();
                } else {
                    //user clicked to listen to recorded sound
                    playRecordedSound();
                }
                break;
            case R.id.btUploadRecording:
                //upload the recorded file
                uploadRecording();
                break;
        }
    }

    private void uploadRecording() {
        //here make call to upload the recorded voice file
        if (mPlayer != null) {
            sbRecording.setProgress(0);
            mHandler.removeCallbacks(runnablePlaying);
            mPlayer.release();
        }
        progressDialog.show();
        UploadAudioService uploadAudioService = new UploadAudioService(getContext(), new AudioUploadListener() {
            @Override
            public void onUploadVideoSuccess() {
                progressDialog.dismiss();
                makeToast(getString(R.string.upload_success));
                Log.d(TAG, "uploaded successfully");
            }

            @Override
            public void onUploadVideoFailure() {
                progressDialog.dismiss();
                makeToast(getString(R.string.upload_failure));
                Log.d(TAG, "uploaded failed");
            }
        });
        uploadAudioService.uploadAudio(mFileName);
    }

    private void stopPlayingSound() {
        //here manage to stop the playing sound
        flRecording.setEnabled(true);
        mPlayer.release();
        sbRecording.setProgress(0);
        mHandler.removeCallbacks(runnablePlaying);
        isPlayingSound = false;
        updatePlayingDrawable(isPlayingSound);
        tvPlayTime.setText(getString(R.string.start_time));
        Log.d(TAG, "stopped playing recording");
    }

    private void playRecordedSound() {
        //here play the sound if any recorded
        if (!checkFileExists()) {
            makeToast(getString(R.string.no_recordings));
        } else {
            try {
                flRecording.setEnabled(false);
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
                sbRecording.setMax(mPlayer.getDuration() / 1000);
                isPlayingSound = true;
                updatePlayingDrawable(isPlayingSound);
                updateSeekbarCursor();
                Log.d(TAG, "playing recording");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "prepare() failed");
            }
        }
    }

    private boolean checkFileExists() {
        File file = new File(mFileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void updateSeekbarCursor() {
        runnablePlaying = new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    if (sbRecording.getMax() > sbRecording.getProgress()) {
                        sbRecording.setProgress(mCurrentPosition + 1);
                        int min = mCurrentPosition / 60;
                        tvPlayTime.setText(min + ":" + String.valueOf(mCurrentPosition));
                        Log.d(TAG, "updating seekBar to " + mCurrentPosition);
                        mHandler.postDelayed(runnablePlaying, 1000);
                    } else {
                        mHandler.removeCallbacks(runnablePlaying);
                        Log.d(TAG, "removing callbacks ");
                    }
                }
            }
        };
        getActivity().runOnUiThread(runnablePlaying);
    }

    private void updatePlayingDrawable(boolean isRecordingSound) {
        if (isRecordingSound) {
            ivPlayPause.setImageResource(R.drawable.ic_stop_black_24dp);
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    private void stopRecordingVoice() {
        //here manage the recording stop
        llPlayPause.setEnabled(true);
        btUploadRecording.setEnabled(true);
        mRecorder.stop();
        mRecorder.release();
        timeSwapBuff = 0l;
        startTime = 0l;
        timeInMilliseconds = 0l;
        mHandler.removeCallbacks(runnableRecording);
        isRecordingSound = false;
        tvRecordTiming.setText(getString(R.string.start_time));
        updateRecordingDrawable(isRecordingSound);
        updateRecordingMessage(isRecordingSound);
        Log.d(TAG, "stoppped recording");
    }

    private void startRecordingVoice() {
        //here record the voice
        try {
            sbRecording.setProgress(0);
            tvPlayTime.setText(getString(R.string.start_time));
            mRecorder = new MediaRecorder();
            startTime = SystemClock.uptimeMillis();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            llPlayPause.setEnabled(false);
            btUploadRecording.setEnabled(false);
            mRecorder.prepare();
            mRecorder.start();
            isRecordingSound = true;
            updateRecordingTimer();
            updateRecordingMessage(isRecordingSound);
            updateRecordingDrawable(isRecordingSound);
            Log.d(TAG, "started recording");
        } catch (RuntimeException | IOException e) {
            makeToast(getString(R.string.missing_microphone));
            e.printStackTrace();
            Log.e(TAG, "prepare() failed");
        }
    }

    private void updateRecordingTimer() {
        runnableRecording = new Runnable() {
            @Override
            public void run() {
                if (isRecordingSound) {
                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    long updatedTime = timeSwapBuff + timeInMilliseconds;
                    int secs = (int) (updatedTime / 1000);
                    int mins = secs / 60;
                    secs = secs % 60;
                    String time = String.valueOf(mins + ":" + secs);
                    tvRecordTiming.setText(time);
                    Log.d(TAG, "timer updated to " + tvRecordTiming.getText());
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        getActivity().runOnUiThread(runnableRecording);
    }

    private void updateRecordingMessage(boolean isRecordingSound) {
        if (isRecordingSound) {
            tvRecordingMessage.setText(getString(R.string.stop_recording_message));
        } else {
            tvRecordingMessage.setText(getString(R.string.start_recording_message));
        }
    }

    private void updateRecordingDrawable(boolean isRecordingSound) {
        if (isRecordingSound) {
            ivRecordingMic.setImageResource(R.drawable.ic_stop_black_24dp);
        } else {
            ivRecordingMic.setImageResource(R.drawable.ic_mic_black_24dp);
        }
    }

    private void makeToast(String message) {
        Snackbar.make(flRecording, message, Snackbar.LENGTH_SHORT).show();
    }
}
