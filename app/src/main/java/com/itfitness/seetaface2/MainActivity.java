package com.itfitness.seetaface2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.itfitness.seetaface2.engine.FaceEngine;
import com.itfitness.seetaface2.facefind.FaceFindActivity;
import com.itfitness.seetaface2.facelogin.FaceLoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化引擎
        FaceEngine.init();
        setClickListener(R.id.bt_one);
        setClickListener(R.id.bt_two);
        setClickListener(R.id.bt_three);
        setClickListener(R.id.bt_four);
        setClickListener(R.id.bt_five);
    }
    private void setClickListener(int id){
        findViewById(id).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(!FaceEngine.INIT_SUCCESS){
            Toast.makeText(this, "引擎尚未加载完毕请稍等", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()){
            case R.id.bt_one:
                gotoActivity(FaceDetectorActivity.class);
                break;
            case R.id.bt_two:
                gotoActivity(PointDetectorActivity.class);
                break;
            case R.id.bt_three:
                gotoActivity(FaceRecognizerActivity.class);
                break;
            case R.id.bt_four:
                gotoActivity(FaceLoginActivity.class);
                break;
            case R.id.bt_five:
                gotoActivity(FaceFindActivity.class);
                break;
        }
    }
    private void gotoActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放
        //用了一个引擎后就不需要释放了
        if(FaceEngine.FACEDETECTOR!=null){
            FaceEngine.FACEDETECTOR.dispose();
        }
        if(FaceEngine.FACERECOGNIZER!=null){
            FaceEngine.FACERECOGNIZER.Clear();//清空注册的人脸
            FaceEngine.FACERECOGNIZER.dispose();
        }
        if(FaceEngine.POINTDETECTOR!=null){
            FaceEngine.POINTDETECTOR.dispose();
        }
    }
}

