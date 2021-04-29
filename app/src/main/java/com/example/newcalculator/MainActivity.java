package com.example.newcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CalculatorActions calculator;
    private TextView text;
    private TextView operation;

    private static final int REQUEST_CODE_SETTING_ACTIVITY = 99;
    private static final String NAME_SHARED_PREFERENCE = "MAIN";
    private static final String APP_THEME = "APP_THEME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getAppTheme(R.style.BlueGreyStyle));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonChooseTheme = findViewById(R.id.buttonChooseTheme);
        buttonChooseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent runChoosingTheme = new Intent(MainActivity.this, ChoosingThemeActivity.class);
                startActivityForResult(runChoosingTheme, REQUEST_CODE_SETTING_ACTIVITY);
            }
        });

        int[] numberIds = new int[]{
                R.id.button0,
                R.id.button1,
                R.id.button2,
                R.id.button3,
                R.id.button4,
                R.id.button5,
                R.id.button6,
                R.id.button7,
                R.id.button8,
                R.id.button9
        };

        int[] actionsIds = new int[]{
                R.id.buttonAdd,
                R.id.buttonSub,
                R.id.buttonMul,
                R.id.buttonDiv,
                R.id.buttonEqual,
                R.id.buttonClear,
                R.id.buttonBackSpace,
                R.id.buttonDot,
                R.id.buttonToggleSign,
                R.id.buttonPercent
        };

        text = findViewById(R.id.number);
        operation = findViewById(R.id.operation);

        calculator = new CalculatorActions();

        View.OnClickListener actionButtonClickListener = view -> {
            calculator.onActionPressed(view.getId());
            operation.setText(calculator.getOperation());
            text.setText(calculator.getText());
        };

        for (int i = 0; i < numberIds.length; i++) {
            int index = i;
            findViewById(numberIds[i]).setOnClickListener( view -> {
                calculator.onNumPressed(index);
                operation.setText(calculator.getOperation());
                text.setText(calculator.getText());
            });
        }

        for (int i = 0; i < actionsIds.length; i++) {
            findViewById(actionsIds[i]).setOnClickListener(actionButtonClickListener);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_SETTING_ACTIVITY) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == RESULT_OK) {
            recreate();
        }
    }

    protected int getAppTheme(int codeStyle) {
        return ChoosingThemeActivity.codeStyleToStyleId(getCodeStyle(codeStyle));
    }

    protected int getCodeStyle(int codeStyle) {
        SharedPreferences sharedPref = getSharedPreferences(NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        return sharedPref.getInt(APP_THEME, codeStyle);
    }
}