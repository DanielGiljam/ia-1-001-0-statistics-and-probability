package com.giljam.daniel.statisticsandprobability;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonDataManagementFragment extends Fragment {

    /**
     * Used internally by the name validation process.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher matchesInvalidCharacters;

    /**
     * Used internally by the name validation process.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher matchesTripleCharacters;

    /**
     * Used internally by the name validation process.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher matchesDoubleSpecialCharacters;

    /**
     * Used internally by the name validation process.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher matchesInvalidNameBeginOrEnd;

    /**
     * Used internally for name separation.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher capturesNameGroups;

    /**
     * Used internally for name separation.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher capturesNameGroupsAlt;

    /**
     * Used internally by the name capitalization process.
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher capitalizesNames;

    /**
     * Used internally to execute "job314".
     * Gets its own variable as an it would otherwise be destroyed and recreated over and over again,
     * so having it as a frequently accessed variable saves performance.
     */
    private static Matcher executeJob;

    /**
     * Into where the birth date input string is parsed.
     */
    private Date birthDateObject;

    /**
     * How many people the add button creates at once.
     * If "auto fields" feature is disabled, the add person only creates
     * one person at a time.
     */
    private int personInstances;

    /**
     * This fragment's root layout/view.
     */
    private FrameLayout rootLayout;

    /**
     * Input field for person name.
     */
    private EditText nameInputField;

    /**
     * Input field for person birth date or age.
     */
    private EditText birthDateAgeInputField;

    /**
     * Switch to toggle between having to enter either birth date or age.
     */
    private Switch birthDateAgeSwitch;

    /**
     * Input field for person shoe size.
     */
    private EditText shoeSizeInputField;

    /**
     * Input field for person height.
     */
    private EditText heightInputField;

    /**
     * Button to submit input field data and add person to the list.
     */
    private Button addButton;

    /**
     * Button to toggle the "auto fields" feature.
     */
    private ToggleButton autoFieldsButton;

    /**
     * Button to toggle between sorting modes for the list.
     */
    private ToggleButton sortButton;

    /**
     * The adapter that will convert the list data so that it can be displayed by a recycler view.
     */
    private PeopleAdapter mAdapter;

    /**
     * The recycler view that will display the list data provided by the adapter.
     */
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(   LayoutInflater inflater,
                                ViewGroup container,
                                Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.person_data_management_fragment, container, false);

        // Set up the input fields and buttons
        rootLayout = view.findViewById(R.id.collection_management_root_layout);
        nameInputField = view.findViewById(R.id.name_input_field);
        birthDateAgeInputField = view.findViewById(R.id.birthdate_age_input_field);
        birthDateAgeSwitch = view.findViewById(R.id.birthdate_age_switch);
        shoeSizeInputField = view.findViewById(R.id.shoe_size_input_field);
        heightInputField = view.findViewById(R.id.height_input_field);
        addButton = view.findViewById(R.id.add_button);
        autoFieldsButton = view.findViewById(R.id.auto_fields_button);
        sortButton = view.findViewById(R.id.sort_button);

        // Attempt fetching personInstances from saved configuration.
        personInstances = ((MainActivity)getActivity()).getSharedPrefs().getInt(getString(R.string.auto_fields_person_instances_key), 1);

        // Attempt fetching birthDateAgeSwitch state from saved configuration.
        if (((MainActivity)getActivity()).getSharedPrefs().getBoolean(getString(R.string.birth_date_age_switch_state_key), false)) {
            birthDateAgeInputField.setHint(R.string.age_input_field_text);
            birthDateAgeInputField.setInputType(2);
            birthDateAgeSwitch.setChecked(true);
        } else {
            birthDateAgeInputField.setHint(R.string.birth_date_input_field_text);
            birthDateAgeInputField.setInputType(20);
            birthDateAgeSwitch.setChecked(false);
        }

        // Attempt fetching autoFieldsButton state from saved configuration.
        if (((MainActivity) getActivity()).getSharedPrefs().getBoolean(getString(R.string.auto_fields_button_state_key), false)) {
            CharSequence abaText = Html.fromHtml(getString(R.string.add_button_text_alt, personInstances));
            addButton.setText(abaText);
            autoFieldsButton.setTextColor(getResources().getColor(R.color.colorAccent));
            autoFieldsButton.setChecked(true);
        } else {
            autoFieldsButton.setTextColor(getResources().getColor(android.R.color.black));
            autoFieldsButton.setChecked(false);
        }

        // Set up sortButton to resemble the PeopleDataFacilitator's activeSortingMode.
        switch (((MainActivity)getActivity()).getActiveSortingMode()) {
            case ORIGINAL:
                sortButton.setTextOn(getString(R.string.original_sort_text));
                sortButton.setChecked(false);
                break;
            case NAME:
                sortButton.setTextOn(getString(R.string.name_sort_text));
                sortButton.setChecked(true);
                break;
            case AGE:
                sortButton.setTextOn(getString(R.string.age_sort_text));
                sortButton.setChecked(true);
                break;
            case SHOE_SIZE:
                sortButton.setTextOn(getString(R.string.shoe_size_sort_text));
                sortButton.setChecked(true);
                break;
            case HEIGHT:
                sortButton.setTextOn(getString(R.string.height_sort_text));
                sortButton.setChecked(true);
                break;
        }

        // Create the adapter that will process the list data for the recycler view to display.
        mAdapter = new PeopleAdapter(getContext(), ((MainActivity)getActivity()).getPeople());

        // Set up the recycler view with the adapter
        mRecyclerView = view.findViewById(R.id.people);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),1, false));
        mRecyclerView.setAdapter(mAdapter);

        // Set up an ItemTouchHelper to handle swiping gestures for deleting list entries
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int index = viewHolder.getAdapterPosition();
                ((MainActivity)getActivity()).RemovePerson(index);
                mAdapter.notifyItemRemoved(index);
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        MiscListenersSetup();

        // Set up listener for the birthDateAgeSwitch
        // so that its state specifies how the input in birthDateAgeInputField should be interpreted
        birthDateAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                birthDateAgeInputField.setHint(R.string.age_input_field_text);
                birthDateAgeInputField.setInputType(2);
                WriteBirthDateAgeSwitchState(true);
            } else {
                birthDateAgeInputField.setHint(R.string.birth_date_input_field_text);
                birthDateAgeInputField.setInputType(20);
                WriteBirthDateAgeSwitchState(false);
            }
            }
        });

        // Set up listener for the addButton
        // so that it triggers the PreAddPerson method (that in turn, starts validating/processing the field input)
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreAddPerson(view);
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (autoFieldsButton.isChecked()) {
                    ShowPersonInstancesDialog();
                    return true;
                }
                return false;
            }
        });

        // Set up listener for the autoFieldsButton
        // so that its state specifies whether the "auto fields" feature is on or off
        autoFieldsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    CharSequence abaText = Html.fromHtml(getString(R.string.add_button_text_alt, personInstances));
                    addButton.setText(abaText);
                    autoFieldsButton.setTextColor(getResources().getColor(R.color.colorAccent));
                    WriteAutoFieldsButtonState(true);
                } else {
                    addButton.setText(getString(R.string.add_button_text));
                    autoFieldsButton.setTextColor(getResources().getColor(android.R.color.black));
                    WriteAutoFieldsButtonState(false);
                }
            }
        });

        // Set up listener for the sortButton
        // so that the list is sorted accordingly to the sortButton's state
        sortButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int range;
                if (b) {
                    range = ((MainActivity)getActivity()).ChangeSortingMode(SortPeopleBy.NAME);
                    sortButton.setTextOn(getString(R.string.name_sort_text));
                    WriteActiveSortingMode(1);
                } else {
                    if (sortButton.getTextOn().equals(getString(R.string.name_sort_text))) {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortPeopleBy.AGE);
                        sortButton.setTextOn(getString(R.string.age_sort_text));
                        sortButton.setChecked(true);
                        WriteActiveSortingMode(2);
                    } else if (sortButton.getTextOn().equals(getString(R.string.age_sort_text))) {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortPeopleBy.SHOE_SIZE);
                        sortButton.setTextOn(getString(R.string.shoe_size_sort_text));
                        sortButton.setChecked(true);
                        WriteActiveSortingMode(3);
                    } else if (sortButton.getTextOn().equals(getString(R.string.shoe_size_sort_text))) {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortPeopleBy.HEIGHT);
                        sortButton.setTextOn(getString(R.string.height_sort_text));
                        sortButton.setChecked(true);
                        WriteActiveSortingMode(4);
                    } else {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortPeopleBy.ORIGINAL);
                        WriteActiveSortingMode(0);
                    }
                }
                mAdapter.notifyItemRangeChanged(0, range);
            }
        });

        return view;
    }

    public void NotifyListWasCleared(int range) {
        mAdapter.notifyItemRangeRemoved(0, range);
    }

    public void NotifyImportEvent() {
        mAdapter.notifyDataSetChanged();
    }

    private void PreAddPerson(View view) {

        // Fetches whatever is in the EditText input fields
        String nameInputString = nameInputField.getText().toString();
        String birthDateAgeInputString = birthDateAgeInputField.getText().toString();
        String shoeSizeInputString = shoeSizeInputField.getText().toString();
        String heightInputString = heightInputField.getText().toString();

        // If the validation of the input is successful,
        // ergo the FieldValidation method returns true,
        // then the method continues,
        // else the addPerson method ends here

        if (FieldValidation(view, nameInputString, birthDateAgeInputString, shoeSizeInputString, heightInputString, birthDateAgeSwitch.isChecked())) {

            // This iterator + local variable + condition + while-loop takes care of adding many people at once
            // if the "auto fields" feature is enabled and a "add button -value" higher than 1 has been selected
            // by long pressing the add button.
            int i = 0;
            int localPersonInstances;
            if (autoFieldsButton.isChecked()) localPersonInstances = personInstances;
            else localPersonInstances = 1;
            while (i < localPersonInstances) {

                // Following code block takes care of generating left out field data
                // if the "auto fields" feature is enabled.
                boolean generateName = false;
                boolean generateBirthDateAge = false;
                boolean generateShoeSize = false;
                boolean generateHeight = false;
                if (autoFieldsButton.isChecked()) {
                    if (nameInputString.isEmpty()) {
                        generateName = true;
                        nameInputString = GenerateNameInputString();
                    }
                    if (birthDateAgeInputString.isEmpty()) {
                        generateBirthDateAge = true;
                        birthDateAgeInputString = GenerateBirthDateAgeInputString();
                    }
                    if (shoeSizeInputString.isEmpty()) {
                        generateShoeSize = true;
                        shoeSizeInputString = GenerateShoeSizeInputString();
                    }
                    if (heightInputString.isEmpty()) {
                        generateHeight = true;
                        heightInputString = GenerateHeightInputString();
                    }
                }

                // If the name validation override option is active,
                // splitting the name input string into a first and a last name
                // (as both person object constructors require that you do, so that you can provide them separately)
                // becomes a bit more complicated, as then it's not already guaranteed
                // at this point, that the name consists of two parts.
                // The following condition tests make sure all cases are handled correspondingly.
                String firstName;
                String lastName;
                if (((MainActivity) getActivity()).getNameOVState()) {
                    if (capturesNameGroupsAlt == null)
                        capturesNameGroupsAlt = Pattern.compile(getString(R.string.captures_name_groups_alt),
                                Pattern.CASE_INSENSITIVE)
                                .matcher(nameInputString.trim());
                    else capturesNameGroupsAlt.reset(nameInputString.trim());
                    if (capturesNameGroupsAlt.matches()) firstName = capturesNameGroupsAlt.group(1);
                    else firstName = "";
                    if (capturesNameGroupsAlt.group(2) != null)
                        lastName = capturesNameGroupsAlt.group(2);
                    else lastName = "";
                } else {
                    firstName = NameCapitalization(capturesNameGroups.group(1));
                    lastName = NameCapitalization(capturesNameGroups.group(2));
                }

                // The state of the birthDateAgeSwitch + generateBirthDateAge tells us which constructor to use when creating the person.
                Person person;
                if (!birthDateAgeSwitch.isChecked() && !generateBirthDateAge)
                    person = new Person(firstName, lastName, birthDateObject, Integer.parseInt(shoeSizeInputString), Integer.parseInt(heightInputString));
                else
                    person = new Person(firstName, lastName, Integer.parseInt(birthDateAgeInputString), Integer.parseInt(shoeSizeInputString), Integer.parseInt(heightInputString));

                // Person is dispatched to the MainActivity, that adds it to the list.
                // In the transaction, the position of the added person in the list is returned.
                int personDestination = ((MainActivity) getActivity()).AddPerson(person);

                // The recycler view's adapter is notified about the added person
                // and asked to scroll to the position in the list, where the person was added.
                mAdapter.notifyItemInserted(personDestination);
                if (i == localPersonInstances - 1)
                    mRecyclerView.smoothScrollToPosition(personDestination);
                i++;

                if (generateName) nameInputString = "";
                if (generateBirthDateAge) birthDateAgeInputString = "";
                if (generateShoeSize) shoeSizeInputString = "";
                if (generateHeight) heightInputString = "";
            }

            // Now that the person adding is finished, it's time to clean up!
            ClearFocus();
            nameInputField.setText("");
            birthDateAgeInputField.setText("");
            shoeSizeInputField.setText("");
            heightInputField.setText("");
        }
    }

    private boolean FieldValidation(View view, String nameInputString, String birthDateAgeInputString, String shoeSizeInputString, String heightInputString, boolean ageNotBirthDate) {

        // Checking whether the input fields where filled out or not.
        boolean birthDateAgeEmpty = birthDateAgeInputString.isEmpty();
        boolean shoeSizeEmpty = shoeSizeInputString.isEmpty();
        boolean heightEmpty = heightInputString.isEmpty();
        boolean nameEmpty = nameInputString.trim().isEmpty();

        if (birthDateAgeEmpty || shoeSizeEmpty || heightEmpty || nameEmpty) {

            // Check results are easier to iterate over when placed inside an array.
            boolean[] isEmptyReturns = new boolean[]{birthDateAgeEmpty, shoeSizeEmpty, heightEmpty, nameEmpty};

            // Initializing validation reports with default values.
            boolean[] birthDateAgeValidationReport = new boolean[]{true, false, false};
            boolean[] shoeSizeValidationReport = new boolean[]{true, false, false};
            boolean[] heightValidationReport = new boolean[]{true, false, false};
            boolean[] nameValidationReport = new boolean[]{true, false, false, false};

            // Validation reports are easier to iterate over when placed inside an array.
            boolean[][] validationReports = new boolean[][]{birthDateAgeValidationReport, shoeSizeValidationReport, heightValidationReport, nameValidationReport};

            // Any non-empty field will still be validated, notifying the user in first hand
            // whether the non-empty fields passed validation or not.
            int i = 0;
            for (boolean isEmptyReturn : isEmptyReturns) {
                if (!isEmptyReturn) {
                    if (i == 0) validationReports[i] = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);
                    if (i == 1) validationReports[i] = ShoeSizeValidation(shoeSizeInputString);
                    if (i == 2) validationReports[i] = HeightValidation(heightInputString);
                    if (i == 3) validationReports[i] = NameValidation(nameInputString);
                    if (!validationReports[i][0]) return InvalidInputProtocol(view, validationReports);
                }
                i++;
            }

            // If all fields where blank, or some fields where blank and the rest passed validation,
            // then the validation is dispatched to the EmptyFieldProtocol.
            return EmptyFieldProtocol(view, ageNotBirthDate, isEmptyReturns);

        } else {

            // Validating all the input...
            boolean[] birthDateAgeValidationReport = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);
            boolean[] shoeSizeValidationReport = ShoeSizeValidation(shoeSizeInputString);
            boolean[] heightValidationReport = HeightValidation(heightInputString);
            boolean[] nameValidationReport = NameValidation(nameInputString);

            // The validation is dispatched to the InvalidInputProtocol along with the validation reports.
            return InvalidInputProtocol(view, new boolean[][]{birthDateAgeValidationReport, shoeSizeValidationReport, heightValidationReport, nameValidationReport});
        }
    }

    private boolean InvalidInputProtocol(View view, boolean[][] validationReports) {
        boolean makeSnackbar = false;
        String snackBarString = "";

        // Determining whether notifying about invalid input is necessary.
        for (boolean[] validationReport : validationReports)
            if (!validationReport[0]) makeSnackbar = true;

        if (makeSnackbar) {

            // Expanding validation reports array.
            boolean[] birthDateAgeValidationReport = validationReports[0];
            boolean[] shoeSizeValidationReport = validationReports[1];
            boolean[] heightValidationReport = validationReports[2];
            boolean[] nameValidationReport = validationReports[3];

            // Fetching string resources.
            String nullLastName = getString(R.string.no_last_name);
            String suspiciousPatterns = getString(R.string.suspicious_patterns_in_name);
            String invalidCharacters = getString(R.string.invalid_characters_in_name);
            String unknownDateFormat = getString(R.string.unknown_date_format);
            String invalidDate = getString(R.string.invalid_date);
            String invalidShoeSizeTooSmall = getString(R.string.invalid_shoe_size_too_small);
            String invalidShoeSizeTooBig = getString(R.string.invalid_shoe_size_too_big);
            String invalidHeightTooShort = getString(R.string.invalid_height_too_short);
            String invalidHeightTooTall = getString(R.string.invalid_height_too_tall);

            // The following if-, else if- and else statement takes care of some fine tuning
            // regarding the choice of what field is focused and given the cursor depending on
            // what field already is in focus/already has the cursor.
            // The order of the nested if statements implies the prioritization
            // according to which the messages will be shown (the subsequent if statement overrides the preceding).

            if (view == nameInputField) {

                if (heightValidationReport[1]) snackBarString = invalidHeightTooShort;
                if (heightValidationReport[2]) snackBarString = invalidHeightTooTall;
                if (shoeSizeValidationReport[1]) snackBarString = invalidShoeSizeTooSmall;
                if (shoeSizeValidationReport[2]) snackBarString = invalidShoeSizeTooBig;
                if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
                if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;
                if (nameValidationReport[1]) snackBarString = nullLastName;
                if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
                if (nameValidationReport[2]) snackBarString = invalidCharacters;

                if (nameValidationReport[0]) {
                    if (!birthDateAgeValidationReport[0]) {
                        view.clearFocus();
                        birthDateAgeInputField.requestFocus();
                    } else if (!shoeSizeValidationReport[0]) {
                        view.clearFocus();
                        shoeSizeInputField.requestFocus();
                    } else if (!heightValidationReport[0]) {
                        view.clearFocus();
                        heightInputField.requestFocus();
                    }
                }

            } else if (view == birthDateAgeInputField) {

                if (nameValidationReport[1]) snackBarString = nullLastName;
                if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
                if (nameValidationReport[2]) snackBarString = invalidCharacters;
                if (heightValidationReport[1]) snackBarString = invalidHeightTooShort;
                if (heightValidationReport[2]) snackBarString = invalidHeightTooTall;
                if (shoeSizeValidationReport[1]) snackBarString = invalidShoeSizeTooSmall;
                if (shoeSizeValidationReport[2]) snackBarString = invalidShoeSizeTooBig;
                if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
                if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;

                if (birthDateAgeValidationReport[0]) {
                    if (!shoeSizeValidationReport[0]) {
                        view.clearFocus();
                        shoeSizeInputField.requestFocus();
                    } else if (!heightValidationReport[0]) {
                        view.clearFocus();
                        heightInputField.requestFocus();
                    } else if (!nameValidationReport[0]) {
                        view.clearFocus();
                        nameInputField.requestFocus();
                    }
                }

            } else if (view == shoeSizeInputField) {

                if (nameValidationReport[1]) snackBarString = nullLastName;
                if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
                if (nameValidationReport[2]) snackBarString = invalidCharacters;
                if (heightValidationReport[1]) snackBarString = invalidHeightTooShort;
                if (heightValidationReport[2]) snackBarString = invalidHeightTooTall;
                if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
                if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;
                if (shoeSizeValidationReport[1]) snackBarString = invalidShoeSizeTooSmall;
                if (shoeSizeValidationReport[2]) snackBarString = invalidShoeSizeTooBig;

                if (shoeSizeValidationReport[0]) {
                    if (!birthDateAgeValidationReport[0]) {
                        view.clearFocus();
                        birthDateAgeInputField.requestFocus();
                    } else if (!heightValidationReport[0]) {
                        view.clearFocus();
                        heightInputField.requestFocus();
                    } else if (!nameValidationReport[0]) {
                        view.clearFocus();
                        nameInputField.requestFocus();
                    }
                }

            } else if (view == heightInputField) {

                if (nameValidationReport[1]) snackBarString = nullLastName;
                if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
                if (nameValidationReport[2]) snackBarString = invalidCharacters;
                if (shoeSizeValidationReport[1]) snackBarString = invalidShoeSizeTooSmall;
                if (shoeSizeValidationReport[2]) snackBarString = invalidShoeSizeTooBig;
                if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
                if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;
                if (heightValidationReport[1]) snackBarString = invalidHeightTooShort;
                if (heightValidationReport[2]) snackBarString = invalidHeightTooTall;

                if (heightValidationReport[0]) {
                    if (!birthDateAgeValidationReport[0]) {
                        view.clearFocus();
                        birthDateAgeInputField.requestFocus();
                    } else if (!shoeSizeValidationReport[0]) {
                        view.clearFocus();
                        shoeSizeInputField.requestFocus();
                    } else if (!nameValidationReport[0]) {
                        view.clearFocus();
                        nameInputField.requestFocus();
                    }
                }

            } else {

                if (nameValidationReport[1]) snackBarString = nullLastName;
                if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
                if (nameValidationReport[2]) snackBarString = invalidCharacters;
                if (heightValidationReport[1]) snackBarString = invalidHeightTooShort;
                if (heightValidationReport[2]) snackBarString = invalidHeightTooTall;
                if (shoeSizeValidationReport[1]) snackBarString = invalidShoeSizeTooSmall;
                if (shoeSizeValidationReport[2]) snackBarString = invalidShoeSizeTooBig;
                if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
                if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;

                if (!birthDateAgeValidationReport[0]) {
                    birthDateAgeInputField.requestFocus();
                } else if (!shoeSizeValidationReport[0]) {
                    shoeSizeInputField.requestFocus();
                } else if (!heightValidationReport[0]) {
                    heightInputField.requestFocus();
                } else if (!nameValidationReport[0]) {
                    nameInputField.requestFocus();
                }
            }

            // Do not show an empty snackbar, in case (god forbid) it would ever lead to that.
            if (snackBarString.isEmpty()) return false;

            Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                            snackBarString,
                            Snackbar.LENGTH_SHORT)
                    .show();

            return false;
        } else
            return true;
    }

    private boolean EmptyFieldProtocol(View view, boolean ageNotBirthDate, boolean[] isEmptyReturns) {

        if (autoFieldsButton.isChecked()) return true;
        
        // The following if- and else if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor

        if (view == nameInputField) {

            if (!isEmptyReturns[3]) {
                if (isEmptyReturns[0]) {
                    view.clearFocus();
                    birthDateAgeInputField.requestFocus();
                } else if (isEmptyReturns[1]) {
                    view.clearFocus();
                    shoeSizeInputField.requestFocus();
                } else {
                    view.clearFocus();
                    heightInputField.requestFocus();
                }
            } else nameInputField.setText("");

        } else if (view == birthDateAgeInputField) {

            if (!isEmptyReturns[0]) {
                if (isEmptyReturns[1]) {
                    view.clearFocus();
                    shoeSizeInputField.requestFocus();
                } else if (isEmptyReturns[2]) {
                    view.clearFocus();
                    heightInputField.requestFocus();
                } else {
                    view.clearFocus();
                    nameInputField.requestFocus();
                    nameInputField.setText("");
                }
            }

        } else if (view == shoeSizeInputField) {

            if (!isEmptyReturns[1]) {
                if (isEmptyReturns[0]) {
                    view.clearFocus();
                    birthDateAgeInputField.requestFocus();
                } else if (isEmptyReturns[2]) {
                    view.clearFocus();
                    heightInputField.requestFocus();
                } else {
                    view.clearFocus();
                    nameInputField.requestFocus();
                    nameInputField.setText("");
                }
            }

        } else if (view == heightInputField) {

            if (!isEmptyReturns[2]) {
                if (isEmptyReturns[0]) {
                    view.clearFocus();
                    birthDateAgeInputField.requestFocus();
                } else if (isEmptyReturns[1]) {
                    view.clearFocus();
                    shoeSizeInputField.requestFocus();
                } else {
                    view.clearFocus();
                    nameInputField.requestFocus();
                    nameInputField.setText("");
                }
            }

        } else {

            if (isEmptyReturns[3]) {
                nameInputField.requestFocus();
                nameInputField.setText("");
            } else if (isEmptyReturns[0]) {
                birthDateAgeInputField.requestFocus();
            } else if (isEmptyReturns[1]) {
                shoeSizeInputField.requestFocus();
            } else {
                heightInputField.requestFocus();
            }
        }

        // The following code blocks take care of
        // setting up an appropriate message to the user

        int i = 0;
        int emptyFields = 0;
        String[] emptyFieldNames = new String[3];
        for (boolean isEmptyReturn : isEmptyReturns) {
            if (isEmptyReturn) {
                if (emptyFields < 3) {
                    if (i == 0) {
                        if (ageNotBirthDate) emptyFieldNames[emptyFields] = getString(R.string.age);
                        else emptyFieldNames[emptyFields] = getString(R.string.birth_date);
                    }
                    if (i == 1) emptyFieldNames[emptyFields] = getString(R.string.shoe_size);
                    if (i == 2) emptyFieldNames[emptyFields] = getString(R.string.height);
                    if (i == 3) emptyFieldNames = new String[]{getString(R.string.name), emptyFieldNames[0], emptyFieldNames[1]};
                }
                emptyFields++;
            }
            i++;
        }

        String snackBarString;
        int snackBarLength;
        switch (emptyFields) {
            case 1:
                snackBarString = getString(R.string.one_field_empty, emptyFieldNames[0]);
                snackBarLength = Snackbar.LENGTH_SHORT;
                break;
            case 2:
                snackBarString = getString(R.string.two_fields_empty, emptyFieldNames[0], emptyFieldNames[1]);
                snackBarLength = Snackbar.LENGTH_LONG;
                break;
            case 3:
                snackBarString = getString(R.string.three_fields_empty, emptyFieldNames[0], emptyFieldNames[1], emptyFieldNames[2]);
                snackBarLength = Snackbar.LENGTH_LONG;
                break;
            default:
                snackBarString = getString(R.string.four_fields_empty);
                snackBarLength = Snackbar.LENGTH_SHORT;
                break;
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        snackBarLength)
                .show();

        return false;
    }

    private boolean[] NameValidation(String nameInputString) {

        if (executeJob == null) executeJob = Pattern.compile(getString(R.string.job_314)).matcher(nameInputString.trim());
        else executeJob.reset(nameInputString.trim());
        if (executeJob.matches()) {
            if (((MainActivity)getActivity()).GenerateDemoList()) {
                ClearFocus();
                nameInputField.setText("");
                mAdapter.notifyItemRangeInserted(0, 16);
                return new boolean[]{false, false, false, false};
            }
        }

        // if the name validation override option is active, then the name validation returns successful no matter what.
        if (((MainActivity) getActivity()).getNameOVState()) return new boolean[]{true, false, false, false};

        // Self-descriptive variable captures first name and last name into separate capturing groups.
        if (capturesNameGroups == null)
            capturesNameGroups = Pattern.compile(   getString(R.string.captures_name_groups),
                                                    Pattern.CASE_INSENSITIVE)
                                    .matcher(nameInputString.trim());
        else capturesNameGroups.reset(nameInputString.trim());
        if (!capturesNameGroups.matches()) return new boolean[]{false, false, false, false};

        // Calculates the occurrences of corresponding character/symbol in the first name.
        int dashesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("-", "").length();
        int apostrophesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("\'", "").length();

        // Tests if first name begins or ends "suspiciously".
        if (matchesInvalidNameBeginOrEnd == null)
            matchesInvalidNameBeginOrEnd = Pattern.compile(getString(   R.string.matches_invalid_name_begin_or_end),
                                                                        Pattern.CASE_INSENSITIVE)
                                                .matcher(capturesNameGroups.group(1));
        else matchesInvalidNameBeginOrEnd.reset(capturesNameGroups.group(1));
        boolean invalidFirstNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        // Declares variables for validating the last name.
        // The actual validation is wrapped in an if statement
        // to ensure that this validation method works regardless of "the existence" of a last name.
        // "The existence" of a last name -property is stored in the nullLastName variable.
        boolean nullLastName = false;
        int dashesCountLastName = 0;
        int apostrophesCountLastName = 0;
        boolean invalidLastNameBeginOrEnd = false;
        if (capturesNameGroups.group(2) != null) {

            // Calculates the occurrences of corresponding character/symbol in the first name.
            dashesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("-", "").length();
            apostrophesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("\'", "").length();

            // Tests if first name begins or ends "suspiciously".
            matchesInvalidNameBeginOrEnd.reset(capturesNameGroups.group(2));
            invalidLastNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        } else nullLastName = true;

        // Some more tests performed on the input as a whole.
        if (matchesInvalidCharacters == null)
            matchesInvalidCharacters = Pattern.compile( getString(R.string.matches_invalid_characters),
                                                        Pattern.CASE_INSENSITIVE)
                                            .matcher(nameInputString.trim());
        else matchesInvalidCharacters.reset(nameInputString.trim());
        if (matchesTripleCharacters == null)
            matchesTripleCharacters = Pattern.compile(  getString(R.string.matches_triple_characters),
                                                        Pattern.CASE_INSENSITIVE)
                                            .matcher(nameInputString.trim());
		else matchesTripleCharacters.reset(nameInputString.trim());
        if (matchesDoubleSpecialCharacters == null)
            matchesDoubleSpecialCharacters = Pattern.compile(   getString(R.string.matches_double_special_characters),
                                                                Pattern.CASE_INSENSITIVE)
                                                .matcher(nameInputString.trim());
		else matchesDoubleSpecialCharacters.reset(nameInputString.trim());
        boolean invalidCharacters = matchesInvalidCharacters.find();
        boolean tripleCharacters = matchesTripleCharacters.find();
        boolean doubleSpecialCharacters = matchesDoubleSpecialCharacters.find();

        // Comprehensive validation data compressed into a vague suspiciousPatterns bool.
        boolean suspiciousPatterns =    tripleCharacters ||
                                        doubleSpecialCharacters ||
                                        dashesCountFirstName >= 2 ||
                                        apostrophesCountFirstName >= 2 ||
                                        invalidFirstNameBeginOrEnd ||
                                        dashesCountLastName >= 4 ||
                                        apostrophesCountLastName >= 4 ||
                                        invalidLastNameBeginOrEnd;

        // The returned array's 0-index - passedValidation - gets its appropriate value.
        boolean passedValidation = true;
        if (invalidCharacters || nullLastName || suspiciousPatterns) passedValidation = false;

        return new boolean[] {  passedValidation,
                                nullLastName,
                                invalidCharacters,
                                suspiciousPatterns};
    }

    private boolean[] BirthDateAgeValidation(String birthDateAgeInputString, boolean ageNotBirthDate) {
        if (!ageNotBirthDate) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.accepted_date_format_1), Locale.getDefault());
                dateFormat.setLenient(false);
                birthDateObject = dateFormat.parse(birthDateAgeInputString);
            } catch (ParseException e2) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.accepted_date_format_2), Locale.getDefault());
                    dateFormat.setLenient(false);
                    birthDateObject = dateFormat.parse(birthDateAgeInputString);
                } catch (ParseException e3) {
                    return new boolean[]{false, false, true};
                }
            }
            if (((MainActivity) getActivity()).getBirthDateOVState()) return new boolean[]{true, false, false};
            return new boolean[]{birthDateObject.getTime() <= new Date().getTime(), !(birthDateObject.getTime() <= new Date().getTime()), false};
        } else return new boolean[]{true, false, false};
    }

    private boolean[] ShoeSizeValidation(String shoeSizeInputString) {
        if (((MainActivity) getActivity()).getShoeSizeOVState()) return new boolean[]{true, false, false};
        int shoeSizeInput = Integer.parseInt(shoeSizeInputString);
        if (shoeSizeInput < getResources().getInteger(R.integer.min_shoe_size)) {
            return new boolean[]{false, true, false};
        } else if (shoeSizeInput > getResources().getInteger(R.integer.max_shoe_size)) {
            return new boolean[]{false, false, true};
        } else {
            return new boolean[]{true, false, false};
        }
    }

    private boolean[] HeightValidation(String heightInputString) {
        if (((MainActivity) getActivity()).getHeightOVState()) return new boolean[]{true, false, false};
        int heightInput = Integer.parseInt(heightInputString);
        if (heightInput < getResources().getInteger(R.integer.min_height)) {
            return new boolean[]{false, true, false};
        } else if (heightInput > getResources().getInteger(R.integer.max_height)) {
            return new boolean[]{false, false, true};
        } else {
            return new boolean[]{true, false, false};
        }
    }

    private String NameCapitalization(String name) {
        if (capitalizesNames == null)
            capitalizesNames = Pattern.compile( getString(R.string.capitalize_names),
                                                Pattern.CASE_INSENSITIVE)
                                    .matcher(name);
        else capitalizesNames.reset(name);
        while (capitalizesNames.find()) {
            int rli = capitalizesNames.end();
            name = name.substring(0, rli - 1) + capitalizesNames.group(1).toUpperCase() + name.substring(rli);
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String GenerateNameInputString() {
        Random random = new Random();
        String generatedName =  getString(getResources().getIdentifier(getString(R.string.first_name_id_template, random.nextInt(getResources().getInteger(R.integer.first_names_total))), "string", getActivity().getPackageName())) + " " +
                                getString(getResources().getIdentifier(getString(R.string.last_name_id_template, random.nextInt(getResources().getInteger(R.integer.last_names_total))), "string", getActivity().getPackageName()));
        if (capturesNameGroups == null)
            capturesNameGroups = Pattern.compile(   getString(R.string.captures_name_groups),
                                                    Pattern.CASE_INSENSITIVE)
                                    .matcher(generatedName);
        else capturesNameGroups.reset(generatedName);
        capturesNameGroups.matches();
        return generatedName;
    }

    private String GenerateBirthDateAgeInputString() {
        return Integer.toString(new Random().nextInt(100));
    }

    // TODO! Generate shoe size and/or height based on regression analysis.

    private String GenerateShoeSizeInputString() {
        return Integer.toString(getResources().getInteger(R.integer.min_shoe_size) + new Random().nextInt(getResources().getInteger(R.integer.max_shoe_size)));
    }

    private String GenerateHeightInputString() {
        return Integer.toString(getResources().getInteger(R.integer.min_height) + new Random().nextInt(getResources().getInteger(R.integer.max_height)));
    }

    @Deprecated
    private boolean ShowSameNameDialog() {
        boolean returnValue = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String name;
        if (((MainActivity) getActivity()).getNameOVState()) name = capturesNameGroups.group();
        else name = NameCapitalization(capturesNameGroups.group(1)) + " " + NameCapitalization(capturesNameGroups.group(2));
        CharSequence text = Html.fromHtml(getString(R.string.same_name_dialog_text_message, personInstances, name));
        builder.setMessage(text);
        builder.setTitle(getString(R.string.same_name_dialog_text, personInstances));
        builder.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // returnValue = false;
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // returnValue = true;
                dialog.cancel();
            }
        });
        builder.create().show();
        return returnValue;
    }

    private void ShowPersonInstancesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final int personInstancesBackup = personInstances;
        final NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(512);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(personInstances);
        builder.setView(numberPicker);
        builder.setTitle(R.string.number_picker_dialog_text_person_instances);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                personInstances = numberPicker.getValue();
                CharSequence abaText = Html.fromHtml(getString(R.string.add_button_text_alt, personInstances));
                addButton.setText(abaText);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                personInstances = personInstancesBackup;
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void ClearFocus() {
        rootLayout.requestFocus();
    }

    private void MiscListenersSetup() {

        // Set up listener for when rootLayout picks up focus
        // to hide on-screen keyboard, as you can't write in the rootLayout
        rootLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        // Set up listener for when focus changes to nameInputField
        // to trigger script that determines what action the enter key should have
        nameInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        nameInputField.setImeOptions(EditorInfo.IME_ACTION_NEXT +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        nameInputField.setImeOptions(EditorInfo.IME_ACTION_DONE +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in nameInputField
        // so that the enter key's action is the same as the addButton's if the other fields already have been filled out
        nameInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PreAddPerson(view);
                    return true;
                }
                return false;
            }
        });
        nameInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (nameInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return false;
                    }
                }
                return false;
            }
        });

        // Set up listener for when focus changes to birthDateAgeInputField
        // to trigger script that determines what action the enter key should have
        birthDateAgeInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (nameInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        birthDateAgeInputField.setImeOptions(EditorInfo.IME_ACTION_NEXT +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        birthDateAgeInputField.setImeOptions(EditorInfo.IME_ACTION_DONE +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in birthDateAgeInputField
        // so that the enter key's action is the same as the addButton's if the other fields already have been filled out
        birthDateAgeInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PreAddPerson(view);
                    return true;
                }
                return false;
            }
        });
        birthDateAgeInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (birthDateAgeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return false;
                    }
                }
                return false;
            }
        });

        // Set up listener for when focus changes to shoeSizeInputField
        // to trigger script that determines what action the enter key should have
        shoeSizeInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        shoeSizeInputField.setImeOptions(EditorInfo.IME_ACTION_NEXT +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        shoeSizeInputField.setImeOptions(EditorInfo.IME_ACTION_DONE +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in shoeSizeInputField
        // so that the enter key's action is the same as the addButton's if the other fields already have been filled out
        shoeSizeInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PreAddPerson(view);
                    return true;
                }
                return false;
            }
        });
        shoeSizeInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (shoeSizeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return false;
                    }
                }
                return false;
            }
        });

        // Set up listener for when focus changes to heightInputField
        // to trigger script that determines what action the enter key should have
        heightInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty())
                        heightInputField.setImeOptions(EditorInfo.IME_ACTION_NEXT +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        heightInputField.setImeOptions(EditorInfo.IME_ACTION_DONE +
                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in heightInputField
        // so that the enter key's action is the same as the addButton's if the other fields already have been filled out
        heightInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PreAddPerson(view);
                    return true;
                }
                return false;
            }
        });
        heightInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (heightInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return false;
                    }
                }
                return false;
            }
        });

        // Set up listener for when rootLayout picks up focus
        // to hide on-screen keyboard, as you can't write on the addButton
        addButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private void WriteBirthDateAgeSwitchState(boolean birthDateAgeSwitchState) {
        SharedPreferences.Editor sharedPrefsEditor = ((MainActivity)getActivity()).getSharedPrefs().edit();
        sharedPrefsEditor.putBoolean(getString(R.string.birth_date_age_switch_state_key), birthDateAgeSwitchState);
        sharedPrefsEditor.apply();
    }

    private void WriteAutoFieldsButtonState(boolean autoFieldsButtonState) {
        SharedPreferences.Editor sharedPrefsEditor = ((MainActivity) getActivity()).getSharedPrefs().edit();
        sharedPrefsEditor.putBoolean(getString(R.string.auto_fields_button_state_key), autoFieldsButtonState);
        sharedPrefsEditor.apply();
    }

    private void WriteActiveSortingMode(int mode) {
        SharedPreferences.Editor sharedPrefsEditor = ((MainActivity) getActivity()).getSharedPrefs().edit();
        sharedPrefsEditor.putInt(getString(R.string.active_sorting_mode_key), mode);
        sharedPrefsEditor.apply();
    }

    private class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView personName;
            TextView personYearAge;

            ViewHolder(View itemView) {
                super(itemView);
                personName = itemView.findViewById(R.id.person_name);
                personYearAge = itemView.findViewById(R.id.person_year_age);
            }
        }

        private Context context;
        private List<Person> people;

        PeopleAdapter(Context context, List<Person> people) {
            this.context = context;
            this.people = people;
        }

        @Override
        public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View personView = inflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(personView);
        }

        @Override
        public void onBindViewHolder(PeopleAdapter.ViewHolder viewHolder, int position) {
            Person person = people.get(position);
            TextView personName = viewHolder.personName;
            TextView personYearAge = viewHolder.personYearAge;
            personName.setText(person.getName());
            personYearAge.setText(String.format(context.getString(R.string.list_view_item_person_details_string), person.getAge(), person.getShoeSize(), person.getHeight()));
        }

        @Override
        public int getItemCount() {
            return people.size();
        }
    }
}
