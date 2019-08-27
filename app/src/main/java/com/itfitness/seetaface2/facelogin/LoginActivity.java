package com.itfitness.seetaface2.facelogin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.itfitness.seetaface2.R;
import com.itfitness.seetaface2.camera.FaceCameraView;
import com.itfitness.seetaface2.utils.ConvertUtil;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import java.io.ByteArrayOutputStream;
public class LoginActivity extends AppCompatActivity{
    private FaceCameraView faceCameraView;
    private boolean isScanning = false;
    private int failedCount = 0;//失败次数
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    failedCount++;
                    if(failedCount>=5){
                        Toast.makeText(LoginActivity.this, "人脸不匹配，登录失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    isScanning = false;
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    private void initView(){
        faceCameraView = (FaceCameraView) findViewById(R.id.camera2);
        faceCameraView.setPreviewCallback(new FaceCameraView.PreviewCallback() {
            @Override
            public void onPreview(final byte[] data, final Camera camera) {
                if(FaceLoginActivity.FACEDETECTOR!=null&&FaceLoginActivity.FACERECOGNIZER!=null&&FaceLoginActivity.POINTDETECTOR!=null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //识别中不处理其他帧数据
                            if (!isScanning) {
                                isScanning = true;
                                try {
                                    //获取Camera预览尺寸
                                    Camera.Size size = camera.getParameters().getPreviewSize();
                                    //将帧数据转为bitmap
                                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                                    if (image != null) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                        //纠正图像的旋转角度问题
                                        Matrix m = new Matrix();
                                        m.setRotate(-90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                        Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
                                        SeetaImageData loginSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
                                        SeetaRect[] faceRects = FaceLoginActivity.FACEDETECTOR.Detect(loginSeetaImageData);
                                        if(faceRects.length>0){
                                            //获取人脸区域（这里只有一个所以取0）
                                            SeetaRect faceRect = faceRects[0];
                                            SeetaPointF[] seetaPoints = FaceLoginActivity.POINTDETECTOR.Detect(loginSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                            float[] similarity = new float[1];//用来存储人脸相似度值
                                            FaceLoginActivity.FACERECOGNIZER.Recognize(loginSeetaImageData, seetaPoints, similarity);//匹配
                                            if(similarity[0]>0.7){
                                                handler.sendEmptyMessage(1);
                                            }else {
                                                handler.sendEmptyMessage(0);
                                            }
                                        }else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, "请保持手机不要晃动", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            isScanning = false;
                                        }
                                    }
                                } catch (Exception ex) {
                                    isScanning = false;
                                }
                            }
                        }
                    }
                    ).start();
                }
            }
        });
    }
}
