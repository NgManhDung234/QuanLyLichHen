package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    EditText edtTitle, edtLocation;
    DatePicker datePicker;
    TimePicker timePicker;
    Switch switchReminder;
    Button btnSave;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_appointment);

        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        timePicker.setIs24HourView(true);

        btnSave.setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {

        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Không được để trống tên lịch hẹn");
            return;
        }

        if (location.isEmpty()) {
            edtLocation.setError("Không được để trống địa điểm");
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar appointmentTime = Calendar.getInstance();

        appointmentTime.set(
                year, month, day, hour, minute, 0
        );

        appointmentTime.set(Calendar.MILLISECOND, 0);

        Calendar currentTime = Calendar.getInstance();

        if (!appointmentTime.after(currentTime)) {
            Toast.makeText(this,
                    "Thời gian hẹn phải sau thời điểm hiện tại",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String date = String.format(
                Locale.US, "%04d-%02d-%02d",
                year, month + 1, day
        );

        String time = String.format(
                Locale.US, "%02d:%02d",
                hour, minute
        );

        boolean reminder = switchReminder.isChecked();

        Appointment appointment = new Appointment(
                title,
                location,
                date,
                time,
                reminder
        );

        long result = databaseHelper.insertAppointment(appointment);

        if (result != -1) {

            Toast.makeText(this,
                    "Lưu lịch hẹn thành công",
                    Toast.LENGTH_SHORT).show();

            finish();

        } else {

            Toast.makeText(this,
                    "Lỗi khi lưu lịch hẹn",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
