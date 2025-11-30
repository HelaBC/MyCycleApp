package tn.rnu.isi.mycycle;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);

        // Example: Set period start and length
        CalendarDay periodStart = CalendarDay.from(2025, 11, 27); // YYYY, MM, DD (Month is 1-indexed!)
        int periodLength = 5;

        // Example: Cycle length for fertile window
        int cycleLength = 28;

        highlightPeriodAndFertile(periodStart, periodLength, cycleLength);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            Toast.makeText(this, "Clicked: " + date, Toast.LENGTH_SHORT).show();
        });
    }

    private void highlightPeriodAndFertile(CalendarDay start, int periodLength, int cycleLength) {
        List<CalendarDay> periodDays = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(start.getYear(), start.getMonth() - 1, start.getDay());

        // Period days
        for (int i = 0; i < periodLength; i++) {
            periodDays.add(CalendarDay.from(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DATE, 1);
        }
        calendarView.addDecorator(new EventDecorator(Color.RED, periodDays));

        // Ovulation day (middle of cycle)
        cal.set(start.getYear(), start.getMonth() - 1, start.getDay());
        cal.add(Calendar.DATE, cycleLength / 2);
        CalendarDay ovulationDay = CalendarDay.from(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        calendarView.addDecorator(new EventDecorator(Color.MAGENTA, List.of(ovulationDay)));

        // Fertile window: 5 days before ovulation
        cal.add(Calendar.DATE, -5);
        List<CalendarDay> fertileDays = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fertileDays.add(CalendarDay.from(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DATE, 1);
        }
        calendarView.addDecorator(new EventDecorator(Color.GREEN, fertileDays));
    }

    // Decorator for coloring dates
    public static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(@NonNull CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(@NonNull DayViewFacade view) {
            view.addSpan(new DotSpan(10, color));
        }
    }
}
