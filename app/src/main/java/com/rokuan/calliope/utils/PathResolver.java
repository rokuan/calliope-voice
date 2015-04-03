package com.rokuan.calliope.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by LEBEAU Christophe on 03/04/2015.
 */
public class PathResolver {
    public static String getPathFromPicture(Context context, Uri uri){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return getPathFromPictureURI19(context, uri);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            return getPathFromPictureURI11To18(context, uri);
        }

        return getRealPathFromURI(context, uri);
    }

    @TargetApi(19)
    private static String getPathFromPictureURI19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] columns = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, MediaStore.Images.Media._ID + " = ?", new String[]{ id }, null);

        if(cursor != null) {
            int columnIndex = cursor.getColumnIndex(columns[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }

        return filePath;
    }

    @TargetApi(11)
    private static String getPathFromPictureURI11To18(Context context, Uri uri){
        String[] columns = { MediaStore.Images.Media.DATA };
        String result = "";

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri, columns, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            if(cursor.moveToFirst()) {
                result = cursor.getString(columnIndex);
            }

            cursor.close();
        }

        return result;
    }

    private static String getRealPathFromURI(Context context, Uri contentUri){
        String[] columns = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, columns, null, null, null);
        String filePath = "";

        if(cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
        }

        cursor.close();

        return filePath;
    }
}
