package com.itfitness.seetaface2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itfitness.seetaface2.R;
import com.seeta.sdk.SeetaImageData;

import java.nio.ByteBuffer;

public class ConvertUtil {
    /**
     * 转换生成SeetaImageData
     * @param bitmap
     * @return
     */
    public static SeetaImageData ConvertToSeetaImageData(Bitmap bitmap) {
        Bitmap bmp_src = bitmap.copy(Bitmap.Config.ARGB_8888, true); // true is RGBA
        //SeetaImageData大小与原图像一致，但是通道数为3个通道即BGR
        SeetaImageData imageData = new SeetaImageData(bmp_src.getWidth(), bmp_src.getHeight(), 3);
        imageData.data = getPixelsBGR(bmp_src);
        return imageData;
    }

    /**
     * 提取图像中的BGR像素
     * @param image
     * @return
     */
    public static byte[] getPixelsBGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length/4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length/4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];        //B
            pixels[i * 3 + 1] = temp[i * 4 + 1];    //G
            pixels[i * 3 + 2] = temp[i * 4 ];       //R

        }

        return pixels;
    }
}
