package com.anna.duanzi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * Created by tfl on 2016/10/17.
 */
public class BookPageFactory {

    private File book_file = null;
    private MappedByteBuffer m_mbBuf = null;
    private int m_mbBufLen = 0;
    private int m_mbBufBegin = 0;
    private int m_mbBufEnd = 0;
    private String m_strCharsetName = "GBK";
    private Bitmap m_book_bg = null;
    private int mWidth;
    private int mHeight;

    private Vector<String> m_lines = new Vector<String>();

    private int m_fontSize = 28;
    private int m_textColor = Color.BLACK;
    private int m_backColor = Color.WHITE; // 背景颜色
    private int marginWidth = 15; // 左右与边缘的距离
    private int marginHeight = 20; // 上下与边缘的距离
    private int mLineCount; // 每页可以显示的行数
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private boolean m_isfirstPage,m_islastPage;

    // private int m_nLineSpaceing = 5;

    private Paint mPaint;

    public BookPageFactory(int w, int h) {
        // TODO Auto-generated constructor stub
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);//设置绘制文字的对齐方向
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / m_fontSize); // 可显示的行数
    }

    public void openbook(String strFilePath) throws IOException {
        book_file = new File(strFilePath);
        long lLen = book_file.length();
        m_mbBufLen = (int) lLen;

        /*
         * 内存映射文件能让你创建和修改那些因为太大而无法放入内存的文件。有了内存映射文件，你就可以认为文件已经全部读进了内存，
         * 然后把它当成一个非常大的数组来访问。这种解决办法能大大简化修改文件的代码。
         *
        * fileChannel.map(FileChannel.MapMode mode, long position, long size)将此通道的文件区域直接映射到内存中。但是，你必
        * 须指明，它是从文件的哪个位置开始映射的，映射的范围又有多大
        */
        FileChannel fc=new RandomAccessFile(book_file, "r").getChannel();

        //文件通道的可读可写要建立在文件流本身可读写的基础之上
        m_mbBuf =fc.map(FileChannel.MapMode.READ_ONLY, 0, lLen);
    }


    protected byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0, b1;
        if (m_strCharsetName.equals("UTF-16LE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if (m_strCharsetName.equals("UTF-16BE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = m_mbBuf.get(i + j);
        }
        return buf;
    }


    //读取上一段落
    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        //共读取了多少字符
        int nParaSize = i - nStart;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            //将已读取的字符放入数组
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }

    protected Vector<String> pageDown() {
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
            m_mbBufEnd += paraBuf.length;//结束位置后移paraBuf.length
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);//通过decode指定的编码格式将byte[]转换为字符串
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strReturn = "";

            //去除将字符串中的特殊字符
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                //计算每行可以显示多少个字符
                //获益匪浅
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);//截取从nSize开始的字符串
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            //当前页没显示完
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn)
                            .getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    protected void pageUp() {
        if (m_mbBufBegin < 0)
            m_mbBufBegin = 0;
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && m_mbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(m_mbBufBegin);
            m_mbBufBegin -= paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");

            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }
        while (lines.size() > mLineCount) {
            try {
                m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        m_mbBufEnd = m_mbBufBegin;
        return;
    }

    public void prePage() throws IOException {
        if (m_mbBufBegin <= 0) {
            //第一页
            m_mbBufBegin = 0;
            m_isfirstPage=true;
            return;
        }else m_isfirstPage=false;
        m_lines.clear();//Removes all elements from this vector, leaving it empty.
        pageUp();
        m_lines = pageDown();
    }

    public void nextPage() throws IOException {
        if (m_mbBufEnd >= m_mbBufLen) {
            m_islastPage=true;
            return;
        }else m_islastPage=false;
        m_lines.clear();
        m_mbBufBegin = m_mbBufEnd;
        m_lines = pageDown();
    }

    public void onDraw(Canvas c) {
        if (m_lines.size() == 0)
            m_lines = pageDown();
        if (m_lines.size() > 0) {
            if (m_book_bg == null)
                c.drawColor(m_backColor);
            else
                c.drawBitmap(m_book_bg, 0, 0, null);
            int y = marginHeight;
            for (String strLine : m_lines) {
                y += m_fontSize;
                //从（x,y）坐标将文字绘于手机屏幕
                c.drawText(strLine, marginWidth, y, mPaint);
            }
        }
        //计算百分比（不包括当前页）并格式化
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
        DecimalFormat df = new DecimalFormat("1");
        String strPercent = "已阅读"+df.format(fPercent * 100) + "%";

        //计算999.9%所占的像素宽度
        int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
        c.drawText(strPercent, mWidth - nPercentWidth-50, mHeight - 20, mPaint);
    }

    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    public boolean isfirstPage() {
        return m_isfirstPage;
    }
    public boolean islastPage() {
        return m_islastPage;
    }
}
