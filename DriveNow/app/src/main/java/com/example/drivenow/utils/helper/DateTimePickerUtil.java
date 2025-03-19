package com.example.drivenow.utils.helper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class DateTimePickerUtil {
    private EditText editText;
    private Context context;

    // Konstruktor
    public DateTimePickerUtil(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    // Metoda za otvaranje DatePicker-a
    public void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editText.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    // Metoda za otvaranje TimePicker-a
    public void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, selectedHour, selectedMinute) -> {
            // Formatiraj vreme u AM/PM stilu
            String amPm = selectedHour >= 12 ? "PM" : "AM";
            int hourIn12Format = selectedHour % 12 == 0 ? 12 : selectedHour % 12; // Pretvaranje u 12-satni format

            String time = String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
            editText.setText(time);
        }, hour, minute, false); // false za 12-satni format

        timePickerDialog.show();
    }
}
