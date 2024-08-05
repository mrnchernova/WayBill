package by.ie.waybill;

import androidx.appcompat.app.AppCompatActivity;

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

    EditText etRashod, etFuelPrice;
    CheckBox checkBoxZima, checkBoxSrok;
    EditText etVesGruza, etRasstoyanieVse, etRasstoyanieVes, etOfferedPrice;
    Button buttonResult, btnProfit;
    TextView tvTekRashodResult, tvResult, tvMoneyRes, tvMoneyForKm, tvProfit;
    ImageButton imgBtnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(FILE_NAME, MODE_PRIVATE);


        etRashod = findViewById(R.id.etRashod);
        checkBoxZima = findViewById(R.id.checkBoxZima);
        checkBoxSrok = findViewById(R.id.checkBoxSrok);
        etVesGruza = findViewById(R.id.etVesGruza);
        etRasstoyanieVse = findViewById(R.id.etRasstoyanieVse);
        etRasstoyanieVes = findViewById(R.id.etRasstoyanieVes);
        buttonResult = findViewById(R.id.buttonResult);
        tvTekRashodResult = findViewById(R.id.tvTekRashodResult);
        tvResult = findViewById(R.id.tvResult);

        btnProfit = findViewById(R.id.btnProfit);                   //!!!
        etOfferedPrice = findViewById(R.id.etOfferedPrice);         //!!!
        tvProfit = findViewById(R.id.tvProfit);                     //!!!

        etFuelPrice = findViewById(R.id.etFuelPrice);
        tvMoneyRes = findViewById(R.id.tvMoneyRes);
        tvMoneyForKm = findViewById(R.id.tvMoneyForKm);

        imgBtnSave = findViewById(R.id.imgBtnSave);


        if (settings.contains(ZIMA)) {
            checkBoxZima.setChecked(settings.getBoolean(ZIMA, false));
        }
        if (settings.contains(SROK)) {
            checkBoxSrok.setChecked(settings.getBoolean(SROK, false));
        }

        if (settings.contains(FUEL_CONSUMPTION)) {
            etRashod.setText(settings.getString(FUEL_CONSUMPTION, "24.0"));
        }

        if (settings.contains(PRICE_DT)) {
            etFuelPrice.setText(settings.getString(PRICE_DT, "2.36"));
        }


        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(ZIMA, checkBoxZima.isChecked());
                editor.putBoolean(SROK, checkBoxSrok.isChecked());
                editor.putString(FUEL_CONSUMPTION, etRashod.getText().toString());
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
                double tekRashod = getCurrentConsumption(); //текущий расход
                tvTekRashodResult.setText(String.format("%.2f", tekRashod));

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
                double tekRashod = getCurrentConsumption(); //текущий расход
                tvTekRashodResult.setText(String.format("%.2f", tekRashod));

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
        double linRashod = Double.parseDouble(etRashod.getText().toString());
        double zimaRashod = 0;
        double srokRashod = 0;
        if (checkBoxZima.isChecked()) zimaRashod = linRashod * 10 / 100;
        if (checkBoxSrok.isChecked()) srokRashod = linRashod * 8 / 100;
        double tekRashod = linRashod + zimaRashod + srokRashod;
        return tekRashod;
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
        }else{
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

        double tekRashod = getCurrentConsumption();
        double vesGruza = getVesGruza();
        double rasstVse = getRasstVse();
        double rasstVes = getRasstVes();

        if (rasstVse <= 0) {
            showInfo("Не указано расстояние");
        } else {
            if (rasstVse < rasstVes) {
                showInfo("Общее расстояние больше, чем расстояние с грузом!");
            } else {
                double vsegoPustoy = rasstVse * tekRashod / 100; //всего проехал и потралит литров
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


}