package com.example.mysocket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class SubActivity extends AppCompatActivity {

    Button btnUpdate;
    Button btnSubExit;
    ImageView ivCanvas;

    public Handler handler = new Handler();

    String Mat;
    String[] Matx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        ivCanvas = (ImageView)findViewById(R.id.ivCanvas);
        Bitmap mBitmap = Bitmap.createBitmap(350,350, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(0x00ffffff);
        Paint mPaint = new Paint();
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = "Hello";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
                try {
                    Toast.makeText(SubActivity.this,"수신중...",
                            Toast.LENGTH_SHORT).show();
                    Thread.sleep(2000);
                    Matx = Mat.split(",");
                    for (int i=0; i < 2500 ; i++)
                    {
                        int line = Math.floorMod(i,50);
                        int item = Math.floorDiv(i,50);
                        mPaint.setColor(0x00000000);
                        if (Integer.parseInt(Matx[i]) == 3)
                        {
                            mPaint.setColor(0x66ff0000);
                        }
                        else if (Integer.parseInt(Matx[i]) == 2)
                        {
                            mPaint.setColor(0x66f8652b);
                        }
                        else if (Integer.parseInt(Matx[i]) == 1)
                        {
                            mPaint.setColor(0x66ffff00);
                        }
                        mCanvas.drawRect(line * 7, item * 7,
                                (line + 1) * 7, (item + 1) * 7, mPaint);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ivCanvas.setImageBitmap(mBitmap);
            }
        });

        btnSubExit = (Button) findViewById(R.id.btnSubExit);
        btnSubExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
                builder.setMessage("설명을 보시겠습니까?");
                builder.setTitle("안내")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("안내");
                alertDialog.show();
            }
        });

    }

    public void printClientLog(final String data) {
        Log.d("MainActivity", data);
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                tvLog.append(data+"\n");
//            }
//        });
    }

    public void send(String data) {
        try {
            int portNumber = 4000;
            Socket socket = new Socket("192.168.0.33", portNumber);
            printClientLog("소켓 연결함");

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            byte b = 0x15;
            outputStream.writeByte(b);
            outputStream.flush();
            printClientLog("데이터 전송함");

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            Mat = inputStream.readLine();
            printClientLog("서버로부터 받음: "+Mat.split(",")[0]);
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


