package com.itfitness.seetaface2.engine;

import android.os.Environment;

import com.seeta.sdk.FaceDetector2;
import com.seeta.sdk.FaceRecognizer2;
import com.seeta.sdk.PointDetector2;

import java.io.File;

public class FaceEngine {
    public static FaceDetector2 FACEDETECTOR;
    public static PointDetector2 POINTDETECTOR;
    public static FaceRecognizer2 FACERECOGNIZER;
    public static boolean INIT_SUCCESS = false;//是否加载成功
    /**
     * 初始化引擎
     */
    public static void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FACEDETECTOR = new FaceDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceDetector2.0.ats");
                POINTDETECTOR = new PointDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaPointDetector2.0.pts5.ats");  //特征点
                FACERECOGNIZER = new FaceRecognizer2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceRecognizer2.0.ats");  //人脸匹配
                INIT_SUCCESS = true;
            }
        }).start();
    }
}
