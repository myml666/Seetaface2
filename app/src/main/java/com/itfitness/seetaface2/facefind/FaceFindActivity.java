package com.itfitness.seetaface2.facefind;

import android.annotation.SuppressLint;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.itfitness.seetaface2.R;
import com.itfitness.seetaface2.engine.FaceEngine;
import com.itfitness.seetaface2.utils.ConvertUtil;
import com.itfitness.seetaface2.widget.FaceCameraView;
import com.itfitness.seetaface2.widget.FaceRectView;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaRect;

import java.io.ByteArrayOutputStream;

public class FaceFindActivity extends AppCompatActivity {
    private FaceCameraView faceCameraView;
    private FaceRectView faceRectView;
    private boolean isScaing = false;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            faceRectView.setFaceDatas((SeetaRect[]) msg.obj);
            isScaing = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facefind);
        initView();
        initPreViewCallback();
    }

    private void initPreViewCallback() {
        faceCameraView.setPreviewCallback(new FaceCameraView.PreviewCallback() {
            @Override
            public void onPreview(final byte[] data, final Camera camera) {
                if (FaceEngine.FACEDETECTOR != null && FaceEngine.FACERECOGNIZER != null && FaceEngine.POINTDETECTOR != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //识别中不处理其他帧数据
                            if (!isScaing) {
                                isScaing = true;
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
                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(loginSeetaImageData);
                                        if(faceRects.length>0){
                                            Message obtain = Message.obtain();
                                            obtain.obj = faceRects;
                                            handler.sendMessage(obtain);
                                        }
                                    }
                                } catch (Exception ex) {
                                    isScaing = false;
                                }
                            }
                        }
                    }
                    ).start();
                }
            }
        });
    }

    private void initView() {
        faceCameraView = findViewById(R.id.camera_findface);
        faceRectView = findViewById(R.id.facerect);
    }
}
