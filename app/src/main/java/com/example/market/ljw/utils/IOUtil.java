package com.example.market.ljw.utils;


import com.example.market.ljw.common.http.HttpGroup;
import com.example.market.ljw.exceptions.AppException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * io工具类
 * @author yepeng
 *
 */
public class IOUtil
{
	
    private static int bufferSize = 38384;
	
    public static interface ProgressListener
    {

        public abstract void notify(int i, int j);
    }


    public IOUtil()
    {
    }

    public static byte[] readAsBytes(InputStream inputstream, ProgressListener progresslistener)
        throws AppException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
        	byte[]  buf = new byte[bufferSize];
        	int len = 0;
        	int progress = 0;
        	while((len = inputstream.read(buf)) !=-1){
        		os.write(buf,0,len);
        		progress += len;
        		progresslistener.notify(progress,len);
        	}
        }catch(Exception e){
        	throw AppException.io(e);
        }finally{
        	try {
				os.close();
				inputstream.close();
			} catch (IOException e) {
				throw AppException.io(e);
			}
        }
        return os.toByteArray();
    }

    public static String readAsString(InputStream inputstream, String s)
        throws Exception
    {
        return readAsString(inputstream, s, null);
    }

    public static String readAsString(InputStream inputstream, String s, ProgressListener progresslistener)
        throws Exception
    {
        String s1;
        try
        {
            s1 = new String(readAsBytes(inputstream, progresslistener), s);
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            s1 = null;
        }
        return s1;
    }
    
    public static void readAsFile(InputStream inputstream, FileOutputStream fileoutputstream, ProgressListener progresslistener, HttpGroup.StopController stopcontroller)
            throws Exception
        {
             try{
             	byte[]  buf = new byte[bufferSize];
             	int len = 0;
             	int progress = 0;
             	while(!stopcontroller.isStop() && (len = inputstream.read(buf)) !=-1){
             		fileoutputstream.write(buf,0,len);
             		if(progresslistener != null){
             			progress += len;
             			progresslistener.notify(progress,len);
             		}
             	}

                if(stopcontroller.isStop()){
                    throw AppException.io(new Exception());
                }
             }catch(Exception e){
             	throw AppException.io(e);
             }finally{
            	fileoutputstream.close();
             	inputstream.close();
             }
        }

}
