package com.example.market.ljw.core.utils;

/**
 * Created by GYH on 2014/6/28.
 */

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Writer;

public class FileService {

    private String pathName = "/sdcard/ljw/";
//    private String fileName = "crmuser.txt";
//    private String fileName = "strcustomerlist.txt";
    private String fileName="";

    public FileService(String fileName){
        this.fileName=fileName;
    }

    /**
     * 获取数据
     * */
    public String  getFromSDCard(){
        String readedStr = "";
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        File path = new File(pathName);
        File file = new File(pathName + fileName);
        if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + pathName);
            return "";
        }
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedReader br= new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String tmp;

            while((tmp=br.readLine())!=null){
                readedStr+=tmp;
            }
            br.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readedStr;
    }

    /**
     * 导出数据
     * */
    public void saveToSDCard(String content) {
        try{
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.d("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            content ="服务时间："+content;
            String xittst = "系统时间："+Utils.getCurrentDate();
            FileWriter fileWriter = new FileWriter(new File(pathName+ fileName), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            // attention 换行需要以下两个一起才会在.txt文件中生效
            bufferWriter.write(content + "\r\n");
            bufferWriter.newLine();
            bufferWriter.write(xittst + "\r\n");
            bufferWriter.newLine();
            bufferWriter.close();
            fileWriter.close();
//            RandomAccessFile raf = new RandomAccessFile(file, "rw");
//            raf.seek(file.length());
//            raf.write(Integer.parseInt("m"));
//            raf.write(content.getBytes());
//            raf.write(Integer.parseInt("m"));
//            raf.write(xittst.getBytes());
//            raf.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }

    /**
     * 判断文件是否存在
     * */
    public boolean ishasFile(){
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return false;
        }
        File path = new File(pathName);
        File file = new File(pathName + fileName);
        if (!path.exists()) {
            Log.d("TestFile", "Create the path:" + pathName);
            return false;
        }
        if (!file.exists()) {
            Log.d("TestFile", "Create the file:" + fileName);
            return false;
        }
        return true;
    }

    public void saveToRom(String name, String content) {
        // TODO Auto-generated method stub
    }

}
