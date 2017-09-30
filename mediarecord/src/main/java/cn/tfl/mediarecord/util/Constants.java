package cn.tfl.mediarecord.util;

import android.os.Environment;

/**
 * Created by Sourab Sharma (sourab.sharma@live.in)  on 1/19/2016.
 */
public class Constants {

    public final static String FILE_START_NAME = "kflx_";
    public final static String VIDEO_EXTENSION = ".mp4";
    public final static String IMAGE_EXTENSION = ".jpg";
    public final static String WATERMARK_NAME = "video_watermark";
    public final static String WATERMARK_EXTENSION = ".png";
    public final static String DCIM_FOLDER = "/DCIM";
    public final static String IMAGE_FOLDER = "/image";
    public final static String VIDEO_FOLDER = "/video";
    public final static String TEMP_FOLDER = "/Temp";


    public final static String TEMP_FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + Constants.DCIM_FOLDER + Constants.VIDEO_FOLDER + Constants.TEMP_FOLDER;

    public final static int RESOLUTION_HIGH_VALUE = 2;
    public final static int RESOLUTION_MEDIUM_VALUE = 1;
    public final static int RESOLUTION_LOW_VALUE = 0;

    public final static int OUTPUT_WIDTH = 480;
    public final static int OUTPUT_HEIGHT = 480;
    public static final int MIN_RECORD_TIME = 3000;
    public static final int MAX_RECORD_TIME = 1000 * 60 * 30;
}
