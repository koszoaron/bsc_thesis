package com.github.koszoaron.maguscada;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ScadaActivity extends Activity implements OnClickListener {
    
    private ImageView ivMain;
    private ImageView ivSemaphoreRed;
    private ImageView ivSemaphoreYellow;
    private ImageView ivSemaphoreGreen;
    
    private Button btnA1;
    private Button btnA2;
    private Button btnA3;
    private Button btnA4;
    private Button btnA5;
    private Button btnB1;
    private Button btnB2;
    private Button btnB3;
    private Button btnB4;
    private Button btnB5;
    
    private int imageLevel = 0;
    private int semaphoreRed = 0;
    private int semaphoreYellow = 0;
    private int semaphoreGreen = 0;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_scada);
        
        ivMain = (ImageView)findViewById(R.id.ivMain);
        ivMain.setImageLevel(imageLevel);
        ivSemaphoreRed = (ImageView)findViewById(R.id.ivSemaphoreRed);
        ivSemaphoreRed.setImageLevel(semaphoreRed);
        ivSemaphoreYellow = (ImageView)findViewById(R.id.ivSemaphoreYellow);
        ivSemaphoreYellow.setImageLevel(semaphoreYellow);
        ivSemaphoreGreen = (ImageView)findViewById(R.id.ivSemaphoreGreen);
        ivSemaphoreGreen.setImageLevel(semaphoreGreen);
        
        btnA1 = (Button)findViewById(R.id.btnA1);
        btnA2 = (Button)findViewById(R.id.btnA2);
        btnA3 = (Button)findViewById(R.id.btnA3);
        btnA4 = (Button)findViewById(R.id.btnA4);
        btnA5 = (Button)findViewById(R.id.btnA5);
        btnB1 = (Button)findViewById(R.id.btnB1);
        btnB2 = (Button)findViewById(R.id.btnB2);
        btnB3 = (Button)findViewById(R.id.btnB3);
        btnB4 = (Button)findViewById(R.id.btnB4);
        btnB5 = (Button)findViewById(R.id.btnB5);
        
        btnA1.setOnClickListener(this);
        btnA2.setOnClickListener(this);
        btnA3.setOnClickListener(this);
        btnA4.setOnClickListener(this);
        btnA5.setOnClickListener(this);
        btnB1.setOnClickListener(this);
        btnB2.setOnClickListener(this);
        btnB3.setOnClickListener(this);
        btnB4.setOnClickListener(this);
        btnB5.setOnClickListener(this);
        
        btnA1.setText("Up/Down");
        btnA2.setText("Red");
        btnA3.setText("Yellow");
        btnA4.setText("Green");
        btnB5.setText("...");

    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v == btnA1) {
            
            if (imageLevel == 0) {
                imageLevel = 1;
            } else if (imageLevel == 1){
                imageLevel = 0;
            }
            ivMain.setImageLevel(imageLevel);
        
        } else if (v == btnA2) {
        
            if (semaphoreRed == 0) {
                semaphoreRed = 1;
            } else if (semaphoreRed == 1){
                semaphoreRed = 0;
            }
            ivSemaphoreRed.setImageLevel(semaphoreRed);
        
        } else if (v == btnA3) {
            
            if (semaphoreYellow == 0) {
                semaphoreYellow = 1;
            } else if (semaphoreYellow == 1){
                semaphoreYellow = 0;
            }
            ivSemaphoreYellow.setImageLevel(semaphoreYellow);
        
        } else if (v == btnA4) {
            
            if (semaphoreGreen == 0) {
                semaphoreGreen = 1;
            } else if (semaphoreGreen == 1){
                semaphoreGreen = 0;
            }
            ivSemaphoreGreen.setImageLevel(semaphoreGreen);
        
        } else if (v == btnA5) {
            
            
        } else if (v == btnB1) {
            
            
        } else if (v == btnB2) {
            
            
        } else if (v == btnB3) {
            
            
        } else if (v == btnB4) {
            
            
        } else if (v == btnB5) {
            
            
        }
    }
}
