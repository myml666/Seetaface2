package com.itfitness.seetaface2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.itfitness.seetaface2.facelogin.FaceLoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setClickListener(R.id.bt_one);
        setClickListener(R.id.bt_two);
        setClickListener(R.id.bt_three);
        setClickListener(R.id.bt_four);
    }
    private void setClickListener(int id){
        findViewById(id).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
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
        }
    }
    private void gotoActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }
}

