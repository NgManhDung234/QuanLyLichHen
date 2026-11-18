package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends AppCompatActivity {

    TextView txtTitle, txtLocation, txtDateTime, txtReminderStatus;

    Switch switchReminder;

    Button btnBack, btnDelete;

    DatabaseHelper databaseHelper;

    int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment_detail);

        txtTitle = findViewById(R.id.txtDetailTitle);
        txtLocation = findViewById(R.id.txtDetailLocation);
        txtDateTime = findViewById(R.id.txtDetailDateTime);
        txtReminderStatus = findViewById(R.id.txtReminderStatus);

        switchReminder = findViewById(R.id.switchDetailReminder);

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        databaseHelper = new DatabaseHelper(this);

        appointmentId = getIntent().getIntExtra(
                "appointment_id", -1
        );

        loadAppointment();

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            databaseHelper.updateReminder(
                    appointmentId, isChecked
            );

            updateReminderText(isChecked);

            Toast.makeText(this,
                    "Đã cập nhật nhắc nhở",
                    Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> deleteAppointment());
    }

    private void loadAppointment() {

        Appointment a = databaseHelper.getAppointmentById(appointmentId);

        if (a == null) {
            Toast.makeText(this,
                    "Không tìm thấy lịch hẹn",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        txtTitle.setText(a.getTitle());

        txtLocation.setText("Địa điểm: " + a.getLocation());

        String displayDate = a.getDate();

        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = inputFormat.parse(a.getDate());

            if (date != null) {
                displayDate = outputFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtDateTime.setText(
                "Thời gian: " + displayDate + " · " + a.getTime()
        );

        switchReminder.setChecked(a.isReminder());

        updateReminderText(a.isReminder());
    }

    private void updateReminderText(boolean isChecked) {

        if (isChecked) {
            txtReminderStatus.setText("Đang bật");
        } else {
            txtReminderStatus.setText("Đang tắt");
        }
    }

    private void deleteAppointment() {

        databaseHelper.deleteAppointment(appointmentId);

        Toast.makeText(this,
                "Đã xóa lịch hẹn",
                Toast.LENGTH_SHORT).show();

        finish();
    }
}
