/*package com.galiyara.sandy.galiyara.GHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.GInterface.TextExtractListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import androidx.annotation.NonNull;

public class TextExtractor {

    private static Thread thread;
    private static Bitmap bitmap;
    private static TextRecognizer recognizer;
    private static Frame frame;

    public static void extractText(@NonNull Context context, @NonNull String img,
                                   @NonNull TextExtractListener listener) {
        if(thread == null) {
            thread = new Thread(() -> {
                try {
                    recognizer = new TextRecognizer.Builder(context).build();
                    if(recognizer.isOperational()) {
                        bitmap = BitmapFactory.decodeFile(img);
                        frame = new Frame.Builder()
                                .setBitmap(bitmap)
                                .build();
                        StringBuilder builder = null;
                        SparseArray<TextBlock> blocks = recognizer.detect(frame);
                        if(blocks.size() > 0)
                            builder = new StringBuilder();
                        for(int i=0; i<blocks.size(); i++) {
                            TextBlock textBlock = blocks.get(blocks.keyAt(i));
                            GaliyaraConst.logData(textBlock.getValue());
                            if(builder != null) {
                                builder.append(textBlock.getValue());
                                builder.append("\n");
                            }
                        }
                        if(builder != null) {
                            listener.onExtracted(builder.toString());
                        }
                    }
                    freeMem();
                } catch (Exception e) {
                    e.printStackTrace();
                    freeMem();
                    listener.onExtractFail();
                }
            });
            thread.start();
        }
    }

    private static void freeMem() {
        if(recognizer != null) {
            recognizer.release();
            recognizer = null;
        }
        if(bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if(frame != null)
            frame = null;
        if(thread != null)
            thread = null;
    }
}
*/