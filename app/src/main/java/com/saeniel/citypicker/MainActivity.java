package com.saeniel.citypicker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    ImageView imvMap;
    int[] aqtay = {105, 292, 228, 343};
    int[] atyray = {95, 210, 229, 252};
    int[] oral = {31, 165, 172, 203};
    int[] aqtobe = {179, 157, 316, 202};
    int[] qostanai = {218, 74, 351, 123};
    int[] qyzylorda = {286, 252, 417, 297};
    int[] astana = {361, 118, 498, 166};
    int[] koksetay = {352, 63, 484, 109};
    int[] petropavl = {387, 15, 525, 66};
    int[] qaragandy = {430, 180, 541, 231};
    int[] taraz = {422, 306, 545, 342};
    int[] symkent = {337, 343, 464, 394};
    int[] almaty = {542, 292, 666, 339};
    int[] oskemen = {590, 195, 729, 244};
    int[] pavlodar = {546, 64, 682, 112};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imvMap = (ImageView) findViewById(R.id.imvMainMap);

        /*imvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityInfoActivity.class );
                startActivity(intent);
            }
        });*/

       imvMap.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {

               int x = (int) motionEvent.getX();
               int y = (int) motionEvent.getY();

               Display display = getWindowManager().getDefaultDisplay();
               Point size = new Point();
               display.getSize(size);
               int screenWidth = size.x;
               int screenHeight = size.y;

               x *= (800.0 / screenWidth);
               y *= (450.0 / screenHeight);

               if(x > aqtay[0] && x < aqtay[2] && y > aqtay[1] && y < aqtay[3]) {
                   // Aqtay'
                   Toast.makeText(getApplicationContext(), "Aqtay'", Toast.LENGTH_SHORT).show();
               } else if(x > atyray[0] && x < atyray[2] && y > atyray[1] && y < atyray[3]) {
                   // Atyray'
                   Toast.makeText(getApplicationContext(), "Atyray'", Toast.LENGTH_SHORT).show();
               } else if (x > oral[0] && x < oral[2] && y > oral[1] && y < oral[3]) {
                   // Oral
                   Toast.makeText(getApplicationContext(), "Oral", Toast.LENGTH_SHORT).show();
               } else if(x > aqtobe[0] && x < aqtobe[2] && y > aqtobe[1] && y < aqtobe[3]) {
                   // Aqto'be
                   Toast.makeText(getApplicationContext(), "Aqto'be", Toast.LENGTH_SHORT).show();
               } else if(x > qostanai[0] && x < qostanai[2] && y > qostanai[1] && y < qostanai[3]) {
                   // Qostanai'
                   Toast.makeText(getApplicationContext(), "Qostanai'", Toast.LENGTH_SHORT).show();
               } else if (x > qyzylorda[0] && x < qyzylorda[2] && y > qyzylorda[1] && y < qyzylorda[3]) {
                   // Qyzylorda
                   Toast.makeText(getApplicationContext(), "Qyzylorda", Toast.LENGTH_SHORT).show();
               } else if(x > astana[0] && x < astana[2] && y > astana[1] && y < astana[3]) {
                   // Astana
                   Toast.makeText(getApplicationContext(), "Astana", Toast.LENGTH_SHORT).show();
               } else if(x > koksetay[0] && x < koksetay[2] && y > koksetay[1] && y < koksetay[3]) {
                   // Ko'ks'etay'
                   Toast.makeText(getApplicationContext(), "Ko'ks'etay'", Toast.LENGTH_SHORT).show();
               } else if(x > petropavl[0] && x < petropavl[2] && y > petropavl[1] && y < petropavl[3]) {
                   // Petropavl
                   Toast.makeText(getApplicationContext(), "Petropavl", Toast.LENGTH_SHORT).show();
               } else if(x > qaragandy[0] && x < qaragandy[2] && y > qaragandy[1] && y < qaragandy[3]) {
                   // Qarag'andy
                   Toast.makeText(getApplicationContext(), "Qarag'andy", Toast.LENGTH_SHORT).show();
               } else if(x > taraz[0] && x < taraz[2] && y > taraz[1] && y < taraz[3]) {
                   // Taraz
                   Toast.makeText(getApplicationContext(), "Taraz", Toast.LENGTH_SHORT).show();
               } else if(x > symkent[0] && x < symkent[2] && y > symkent[1] && y < symkent[3]) {
                   // S'ymkent
                   Toast.makeText(getApplicationContext(), "S'ymkent", Toast.LENGTH_SHORT).show();
               } else if(x > almaty[0] && x < almaty[2] && y > almaty[1] && y < almaty[3]) {
                   // Almaty
                   Toast.makeText(getApplicationContext(), "Almaty", Toast.LENGTH_SHORT).show();
               } else if(x > oskemen[0] && x < oskemen[2] && y > oskemen[1] && y < oskemen[3]) {
                   // O'skemen
                   Toast.makeText(getApplicationContext(), "O'skemen", Toast.LENGTH_SHORT).show();
               } else if(x > pavlodar[0] && x < pavlodar[2] && y > pavlodar[1] && y < pavlodar[3]) {
                   // Pavlodar
                   Toast.makeText(getApplicationContext(), "Pavlodar", Toast.LENGTH_SHORT).show();
               }


               return false;
           }
       });
    }

}