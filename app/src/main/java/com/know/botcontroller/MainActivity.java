package com.know.botcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LogPrinter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public  class MainActivity extends AppCompatActivity implements Main2Activity.JoystickListener  {
    EditText ipaddress;
    public static String ipadd;
    Button shoot;
    InetAddress ipnew;
    byte[] packetBuffer = new byte[512];
    int port1=1111;


    //MainActivity IP = new MainActivity();






    public void enter(View view) throws UnknownHostException {
        ipaddress = (EditText) findViewById(R.id.editText2);
        ipadd = ipaddress.getText().toString();
        ipnew=InetAddress.getByName(ipadd);
        String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";

        String pattern = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern r = Pattern.compile(pattern);

        Matcher m= r.matcher(ipadd);

        if(m.find()){
            setContentView(R.layout.activity_main2);
        }

        else{
            String alert="Please enter valid IP address";
            Toast.makeText(this,alert,Toast.LENGTH_LONG).show();
        }




    }



    @SuppressLint("ClickableViewAccessibility")
    public void shoot(View view){


            shoot=(Button) findViewById(R.id.button3);
            //shoot.setBackgroundColor(Color.GREEN);
            shoot.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        shoot.setBackgroundColor(Color.TRANSPARENT);
                        try {
                            command();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(event.getAction()!=MotionEvent.ACTION_UP){
                        shoot.setBackgroundColor(0xFFCDDC39);
                        try {
                            reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    return true;
                }
            });

    }

    public void editIP(View view){
        setContentView(R.layout.activity_main3);
        EditText edit = findViewById(R.id.editText2);
        edit.setText(ipadd);
    }


    public void command() throws IOException {
        new SendDataByButton().execute("");
    }

    public void reset() throws IOException {
        new ResetDataByButton().execute("");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main3);
        Main2Activity.JoystickListener joystickCallback = new Main2Activity.JoystickListener() {
            @Override
            public void onJoystickMoved(float xPercent, float yPercent, int id) throws IOException {
                Log.d("Main Method", "X percent" + xPercent + "Y percent" + yPercent);


            }
        } ;

        /*int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if(permission!= PackageManager.PERMISSION_GRANTED){
            Log.i("permission denied","");
            makeRequest();
        }*/









    }
   /* public void makeRequest(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},);
    }*/

   @SuppressLint("StaticFieldLeak")
   private  class SendDataByButton extends AsyncTask<String,Void,String>{

       @Override
       protected String doInBackground(String... strings) {
           packetBuffer[0]=59;
           String checkPacket = String.valueOf(packetBuffer[0]);
           Log.d("yo",checkPacket);
           DatagramPacket command = new DatagramPacket(packetBuffer,packetBuffer.length,ipnew,port1);
           DatagramSocket socket = null;
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

           return "Executed";

       }
   }


   @SuppressLint("StaticFieldLeak")
   private class ResetDataByButton extends AsyncTask<String,Void,String>{

       @Override
       protected String doInBackground(String... strings) {
           packetBuffer[0]=60;
           String checkPacket = String.valueOf(packetBuffer[0]);
           Log.d("yo",checkPacket);
           Log.d("yo",ipadd);
           DatagramPacket command = new DatagramPacket(packetBuffer,packetBuffer.length,ipnew,port1);
           //DatagramSocket socket = null;
           try {
              DatagramSocket socket = new DatagramSocket();
              socket.send(command);

           } catch (SocketException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }


           return "Executed";
       }
   }

   public static String getIP(){
       return ipadd;
   }














    //setContentView(joystick);





    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        Log.d("Main Method","X percent" + xPercent + "Y percent" + yPercent);

    }

    //@Override
    //public void onJoystickMoved(float xPercent, float yPercent, int id) {
        //Log.d("Main Method","X percent" + xPercent + "Y percent" + yPercent);

    //}




    //@Override
    //public void onJoystickMoved(float xPercent,float yPercent, int id){
        //Log.d("Main Method","X percent" + xPercent + "Y percent" + yPercent);
    //}


}
