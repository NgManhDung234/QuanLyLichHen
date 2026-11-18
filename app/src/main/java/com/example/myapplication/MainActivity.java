package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txtTotal, txtReminder;
    ListView lvAppointments;
    Button btnAdd;

    DatabaseHelper databaseHelper;

    ArrayList<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtTotal = findViewById(R.id.txtTotal);
        txtReminder = findViewById(R.id.txtReminder);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAdd = findViewById(R.id.btnAdd);

        databaseHelper = new DatabaseHelper(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddAppointmentActivity.class
            );
            startActivity(intent);
        });

        lvAppointments.setOnItemClickListener((parent, view, position, id) -> {

            Appointment a = appointmentList.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    AppointmentDetailActivity.class
            );

            intent.putExtra("appointment_id", a.getId());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAppointments();
    }

    private void loadAppointments() {

        appointmentList = databaseHelper.getAllAppointments();

        txtTotal.setText("Tổng lịch hẹn: " +
                databaseHelper.countAll());

        txtReminder.setText("Đang bật nhắc nhở: " +
                databaseHelper.countReminders());

        ArrayList<String> displayList = new ArrayList<>();

        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SimpleDateFormat outputFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Appointment a : appointmentList) {

            String displayDate = a.getDate();

            try {
                Date date = inputFormat.parse(a.getDate());

                if (date != null) {
                    displayDate = outputFormat.format(date);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String reminderText = a.isReminder()
                    ? "Có nhắc nhở"
                    : "Không nhắc nhở";

            String item = a.getTitle() +
                    "\nĐịa điểm: " + a.getLocation() +
                    "\nThời gian: " + displayDate +
                    " - " + a.getTime() +
                    "\n" + reminderText;

            displayList.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );

        lvAppointments.setAdapter(adapter);
    }
}
