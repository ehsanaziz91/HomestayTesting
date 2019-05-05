package com.example.homestaytesting.HomestayBooking;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.homestaytesting.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class HomestayBookingActivity extends AppCompatActivity implements SlyCalendarDialog.Callback {

    EditText editTextDate;
    Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homestay_booking);

        editTextDate = findViewById(R.id.edtTextCalendar);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomestayBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        editTextDate.setText(selectedDay+"-"+selectedMonth+"-"+selectedYear);

                        currentDate.set(selectedYear,selectedMonth,selectedDay);
                    }
                }, year, month, day);
                datePickerDialog.show();*/
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setFirstMonday(false)
                        .setCallback(HomestayBookingActivity.this)
                        .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
            }
        });
    }

    @Override
    public void onCancelled() {
        //Nothing
    }

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours);
                firstDate.set(Calendar.MINUTE, minutes);
                Toast.makeText(
                        this,
                        new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(firstDate.getTime()),
                        Toast.LENGTH_LONG

                ).show();
            } else {
                Toast.makeText(
                        this,
                        getString(
                                R.string.period,
                                new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime()),
                                new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime())
                        ),
                        Toast.LENGTH_LONG

                ).show();
            }
        }
    }
}
