package com.itfitness.seetaface2.facelogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.itfitness.seetaface2.R;
import com.seeta.sdk.FaceDetector2;
import com.seeta.sdk.FaceRecognizer2;
import com.seeta.sdk.PointDetector2;

import java.io.File;

public class FaceLoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static FaceDetector2 FACEDETECTOR;
    public static PointDetector2 POINTDETECTOR;
    public static FaceRecognizer2 FACERECOGNIZER;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facelogin);
        initModule();
        setClickListener(R.id.bt_initfacedata);
        setClickListener(R.id.bt_facelogin);
    }

    private void initModule() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FACEDETECTOR = new FaceDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceDetector2.0.ats");
                POINTDETECTOR = new PointDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaPointDetector2.0.pts5.ats");  //特征点
                FACERECOGNIZER = new FaceRecognizer2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceRecognizer2.0.ats");  //人脸匹配
            }
        }).start();
    }

    private void setClickListener(int id){
        findViewById(id).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_initfacedata:
                //录入人脸信息
                gotoActivity(FaceInitActivity.class);
                break;
            case R.id.bt_facelogin:
                //人脸登录
                gotoActivity(LoginActivity.class);
                break;
        }
    }
    private void gotoActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空人脸数据
        if(FACERECOGNIZER!=null){
            FACERECOGNIZER.Clear();
            FACERECOGNIZER.dispose();
        }
        if(POINTDETECTOR!=null){
            POINTDETECTOR.dispose();
        }
        if(FACEDETECTOR!=null){
            FACEDETECTOR.dispose();
        }
    }
}
