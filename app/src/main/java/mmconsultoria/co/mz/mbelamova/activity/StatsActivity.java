package mmconsultoria.co.mz.mbelamova.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import butterknife.ButterKnife;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.adapter.HistoryAdapterByGam;
import mmconsultoria.co.mz.mbelamova.fragment.BarChartFrag;
import mmconsultoria.co.mz.mbelamova.fragment.PieChartFrag;
import mmconsultoria.co.mz.mbelamova.listviewitems.ChartItem;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Person;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends BaseActivity {

    public Toolbar toolbar;

    public TabLayout tabs;
    LineChart chart;
    RecyclerView lista_cacrds;
    private HistoryAdapterByGam adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_chart);
        chart =  findViewById(R.id.chart);

        ViewPager pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);


        PageAdapter a = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(a);




//        ListView lv = findViewById(R.id.listView1);
//
//        ArrayList<ChartItem> list = new ArrayList<>();
//
//        list.add(new LineChartItem(generateDataLine(0 + 1), getApplicationContext()));
//        list.add(new PieChartItem(generateDataPie(), getApplicationContext()));
//
//        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
//        lv.setAdapter(cda);

        lista_cacrds = findViewById(R.id.card_view_recycler);
        lista_cacrds.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapterByGam(this, new ArrayList<>());
        lista_cacrds.setAdapter(adapter);

//        adapter.setOnDriverClick(ride -> {
        // Timber.d("current person: %s", currentPerson);
        //Trip trip = Trip.builder(currentPerson, new Place(), new Place()).status(TripStatus.CLIENT_REQUESTED).build();
//            double price = valueToPay(1.3, 79, currentTrip.getDistanceInKilo());
//            currentTrip.setDriverNotificationId(ride.getDriverNotifyId());
//            currentTrip.setDriverName(ride.getDriverName());
//            currentTrip.setDriverId(ride.getDriverId());
//
//            Timber.i("Preco: %s, distance: %s", price, currentTrip.getDistanceInKilo());
//
//            currentTrip.setPrice(price);
//
//            requestRide = mapModel.requestTrip(currentTrip, ride.getId());
//            requestRide.observe(DriversListBottomSheet.this, DriversListBottomSheet.this::onRideRequest);

        //   });


        ButterKnife.bind(this);




    }

    private class PageAdapter extends FragmentPagerAdapter {

        PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment f = null;

            switch(pos) {
                case 0:
                    f = PieChartFrag.newInstance();
                    break;
                case 1:
                    f = BarChartFrag.newInstance();
                    break;

            }

            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Viagem " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(d);
    }
    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values2.add(new Entry(i, values1.get(i).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(values2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }
    @Override
    protected void onStart() {
        super.onStart();


    }

    private void updateUserOnView(Person person) {
        toolbar.setTitle(person.retrieveFullName());
    }
}
