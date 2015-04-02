package com.rokuan.calliope.source;

import android.net.Uri;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class VideoFileSource extends MediaFileSource {
    public VideoFileSource(Uri uri) {
        super(ObjectType.VIDEO, uri);
    }
}
