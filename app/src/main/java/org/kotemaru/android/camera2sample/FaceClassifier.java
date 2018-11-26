package org.kotemaru.android.camera2sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Rect;

public class FaceClassifier {
    // OpenCVライブラリのロード
    static {
        System.loadLibrary("opencv_java3");
    }
    private Activity activity;
    public FaceClassifier (Activity activity) {
        this.activity = activity;
    }
    public boolean checkFaceExistence (Bitmap image) throws IOException {
        // 画像データを変換（BitmapのMatファイル変換）
        Mat matImg = new Mat();
        Utils.bitmapToMat(image,matImg);
        // 顔認識を行うカスケード分類器インスタンスの生成（一度ファイルを書き出してファイルのパスを取得する）
        // 一度raw配下に格納されたxmlファイルを取得
        InputStream inStream = this.activity.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
        File cascadeDir = this.activity.getDir("cascade", Context.MODE_PRIVATE);
        File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
        // 取得したxmlファイルを特定ディレクトリに出力
        FileOutputStream outStream = new FileOutputStream(cascadeFile);
        byte[] buf = new byte[2048];
        int rdBytes;
        while ((rdBytes = inStream.read(buf)) != -1) {
            outStream.write(buf, 0, rdBytes);
        }
        outStream.close();
        inStream.close();
        // 出力したxmlファイルのパスをCascadeClassifierの引数にする
        CascadeClassifier faceDetetcor = new CascadeClassifier(cascadeFile.getAbsolutePath());
        // CascadeClassifierインスタンスができたら出力したファイルはいらないので削除
        if (faceDetetcor.empty()) {
            faceDetetcor = null;
        } else {
            cascadeDir.delete();
            cascadeFile.delete();
        }
        // カスケード分類器に画像データを与え顔認識
        MatOfRect faceRects = new MatOfRect();
        faceDetetcor.detectMultiScale(matImg, faceRects);
        // 顔認識の結果の確認
        Log.i("OpenCV" ,"認識された顔の数:" + faceRects.toArray().length);
        if (faceRects.toArray().length > 0) {
            for (Rect face : faceRects.toArray()) {
                Log.i("OpenCV" ,"顔の縦幅" + face.height);
                Log.i("OpenCV" ,"顔の横幅" + face.width);
                Log.i("OpenCV" ,"顔の位置（Y座標）" + face.y);
                Log.i("OpenCV" ,"顔の位置（X座標）" + face.x);
            }
            return true;
        } else {
            Log.i("OpenCV" ,"顔が検出されませんでした");
            return false;
        }
    }
}