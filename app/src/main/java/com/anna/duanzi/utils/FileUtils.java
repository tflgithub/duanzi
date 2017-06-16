package com.anna.duanzi.utils;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tfl on 2016/10/17.
 */
public class FileUtils {

    private String SDCardRoot;
    private String SDStateString;
    private static FileUtils fileUtils;
    private String FileCacheRoot;

    public static FileUtils getInstance() {
        if (fileUtils == null) {
            fileUtils = new FileUtils();
        }
        return fileUtils;
    }


    public FileUtils() {
        //得到当前外部存储设备的目录
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        // 获取扩展SD卡设备状态
        SDStateString = Environment.getExternalStorageState();
    }


    public void setFileCacheRoot(String dir) {
        FileCacheRoot = dir;
    }

    public String getFileCacheRoot() {
        return FileCacheRoot;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param dir      目录路径
     * @param fileName
     * @return
     * @throws IOException
     **/
    public File createFileInSDCard(String dir, String fileName) throws IOException {
        File file;
        if (FileCacheRoot==null) {
            file = new File(SDCardRoot + dir + File.separator + fileName);
        } else {
            file = new File(FileCacheRoot+ File.separator + fileName);
        }
        file.createNewFile();
        return file;
    }


    /**
     * 在SD卡上创建目录
     *
     * @param dir 目录路径
     * @return
     **/
    public File createSDDir(String dir) {
        File dirFile;
        if (FileCacheRoot==null) {
            dirFile = new File(SDCardRoot + dir + File.separator);
        } else {
            dirFile = new File(FileCacheRoot+File.separator);
        }

        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param dir      目录路径
     * @param fileName 文件名称
     * @return
     **/

    public boolean isFileExist(String dir, String fileName) {
        File file;
        if (FileCacheRoot.isEmpty()) {
            file = new File(SDCardRoot + dir + File.separator + fileName);
        } else {
            file = new File(FileCacheRoot + File.separator+ fileName);
        }
        return file.exists();
    }

    /***
     * 获取文件的路径
     *
     * @param dir
     * @param fileName
     * @return
     **/
    public String getFilePath(String dir, String fileName) {
        if (FileCacheRoot.isEmpty()) {
            return SDCardRoot + dir + File.separator + fileName;
        } else {
            return FileCacheRoot + File.separator + fileName;
        }
    }


    /***
     * 获取SD卡的剩余容量,单位是Byte
     *
     * @return
     **/
    public long getSDAvailableSize() {
        if (SDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File pathFile = android.os.Environment.getExternalStorageDirectory();
            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
            // 获取SDCard上每个block的SIZE
            long nBlocSize = statfs.getBlockSize();
            // 获取可供程序使用的Block的数量
            long nAvailaBlock = statfs.getAvailableBlocks();
            // 计算 SDCard 剩余大小Byte
            long nSDFreeSize = nAvailaBlock * nBlocSize;
            return nSDFreeSize;
        }
        return 0;
    }

    /**
     * 将一个字节数组数据写入到SD卡中
     **/
    public boolean write2SD(String dir, String fileName, byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        OutputStream output = null;
        try {
            // 拥有可读可写权限，并且有足够的容量
            if (SDStateString.equals(android.os.Environment.MEDIA_MOUNTED) && bytes.length < getSDAvailableSize()) {
                File file = null;
                createSDDir(dir);
                file = createFileInSDCard(dir, fileName);
                output = new BufferedOutputStream(new FileOutputStream(file));
                output.write(bytes);
                output.flush();
                return true;
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

