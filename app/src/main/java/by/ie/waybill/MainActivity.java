package by.ie.waybill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final String FILE_NAME = "MY_FILENAME";
    private static final String ZIMA = "zimaChecked";
    private static final String SROK = "srokChecked";
    private static final String PRICE_DT = "priceDT";
    private static final String FUEL_CONSUMPTION = "fuelConsumption";


    SharedPreferences settings;

    EditText etFuelConsumption, etFuelPrice;
    CheckBox checkBoxWinter, checkBoxLifetime;
    EditText etVesGruza, etRasstoyanieVse, etRasstoyanieVes, etOfferedPrice;
    Button buttonResult, btnProfit;
    TextView tvCurrentFuelConsumption, tvResult, tvMoneyRes, tvMoneyForKm, tvProfit;
    ImageButton imgBtnSave;
    ImageButton imgBtnEdit3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(FILE_NAME, MODE_PRIVATE);


        etFuelConsumption = findViewById(R.id.etCurrentFuelConsumption);
        checkBoxWinter = findViewById(R.id.checkBoxWinter);
        checkBoxLifetime = findViewById(R.id.checkBoxLifetime);
        etVesGruza = findViewById(R.id.etVesGruza);
        etRasstoyanieVse = findViewById(R.id.etRasstoyanieVse);
        etRasstoyanieVes = findViewById(R.id.etRasstoyanieVes);
        buttonResult = findViewById(R.id.buttonResult);
        tvCurrentFuelConsumption = findViewById(R.id.tvCurrentFuelConsumption);
        tvResult = findViewById(R.id.tvResult);

        btnProfit = findViewById(R.id.btnProfit);                   //!!!
        etOfferedPrice = findViewById(R.id.etOfferedPrice);         //!!!
        tvProfit = findViewById(R.id.tvProfit);                     //!!!

        etFuelPrice = findViewById(R.id.etFuelPrice);
        tvMoneyRes = findViewById(R.id.tvMoneyRes);
        tvMoneyForKm = findViewById(R.id.tvMoneyForKm);

        imgBtnSave = findViewById(R.id.imgBtnSave);
        imgBtnEdit3 = findViewById(R.id.imgBtnEdit3);


        if (settings.contains(ZIMA)) {
            checkBoxWinter.setChecked(settings.getBoolean(ZIMA, false));
        }
        if (settings.contains(SROK)) {
            checkBoxLifetime.setChecked(settings.getBoolean(SROK, false));
        }

        if (settings.contains(FUEL_CONSUMPTION)) {
            etFuelConsumption.setText(settings.getString(FUEL_CONSUMPTION, "24.0"));
        }

        if (settings.contains(PRICE_DT)) {
            etFuelPrice.setText(settings.getString(PRICE_DT, "2.44"));
        }


        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(ZIMA, checkBoxWinter.isChecked());
                editor.putBoolean(SROK, checkBoxLifetime.isChecked());
                editor.putString(FUEL_CONSUMPTION, etFuelConsumption.getText().toString());
                editor.putString(PRICE_DT, etFuelPrice.getText().toString());
                editor.commit();
                showSave("Сохранено");
            }
        });


        /**
         * Основной блок расчетов
         *
         * Закомментирован
         *
         */
        buttonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Part I */
                double currentConsumption = getCurrentConsumption(); //текущий расход
                tvCurrentFuelConsumption.setText(String.format("%.2f", currentConsumption));

                /* Part II */
                double fuelPrice = getFuelPrice();
                tvMoneyRes.setText(String.format("%.2f", fuelPrice));
                getMoneyForKm(fuelPrice);

            }
        });


        /**
         * Считает выгодна поездка или нет
         */
        btnProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Part I */
                double currentConsumption = getCurrentConsumption(); //текущий расход
                tvCurrentFuelConsumption.setText(String.format("%.2f", currentConsumption));

                /* Part II  + Profit*/

                double fuelPrice = getFuelPrice();
                tvMoneyRes.setText(String.format("%.2f", fuelPrice));
                getMoneyForKm(fuelPrice);

                if (!etOfferedPrice.getText().toString().isEmpty()) {
                    double offeredPrice = Double.parseDouble(etOfferedPrice.getText().toString());


                    tvProfit.setText(String.format("%.2f", offeredPrice - fuelPrice));

                } else {
                    showInfo("Предложение продавца 0");
                }
            }
        });
    }


    private double getCurrentConsumption() {
        double linearConsumption = Double.parseDouble(etFuelConsumption.getText().toString());
        double winterConsumption = 0;
        double lifetimeConsumption = 0;
        if (checkBoxWinter.isChecked()) winterConsumption = linearConsumption * 10 / 100;
        if (checkBoxLifetime.isChecked()) lifetimeConsumption = linearConsumption * 8 / 100;
        double currentConsumption = linearConsumption + winterConsumption + lifetimeConsumption;
        return currentConsumption;
    }

    private double getVesGruza() {
        double vesGruza = 0;
        if (!etVesGruza.getText().toString().isEmpty()) {
            vesGruza = Double.parseDouble(etVesGruza.getText().toString());
        }
        return vesGruza;
    }

    private double getRasstVse() {
        double rasstVse = 0;
        if (!etRasstoyanieVse.getText().toString().isEmpty()) {
            rasstVse = Double.parseDouble(etRasstoyanieVse.getText().toString());
        } else {
            etRasstoyanieVse.setText("0");
        }
        return rasstVse;
    }

    private double getRasstVes() {
        double rasstVes = 0;
        if (!etRasstoyanieVes.getText().toString().isEmpty()) {
            rasstVes = Double.parseDouble(etRasstoyanieVes.getText().toString());
        }
        return rasstVes;
    }


    private double getFuelPrice() {

        double currentConsumption = getCurrentConsumption();
        double vesGruza = getVesGruza();
        double rasstVse = getRasstVse();
        double rasstVes = getRasstVes();

        if (rasstVse <= 0) {
            showInfo("Не указано расстояние");
        } else {
            if (rasstVse < rasstVes) {
                showInfo("Общее расстояние больше, чем расстояние с грузом!");
            } else {
                double vsegoPustoy = rasstVse * currentConsumption / 100; //всего проехал и потралит литров
                double vsegoVes = 1.3 * vesGruza * rasstVes / 100;

                double fuelPrice = Double.parseDouble(etFuelPrice.getText().toString()) * (vsegoPustoy + vsegoVes);

                tvResult.setText(String.format("%.2f", vsegoPustoy + vsegoVes));
                return fuelPrice;
            }
        }
        double fuelPrice = 0;
        return fuelPrice;

    }


    private void getMoneyForKm(double fuelPrice) {
        double rasstVse = Double.parseDouble(etRasstoyanieVse.getText().toString());
        tvMoneyForKm.setText(String.format("%.2f", fuelPrice / rasstVse));
    }

    private void showInfo(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void showSave(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    //        imgBtnEdit3 Для окна с расчетами
//        imgBtnEdit3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//        OR
    public void startCalculationRes(View v) {

        EditText fuelPrice = findViewById(R.id.etFuelPrice);
        String etFuelPrice = fuelPrice.getText().toString();


        Intent intent = new Intent(this, CalculationActivity.class);
        intent.putExtra("FuelPrice", etFuelPrice);
        startActivity(intent);

    }


}