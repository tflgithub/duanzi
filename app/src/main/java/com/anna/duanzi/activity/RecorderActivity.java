package com.anna.duanzi.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;

import org.bytedeco.javacv.FrameRecorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.tfl.mediarecord.FFmpegFrameRecorder;
import cn.tfl.mediarecord.RecorderParameters;
import cn.tfl.mediarecord.RecorderThread;
import cn.tfl.mediarecord.SavedFrames;
import cn.tfl.mediarecord.ui.ProgressView;
import cn.tfl.mediarecord.util.Constants;
import cn.tfl.mediarecord.util.Util;

import static cn.tfl.mediarecord.util.Constants.MAX_RECORD_TIME;
import static cn.tfl.mediarecord.util.Constants.MIN_RECORD_TIME;

public class RecorderActivity extends Activity implements OnClickListener {

    private static final int CAMERA_OK = 100;
    private final String TAG = getClass().getSimpleName();
    private PowerManager.WakeLock mWakeLock;
    private String strVideoPath;
    private File fileVideoPath = null;
    private AudioRecordRunnable audioRecordRunnable;
    private Thread audioThread;
    private Camera cameraDevice;
    private CameraView cameraView;
    private Parameters cameraParameters = null;
    private volatile FFmpegFrameRecorder videoRecorder;
    private RecorderThread recorderThread;
    private Button cancelBtn, nextBtn;
    private ProgressView progressView;
    private ImageView recordBtn;
    private ImageButton switchCameraIcon, flashIcon, backBtn;
    private RelativeLayout topLayout = null;
    private Chronometer chronometer;
    private boolean recording = false;
    private boolean isRecordingStarted = false;
    private boolean isFlashOn = false;
    private boolean isRotateVideo = false;
    private boolean isFrontCam = false;
    private boolean isPreviewOn = false;
    private boolean recordFinish = false;
    volatile boolean runAudioThread = true;
    private SavedFrames lastSavedframe = new SavedFrames(null, 0L, false, false);
    private int currentResolution = Constants.RESOLUTION_MEDIUM_VALUE;
    private int previewWidth = 480;
    private int previewHeight = 480;
    private int screenWidth = 480;
    private int sampleRate = 44100;
    private int defaultCameraId = -1;
    private int defaultScreenResolution = -1;
    private int cameraSelection = 0;
    private int frameRate = 30;

    private long firstTime = 0;
    private long totalTime = 0;
    private long totalPauseTime = 0;
    private long pausedTime = 0;
    private volatile long mAudioTimestamp = 0L;
    private long mLastAudioTimestamp = 0L;
    private long frameTime = 0L;
    private long mVideoTimestamp = 0L;
    private volatile long mAudioTimeRecorded;
    private String imagePath = null;

    private byte[] firstData = null;
    private byte[] bufferByte;

