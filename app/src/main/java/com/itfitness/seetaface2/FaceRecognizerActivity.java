package com.itfitness.seetaface2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.itfitness.seetaface2.engine.FaceEngine;
import com.itfitness.seetaface2.utils.ConvertUtil;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

/**
 * 人脸匹配
 */
public class FaceRecognizerActivity extends AppCompatActivity {
    private Button mBt;
    private ImageView mImg;
    private int registerIndex;
    private boolean moduleFlag = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mImg.setImageBitmap((Bitmap) msg.obj);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facerecognizer);
        //将模型拷贝到SD卡中
        initView();
        initFace();
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果人脸模型初始化成功了再操作
                if(moduleFlag){
                    Toast.makeText(FaceRecognizerActivity.this, "匹配中请稍等", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //加载进行匹配的图像
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tang08);
                            //这里必须进行copy否则修改不了
                            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                            //利用Bitmap创建Canvas，为了在图像上绘制人脸区域
                            Canvas canvas = new Canvas(copy);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setColor(Color.RED);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(3);
                            SeetaImageData seetaImageData = ConvertUtil.ConvertToSeetaImageData(bitmap);
                            //人脸检测
                            SeetaRect[] detects = FaceEngine.FACEDETECTOR.Detect(seetaImageData);
                            if(detects.length>0){
                                //将所有检测到的人脸与注册到数据库的人脸进行匹配
                                for(int i = 0 ; i < detects.length ; i++){
                                    SeetaRect faceRect = detects[i];
                                    SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(seetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                    float[] similarity = new float[1];//用来存储人脸相似度值
                                    int targetIndex = FaceEngine.FACERECOGNIZER.Recognize(seetaImageData, seetaPoints, similarity);//匹配
                                    Log.e("人脸匹配",targetIndex+"======="+registerIndex+"====="+similarity[0]);
                                    //如果匹配值大于0.7说明是同一个人
                                    if(similarity[0]>0.7){
                                        //将匹配出来的人脸区域绘制出来
                                        Rect rect = new Rect(faceRect.x,faceRect.y,faceRect.x+faceRect.width,faceRect.y+faceRect.height);
                                        canvas.drawRect(rect,paint);
                                    }
                                }
                                //通知主线程更新UI
                                Message obtain = Message.obtain();
                                obtain.obj = copy;
                                handler.sendMessage(obtain);
                            }
                        }
                    }).start();
                }else {
                    Toast.makeText(FaceRecognizerActivity.this, "人脸模型尚未初始化成功请稍等", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化人脸检测器
     */
    private void initFace() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap registBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tang01);
                //利用SeetaFace2提供的转换方法获取SeetaRect（人脸识别结果）
                SeetaImageData RegistSeetaImageData = ConvertUtil.ConvertToSeetaImageData(registBitmap);
                SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(RegistSeetaImageData);
                if(faceRects.length>0){
                    //获取人脸区域（这里只有一个所以取0）
                    SeetaRect faceRect = faceRects[0];
                    SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(RegistSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                    registerIndex = FaceEngine.FACERECOGNIZER.Register(RegistSeetaImageData, seetaPoints);//将人脸注册到SeetaFace2数据库
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FaceRecognizerActivity.this, "人脸模型初始化成功", Toast.LENGTH_SHORT).show();
                    }
                });
                //模型加载标记
                moduleFlag = true;
            }
        }).start();
    }

    private void initView() {
        mBt = findViewById(R.id.bt_face);
        mImg = findViewById(R.id.img);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放
        //用了一个引擎后就不需要释放了
//        if(FaceEngine.FACEDETECTOR!=null){
//            FaceEngine.FACEDETECTOR.dispose();
//        }
        if(FaceEngine.FACERECOGNIZER!=null){
            FaceEngine.FACERECOGNIZER.Clear();//清空注册的人脸
//            FaceEngine.FACERECOGNIZER.dispose();
        }
//        if(FaceEngine.POINTDETECTOR!=null){
//            FaceEngine.POINTDETECTOR.dispose();
//        }
    }
}
