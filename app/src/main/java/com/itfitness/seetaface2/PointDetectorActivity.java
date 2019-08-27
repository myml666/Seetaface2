package com.itfitness.seetaface2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itfitness.seetaface2.utils.ConvertUtil;
import com.seeta.sdk.FaceDetector2;
import com.seeta.sdk.PointDetector2;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

import java.io.File;

/**
 * 特征点检测
 */
public class PointDetectorActivity extends AppCompatActivity {
    private Button mBt;
    private ImageView mImg;
    private FaceDetector2 faceDetector;
    private PointDetector2 pointDetector;
    private SeetaRect[] faceRects;
    private Bitmap bitmap;
    private SeetaImageData seetaImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将模型拷贝到SD卡中
        //FileUtil.CopyAssets(this,"SeetaFaceDetector2.0.ats",Environment.getExternalStorageDirectory()+ File.separator+"SeetaFaceDetector2.0.ats");
        initView();
        initFace();
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果图像中检测出人脸了再进行绘制
                if(faceRects.length>0){
                    //这里必须进行copy否则修改不了
                    Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    //利用Bitmap创建Canvas，为了在图像上绘制人脸区域
                    Canvas canvas = new Canvas(copy);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    //绘制出所有的检测出来的人脸的区域
                    for(int i = 0 ; i < faceRects.length ; i++){
                        SeetaRect faceRect = faceRects[i];
                        SeetaPointF[] seetaPoints = pointDetector.Detect(seetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                        if(seetaPoints.length>0){
                            for(SeetaPointF seetaPoint:seetaPoints){
                                //绘制特征点
                                canvas.drawCircle((float) seetaPoint.x,(float) seetaPoint.y,5,paint);
                            }
                        }
                    }
                    mImg.setImageBitmap(copy);
                }
            }
        });
    }

    /**
     * 初始化人脸检测器
     */
    private void initFace() {
        //初始化检测器（参数是模型在SD卡的位置）
        faceDetector = new FaceDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceDetector2.0.ats");
        pointDetector = new PointDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaPointDetector2.0.pts5.ats");  //特征点
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heying);
        //利用SeetaFace2提供的转换方法获取SeetaRect（人脸识别结果）
        seetaImageData = ConvertUtil.ConvertToSeetaImageData(bitmap);
        faceRects = faceDetector.Detect(seetaImageData);
    }

    private void initView() {
        mBt = findViewById(R.id.bt_face);
        mImg = findViewById(R.id.img);
    }
}
