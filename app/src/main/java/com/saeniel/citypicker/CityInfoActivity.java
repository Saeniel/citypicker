package com.saeniel.citypicker;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

/**
 * Created by Saeniel on 18.02.2018.
 */

public class CityInfoActivity extends AppCompatActivity {

    ImageView imvCityPicture;
    TextView tvCityDescription;
    Context context;
    GraphView graph;
    Uri filePath;
    String realPath;
    static int[] excelYears;
    static double[] excelMoney;

    static LineGraphSeries<DataPoint> series;

    public static final int PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        graph = (GraphView) findViewById(R.id.graph);
        context = getApplicationContext();

        ActivityCompat.requestPermissions(CityInfoActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File f = new File(getCacheDir()+"/test.xls");
                    if (!f.exists()) try {

                        InputStream is = getAssets().open("test.xls");
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();

                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(buffer);
                        fos.close();
                    } catch (Exception e) { throw new RuntimeException(e); }

                    try {
                        readFromExcel(f.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DataPoint[] dataPoints = new DataPoint[excelMoney.length];

                    for (int i = 0; i < excelYears.length; i++) {
                        dataPoints[i] = new DataPoint(excelYears[i], excelMoney[i]);
                    }

                    String[] yearLabels = new String[excelYears.length];
                    for (int i = 0; i < excelYears.length; i++) {
                        yearLabels[i] = new String(String.valueOf(excelYears[i]));
                    }

                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(yearLabels);

                    series = new LineGraphSeries<>(dataPoints);
                    series.setTitle("foo");

                    graph.getLegendRenderer().setVisible(true);
                    graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graph.getViewport().setScrollable(true);
                    graph.addSeries(series);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CityInfoActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FILE && data != null) {
            filePath = data.getData();
            realPath = getPath(context, filePath);
            try {
                readFromExcel(realPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            DataPoint[] dataPoints = new DataPoint[excelMoney.length];

            for (int i = 0; i < excelYears.length; i++) {
                dataPoints[i] = new DataPoint(excelYears[i], excelMoney[i]);
            }

            String[] yearLabels = new String[excelYears.length];
            for (int i = 0; i < excelYears.length; i++) {
                yearLabels[i] = new String(String.valueOf(excelYears[i]));
            }

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(yearLabels);

            series = new LineGraphSeries<>(dataPoints);
            series.setTitle("foo");

            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph.getViewport().setScrollable(true);
            graph.addSeries(series);
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        }
    }

    public static void readFromExcel(String file) throws IOException {
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet myExcelSheet = myExcelBook.getSheet("list1");
        HSSFRow years = myExcelSheet.getRow(0);
        HSSFRow money = myExcelSheet.getRow(1);
        HSSFCell cell;

        excelYears = new int[years.getPhysicalNumberOfCells()];
        excelMoney = new double[money.getPhysicalNumberOfCells()];

        for (int i = 0; i < years.getPhysicalNumberOfCells(); i++) {
            cell = years.getCell(i);
            excelYears[i] = (int) cell.getNumericCellValue();
        }

        for (int j = 0; j < money.getPhysicalNumberOfCells(); j++) {
            cell = money.getCell(j);
            excelMoney[j] = cell.getNumericCellValue();
        }
    }

    @Nullable
    public static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
