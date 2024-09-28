package com.truong.miniproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SecondActivity extends AppCompatActivity {

    private SeekBar horse1SeekBar, horse2SeekBar, horse3SeekBar;
    private Button startButton, resetButton, resetMoneyButton, logoutButton;
    private EditText betHorse1, betHorse2, betHorse3, resultTextNumber;
    private CheckBox horse1CheckBox, horse2CheckBox, horse3CheckBox;
    private Random random = new Random();
    private Handler handler = new Handler();
    private int coins = 100;
    private  Button btnMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        horse1SeekBar = findViewById(R.id.road_1);
        horse2SeekBar = findViewById(R.id.road_2);
        horse3SeekBar = findViewById(R.id.road_3);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
        resetMoneyButton = findViewById(R.id.resetMoneyButton);
        logoutButton = findViewById(R.id.logoutButton);
        resultTextNumber = findViewById(R.id.resultTextNumber);
        betHorse1 = findViewById(R.id.bet_horse_1);
        betHorse2 = findViewById(R.id.bet_horse_2);
        betHorse3 = findViewById(R.id.bet_horse_3);
        horse1CheckBox = findViewById(R.id.horse_1);
        horse2CheckBox = findViewById(R.id.horse_2);
        horse3CheckBox = findViewById(R.id.horse_3);
        btnMusic = findViewById(R.id.musicButton);

        resultTextNumber.setEnabled(false);
        resultTextNumber.setText(String.valueOf(coins));

        disableSeekBars();
        //music button
        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, BackgroundService.class);
                startService(intent);
            }
        });

        startButton.setOnClickListener(v -> {
            resetProgress();
            if (!validateBets()) {
                return;
            }

            startButton.setEnabled(false);
            resetButton.setEnabled(false);
            resetMoneyButton.setEnabled(false);
            runRace();
        });

        resetButton.setOnClickListener(v -> resetRace());
        resetMoneyButton.setOnClickListener(v -> {
            coins = 100;
            resultTextNumber.setText(String.valueOf(coins));
        });

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }


    private boolean validateBets() {
        if (!horse1CheckBox.isChecked() && !horse2CheckBox.isChecked() && !horse3CheckBox.isChecked()) {
            Toast.makeText(SecondActivity.this, "Please select at least one horse to bet on.", Toast.LENGTH_SHORT).show();
            return false;
        }

        int totalBet = 0;

        if (horse1CheckBox.isChecked()) {
            String bet1 = betHorse1.getText().toString();
            if (bet1.isEmpty() || Integer.parseInt(bet1) <= 0) {
                Toast.makeText(SecondActivity.this, "Please enter a valid bet for Horse 1.", Toast.LENGTH_SHORT).show();
                return false;
            }
            totalBet += Integer.parseInt(bet1);
        }

        if (horse2CheckBox.isChecked()) {
            String bet2 = betHorse2.getText().toString();
            if (bet2.isEmpty() || Integer.parseInt(bet2) <= 0) {
                Toast.makeText(SecondActivity.this, "Please enter a valid bet for Horse 2.", Toast.LENGTH_SHORT).show();
                return false;
            }
            totalBet += Integer.parseInt(bet2);
        }

        if (horse3CheckBox.isChecked()) {
            String bet3 = betHorse3.getText().toString();
            if (bet3.isEmpty() || Integer.parseInt(bet3) <= 0) {
                Toast.makeText(SecondActivity.this, "Please enter a valid bet for Horse 3.", Toast.LENGTH_SHORT).show();
                return false;
            }
            totalBet += Integer.parseInt(bet3);
        }

        if (totalBet > coins) {
            Toast.makeText(SecondActivity.this, "Not enough coins for this bet!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void runRace() {
        horse1CheckBox.setEnabled(false);
        horse2CheckBox.setEnabled(false);
        horse3CheckBox.setEnabled(false);
        betHorse1.setEnabled(false);
        betHorse2.setEnabled(false);
        betHorse3.setEnabled(false);
        handler.post(new Runnable() {
            @Override
            public void run() {
                int progress1 = horse1SeekBar.getProgress();
                int progress2 = horse2SeekBar.getProgress();
                int progress3 = horse3SeekBar.getProgress();

                if (progress1 >= 100 || progress2 >= 100 || progress3 >= 100) {
                    determineWinner(progress1, progress2, progress3);
                } else {
                    horse1SeekBar.setProgress(progress1 + random.nextInt(5));
                    horse2SeekBar.setProgress(progress2 + random.nextInt(5));
                    horse3SeekBar.setProgress(progress3 + random.nextInt(5));

                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    private void determineWinner(int progress1, int progress2, int progress3) {
        String winner = "";
        int winningHorse = 0;

        if (progress1 >= 100) {
            winner = "Horse 1";
            winningHorse = 1;
        } else if (progress2 >= 100) {
            winner = "Horse 2";
            winningHorse = 2;
        } else if (progress3 >= 100) {
            winner = "Horse 3";
            winningHorse = 3;
        }

        calculateWinnings(winningHorse);
        Toast.makeText(SecondActivity.this, winner + " wins the race!", Toast.LENGTH_SHORT).show();
        resultTextNumber.setText(String.valueOf(coins));

        horse1CheckBox.setEnabled(true);
        horse2CheckBox.setEnabled(true);
        horse3CheckBox.setEnabled(true);
        betHorse1.setEnabled(true);
        betHorse2.setEnabled(true);
        betHorse3.setEnabled(true);

        startButton.setEnabled(true);
        resetButton.setEnabled(true);
        resetMoneyButton.setEnabled(true);
    }

    private void calculateWinnings(int winningHorse) {
        int totalBet = 0;

        if (winningHorse == 1 && horse1CheckBox.isChecked()) {
            int bet = Integer.parseInt(betHorse1.getText().toString());
            coins += bet * 2;
        }
        if (winningHorse == 2 && horse2CheckBox.isChecked()) {
            int bet = Integer.parseInt(betHorse2.getText().toString());
            coins += bet * 2;
        }
        if (winningHorse == 3 && horse3CheckBox.isChecked()) {
            int bet = Integer.parseInt(betHorse3.getText().toString());
            coins += bet * 2;
        }

        if (horse1CheckBox.isChecked()) {
            totalBet += Integer.parseInt(betHorse1.getText().toString());
        }
        if (horse2CheckBox.isChecked()) {
            totalBet += Integer.parseInt(betHorse2.getText().toString());
        }
        if (horse3CheckBox.isChecked()) {
            totalBet += Integer.parseInt(betHorse3.getText().toString());
        }
        coins -= totalBet;
    }

    private void resetRace() {
        horse1SeekBar.setProgress(0);
        horse2SeekBar.setProgress(0);
        horse3SeekBar.setProgress(0);
        betHorse1.setText("");
        betHorse2.setText("");
        betHorse3.setText("");
        horse1CheckBox.setChecked(false);
        horse2CheckBox.setChecked(false);
        horse3CheckBox.setChecked(false);
        resultTextNumber.setText(String.valueOf(coins));
        startButton.setEnabled(true);
    }

    private void resetProgress() {
        horse1SeekBar.setProgress(0);
        horse2SeekBar.setProgress(0);
        horse3SeekBar.setProgress(0);
        resultTextNumber.setText(String.valueOf(coins));
    }

    private void disableSeekBars() {
        horse1SeekBar.setEnabled(false);
        horse2SeekBar.setEnabled(false);
        horse3SeekBar.setEnabled(false);
    }


    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to quit the game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
