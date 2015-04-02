package com.rokuan.calliope.source;

import android.net.Uri;

/**
 * Created by LEBEAU Christophe on 02/04/2015.
 */
public abstract class MediaFileSource extends SourceObject {
    private Uri fileUri;

    protected MediaFileSource(ObjectType ty, Uri uri) {
        super(ty);
        fileUri = uri;
    }

    public Uri getFileUri(){
        return fileUri;
    }
}
