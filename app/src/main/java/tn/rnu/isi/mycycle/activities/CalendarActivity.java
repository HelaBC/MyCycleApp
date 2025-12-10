package tn.rnu.isi.mycycle.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import tn.rnu.isi.mycycle.R;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private LinearLayout navHome;
    private LinearLayout navCalendar;
    private LinearLayout navStats;
    private LinearLayout navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        ImageView btnBack = findViewById(R.id.btn_back);

        navHome = findViewById(R.id.nav_home);
        navCalendar = findViewById(R.id.nav_calendar);
        navStats = findViewById(R.id.nav_stats);
        navSettings = findViewById(R.id.nav_settings);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        setupNavigation();

        // Get current date for example
        Calendar today = Calendar.getInstance();
        CalendarDay periodStart = CalendarDay.from(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, 1);
        int periodLength = 5; // days of bleeding
        int cycleLength = 28; // full cycle length

        highlightCycle(periodStart, periodLength, cycleLength);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            Toast.makeText(this, "Selected: " + date, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navCalendar.setOnClickListener(v -> {
            // Already on calendar
        });

        navStats.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, StatisticsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void highlightCycle(CalendarDay start, int periodLength, int cycleLength) {
        List<CalendarDay> periodDays = new ArrayList<>();
        List<CalendarDay> fertileDays = new ArrayList<>();
        List<CalendarDay> ovulationDays = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        // CalendarDay month is 1-based, Calendar month is 0-based
        cal.set(start.getYear(), start.getMonth() - 1, start.getDay());

        // Period days (red background)
        for (int i = 0; i < periodLength; i++) {
            periodDays.add(CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Ovulation day: period start + (cycleLength - 14) days
        cal.set(start.getYear(), start.getMonth() - 1, start.getDay());
        cal.add(Calendar.DAY_OF_MONTH, cycleLength - 14);
        CalendarDay ovulation = CalendarDay.from(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        ovulationDays.add(ovulation);

        // Fertile window: 5 days before ovulation (green background)
        cal.add(Calendar.DAY_OF_MONTH, -5);
        for (int i = 0; i < 6; i++) { // 5 days before + ovulation day
            fertileDays.add(CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Add decorators with background colors (lower priority decorators first)
        // Fertile window - light green
        calendarView.addDecorator(new BackgroundDecorator(Color.parseColor("#A5D6A7"), fertileDays));

        // Period days - light red/pink
        calendarView.addDecorator(new BackgroundDecorator(Color.parseColor("#EF9A9A"), periodDays));

        // Ovulation day - purple (will override fertile window for that one day)
        calendarView.addDecorator(new BackgroundDecorator(Color.parseColor("#CE93D8"), ovulationDays));
    }

    // Decorator class that adds background color to days
    public static class BackgroundDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public BackgroundDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(@NonNull CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(@NonNull DayViewFacade view) {
            view.setBackgroundDrawable(new ColorDrawable(color));
        }
    }
}