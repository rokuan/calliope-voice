package com.rokuan.calliope.source;

import android.content.Context;
import android.net.Uri;

import com.rokuan.calliope.extract.HypertextLinkExtractionListener;
import com.rokuan.calliope.extract.HypertextLinkExtractor;
import com.rokuan.calliope.extract.TextExtractionListener;
import com.rokuan.calliope.extract.TextExtractor;

import java.net.URL;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class ImageFileSource extends MediaFileSource implements HypertextLinkExtractor, TextExtractor {
    public ImageFileSource(Context context, Uri uri) {
        super(ObjectType.IMAGE, context, uri);
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public void getURLAsync(HypertextLinkExtractionListener listener) {
        // TODO:
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void getTextAsync(TextExtractionListener listener) {
        // TODO:
    }
}
