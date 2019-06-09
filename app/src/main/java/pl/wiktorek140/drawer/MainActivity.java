package pl.wiktorek140.drawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    public static int color = Color.RED; //Ustawienie początkowego koloru na czerwony
    public static boolean clear = false; //Flaga przycisku clear
    public static int buttonHeight = 0;
    public static int brushSize = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonHeight = ((Button)findViewById(R.id.blue)).getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Ustawienie koloru na czerowny
    public void buttonBlack(View view) {
        color = Color.BLACK;
    }

    //Ustawienie koloru na czerowny
    public void buttonRed(View view) {
        color = Color.RED;
    }

    //Ustawienie koloru na zółty
    public void buttonYellow(View view) {
        color = Color.YELLOW;
    }

    //Ustawienie koloru na niebieski
    public void buttonBlue(View view) {
        color = Color.BLUE;
    }

    //Ustawienie koloru na zielony
    public void buttonGreen(View view) {
        color = Color.GREEN;
    }

    // Czyszczenie płótna
    public void buttonClear(View view) {
        clear = true;
    }

    public void buttonSmall(View view) {
        if(brushSize>2)
            brushSize--;
        else brushSize = 2;
    }

    public void buttonLarge(View view) {
        if(brushSize<10)
            brushSize++;
        else brushSize = 10;
    }

    protected void onSaveInstanceState(Bundle outState) {

        //outState.putInt(, surfaceViewClass.getVar());
        super.onSaveInstanceState(outState);
    }


}