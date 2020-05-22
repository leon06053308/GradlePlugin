package com.way.gradleplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.way.annotation.DebugLog;
import com.way.annotation.InfoLog;
import com.way.annotation.TestAnno;

public class MainActivity extends AppCompatActivity {

    @InfoLog
    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //android.util.Log.i("MainActivity", "onCreate");

        test("test", 5);
    }

    @DebugLog(fields = {"1"})
    private void test(String name, int value){
    }
}
