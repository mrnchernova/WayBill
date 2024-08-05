package by.ie.waybill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CalculationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);


        /** это работает
         TextView tvPrint = findViewById(R.id.tvPrint);
         tvPrint.setText("Hello Kitty!");
         */
        TextView tvPrint = new TextView(this);
        tvPrint.setTextSize(26);
        tvPrint.setPadding(16, 16, 16, 16);

        Bundle arguments = getIntent().getExtras();

        if (arguments != null) {
            String fuelPrice = arguments.get("FuelPrice").toString();
            tvPrint.setText("FuelPrice: " + fuelPrice);
        }
        setContentView(tvPrint);

    }

    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}