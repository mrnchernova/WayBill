package by.ie.waybill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CalculationActivity extends AppCompatActivity {

    TextView tvPrint, titleFormula;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        tvPrint = (TextView) findViewById(R.id.tvPrint);
        titleFormula = (TextView) findViewById(R.id.titleFormula);

        tvPrint.setPadding(16, 16, 16, 16);
        tvPrint.setText(getIntent().getStringExtra("formula"));
    }

}