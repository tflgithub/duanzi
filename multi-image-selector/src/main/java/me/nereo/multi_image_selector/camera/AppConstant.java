package me.nereo.multi_image_selector.camera;

/**
 * Created by tfl on 2016/11/2.
 */
public class AppConstant {
    //WHAT 0-10 预留值
    public interface WHAT {
        int SUCCESS = 0;
        int FAILURE = 1;
        int ERROR = 2;
    }

    public interface KEY {
        String IMG_PATH = "IMG_PATH";
        String VIDEO_PATH = "VIDEO_PATH";
        String PIC_WIDTH = "PIC_WIDTH";
        String PIC_HEIGHT = "PIC_HEIGHT";
    }

    public interface REQUEST_CODE {
        int CAMERA = 0;
        int CROP_PHOTO = 1;
    }

    public interface RESULT_CODE {
        int RESULT_OK = -1;
        int RESULT_CANCELED = 0;
        int RESULT_ERROR = 1;
    }

    public interface CLIP_TYPE {
        int ROUND = 1;
        int FOUND = 2;
    }

}
