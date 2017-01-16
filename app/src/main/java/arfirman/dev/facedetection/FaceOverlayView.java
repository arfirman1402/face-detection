package arfirman.dev.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

/**
 * Created by alodokter-it on 16/01/17.
 */

public class FaceOverlayView extends View {

    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceOverlayView(Context context) {
        super(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        FaceDetector detector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if (!detector.isOperational()) {
            Log.d("FaceOverlayView", "FaceDetector is Not Operate now");
        } else {
            Log.d("FaceOverlayView", "FaceDetector is Operate now");
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            detector.release();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceBox(canvas, scale);
        }
    }

    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);

        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float left;
        float top;
        float right;
        float bottom;

        for (int i = 0; i < mFaces.size(); i++) {
            Face face = mFaces.valueAt(i);

            List<Landmark> landmarks = face.getLandmarks();
            for (int j = 0; j < landmarks.size(); j++) {
                Landmark landmark = landmarks.get(j);
                int circlex = (int) (landmark.getPosition().x * scale);
                int circley = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(circlex, circley, 10, paint);
            }
            left = (float) (face.getPosition().x * scale);
            top = (float) (face.getPosition().y * scale);
            right = (float) ((face.getPosition().x + face.getWidth()) * scale);
            bottom = (float) ((face.getPosition().y + face.getHeight()) * scale);

            canvas.drawRect(left, top, right, bottom, paint);
        }

        logFaceData();
    }

    private void logFaceData() {
        float smilingProbability;
        float leftEyeOpenProbability;
        float rightEyeOpenProbability;
        float eulerY;
        float eulerZ;

        for (int i = 0; i < mFaces.size(); i++) {
            Face face = mFaces.valueAt(i);

            smilingProbability = face.getIsSmilingProbability();
            leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
            rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            eulerY = face.getEulerY();
            eulerZ = face.getEulerZ();

            Log.d("FaceDetector", "Person number " + (i + 1));
            Log.d("FaceDetector", "Smiling = " + smilingProbability);
            Log.d("FaceDetector", "Left Eye Open = " + leftEyeOpenProbability);
            Log.d("FaceDetector", "Right Eye Open = " + rightEyeOpenProbability);
            Log.d("FaceDetector", "Euler Y = " + eulerY);
            Log.d("FaceDetector", "Euler Z = " + eulerZ);
        }


    }
}
