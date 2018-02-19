package com.saeniel.citypicker;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Saeniel on 18.02.2018.
 */

public class CityInfoActivity extends AppCompatActivity {

    ImageView imvCityPicture;
    TextView tvCityDescription;
    Context context;
    LineChartView chart;
    Uri filePath;
    String realPath;
    static int[] excelYears;
    static double[] excelMoney;

    public static final int PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        chart = (LineChartView) findViewById(R.id.chart);
        context = getApplicationContext();

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select FIle"), PICK_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FILE && data != null) {
            filePath = data.getData();
            realPath = getRealPathFromURI(context, filePath);
            try {
                readFromExcel(realPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<PointValue> values = new ArrayList<PointValue>();
            for(int k = 0; k < excelYears.length; k++) {
                values.add(new PointValue((float)excelYears[k], (float)excelMoney[k]));
            }
            Line line = new Line(values).setColor(Color.GREEN).setCubic(true);
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            /*List<Integer> axisValuesForX = new ArrayList<>();
            List<Double> axisValuesForY = new ArrayList<>();

            for(int i = 0; i < excelYears.length; i++) {
                axisValuesForX.add(excelYears[i]);
            }

            for(int i = 0; i < excelMoney.length; i++) {
                axisValuesForY.add(excelMoney[i]);
            }*/

            List<AxisValue> axisValuesX = new ArrayList<AxisValue>();
            for(int i = 0; i < excelYears.length; i++) {
                axisValuesX.add(new AxisValue(i, ("" + excelYears[i]).toCharArray()));
            }

            List<AxisValue> axisValuesY = new ArrayList<AxisValue>();
            for(int i = 0; i < excelMoney.length; i++) {
                axisValuesY.add(new AxisValue(i, ("" + excelMoney[i]).toCharArray()));
            }

            Axis axisX = new Axis(axisValuesX);
            axisX.setHasLines(true);
            axisX.setTextColor(Color.BLACK);

            Axis axisY = new Axis(axisValuesY);
            axisY.setHasLines(true);
            axisY.setTextColor(Color.BLACK);

            LineChartData dataChart = new LineChartData();
            dataChart.setLines(lines);
            dataChart.setAxisYLeft(axisY);
            dataChart.setAxisXBottom(axisX);
            chart.setLineChartData(dataChart);
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

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
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

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