    private DeviceOrientationEventListener orientationListener;
    // The degrees of the device rotated clockwise from its natural orientation.
    private int deviceOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    //private RecordHelper recordHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        mWakeLock.acquire();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //Find screen dimensions
        screenWidth = displaymetrics.widthPixels;
        orientationListener = new DeviceOrientationEventListener(this);
        initLayout();
    }

    private void initLayout() {
        progressView = (ProgressView) findViewById(R.id.progress_recorder);
        recordBtn = (ImageView) findViewById(R.id.btn_recorder_record);
        recordBtn.setOnClickListener(this);
        chronometer = (Chronometer) findViewById(R.id.tv_total_time);
        cancelBtn = (Button) findViewById(R.id.recorder_rollback);
        cancelBtn.setOnClickListener(this);
        nextBtn = (Button) findViewById(R.id.recorder_next);
        nextBtn.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        flashIcon = (ImageButton) findViewById(R.id.recorder_flashlight);
        switchCameraIcon = (ImageButton) findViewById(R.id.recorder_frontcamera);
        flashIcon.setOnClickListener(this);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            switchCameraIcon.setVisibility(View.VISIBLE);
        }
        initCameraLayout();
    }

    private void initCameraLayout() {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean result = false;
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(RecorderActivity.this,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //先判断有没有权限 ，没有就在这里进行权限的申请
                        ActivityCompat.requestPermissions(RecorderActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, CAMERA_OK);
                    } else {
                        //说明已经获取到摄像头权限了 想干嘛干嘛
                        result = setCamera();
                    }
                } else {
                    //这个说明系统版本在6.0之下，不需要动态获取权限。
                    result = setCamera();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result || cameraDevice == null) {
                    finish();
                    return;
                }
                topLayout = (RelativeLayout) findViewById(R.id.layout_recorder_surface);
                if (topLayout != null && topLayout.getChildCount() > 0)
                    topLayout.removeAllViews();
                cameraView = new CameraView(RecorderActivity.this, cameraDevice);
                handleSurfaceChanged();
                RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth, (int) (screenWidth * (previewWidth / (previewHeight * 1f))));
                layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                View view = new View(RecorderActivity.this);
                view.setFocusable(false);
                view.setBackgroundColor(Color.BLACK);
                view.setFocusableInTouchMode(false);
                topLayout.addView(cameraView, layoutParam1);
                switchCameraIcon.setOnClickListener(RecorderActivity.this);
                if (cameraSelection == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    flashIcon.setVisibility(View.GONE);
                    isFrontCam = true;
                } else {
                    flashIcon.setVisibility(View.VISIBLE);
                    isFrontCam = false;
                }
            }

        }.execute("start");
    }

    private boolean isFirstFrame = true;

    class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

        private SurfaceHolder mHolder;


        public CameraView(Context context, Camera camera) {
            super(context);
            cameraDevice = camera;
            cameraParameters = cameraDevice.getParameters();
            mHolder = getHolder();
            mHolder.addCallback(CameraView.this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            cameraDevice.setPreviewCallbackWithBuffer(CameraView.this);
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                stopPreview();
                cameraDevice.setPreviewDisplay(holder);
            } catch (IOException exception) {
                cameraDevice.release();
                cameraDevice = null;
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (isPreviewOn)
                cameraDevice.stopPreview();
            handleSurfaceChanged();
            startPreview();
            cameraDevice.autoFocus(null);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//            try {
//                mHolder.addCallback(null);
//                cameraDevice.setPreviewCallback(null);
//            } catch (RuntimeException e) {
//            }
        }

        public void startPreview() {
            if (!isPreviewOn && cameraDevice != null) {
                isPreviewOn = true;
                cameraDevice.startPreview();
            }
        }

        public void stopPreview() {
            if (isPreviewOn && cameraDevice != null) {
                isPreviewOn = false;
                cameraDevice.stopPreview();
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            long frameTimeStamp = 0L;
            if (mAudioTimestamp == 0L && firstTime > 0L) {
                frameTimeStamp = 1000L * (System.currentTimeMillis() - firstTime);
            } else if (mLastAudioTimestamp == mAudioTimestamp) {
                frameTimeStamp = mAudioTimestamp + frameTime;
            } else {
                long l2 = (System.nanoTime() - mAudioTimeRecorded) / 1000L;
                frameTimeStamp = l2 + mAudioTimestamp;
                mLastAudioTimestamp = mAudioTimestamp;
            }
            if (isRecordingStarted && recording) {
                if (lastSavedframe != null
                        && lastSavedframe.getFrameBytesData() != null) {
                    if (isFirstFrame) {
                        isFirstFrame = false;
                        firstData = data;
                    }
                    totalTime = System.currentTimeMillis() - firstTime - pausedTime - rollbackTime - ((long) (1.0 / (double) frameRate) * 1000);
                    if (totalTime >= MIN_RECORD_TIME) {
                        nextBtn.setEnabled(true);
                    }
                    if (totalTime >= MAX_RECORD_TIME) {
                        onRecordPause();
                        saveRecorder();
                        return;
                    }
                    if (totalTime > 0)
                        cancelBtn.setEnabled(true);
                    mVideoTimestamp += frameTime;
                    if (lastSavedframe.getTimeStamp() > mVideoTimestamp) {
                        mVideoTimestamp = lastSavedframe.getTimeStamp();
                    }
                    //tempVideoList.add(lastSavedframe);
                    recorderThread.putByteData(lastSavedframe);
                }
            }
            lastSavedframe = new SavedFrames(data, frameTimeStamp, isRotateVideo, isFrontCam);
            cameraDevice.addCallbackBuffer(bufferByte);
        }
    }


    private void handleSurfaceChanged() {
        if (cameraDevice == null) {
            finish();
            return;
        }
        List<Camera.Size> resolutionList = Util.getResolutionList(cameraDevice);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new Util.ResolutionComparator());
            Camera.Size previewSize = null;
            if (defaultScreenResolution == -1) {
                boolean hasSize = false;
                for (int i = 0; i < resolutionList.size(); i++) {
                    Camera.Size size = resolutionList.get(i);
                    if (size != null && size.width == 640 && size.height == 480) {
                        previewSize = size;
                        hasSize = true;
                        break;
                    }
                }
                if (!hasSize) {
                    int mediumResolution = resolutionList.size() / 2;
                    if (mediumResolution >= resolutionList.size())
                        mediumResolution = resolutionList.size() - 1;
                    previewSize = resolutionList.get(mediumResolution);
                }
            } else {
                if (defaultScreenResolution >= resolutionList.size())
                    defaultScreenResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(defaultScreenResolution);
            }
            if (previewSize != null) {
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
                cameraParameters.setPreviewSize(previewWidth, previewHeight);
                if (videoRecorder != null) {
                    videoRecorder.setImageWidth(previewWidth);
                    videoRecorder.setImageHeight(previewHeight);
                }
            }
        }

        bufferByte = new byte[previewWidth * previewHeight * 3 / 2];
        cameraDevice.addCallbackBuffer(bufferByte);
        cameraParameters.setPreviewFrameRate(frameRate);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            cameraDevice.setDisplayOrientation(Util.determineDisplayOrientation(this, defaultCameraId));
            List<String> focusModes = cameraParameters.getSupportedFocusModes();
            if (focusModes != null) {
                Log.i("video", Build.MODEL);
                if (((Build.MODEL.startsWith("GT-I950"))
                        || (Build.MODEL.endsWith("SCH-I959"))
                        || (Build.MODEL.endsWith("MEIZU MX3"))) && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                    cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                }
            }
        } else
            cameraDevice.setDisplayOrientation(90);
        cameraDevice.setParameters(cameraParameters);
    }

    private boolean setCamera() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                int numberOfCameras = Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == cameraSelection) {
                        defaultCameraId = i;
                    }
                }
            }
            stopPreview();
            if (cameraDevice != null)
                cameraDevice.release();

            if (defaultCameraId >= 0)
                cameraDevice = Camera.open(defaultCameraId);
            else
                cameraDevice = Camera.open();

        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    setCamera();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(RecorderActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    private void initVideoRecorder() {
        strVideoPath = Util.createFinalPath(this);
        RecorderParameters recorderParameters = Util.getRecorderParameter(currentResolution);
        sampleRate = recorderParameters.getAudioSamplingRate();
        frameRate = recorderParameters.getVideoFrameRate();
        frameTime = (1000000L / frameRate);
        fileVideoPath = new File(strVideoPath);
        videoRecorder = new FFmpegFrameRecorder(strVideoPath, Constants.OUTPUT_WIDTH, Constants.OUTPUT_HEIGHT, recorderParameters.getAudioChannel());
        videoRecorder.setContext(this);
        videoRecorder.setFormat(recorderParameters.getVideoOutputFormat());
        videoRecorder.setSampleRate(recorderParameters.getAudioSamplingRate());
        videoRecorder.setFrameRate(recorderParameters.getVideoFrameRate());
        videoRecorder.setVideoCodec(recorderParameters.getVideoCodec());
        videoRecorder.setVideoQuality(recorderParameters.getVideoQuality());
        videoRecorder.setAudioQuality(recorderParameters.getVideoQuality());
        videoRecorder.setAudioCodec(recorderParameters.getAudioCodec());
        videoRecorder.setVideoBitrate(recorderParameters.getVideoBitrate());
        videoRecorder.setAudioBitrate(recorderParameters.getAudioBitrate());
        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);
        recorderThread = new RecorderThread(videoRecorder, previewWidth, previewHeight);
        recorderThread.start();
        //recordHelper = new RecordHelper(videoRecorder, previewWidth, previewHeight);
    }

    class AudioRecordRunnable implements Runnable {

        int bufferSize;
        short[] audioData;
        int bufferReadResult;
        private final AudioRecord audioRecord;
        public volatile boolean isInitialized;
        private int mCount = 0;

        private AudioRecordRunnable() {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            audioData = new short[bufferSize];
        }

        private void record(ShortBuffer shortBuffer) {
            this.mCount += shortBuffer.limit();
            try {
                videoRecorder.recordSamples(new Buffer[]{shortBuffer});
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
            return;
        }

        private void updateTimestamp() {
            if (videoRecorder != null) {
                int i = Util.getTimeStampInNsFromSampleCounted(this.mCount);
                if (mAudioTimestamp != i) {
                    mAudioTimestamp = i;
                    mAudioTimeRecorded = System.nanoTime();
                }
            }
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            this.isInitialized = false;
            if (audioRecord != null) {
                while (this.audioRecord.getState() == 0) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException localInterruptedException) {
                    }
                }
                this.isInitialized = true;
                this.audioRecord.startRecording();
                while (((runAudioThread) || (mVideoTimestamp > mAudioTimestamp)) && (mAudioTimestamp < (1000 * MAX_RECORD_TIME))) {
                    updateTimestamp();
                    bufferReadResult = this.audioRecord.read(audioData, 0, audioData.length);
                    if ((bufferReadResult > 0) && ((isRecordingStarted && recording) || (mVideoTimestamp > mAudioTimestamp)))
                        record(ShortBuffer.wrap(audioData, 0, bufferReadResult));
                }
                this.audioRecord.stop();
                this.audioRecord.release();
            }
        }
    }

    public void startRecording() {
        try {
            if (videoRecorder != null)
                videoRecorder.start();
            else finish();
            if (audioThread != null)
                audioThread.start();
            else finish();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (isPreviewOn && cameraDevice != null) {
            isPreviewOn = false;
            cameraDevice.stopPreview();
        }
    }

    private void initiateRecording() {
        initVideoRecorder();
        startRecording();
        firstTime = System.currentTimeMillis();
    }

    private class DeviceOrientationEventListener
            extends OrientationEventListener {
        public DeviceOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            // We keep the last known orientation. So if the user first orient
            // the camera then point the camera to floor or sky, we still have
            // the correct orientation.
            if (orientation == ORIENTATION_UNKNOWN) return;
            deviceOrientation = Util.roundOrientation(orientation, deviceOrientation);
            if (deviceOrientation == 0) {
                isRotateVideo = true;
            } else {
                isRotateVideo = false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        videoTheEnd(false);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orientationListener != null)
            orientationListener.enable();
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
            mWakeLock.acquire();
        }
        if (isRecordingStarted) {
            onRecordStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!recordFinish) {
            onRecordPause();
        }
        if (orientationListener != null)
            orientationListener.disable();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecordingStarted = false;
        runAudioThread = false;
        if (cameraView != null) {
            cameraView.stopPreview();
            if (cameraDevice != null) {
                cameraDevice.setPreviewCallback(null);
                cameraDevice.release();
            }
            cameraDevice = null;
        }
        firstData = null;
        cameraDevice = null;
        cameraView = null;
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        videoTheEnd(recordFinish);
    }

    private long recordingTime = 0;// 记录下来的总时间
    private long startPauseTime = 0;// 暂停录制的开始时间
    private long stopPauseTime = 0;// 暂停录制的结束时间

    public void onRecordStart() {
        recording = true;
        if (!isRecordingStarted) {
            isRecordingStarted = true;
            initiateRecording();
        } else {
            stopPauseTime = System.currentTimeMillis();
            totalPauseTime = stopPauseTime - startPauseTime - ((long) (1.0 / (double) frameRate) * 1000);
            pausedTime += totalPauseTime;
        }
        recordBtn.setImageResource(R.drawable.stop);
        if (deviceOrientation == 0) {
            isRotateVideo = true;
        } else {
            isRotateVideo = false;
        }
        progressView.setCurrentState(ProgressView.State.START);
        chronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
        chronometer.setVisibility(View.VISIBLE);
        chronometer.start();
        cancelBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
    }

    public void onRecordPause() {
        recordBtn.setImageResource(R.drawable.btn_recorder_video_big);
        recording = false;
        chronometer.stop();
        recordingTime = SystemClock.elapsedRealtime()
                - chronometer.getBase();// 保存这次记录了的时间
        recordTimeList.add(chronometer.getText().toString());
//        if (isRecordingStarted) {
//            cancelBtn.setVisibility(View.VISIBLE);
           nextBtn.setVisibility(View.VISIBLE);
//        }
        progressView.setCurrentState(ProgressView.State.PAUSE);
        progressView.putTimeList((int) totalTime);
        startPauseTime = System.currentTimeMillis();
        // 保存本次录制的视频数据
        ArrayList<SavedFrames> tempList1 = (ArrayList<SavedFrames>) tempVideoList
                .clone();
        allVideoList.add(tempList1);
        tempVideoList.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recorder_record:
                if (!recordFinish) {
                    if (!recording) {
                        onRecordStart();
                    } else {
                        onRecordPause();
                    }
                }
                break;
            case R.id.recorder_next:
                //onRecordPause();
                saveRecorder();
                break;
            case R.id.recorder_flashlight:
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    return;
                }
                if (isFlashOn) {
                    isFlashOn = false;
                    flashIcon.setSelected(false);
                    cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                } else {
                    isFlashOn = true;
                    flashIcon.setSelected(true);
                    cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                }
                cameraDevice.setParameters(cameraParameters);
                break;
            case R.id.recorder_frontcamera:
                cameraSelection = ((cameraSelection == Camera.CameraInfo.CAMERA_FACING_BACK) ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
                isFrontCam = ((cameraSelection == Camera.CameraInfo.CAMERA_FACING_BACK) ? false : true);
                initCameraLayout();
                if (cameraSelection == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    flashIcon.setVisibility(View.GONE);
                else {
                    flashIcon.setVisibility(View.VISIBLE);
                    if (isFlashOn) {
                        cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        cameraDevice.setParameters(cameraParameters);
                    }
                }
                break;
            case R.id.recorder_rollback:
                if (!isRollbackSate) {
                    progressView.setCurrentState(ProgressView.State.ROLLBACK);
                    isRollbackSate = true;
                } else {
                    rollbackVideo();
                }
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    // 用于暂存录制的视频数据
    private ArrayList<SavedFrames> tempVideoList = new ArrayList<>();

    private LinkedList<String> recordTimeList = new LinkedList<>();
    private LinkedList<ArrayList<SavedFrames>> allVideoList = new LinkedList<>();
    private long rollbackTime = 0;// 回删的视频时长
    private boolean isRollbackSate = false;// 回删状态标识，点击"回删"标记为true，再次点击"回删"会删除最近的视频片段

    private void rollbackVideo() {
        if (allVideoList != null && allVideoList.size() > 0) {
            allVideoList.removeLast();
        }
        if (recordTimeList != null && recordTimeList.size() > 0) {
            recordTimeList.removeLast();
        }
        if (!recordTimeList.isEmpty()) {
            chronometer.setText(recordTimeList.getLast());
        } else {
            recordingTime = 0;
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setVisibility(View.GONE);
        }
        int frontTime = progressView.getLastTime();
        progressView.setCurrentState(ProgressView.State.DELETE);
        isRollbackSate = false;
        // 若进度条队列为空，设置回删按钮不可点击
        if (progressView.isTimeListEmpty()) {
            cancelBtn.setEnabled(false);
            totalTime = 0;
        }
        int lastTime = progressView.getLastTime();
        rollbackTime += (frontTime - lastTime);
        nextBtn.setEnabled(lastTime >= MIN_RECORD_TIME ? true : false);
    }

    public void videoTheEnd(boolean isSuccess) {
        releaseResources();
        if (fileVideoPath != null && fileVideoPath.exists() && !isSuccess)
            fileVideoPath.delete();
    }

    private void saveRecorder() {
        new AsyncTask<Void, Integer, Void>() {
            private Dialog savingDialog;
            private ProgressBar progressBar;
            private TextView percent;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                recordFinish = true;
                runAudioThread = false;
                savingDialog = new Dialog(RecorderActivity.this,
                        R.style.Dialog_loading_noDim);
                savingDialog.setCanceledOnTouchOutside(false);
                savingDialog
                        .setContentView(R.layout.activity_recorder_progress);
                progressBar = (ProgressBar) savingDialog
                        .findViewById(R.id.recorder_progress_bar);
                percent = (TextView) savingDialog
                        .findViewById(R.id.recorder_progress_percent);
                savingDialog.show();
            }

            private void getFirstCapture(byte[] data) {
                String captureBitmapPath = Util.createImagePath(RecorderActivity.this);
                YuvImage localYuvImage = new YuvImage(data, 17, previewWidth, previewHeight, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                FileOutputStream outStream = null;

                try {
                    File file = new File(captureBitmapPath);
                    if (!file.exists())
                        file.createNewFile();
                    localYuvImage.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 100, bos);
                    Bitmap localBitmap1 = BitmapFactory.decodeByteArray(bos.toByteArray(),
                            0, bos.toByteArray().length);
                    bos.close();
                    Matrix localMatrix = new Matrix();
                    if (cameraSelection == 0)
                        localMatrix.setRotate(90.0F);
                    else
                        localMatrix.setRotate(270.0F);
                    Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0,
                            localBitmap1.getHeight(),
                            localBitmap1.getHeight(),
                            localMatrix, true);
                    ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                    localBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, bos2);
                    outStream = new FileOutputStream(captureBitmapPath);
                    outStream.write(bos2.toByteArray());
                    outStream.close();
                    localBitmap1.recycle();
                    localBitmap2.recycle();
                    isFirstFrame = false;
                    publishProgress(50);
                    imagePath = captureBitmapPath;
                } catch (FileNotFoundException e) {
                    isFirstFrame = true;
                    e.printStackTrace();
                } catch (IOException e) {
                    isFirstFrame = true;
                    e.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                publishProgress(20);
                recorderThread.stopRecord();
//                Iterator<ArrayList<SavedFrames>> videoIterator = allVideoList
//                        .iterator();
//                SavedFrames savedFrames = null;
//                ArrayList<SavedFrames> videoList = new ArrayList<>();
//                while (videoIterator.hasNext()) {
//                    for (int i = 0; i < videoList.size(); i++) {
//                        savedFrames = videoList.get(i);
//                        recorderThread.putByteData(savedFrames);
//                    }
//                }
                //recordHelper.processByteData(videoList);
                publishProgress(60);
                if (firstData != null)
                    getFirstCapture(firstData);
                publishProgress(90);
                publishProgress(100);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBar.setProgress(values[0]);
                percent.setText(values[0] + "%");
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if(!isFinishing()) {
                    savingDialog.dismiss();
                    returnToCaller();
                }
            }
        }.execute();
    }

    private void returnToCaller() {
        try {
            Intent intent = new Intent(this, PublishActivity.class);
            intent.putExtra("path", strVideoPath);
            intent.putExtra("imagePath", imagePath);
            intent.putExtra(com.anna.duanzi.common.Constants.CATEGORY, 2);
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            //           recorderThread.stopRecord();
//            if (videoRecorder != null && isRecordingStarted) {
//                isRecordingStarted = false;
//                releaseResources();
//            }
            finish();
        }
    }

    private void releaseResources() {
        if (recorderThread != null) {
            recorderThread.finish();
        }
        try {
            if (videoRecorder != null) {
                videoRecorder.stop();
                videoRecorder.release();
            }
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        lastSavedframe = null;
        videoRecorder = null;
       // recordHelper = null;
    }
}
