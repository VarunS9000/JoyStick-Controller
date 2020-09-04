package com.know.botcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main4Activity extends SurfaceView implements SurfaceHolder.Callback,View.OnTouchListener {
    float centerX;
    float centerY;
    float baseRadius;
    float hatRadius;
    int port=1111;
    Context context;
    //EditText IP_ADD;
    //MainActivity IP = new MainActivity();

    String IP_ADD=MainActivity.getIP() ;
    //String ipadd= IP_ADD.getText().toString();

    InetAddress ip= InetAddress.getByName(IP_ADD);
    //InetAddress ip= IP.getIP();
    //Button shoot;
    byte[] packetBuffer= new byte[512];



    public JoystickListener joystickCallback = new JoystickListener() {
        @Override
        public void onJoystickMoved(float xPercent, float yPercent, int id) throws IOException {
            float check;
            Log.d("yo","bro");
            Log.d("yo",IP_ADD);
            //Log.d("yo");



            check= (float) (baseRadius/1.414);
            if(yPercent>0&&(xPercent>-check && xPercent<check)) {
                packetBuffer[0]=51;
            }
            else if(yPercent<0&&(xPercent>-check && xPercent<check)){
                packetBuffer[0]=55;

            }
            else if(xPercent>0&&(yPercent>-check && yPercent<check)){
                packetBuffer[0]=50;

            }
            else if(xPercent<0&&(yPercent>-check && yPercent<check)){
                packetBuffer[0]=53;

            }

            else if(xPercent==0 && yPercent==0){
                packetBuffer[0]=57;

            }

            new JoystickUsed().execute("");


            Log.d("Main Method", "X percent" + xPercent + "Y percent" + yPercent);
        }
    };


    ;
    //Main2Activity joystickCallback=new Main2Activity();


    public void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = (getHeight()) / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 8;
    }

    public Main4Activity(Context context) throws UnknownHostException {

        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;

        }
        //joystickCallback=(JoystickListener) context;
    }

    public Main4Activity(Context context, AttributeSet attributes, int style) throws UnknownHostException {
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);


    }

    public Main4Activity(Context context, AttributeSet attributes) throws UnknownHostException {
        super(context, attributes);
        getHolder().addCallback(this);
        //getHolder().addCallback();
        setOnTouchListener(this);

    }

    public void drawJoyStick(float newX, float newY) {

        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            colors.setARGB(255, 50, 50, 50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            colors.setARGB(255, 205, 220, 57);
            myCanvas.drawCircle(newX, newY, hatRadius, colors);
            getHolder().unlockCanvasAndPost(myCanvas);

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoyStick(centerX, centerY);
        //shoot = (Button) findViewById(R.id.button2);

    }





    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //setupDimensions();
        //drawJoyStick(centerX,centerY);




    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //setupDimensions();
        //drawJoyStick(centerX,centerY);

    }

    public boolean onTouch(@org.jetbrains.annotations.NotNull View v, @org.jetbrains.annotations.NotNull MotionEvent e) {
        //joystickCallback=this;
        if (v.equals(this)) {
            if (e.getAction() != e.ACTION_UP) {
                float displacement = (float) Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoyStick(e.getX(), e.getY());

                    try {
                        joystickCallback.onJoystickMoved((e.getX() - centerX) , (e.getY() - centerY) , getId());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                } else {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;
                    drawJoyStick(constrainedX, constrainedY);
                    try {
                        joystickCallback.onJoystickMoved((constrainedX - centerX) , (constrainedY - centerY), getId());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }

            } else {
                drawJoyStick(centerX, centerY);
                try {
                    joystickCallback.onJoystickMoved(0, 0, getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class JoystickUsed extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String checkPacket = String.valueOf(packetBuffer[0]);
            Log.d("yo",checkPacket);
            DatagramPacket command = new DatagramPacket(packetBuffer,packetBuffer.length,ip,port);
            DatagramSocket socket= null;
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                socket.send(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Execute";
        }
    }









    public interface JoystickListener {

        void onJoystickMoved(float xPercent, float yPercent, int id) throws IOException;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {

    }*/













}
