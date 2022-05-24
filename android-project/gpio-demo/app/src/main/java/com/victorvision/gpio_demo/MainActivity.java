package com.victorvision.gpio_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.ArrayList;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = "GPIO Demo";

    boolean state = false;
    ArrayList<Gpio> ios = new ArrayList<>();
    Gpio outputIo;

    ArrayList<Switch> directionSwitches = new ArrayList<>();
    ArrayList<Switch> valueSwitches = new ArrayList<>();
    ArrayList<TextView> names = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* GPIO initialization */
        ios.add(new Gpio(Gpio.IoPin.Pin1));
        ios.add(new Gpio(Gpio.IoPin.Pin2));
        ios.add(new Gpio(Gpio.IoPin.Pin3));
        ios.add(new Gpio(Gpio.IoPin.Pin4));
        ios.add(new Gpio(Gpio.IoPin.Pin5));
        ios.add(new Gpio(Gpio.IoPin.Pin6));


        /* IO name initialization */
        names.add((TextView) findViewById(R.id.name_1));
        names.add((TextView) findViewById(R.id.name_2));
        names.add((TextView) findViewById(R.id.name_3));
        names.add((TextView) findViewById(R.id.name_4));
        names.add((TextView) findViewById(R.id.name_5));
        names.add((TextView) findViewById(R.id.name_6));


        /* IO direction initialization */
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_1));
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_2));
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_3));
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_4));
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_5));
        directionSwitches.add((Switch) findViewById(R.id.directionSwitch_6));

        CompoundButton.OnCheckedChangeListener directionSwitchListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index =Integer.parseInt( (String)buttonView.getTag());
                Gpio io = ios.get(index);
                Switch sw = valueSwitches.get(index);
                TextView tv = names.get(index);

                if (isChecked == true){
                    io.setDirection(Gpio.IoDirection.Out);
                    tv.setText("IO"+(index+1)+":OUT");
                    sw.setEnabled(true);
                } else {
                    io.setDirection(Gpio.IoDirection.In);
                    tv.setText("IO"+(index+1)+": IN");
                    sw.setEnabled(false);
                }
            }
        };

        for(Switch sw : directionSwitches){
            sw.setOnCheckedChangeListener(directionSwitchListener);
        }

        /* IO value initialization */
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_1));
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_2));
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_3));
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_4));
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_5));
        valueSwitches.add((Switch) findViewById(R.id.valueSwitch_6));

        CompoundButton.OnCheckedChangeListener valueSwitchListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Gpio io = ios.get(Integer.parseInt((String)buttonView.getTag()));
                io.setValue(isChecked ? Gpio.IoValue.High : Gpio.IoValue.Low);
            }
        };

        for(Switch sw : valueSwitches){
            sw.setOnCheckedChangeListener(valueSwitchListener);
            sw.setEnabled(false);
        }

        outputIo = ios.get(0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> currentValues = new ArrayList<>();

                int index = 0;
                for (Gpio gpio : ios) {
                    Gpio.IoValue currentValue = gpio.getValue();
                    currentValues.add(gpio.getPin() + ": " + currentValue.toString());
                    Switch valueSwitch = valueSwitches.get(index);

                    boolean readValue = currentValue == Gpio.IoValue.High ? true : false;
                    if (valueSwitch.isChecked() != readValue){
                        valueSwitch.setChecked(readValue);
                    }

                    index++;
                }

                Log.d(TAG, TextUtils.join(", ", currentValues));

                handler.postDelayed(this, 100);
            }
        }, 100);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                state = !state;
//
////                if (state){
////                    Log.d(TAG, "--- Setting " + outputIo .getPin() + " to low ---");
////                    outputIo.setValue(Gpio.IoValue.Low);
////                }
////                else {
////                    Log.d(TAG, "--- Setting " + outputIo .getPin() + " to high ---");
////                    outputIo.setValue(Gpio.IoValue.High);
////                }
//
//                ArrayList<String> currentValues = new ArrayList<>();
//
//                for (Gpio gpio : ios) {
//                    Gpio.IoValue currentValue = gpio.getValue();
//                    currentValues.add(gpio.getPin() + ": " + currentValue.toString());
//                }
//
//                Log.d(TAG, TextUtils.join(", ", currentValues));
//
//                handler.postDelayed(this, 500);
//            }
//        }, 500);
    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }
}