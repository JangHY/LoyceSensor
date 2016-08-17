package com.hyon.example.stepcounter;
//https://www.youtube.com/watch?v=pDz8y5B8GsE 여기 보고 했당
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

    private SensorManager sensorManager;//sm
    private TextView count;
    boolean activityRunning;

    double myHeight = 166;
    TextView distance;
    TextView degree;

    SensorEventListener oriL;
    SensorEventListener stepCounter;
    Sensor oriSensor;
    Sensor stepSensor;
    TextView ox, oy, oz;

    int theta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = (TextView) findViewById(R.id.count);
        distance = (TextView) findViewById(R.id.distance);
        degree = (TextView) findViewById(R.id.degree);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        oriL = new oriListener();
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepCounter = new stepListener();
        ox = (TextView)findViewById(R.id.ori_x);
        oy = (TextView)findViewById(R.id.ori_y);
        oz = (TextView)findViewById(R.id.ori_z);

    }

    private class oriListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {  // 방향 센서 값이 바뀔때마다 호출됨
            theta = (int)event.values[0];
            ox.setText(Integer.toString(theta));    //방위각   0=북쪽, 90=동쪽, 180=남쪽, 270=서쪽
            //기기를 수평으로 두었을 때 기기의 머리부분이 어느방향을 가리키고 있는가.
            oy.setText(Float.toString(event.values[1]));    //경사도 : 기기의 수직 기울기
            //기기의 머리부분과 아래부분이 수평을 이룰 때 0값을 가지며 머리부분의 높이가 높아지면 수치값이 감소한다
            //머리부분의 높이가 낮아지면 수치값이 증가한다.
            oz.setText(Float.toString(event.values[2]));    //좌우회전 : 기기의 수평 기울기
            //화면이 하늘을 향하고 있을 때 기기의 좌, 우 부분이 수평이면 0
            //기기의 좌측위치가 높아지면 증가, 우측 위치가 높아지면 감소

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
    private class stepListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            if(activityRunning){
                count.setText(String.valueOf(event.values[0]));
                double step = (myHeight - 100) * 0.37;
                double dist = step * event.values[0];

                distance.setText(Double.toString(dist) + "cm");
                degree.setText(Double.toString(dist*(Math.sin(theta))));
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    protected void onResume(){
        super.onResume();
        activityRunning = true;

        if(oriSensor != null && stepSensor != null){
            sensorManager.registerListener(stepCounter, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(oriL, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            Toast.makeText(this, "Sensor not available",Toast.LENGTH_LONG).show();
        }
    }

    protected void onPause(){
        super.onPause();

        sensorManager.unregisterListener(oriL);
        sensorManager.unregisterListener(stepCounter);
        activityRunning = false;
    }

}