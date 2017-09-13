package com.anna.duanzi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by tfl on 2017/8/28.
 */

public class ZipUtils {
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
    /**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @param isDelete
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static String upZipFile(File zipFile, String folderPath, boolean isDelete)
    {
        String strZipName = zipFile.getName();
        folderPath += "/" + strZipName.substring(0, strZipName.lastIndexOf("."));
        File desDir = new File(folderPath);
        if (!desDir.exists())
        {
            desDir.mkdirs();
        }
        ZipFile zf;
        try
        {
            zf = new ZipFile(zipFile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();)
            {
                ZipEntry entry = ((ZipEntry)entries.nextElement());
                if(entry.isDirectory())
                {
                    String dirstr = entry.getName();
                    dirstr = new String(dirstr.getBytes("UTF-8"));
                    File f=new File(dirstr);
                    f.mkdir();
                    continue;
                }
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("UTF-8"));
                File desFile = new File(str);
                if (!desFile.exists())
                {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists())
                    {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
            }

            if (isDelete)
            {
                zipFile.delete();
            }
        } catch (ZipException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return desDir.getAbsolutePath();
    }
}
