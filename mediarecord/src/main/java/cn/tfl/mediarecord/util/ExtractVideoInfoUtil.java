package cn.tfl.mediarecord.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/29.
 */

public class ExtractVideoInfoUtil {

    public static List<Bitmap> getVideoThumbnailsInfoForEdit(String videoPath, int thumbnailsCount) {
        List<Bitmap> bitmapList = new ArrayList<>();
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoPath);
        long startPosition = 0;
        long endPosition = Long.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        long interval = (endPosition - startPosition) / (thumbnailsCount - startPosition);
        for (int i = 0; i < thumbnailsCount; i++) {
            long time = startPosition + interval * i;
            bitmapList.add(extractFrame(metadataRetriever, time));
        }
        metadataRetriever.release();
        return bitmapList;
    }

    private static Bitmap extractFrame(MediaMetadataRetriever metadataRetriever, long time) {
        Bitmap bitmap = metadataRetriever.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }
}
