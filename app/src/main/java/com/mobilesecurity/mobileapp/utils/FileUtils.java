package com.mobilesecurity.mobileapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import android.text.TextUtils;

import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * The class offers {@link File} related useful functions.
 */

public class FileUtils {


 public static void saveFileWithStream(File file, InputStream in) throws IOException {
        // check the arguments
        if(null == in) {
            throw new NullPointerException("in can't be null");
        }
        if(null == file) {
            in.close();
            throw new NullPointerException("file can't be null");
        }
        // save file
        // sink
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        // source
        BufferedSource source = Okio.buffer(Okio.source(in));
        try{
            // write to sink
            source.readAll(sink);
        }catch (IOException e) {
            throw new IOException(e);
        } finally {
            // close
            source.close();
            sink.close();
            in.close();
            System.out.println("saveFileWithStream: stream closed");
        }
    }


	public static void saveFileWithString(File file, String data) throws IOException {
        // check the arguments
        if(TextUtils.isEmpty(data)) {
            throw new IllegalArgumentException("data can't be empty");
        }
        if(null == file) {
            throw new NullPointerException("file can't be null");
        }
        // save file

        // sink
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        try{
            // write to sink
            sink.writeUtf8(data);
        }catch (IOException e) {
            throw new IOException(e);
        } finally {
            // close
            sink.close();
            System.out.println("saveFileWithStream: stream closed");
        }
    }
	
    public static boolean makeDirectory(File folder) {
        try {
            return folder.mkdirs();
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }
    }


    public static String getRealFilePathFromURI(Context context, Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(
                context, contentUri, projection, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }


    public static String getMimeType(String uri) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);

        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }


    public static void openFile(File file, Activity activity) {
        String mimeType = getMimeType(Uri.fromFile(file).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        activity.startActivity(Intent.createChooser(intent, "Open with"));
    }


    public static void copy(File firstFile, File secondFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(firstFile);
        FileOutputStream outputStream = new FileOutputStream(secondFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, length);

        inputStream.close();
        outputStream.close();
    }


    public static void move(File oldFile, File newFile) throws IOException {
        FileInputStream input = new FileInputStream(oldFile);
        FileOutputStream output = new FileOutputStream(newFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0)
            output.write(buffer, 0, length);

        input.close();
        output.close();
    }


    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static String humanReadableSizeOf(long size) {
        DecimalFormat df = new DecimalFormat("##.##");

        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            return "" + df.format(tmpSize) + "Mb";
        } else if (size / 1024 > 0) {
            return "" + df.format((size / (1024))) + "Kb";
        } else
            return "" + df.format(size) + "B";
    }


    public static String humanReadableSizeOf(double size) {
        DecimalFormat df = new DecimalFormat("##.##");

        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            return "" + df.format(tmpSize) + "Mb";
        } else if (size / 1024 > 0) {
            return "" + df.format((size / (1024))) + "Kb";
        } else
            return "" + df.format(size) + "Kb";
    }


    private void moveToInternal(Context context, File file) throws IOException {
        try {
            OutputStream outputStream = context.openFileOutput(file.getName(), 0);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.close();
        } catch (Throwable error) {
            error.printStackTrace();
            throw error;
        }
    }

}
