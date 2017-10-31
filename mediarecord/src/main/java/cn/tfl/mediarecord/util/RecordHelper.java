package cn.tfl.mediarecord.util;

import android.util.Log;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameRecorder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.tfl.mediarecord.FFmpegFrameRecorder;
import cn.tfl.mediarecord.SavedFrames;

/**
 * Created by Administrator on 2017/10/24.
 */

public class RecordHelper {

    private FFmpegFrameRecorder mVideoRecorder;
    private Frame yuvFrame;
    private FFmpegFrameFilter filterRotateVideoFrontCam = null;
    private FFmpegFrameFilter filterRotateVideoBackCam = null;
    private FFmpegFrameFilter filterCropVideoBackCam = null;
    private FFmpegFrameFilter filterCropVideoFrontCam = null;

    private int previewWidth = 0, previewHeight = 0;

    public RecordHelper(FFmpegFrameRecorder videoRecorder, int previewWidth, int previewHeight) {
        try {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.yuvFrame = new Frame(previewWidth, previewHeight, Frame.DEPTH_UBYTE, 2);
            this.mVideoRecorder = videoRecorder;
            setFilters();
        } catch (Exception e) {
        }
    }


    public void processByteData(ArrayList<SavedFrames> list) {
        if (list != null && list.size() > 0) {
            for (SavedFrames savedFrame : list) {
                mVideoRecorder.setTimestamp(savedFrame.getTimeStamp());
                processBytesUsingFrame(savedFrame);
            }
        }
    }

    private void processBytesUsingFrame(SavedFrames frame) {
        ((ByteBuffer) yuvFrame.image[0].position(0)).put(frame.getFrameBytesData());
        Frame pulledFrame = null;
        if (frame.isRotateVideo()) {
            if (frame.isFrontCam()) {
                try {
                    filterRotateVideoFrontCam.push(yuvFrame);
                    while ((pulledFrame = filterRotateVideoFrontCam.pull()) != null) {
                        mVideoRecorder.record(pulledFrame);
                    }
                } catch (FrameFilter.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    filterRotateVideoBackCam.push(yuvFrame);
                    while ((pulledFrame = filterRotateVideoBackCam.pull()) != null) {
                        mVideoRecorder.record(pulledFrame);
                    }
                } catch (FrameFilter.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (frame.isFrontCam()) {
                try {
                    filterCropVideoFrontCam.push(yuvFrame);
                    while ((pulledFrame = filterCropVideoFrontCam.pull()) != null) {
                        mVideoRecorder.record(pulledFrame);
                    }
                } catch (FrameFilter.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    filterCropVideoBackCam.push(yuvFrame);
                    while ((pulledFrame = filterCropVideoBackCam.pull()) != null) {
                        mVideoRecorder.record(pulledFrame);
                    }
                } catch (FrameFilter.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setFilters() {
        String cropVideo = "crop=w=" + Constants.OUTPUT_WIDTH + ":h=" + Constants.OUTPUT_HEIGHT + ":x=" + 0 + ":y=" + 0;
        String rotateVideoFrontCam = "transpose=cclock,hflip," + cropVideo;
        String rotateVideoBackCam = "transpose=clock," + cropVideo;
        String cropVideoFrontCam = "hflip," + cropVideo;
        filterRotateVideoFrontCam = new FFmpegFrameFilter(rotateVideoFrontCam, previewWidth, previewHeight);
        filterRotateVideoFrontCam.setPixelFormat(avutil.AV_PIX_FMT_NV21);
        filterRotateVideoBackCam = new FFmpegFrameFilter(rotateVideoBackCam, previewWidth, previewHeight);
        filterRotateVideoBackCam.setPixelFormat(avutil.AV_PIX_FMT_NV21);
        filterCropVideoBackCam = new FFmpegFrameFilter(cropVideo, previewWidth, previewHeight);
        filterCropVideoBackCam.setPixelFormat(avutil.AV_PIX_FMT_NV21);
        filterCropVideoFrontCam = new FFmpegFrameFilter(cropVideoFrontCam, previewWidth, previewHeight);
        filterCropVideoFrontCam.setPixelFormat(avutil.AV_PIX_FMT_NV21);
        try {
            filterRotateVideoFrontCam.start();
            filterRotateVideoBackCam.start();
            filterCropVideoBackCam.start();
            filterCropVideoFrontCam.start();
        } catch (FrameFilter.Exception e) {
            Log.e("RecordThread setFilter", e.getMessage());
        }
    }
}
