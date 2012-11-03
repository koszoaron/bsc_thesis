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
        btnA1.setText("Up/Down");
        btnA1.setOnClickListener(this);
        btnA2 = (Button)findViewById(R.id.btnA2);
        btnA2.setText("Red");
        btnA2.setOnClickListener(this);
        btnA3 = (Button)findViewById(R.id.btnA3);
        btnA3.setText("Yellow");
        btnA3.setOnClickListener(this);
        btnA4 = (Button)findViewById(R.id.btnA4);
        btnA4.setText("Green");
        btnA4.setOnClickListener(this);
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
        }
    }

}
