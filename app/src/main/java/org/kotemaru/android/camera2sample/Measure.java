package org.kotemaru.android.camera2sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Measure {
    private Activity activity;
    public Measure(Activity activity) {
        this.activity = activity;
    }

    public Bitmap measure (Bitmap image) throws IOException {
        // 画像データを変換（BitmapのMatファイル変換）
        Mat matImg = new Mat();
        Utils.bitmapToMat(image,matImg);

        Mat gray = new Mat();
        Imgproc.cvtColor(matImg, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(
                gray, gray, new Size(7, 7), 0.0, 0.0);
        Mat edged = new Mat();
        Imgproc.Canny(gray, edged, 50, 100);

        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11));
        Imgproc.dilate(edged, edged, kernel1, new Point(), 1); // TODO
        Imgproc.erode(edged, edged, kernel1, new Point(-1, -1), 1); // TODO

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = Mat.zeros(new Size(5, 5), CvType.CV_8UC1); // TODO
        Imgproc.findContours(edged, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0, n = contours.size(); i < n; i++) {
            MatOfPoint c = contours.get(i);
            double tmp = Imgproc.contourArea(c);
            if (tmp < 100) {
                continue;
            }

            MatOfPoint2f ptmat2 = new MatOfPoint2f( c.toArray() );
            RotatedRect best_rect = Imgproc.minAreaRect(ptmat2);
            Point[] points = new Point[4];
            best_rect.points(points);

            List<MatOfPoint> boxContours = new ArrayList<MatOfPoint>();
            boxContours.add(new MatOfPoint(points));

            Imgproc.drawContours(matImg, boxContours, -1, new Scalar(0, 128, 0), 2);
        }

        Utils.matToBitmap(matImg, image);
        return image;
    }
}