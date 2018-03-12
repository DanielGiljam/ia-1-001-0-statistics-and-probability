package com.giljam.daniel.statisticsandprobability;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     * Tells whether the first read/write exception mode is active or not.
     * This first read/write exception mode becomes active if the app
     * for some reason couldn't load data from internal storage.
     * When active, the app won't attempt saving to internal storage on app exit,
     * to protect user from potential data loss (as well as just from more errors,
     * as saving would most likely also not work, if loading didn't work), as in this
     * case, the app doesn't what data it might be overwriting.
     */
    private boolean readWriteExceptionMode1 = false;

    /**
     * Tells whether the second read/write exception mode is active or not.
     * This second read/write exception mode becomes active if the app
     * was able to load data, but the file was incomplete. This mode does nothing else than
     * show the user a notification about the discovery of corrupt data.
     */
    private boolean readWriteExceptionMode2 = false;

    /**
     * Tells whether the third read/write exception mode is active or not.
     * This read/write exception mode is actually, if triggered, forwarded to
     * the next app launch thanks to the persistence of the SharedPreferences API.
     * The trigger is a failed attempt of saving data on app exit.
     * All this mode does is notify the user about the incident, apologize about the data loss
     * (I know, embarrassing, right?), and give a timestamp to the user of when
     * the data was last saved (successfully).
     */
    private boolean readWriteExceptionMode3 = false;

    /**
     * Keeps the states for the validation override options.
     */
    private boolean[] ovStates;

    /**
     * In the dialog for validation override options,
     * changes to those options are first written here,
     * before being copied over all at once to the ovStates -variable when the dialog is confirmed.
     * This is so that the validation override options would remain unchanged in case the dialog is cancelled.
     */
    private boolean[] intermediateOVStates;

    /**
     * Saves configuration through this object when user exits the app,
     * and loads configuration through this object on app start.
     */
    private SharedPreferences sharedPrefs;

    /**
     * Manages the person data at the center of this app.
     */
    private PeopleDataFacilitator pdf;

    /**
     * Collection of people are displayed and managed in this fragment.
     */
    private PersonDataManagementFragment personDataManagementFragment;

    /**
     * Statistical calculations are displayed in this fragment.
     */
    private FirstStatisticsFragment firstStatisticsFragment;

    /**
     * More statistical calculations are displayed in this fragment.
     */
    private SecondStatisticsFragment secondStatisticsFragment;

    /**
     * The linear regression line is stored in this variable
     * for quick access by the PersonDataManagement fragment
     * if it needs the function for automatic field generation.
     */
    private LinearFunction linearRegressionLine;

    /**
     * The standard deviation of the shoe sizes is stored in this variable
     * for quick access by the PersonDataManagement fragment
     * if it needs the function for automatic field generation.
     */
    private double shoeSizesStdDev;

    /**
     * The standard deviation of the heights is stored in this variable
     * for quick access by the PersonDataManagement fragment
     * if it needs the function for automatic field generation.
     */
    private double heightsStdDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up action bar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the shared preferences variable.
        // (Cleanup is performed to reduce risk of duplicate, redundant or deprecated preferences that might cause inconsistency in the user experience.)
        sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        CleanupSharedPrefs();

        // Initialize validation override options state variables
        // and attempt fetching from saved configuration.
        ovStates = new boolean[]{               sharedPrefs.getBoolean(getString(R.string.ov_state_0_key), false),
                                                sharedPrefs.getBoolean(getString(R.string.ov_state_1_key), false),
                                                sharedPrefs.getBoolean(getString(R.string.ov_state_2_key), false),
                                                sharedPrefs.getBoolean(getString(R.string.ov_state_3_key), false)};
        intermediateOVStates = new boolean[]{   ovStates[0],
                                                ovStates[1],
                                                ovStates[2],
                                                ovStates[3]};

        // Try fetching previously active sorting mode from saved configuration.
        SortPeopleBy sortingMode;
        switch (sharedPrefs.getInt(getString(R.string.active_sorting_mode_key), 0)) {
            case 1:
                sortingMode = SortPeopleBy.NAME;
                break;
            case 2:
                sortingMode = SortPeopleBy.AGE;
                break;
            case 3:
                sortingMode = SortPeopleBy.SHOE_SIZE;
                break;
            case 4:
                sortingMode = SortPeopleBy.HEIGHT;
                break;
            default:
                sortingMode = SortPeopleBy.ORIGINAL;
                break;
        }

        // Initialize the PeopleDataFacilitator -object.
        pdf = new PeopleDataFacilitator(sortingMode);

        // Import list from persistent storage.
        File peopleData = new File(getFilesDir(), "people-data.csv");
        ReadWriteReport noCsvExceptions = pdf.ReadFromCSV(peopleData, false);
        if (noCsvExceptions == ReadWriteReport.FILE_NOT_FOUND) {
            try {
                peopleData.createNewFile();
            } catch (IOException e) {
                readWriteExceptionMode1 = true;
            }
        }

        // Fetch and reset readWriteExceptionMode3.
        FetchAndResetReadWriteExceptionMode3();

        // Create the adapter that will manage returning fragments
        // to the view pager for displaying them as separate "swipeable" screens.
        MyFragmentPagerAdapter mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        // Initialize fragments.
        personDataManagementFragment = (PersonDataManagementFragment) mFragmentPagerAdapter.addFragment(new PersonDataManagementFragment());
        firstStatisticsFragment = (FirstStatisticsFragment) mFragmentPagerAdapter.addFragment(new FirstStatisticsFragment());
        secondStatisticsFragment = (SecondStatisticsFragment) mFragmentPagerAdapter.addFragment(new SecondStatisticsFragment());

        // Set up the view pager with the adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        if (readWriteExceptionMode1) ShowReadWriteExceptionMode1Alert();
        else if (readWriteExceptionMode3) ShowReadWriteExceptionMode3Alert(peopleData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        File peopleData = new File(getFilesDir(), "people-data.csv");
        ReadWriteReport noCsvExceptions = pdf.WriteToCSV(peopleData, false);
        if (noCsvExceptions == ReadWriteReport.IO_EXCEPTION || noCsvExceptions == ReadWriteReport.FILE_NOT_FOUND) {
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putBoolean(getString(R.string.read_write_exception_mode_3_key), true);
            sharedPrefsEditor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If collection is empty, the "clear list" and "export list" options are disabled,
        // as there would be nothing to clear out or export.
        if (pdf.GetAmountOfPeople() == 0) {
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
        int id = item.getItemId();

        // Course of actions taken is determined based on the id of the action bar item.
        if (id == R.id.override_validation) {
            // Create and show a simple validation override options dialog.
            ShowValidationOverrideOptionsDialog();
        }
        if (id == R.id.clear_list) {
            // Create and show a simple confirmation prompt.
            ShowClearListConfirmationPrompt();
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

    public SortPeopleBy getActiveSortingMode() {
        return pdf.GetActiveSortingMode();
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public List<Person> getPeople() {
        return pdf.GetPeople();
    }

    public boolean GenerateDemoList() {
        if (pdf.GetAmountOfPeople() == 0) {
            List<Person> demoList = new ArrayList<>();
            demoList.add(new Person(getString(R.string.first_name_0), getString(R.string.last_name_0), 26, 36, 178));
            demoList.add(new Person(getString(R.string.first_name_1), getString(R.string.last_name_1), 38, 38, 168));
            demoList.add(new Person(getString(R.string.first_name_2), getString(R.string.last_name_2), 62, 39, 186));
            demoList.add(new Person(getString(R.string.first_name_3), getString(R.string.last_name_3), 35, 38, 168));
            demoList.add(new Person(getString(R.string.first_name_4), getString(R.string.last_name_4), 19, 39, 167));
            demoList.add(new Person(getString(R.string.first_name_5), getString(R.string.last_name_5), 50, 44, 166));
            demoList.add(new Person(getString(R.string.first_name_6), getString(R.string.last_name_6), 36, 44, 169));
            demoList.add(new Person(getString(R.string.first_name_7), getString(R.string.last_name_7), 41, 45, 174));
            demoList.add(new Person(getString(R.string.first_name_8), getString(R.string.last_name_8), 32, 40, 173));
            demoList.add(new Person(getString(R.string.first_name_9), getString(R.string.last_name_9), 22, 40, 177));
            demoList.add(new Person(getString(R.string.first_name_10), getString(R.string.last_name_10), 20, 41, 182));
            demoList.add(new Person(getString(R.string.first_name_11), getString(R.string.last_name_11), 76, 44, 182));
            demoList.add(new Person(getString(R.string.first_name_12), getString(R.string.last_name_12), 37, 39, 184));
            demoList.add(new Person(getString(R.string.first_name_13), getString(R.string.last_name_13), 29, 41, 173));
            demoList.add(new Person(getString(R.string.first_name_14), getString(R.string.last_name_14), 43, 41, 188));
            demoList.add(new Person(getString(R.string.first_name_15), getString(R.string.last_name_15), 49, 37, 168));
            pdf.AddPeople(demoList);
            RefreshCalculations();
            return true;
        } else
            return false;
    }

    public int AddPerson(Person person) {
        pdf.AddPerson(person);
        RefreshCalculations();
        return pdf.WhereIs(person);
    }

    public void RemovePerson(int index) {
        pdf.RemovePerson(index);
        RefreshCalculations();
    }

    public int ChangeSortingMode(SortPeopleBy sortingMode) {
        pdf.SortPeople(sortingMode);
        RefreshCalculations();
        return pdf.GetAmountOfPeople();
    }

    public void RequestRefresh(Fragment requester) {
        if (requester == firstStatisticsFragment) {
            double[] forstaHalvanAvRubbet = Statistics.forstaHalvanAvRubbet(pdf.GetPeopleAgeData());
            firstStatisticsFragment.ReceiveCalculations(forstaHalvanAvRubbet);
            return;
        }
        if (requester == secondStatisticsFragment) {
            List<Double> shoeSizes = pdf.GetPeopleShoeSizeData();
            List<Double> heights = pdf.GetPeopleHeightData();
            List<Object> andraHalvanAvRubbet = Statistics.andraHalvanAvRubbet(shoeSizes, heights);
            linearRegressionLine = (LinearFunction) andraHalvanAvRubbet.get(0);
            shoeSizesStdDev = Statistics.getStandardSampleDeviation(shoeSizes);
            heightsStdDev = Statistics.getStandardSampleDeviation(heights);
            secondStatisticsFragment.ReceiveCalculations(linearRegressionLine, (double) andraHalvanAvRubbet.get(1), pdf.GetPeopleShoeSizeData(), pdf.GetPeopleHeightData());
        }
    }

    private void RefreshCalculations() {
        List<Double> ages = pdf.GetPeopleAgeData();
        List<Double> shoeSizes = pdf.GetPeopleShoeSizeData();
        List<Double> heights = pdf.GetPeopleHeightData();
        List<Object> helaRubbet = Statistics.helaRubbet(ages, shoeSizes, heights);
        firstStatisticsFragment.ReceiveCalculations((double[]) helaRubbet.get(0));
        linearRegressionLine = (LinearFunction) helaRubbet.get(1);
        shoeSizesStdDev = Statistics.getStandardSampleDeviation(shoeSizes);
        heightsStdDev = Statistics.getStandardSampleDeviation(heights);
        secondStatisticsFragment.ReceiveCalculations(linearRegressionLine, (double) helaRubbet.get(2), shoeSizes, heights);
        invalidateOptionsMenu();
    }

    private void CleanupSharedPrefs() {

        boolean ReadWriteExceptionMode3 = sharedPrefs.getBoolean(getString(R.string.read_write_exception_mode_3_key), false);
        boolean ovState0 = sharedPrefs.getBoolean(getString(R.string.ov_state_0_key), false);
        boolean ovState1 = sharedPrefs.getBoolean(getString(R.string.ov_state_1_key), false);
        boolean ovState2 = sharedPrefs.getBoolean(getString(R.string.ov_state_2_key), false);
        boolean ovState3 = sharedPrefs.getBoolean(getString(R.string.ov_state_3_key), false);
        boolean birthDateAgeSwitchState = sharedPrefs.getBoolean(getString(R.string.birth_date_age_switch_state_key), false);
        boolean autoFieldsButtonState = sharedPrefs.getBoolean(getString(R.string.auto_fields_button_state_key), false);
        int autoFieldsPersonInstances = sharedPrefs.getInt(getString(R.string.auto_fields_person_instances_key), 1);
        int activeSortingMode = sharedPrefs.getInt(getString(R.string.active_sorting_mode_key), 0);

        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();

        sharedPrefsEditor.clear();

        sharedPrefsEditor.putBoolean(getString(R.string.read_write_exception_mode_3_key), ReadWriteExceptionMode3);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_0_key), ovState0);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_1_key), ovState1);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_2_key), ovState2);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_3_key), ovState3);
        sharedPrefsEditor.putBoolean(getString(R.string.birth_date_age_switch_state_key), birthDateAgeSwitchState);
        sharedPrefsEditor.putBoolean(getString(R.string.auto_fields_button_state_key), autoFieldsButtonState);
        sharedPrefsEditor.putInt(getString(R.string.auto_fields_person_instances_key), autoFieldsPersonInstances);
        sharedPrefsEditor.putInt(getString(R.string.active_sorting_mode_key), activeSortingMode);

        sharedPrefsEditor.apply();
    }

    private void FetchAndResetReadWriteExceptionMode3() {
        readWriteExceptionMode3 = sharedPrefs.getBoolean(getString(R.string.read_write_exception_mode_3_key), false);
        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
        sharedPrefsEditor.putBoolean(getString(R.string.read_write_exception_mode_3_key), false);
        sharedPrefsEditor.apply();
    }

    private void ShowReadWriteExceptionMode1Alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.read_write_exception_mode_1_text);
        if (readWriteExceptionMode3) {
            CharSequence text = Html.fromHtml(getString(R.string.read_write_exception_mode_1_text_message_2));
            builder.setMessage(text);
        }
        else builder.setMessage(R.string.read_write_exception_mode_1_text_message_1);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Deprecated
    private void ShowReadWriteExceptionMode2Alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.read_write_exception_mode_2_text);
        builder.setMessage(R.string.read_write_exception_mode_2_text_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void ShowReadWriteExceptionMode3Alert(File peopleData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.read_write_exception_mode_3_text);
        String latestAvailable = new SimpleDateFormat(  getString(R.string.read_write_exception_mode_3_timestamp_format),
                                                        Locale.getDefault())
                                        .format(new Date(peopleData.lastModified()));
        builder.setMessage(getString(R.string.read_write_exception_mode_3_text_message, latestAvailable));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void ShowValidationOverrideOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.override_validation_text));
        builder.setMultiChoiceItems(
                new String[]{   getString(R.string.override_validation_text_specific, getString(R.string.name)),
                                getString(R.string.override_validation_text_specific, getString(R.string.birth_date)),
                                getString(R.string.override_validation_text_specific, getString(R.string.shoe_size)),
                                getString(R.string.override_validation_text_specific, getString(R.string.height))},
                intermediateOVStates,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        intermediateOVStates[which] = isChecked;
                    }
                });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int i = 0;
                for (boolean dialogOVState : intermediateOVStates) {
                    ovStates[i] = dialogOVState;
                    i++;
                }
                WriteOVStates();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int i = 0;
                for (boolean ovState : ovStates) {
                    intermediateOVStates[i] = ovState;
                    i++;
                }
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void WriteOVStates() {
        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_0_key), ovStates[0]);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_1_key), ovStates[1]);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_2_key), ovStates[2]);
        sharedPrefsEditor.putBoolean(getString(R.string.ov_state_3_key), ovStates[3]);
        sharedPrefsEditor.apply();
    }

    private void ShowClearListConfirmationPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setTitle(R.string.clear_list_text);
        builder.setMessage(R.string.clear_list_text_message);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int range = pdf.GetAmountOfPeople();
                pdf.ClearPeople();
                RefreshCalculations();
                personDataManagementFragment.NotifyListWasCleared(range);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        Fragment addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
