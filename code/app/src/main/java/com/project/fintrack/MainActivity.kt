package com.project.fintrack;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    fun LoggingButton_onClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popupView = inflater.inflate(R.layout.popup_design, null);
        
        val width = LinearLayout.LayoutParams.WRAP_CONTENT;
        val height = LinearLayout.LayoutParams.WRAP_CONTENT;
        val focus = true;
        val popupWindow = PopupWindow(popupView, width, height, focus);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}