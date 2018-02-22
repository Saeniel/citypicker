package com.saeniel.citypicker;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

/**
 * Created by Saeniel on 18.02.2018.
 */

public class CityInfoActivity extends AppCompatActivity {

    ImageView imvCityPicture;
    TextView tvCityDescription, tvCityArea, tvCityPopulation;
    Button btnCalcNextYear;
    Context context;
    GraphView graph;
    static int[] excelYears;
    static double[] excelMoney;

    static LineGraphSeries<DataPoint> series;

    public static final int PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        graph = findViewById(R.id.graph);
        imvCityPicture = findViewById(R.id.imvCityPicture);
        tvCityDescription = findViewById(R.id.tvCityDescription);
        tvCityArea = findViewById(R.id.tvCityArea);
        tvCityPopulation = findViewById(R.id.tvCityPopulation);
        btnCalcNextYear = findViewById(R.id.btnCalcNextYear);

        context = getApplicationContext();

        if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 23) {
            ActivityCompat.requestPermissions(CityInfoActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            fillData();
        }

        btnCalcNextYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double lastYearSum = excelMoney[excelMoney.length - 1];
                double preLastYearSum = excelMoney[excelMoney.length - 2];
                double newMoney = ((lastYearSum-preLastYearSum)/preLastYearSum) * lastYearSum + lastYearSum;

                int[] addExcelYears = new int[excelYears.length + 1];
                double[] addExcelMoney = new double[excelMoney.length + 1];

                for (int i = 0; i < excelYears.length; i++) {
                    addExcelYears[i] = excelYears[i];
                }
                addExcelYears[addExcelYears.length - 1] = excelYears[excelYears.length - 1] + 1;

                for (int i = 0; i < excelMoney.length; i++) {
                    addExcelMoney[i] = excelMoney[i];
                }
                addExcelMoney[addExcelMoney.length - 1] = newMoney;

                DataPoint[] dataPoints = new DataPoint[addExcelYears.length];
                for (int i = 0; i < addExcelYears.length; i++) {
                    dataPoints[i] = new DataPoint(addExcelYears[i], addExcelMoney[i]);
                }

                String[] yearLabels = new String[addExcelYears.length];
                for (int i = 0; i < addExcelYears.length; i++) {
                    yearLabels[i] = new String(String.valueOf(addExcelYears[i]));
                }

                StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                staticLabelsFormatter.setHorizontalLabels(yearLabels);
                series = new LineGraphSeries<>(dataPoints);
                graph.addSeries(series);
                graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fillData();
                } else {
                    Toast.makeText(CityInfoActivity.this,
                            "Permission denied to read your external storage",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    public void fillData() {

        Bundle extra = getIntent().getExtras();
        int id = extra.getInt("id");

        Gson gson = new Gson();
        City[] founderArray = gson.fromJson(readFromAssetJSON(), City[].class);
        String image = founderArray[id].image;

        imvCityPicture.setImageResource(getResources().getIdentifier(image , "drawable", getPackageName()));
        tvCityDescription.setText(founderArray[id].description);
        tvCityArea.setText(founderArray[id].area);
        tvCityPopulation.setText(founderArray[id].population);

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
            readFromExcel(f.getPath(), id);
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
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public static void readFromExcel(String file, int id) throws IOException {
        int yearRow = 0;
        int moneyRow = 0;
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet myExcelSheet = myExcelBook.getSheet("list");

        switch (id) {
            case 0: {
                yearRow = 1;
                moneyRow = 2;
                break;
            }
            case 1: {
                yearRow = 4;
                moneyRow = 5;
                break;
            }
            case 2: {
                yearRow = 7;
                moneyRow = 8;
                break;
            }
            case 3: {
                yearRow = 10;
                moneyRow = 11;
                break;
            }
            case 4: {
                yearRow = 13;
                moneyRow = 14;
                break;
            }
            case 5: {
                yearRow = 16;
                moneyRow = 17;
                break;
            }
            case 6: {
                yearRow = 19;
                moneyRow = 20;
                break;
            }
            case 7: {
                yearRow = 22;
                moneyRow = 23;
                break;
            }
            case 8: {
                yearRow = 25;
                moneyRow = 26;
                break;
            }
            case 9: {
                yearRow = 28;
                moneyRow = 29;
                break;
            }
            case 10: {
                yearRow = 31;
                moneyRow = 32;
                break;
            }
            case 11: {
                yearRow = 34;
                moneyRow = 35;
                break;
            }
            case 12: {
                yearRow = 37;
                moneyRow = 38;
                break;
            }
            case 13: {
                yearRow = 40;
                moneyRow = 41;
                break;
            }
            case 14: {
                yearRow = 43;
                moneyRow = 44;
                break;
            }
        }

        HSSFRow years = myExcelSheet.getRow(yearRow);
        HSSFRow money = myExcelSheet.getRow(moneyRow);
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

    String readFromAssetJSON(){
        String json = null;
        try {
            InputStream is = getAssets().open("city.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

}
