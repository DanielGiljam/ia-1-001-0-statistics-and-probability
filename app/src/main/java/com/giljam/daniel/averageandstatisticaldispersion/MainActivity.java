package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Keep the override option states
    static boolean overrideNameValidation = false;
    static boolean overrideBirthDateValidation = false;
    static boolean overrideShoeSizeValidation = false;
    static boolean overrideHeightValidation = false;
    private boolean[] ovStates = new boolean[]{false, false, false, false};
    private boolean[] displayedOVStates = new boolean[]{false, false, false, false};

    // Prevents using GenerateDemoList more than once
    private boolean generatedDemoList = false;

    /**
     * The {@link List} object that manages the person data at the center of this app.
     */
    static PersonDataManagement pdm;

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
     * Collection of people are displayed and managed in this fragment.
     */
    private CollectionManagementFragment collectionManagementFragment;

    /**
     * Statistical calculations are displayed in this fragment.
     */
    private StatisticsCalculationsFragment statisticsCalculationsFragment;

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
        pdm = new PersonDataManagement();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Add fragments
        collectionManagementFragment = (CollectionManagementFragment) mSectionsPagerAdapter.addFragment(new CollectionManagementFragment());
        statisticsCalculationsFragment = (StatisticsCalculationsFragment) mSectionsPagerAdapter.addFragment(new StatisticsCalculationsFragment());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.settings_main, menu);
        getMenuInflater().inflate(R.menu.settings_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (pdm.getPeople().size() == 0) {
            menu.findItem(R.id.clear_list).setEnabled(false);
            menu.findItem(R.id.export_list).setEnabled(false);
        } else {
            menu.findItem(R.id.clear_list).setEnabled(true);
            menu.findItem(R.id.export_list).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.override_validation) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.override_validation_text, ""));
            builder.setMultiChoiceItems(
                    new String[]{   getString(R.string.override_validation_text_name, "name"),
                                    getString(R.string.override_validation_text, "birth date"),
                                    getString(R.string.override_validation_text, "shoe size"),
                                    getString(R.string.override_validation_text, "height")},
                    new boolean[]{  overrideNameValidation,
                                    overrideBirthDateValidation,
                                    overrideShoeSizeValidation,
                                    overrideHeightValidation},
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which,
                                            boolean isChecked) {
                            if (isChecked) {
                                displayedOVStates[which] = true;
                            } else {
                                displayedOVStates[which] = true;
                            }
                        }
                    });
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            int i = 0;
                            for (boolean dialogOVState : displayedOVStates) {
                                ovStates[i] = dialogOVState;
                                i++;
                            }
                        }
                    });
            builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
        if (id == R.id.clear_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder .setMessage(R.string.clear_list_text_message)
                    .setTitle(R.string.clear_list_text);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int range = pdm.getAmountOfPeople();
                            pdm.ClearPeople();
                            RefreshCalculations();
                            collectionManagementFragment.NotifyListWasCleared(range);
                        }
                    });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getNameOVState() {
        return ovStates[0];
    }

    public boolean getBirthDateOVState() {
        return ovStates[1];
    }

    public boolean getShoeSizeOVState() {
        return ovStates[2];
    }

    public boolean getHeightOVState() {
        return ovStates[3];
    }

    public boolean GenerateDemoList() {
        if (generatedDemoList || pdm.getPeople().size() == 0) {
            generatedDemoList = true;
            List<Person> demoList = new ArrayList<>();
            demoList.add(new Person("Ulgarf", "Sunders", 38, 38, 168));
            demoList.add(new Person("Dahmad", "Bax", 62, 39, 186));
            demoList.add(new Person("Mirina", "Kelt", 35, 38, 168));
            demoList.add(new Person("Loe", "Karinov", 19, 39, 167));
            demoList.add(new Person("Olon", "Septoros", 50, 44, 166));
            demoList.add(new Person("Ning", "Jin-Yiang", 36, 44, 169));
            demoList.add(new Person("William", "Mercury", 41, 45, 174));
            demoList.add(new Person("Per-Erik", "Baltmers", 32, 40, 173));
            demoList.add(new Person("Cedir", "O'Durkniff", 22, 40, 177));
            demoList.add(new Person("Morod", "Kaffner", 20, 41, 182));
            demoList.add(new Person("Melina", "Joric", 76, 44, 182));
            demoList.add(new Person("Sudaro", "Moniz", 37, 39, 184));
            demoList.add(new Person("Nev Barit", "Kompálo", 29, 41, 173));
            demoList.add(new Person("Yri", "Kalav", 43, 41, 188));
            demoList.add(new Person("Gurkav", "Nît-Balal", 49, 37, 168));
            demoList.add(new Person("Sarab", "Kehschni", 26, 36, 178));
            pdm.CollectPeople(demoList);
            RefreshCalculations();
            return true;
        } else
            return false;
    }

    public int AddPerson(Person person) {
        List<Person> personToBeAdded = new ArrayList<>();
        personToBeAdded.add(person);
        pdm.CollectPeople(personToBeAdded);
        RefreshCalculations();
        return pdm.WhereIs(person);
    }

    public int ChangeSortingMode(SortingMode sortingMode) {
        pdm.SortPeople(sortingMode);
        RefreshCalculations();
        return pdm.getAmountOfPeople();
    }

    public void RefreshCalculations() {
        double[] helaRubbet = Statistics.helaRubbet(pdm.getPeopleData());
        statisticsCalculationsFragment.ReceiveCalculations(helaRubbet);
        invalidateOptionsMenu();
    }
}
