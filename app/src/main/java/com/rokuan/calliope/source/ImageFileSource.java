package com.rokuan.calliope.source;

import android.net.Uri;

import com.aspose.ocr.ImageStream;
import com.aspose.ocr.OcrEngine;
import com.rokuan.calliope.extract.HypertextLinkExtractor;
import com.rokuan.calliope.extract.TextExtractor;

import java.net.URL;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class ImageFileSource extends MediaFileSource implements HypertextLinkExtractor, TextExtractor {
    public ImageFileSource(Uri uri) {
        super(ObjectType.IMAGE, uri);
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public String getText() {
        OcrEngine ocrEngine = new OcrEngine();
        String path = getFileUri().getPath();

        System.out.println("Path: " + path);

        ocrEngine.setImage(ImageStream.fromFile(path));

        if(ocrEngine.process()){
            return ocrEngine.getText().toString();
        }

        return "";
    }
}
