package com.rokuan.calliope.source;

import android.net.Uri;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class AudioFileSource extends MediaFileSource {
    public AudioFileSource(Uri uri) {
        super(ObjectType.AUDIO, uri);
    }
}
