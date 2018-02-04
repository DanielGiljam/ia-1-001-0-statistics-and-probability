package com.giljam.daniel.averageandstatisticaldispersion;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link List} object that manages the person data at the center of this app.
     */
    static List<Person> people;

    /**
     * The {@link android.widget.ArrayAdapter} that will convert the person
     * {@link List} data so that it can be displayed by a {@link android.widget.ListView}.
     */
    static ArrayAdapter<Person> mArrayAdapter;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the person list
        people = new ArrayList<>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Add fragments
        mSectionsPagerAdapter.addFragment(new CollectionManagementFragment());
        mSectionsPagerAdapter.addFragment(new StatisticsCalculationsFragment());
        mSectionsPagerAdapter.addFragment(new StatisticsVisualizationsFragment());

        // Create the adapter that will convert the person list data into a "list-displayable" data.
        mArrayAdapter = new ArrayAdapter<>( this,
                                            android.R.layout.simple_list_item_1,
                                            people);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void RefreshCalculations() {
        // getAvg();
        // getMed();
        // getStndrdDev();
        mArrayAdapter.notifyDataSetChanged();
    }

    private List<Person> CollectFromString(String string) {
        Matcher matcher = compile("\\d+(?:\\.\\d+)?").matcher(string);
        List<Person> people = new ArrayList<>();

        int start;
        int end;

        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();

            // people.add(string.substring(start, end)); TODO: start composing functionality for Person -item creation!
        }
        return people;
    }

    public void onAdd(String addedPeopleString) {

        if (addedPeopleString.isEmpty()) return;

        List<Person> people = CollectFromString(addedPeopleString);

        // updating person list data
        MainActivity.people.addAll(people);

        // statistical calculations are refreshed
        RefreshCalculations();
    }
}
