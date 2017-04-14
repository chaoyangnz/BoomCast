package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.util.DateUtil;

public class StatisticsActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.total_listen_time) ImageView totalListenTime;
    @BindView(R.id.chart_listen_by_date) LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        addToolbar();
        setTitle("Daily Report");

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(80) /* size in px */
                .endConfig().buildRound(DateUtil.formatDuration(getTodayListenTime()), R.color.colorAccent);
        totalListenTime.setImageDrawable(drawable);

        List<ListenPerDay> listensByDate = getListenTimeByDate();

        Map<String, Long> dateDurationMap = new HashMap();
        for(ListenPerDay listenPerDay : listensByDate) {
            dateDurationMap.put(listenPerDay.date, listenPerDay.duration);
        }
        List<String> fifteenDaysEarlier = lastFifteenDays();

        List<Entry> entries = new ArrayList<>();
        Map<Integer, String> labels = new HashMap<>();
        for(int i = 0; i < fifteenDaysEarlier.size(); ++i) {
            String date = fifteenDaysEarlier.get(i);
            Long duration = dateDurationMap.get(date);
            if(duration == null) duration = 0L;
            entries.add(new Entry(i, duration/(1000*60f)));
            labels.put(i, date.substring(5));
        }

        LineDataSet dataset = new LineDataSet(entries, "Time per day (minutes)");
        dataset.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData data = new LineData(dataset);
        // set X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter((value, axisBase) ->  {
            String label = labels.get((int)value);
            return label != null ? label : "";
        });

        // set Y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        // set legend
        Legend legend = lineChart.getLegend();
        xAxis.setTextColor(Color.WHITE);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisRight.setTextColor(Color.WHITE);
        legend.setTextColor(Color.WHITE);
        data.setValueTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        lineChart.getDescription().setText("");

        lineChart.setData(data); // set the data and
    }

    public List<ListenPerDay> getListenTimeByDate() {
        return SugarRecord.findWithQuery(ListenPerDay.class,
                "select DATE, sum(duration) as DURATION, 0 as ID from listen_log group by date limit 15");
    }

    public static class ListenPerDay {
        private String date;
        private long duration;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    private long getTodayListenTime() {
        String today = DateUtil.formatDate(new Date());
        List<ListenPerDay> listensToday = SugarRecord.findWithQuery(ListenPerDay.class,
                "select DATE, sum(duration) as DURATION, 0 as ID from listen_log where DATE = ? group by date ", today);
        return listensToday.isEmpty() ? 0L : listensToday.get(0).getDuration();
    }

    private static List<String> lastFifteenDays() {
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -15);

        List<String> list = new ArrayList<>();
        for(int i = 0; i < 15; ++i) {
            cal.add(Calendar.DAY_OF_MONTH, 1);

            list.add(DateUtil.formatDate(cal.getTime()));
        }

        return list;
    }
}
