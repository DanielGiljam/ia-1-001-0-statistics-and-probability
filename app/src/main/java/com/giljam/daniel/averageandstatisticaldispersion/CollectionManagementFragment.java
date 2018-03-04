package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionManagementFragment extends Fragment {

    // Limits for shoe size and height for input validation

    private static final int MINIMUM_SHOE_SIZE = 15;
    private static final int MAXIMUM_SHOE_SIZE = 50;

    private static final int MINIMUM_HEIGHT = 50;
    private static final int MAXIMUM_HEIGHT = 275;

    // Patterns needed for validating the name input string

    private static final Pattern mic =
            Pattern.compile(    "[^a-zàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿŒœŠšŸƒ' \\-]",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern mtc =
            Pattern.compile(    "([a-zàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿŒœŠšŸƒ])\\1{2}",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern mdsc =
            Pattern.compile(    "([-'])\\1",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern minbe =
            Pattern.compile(    "\\A(-.*)|(.*-)\\z",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern cfln =
            Pattern.compile(    "\\A(\\S+)(?:\\s+(\\S+))*\\z",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern nc =
            Pattern.compile(    "[-'](\\S)",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern jtf =
            Pattern.compile(    "job314");

    // Matcher variables for more efficiently matching patterns

    private static Matcher matchesInvalidCharacters;        // goes with mic
    private static Matcher matchesTripleCharacters;         // goes with mtc
    private static Matcher matchesDoubleSpecialCharacters;  // goes with mdsc
    private static Matcher matchesInvalidNameBeginOrEnd;    // goes with minbe
    private static Matcher capturesNameGroups;              // goes with cfln
    private static Matcher capitalizesNames;                // goes with nc
    private static Matcher executeJob;                      // goes with jtf

    // Date object that holds a parsed birthdate input string

    private static Date birthDateObject;

    /**
     * Reference to this fragment's root layout/view.
     */
    private FrameLayout rootLayout;

    /**
     * Input field for person name.
     */
    private EditText nameInputField;

    /**
     * Input field for person birthdate or age.
     */
    private EditText birthDateAgeInputField;

    /**
     * Switch to toggle between the birthyear input field and the age input field.
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
     * Button to submit input field data.
     */
    private Button addButton;

    /**
     * Switch to toggles between sorting modes for the list.
     */
    private ToggleButton sortButton;

    /**
     * The {@link RecyclerView.Adapter} that will convert the person
     * {@link List} data so that it can be displayed by a {@link RecyclerView}.
     */
    private PeopleAdapter mAdapter;

    /**
     * The {@link RecyclerView} that will display the data converted by an {@link RecyclerView.Adapter}.
     */
    private RecyclerView mRecyclerView;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(   LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_collection_management, container, false);

        // Set up the input fields and buttons
        rootLayout = view.findViewById(R.id.collection_management_root_layout);
        nameInputField = view.findViewById(R.id.name_input_field);
        birthDateAgeInputField = view.findViewById(R.id.birthdate_age_input_field);
        birthDateAgeSwitch = view.findViewById(R.id.birthdate_age_switch);
        shoeSizeInputField = view.findViewById(R.id.shoe_size_input_field);
        heightInputField = view.findViewById(R.id.height_input_field);
        addButton = view.findViewById(R.id.add_button);
        sortButton = view.findViewById(R.id.sort_button);

        // Create the adapter that will convert the person list data into "list-displayable" data.
        mAdapter = new PeopleAdapter(getContext(), MainActivity.pdm.getPeople());

        // Set up the ListView with the array adapter
        mRecyclerView = view.findViewById(R.id.people);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),1, false));
        mRecyclerView.setAdapter(mAdapter);

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
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        nameInputField.setImeOptions(   EditorInfo.IME_ACTION_NEXT +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                        EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        nameInputField.setImeOptions(   EditorInfo.IME_ACTION_DONE +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
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
                    if (nameInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_NEXT + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return true;
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
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        birthDateAgeInputField.setImeOptions(   EditorInfo.IME_ACTION_NEXT +
                                                                EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        birthDateAgeInputField.setImeOptions(   EditorInfo.IME_ACTION_DONE +
                                                                EditorInfo.IME_FLAG_NAVIGATE_NEXT +
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
                    if (birthDateAgeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_NEXT + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return true;
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
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        shoeSizeInputField.setImeOptions(   EditorInfo.IME_ACTION_NEXT +
                                                            EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        shoeSizeInputField.setImeOptions(   EditorInfo.IME_ACTION_DONE +
                                                            EditorInfo.IME_FLAG_NAVIGATE_NEXT +
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
                    if (shoeSizeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_NEXT + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return true;
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
                    if (nameInputField.getText().toString().isEmpty() || birthDateAgeInputField.getText().toString().isEmpty() || shoeSizeInputField.getText().toString().isEmpty() || heightInputField.getText().toString().isEmpty())
                        heightInputField.setImeOptions( EditorInfo.IME_ACTION_NEXT +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                        EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        heightInputField.setImeOptions( EditorInfo.IME_ACTION_DONE +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
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
                    if (heightInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_NEXT + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up listener for the birthDateAgeSwitch
        // so that its state specifies how the input in birthDateAgeInputField should be interpreted
        birthDateAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                birthDateAgeInputField.setHint(R.string.age_input_field_text);
                birthDateAgeInputField.setInputType(2);
            } else {
                birthDateAgeInputField.setHint(R.string.birth_date_input_field_text);
                birthDateAgeInputField.setInputType(20);
            }
            }
        });

        // Set up listener for when rootLayout picks up focus
        // to hide on-screen keyboard, as you can't write in the rootLayout
        addButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        // Set up listener for the addButton
        // so that it triggers addPerson method
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreAddPerson(addButton);
            }
        });

        // Set up listener for the sortButton
        // so that the list is sorted accordingly to the sortButton's state
        sortButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int range;
                if (b) {
                    range = ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.NAME);
                    sortButton.setTextOn(getString(R.string.name_sort_text));
                } else {
                    if (sortButton.getTextOn().equals(getString(R.string.name_sort_text))) {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.AGE);
                        sortButton.setChecked(true);
                        sortButton.setTextOn(getString(R.string.age_sort_text));
                    } else if (sortButton.getTextOn().equals(getString(R.string.age_sort_text))) {
                        range = ((MainActivity) getActivity()).ChangeSortingMode(SortingMode.SHOE_SIZE);
                        sortButton.setChecked(true);
                        sortButton.setTextOn(getString(R.string.shoe_size_sort_text));
                    } else if (sortButton.getTextOn().equals(getString(R.string.shoe_size_sort_text))) {
                        range = ((MainActivity) getActivity()).ChangeSortingMode(SortingMode.HEIGHT);
                        sortButton.setChecked(true);
                        sortButton.setTextOn(getString(R.string.height_sort_text));
                    } else {
                        range = ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.ORIGINAL);
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
            String firstName = NameCapitalization(capturesNameGroups.group(1));
            String lastName = NameCapitalization(capturesNameGroups.group(2));
            Person person;
            if (!birthDateAgeSwitch.isChecked())
                person = new Person(firstName, lastName, birthDateObject, Integer.parseInt(shoeSizeInputString), Integer.parseInt(heightInputString));
            else
                person = new Person(firstName, lastName, Integer.parseInt(birthDateAgeInputString), Integer.parseInt(shoeSizeInputString), Integer.parseInt(heightInputString));
            ClearFocus();
            nameInputField.setText("");
            birthDateAgeInputField.setText("");
            shoeSizeInputField.setText("");
            heightInputField.setText("");
            int personDestination = ((MainActivity)getActivity()).AddPerson(person);
            mAdapter.notifyItemInserted(personDestination);
        }
    }

    private boolean FieldValidation(View view, String nameInputString, String birthDateAgeInputString, String shoeSizeInputString, String heightInputString, boolean ageNotBirthDate) {

        // Checks whether the input fields where filled out or not
        boolean birthDateAgeEmpty = birthDateAgeInputString.isEmpty();
        boolean shoeSizeEmpty = shoeSizeInputString.isEmpty();
        boolean heightEmpty = heightInputString.isEmpty();
        boolean nameEmpty = nameInputString.isEmpty();

        boolean[] isEmptyReturns = new boolean[]{birthDateAgeEmpty, shoeSizeEmpty, heightEmpty, nameEmpty};
        boolean aFieldIsEmpty = false;

        if (isEmptyReturns[0] || isEmptyReturns[1] || isEmptyReturns[2] || isEmptyReturns[3]) {

            // Creating the validation reports with preset base values
            boolean[] birthDateAgeValidationReport = new boolean[]{true, false, false};
            boolean[] shoeSizeValidationReport = new boolean[]{true, false, false};
            boolean[] heightValidationReport = new boolean[]{true, false, false};
            boolean[] nameValidationReport = new boolean[]{true, false, false, false};

            boolean[][] validationReports = new boolean[][]
            {
                    birthDateAgeValidationReport,
                    shoeSizeValidationReport,
                    heightValidationReport,
                    nameValidationReport
            };

            // If any input field was left empty,
            // then the following code block (if statement) shuts down the validation process
            // and notifies the user appropriately
            int i = 0;
            for (boolean isEmptyReturn : isEmptyReturns) {
                if (!isEmptyReturn) {
                    if (i == 0) validationReports[i] = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);
                    if (i == 1) validationReports[i] = ShoeSizeValidation(shoeSizeInputString);
                    if (i == 2) validationReports[i] = HeightValidation(heightInputString);
                    if (i == 3) validationReports[i] = NameValidation(nameInputString);
                    if (!validationReports[i][0]) return InvalidInputProtocol(view, validationReports);
                } else {
                    aFieldIsEmpty = true;
                }
                i++;
            }

        } else {

            boolean[] birthDateAgeValidationReport = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);
            boolean[] shoeSizeValidationReport = ShoeSizeValidation(shoeSizeInputString);
            boolean[] heightValidationReport = HeightValidation(heightInputString);
            boolean[] nameValidationReport = NameValidation(nameInputString);

            boolean iipFeedback = InvalidInputProtocol(view, new boolean[][]{birthDateAgeValidationReport, shoeSizeValidationReport, heightValidationReport, nameValidationReport});
            if (!iipFeedback) return false;
        }

        if (aFieldIsEmpty){
            EmptyFieldProtocol(view, ageNotBirthDate, isEmptyReturns);
            return false;
        } else {
            return true;
        }
    }

    private boolean InvalidInputProtocol(View view, boolean[][] validationReports) {
        boolean makeSnackbar = false;
        String snackBarString = "";

        for (boolean[] validationReport : validationReports)
            if (!validationReport[0]) makeSnackbar = true;

        if (makeSnackbar) {

            boolean[] birthDateAgeValidationReport = validationReports[0];
            boolean[] shoeSizeValidationReport = validationReports[1];
            boolean[] heightValidationReport = validationReports[2];
            boolean[] nameValidationReport = validationReports[3];

            // Fetching string resources
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

            if (snackBarString.isEmpty()) return false;

            Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                            snackBarString,
                            Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        } else
            return true;
    }

    private void EmptyFieldProtocol(View view, boolean ageNotBirthDate, boolean[] isEmptyReturns) {
        String snackBarString;

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
            }

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
                }
            }

        } else {

            if (isEmptyReturns[3]) {
                nameInputField.requestFocus();
            } else if (isEmptyReturns[0]) {
                birthDateAgeInputField.requestFocus();
            } else if (isEmptyReturns[1]) {
                shoeSizeInputField.requestFocus();
            } else {
                heightInputField.requestFocus();
            }
        }

        // The following code blocks take care of
        // setting up an appropriate message for the user

        int i = 0;
        int emptyFields = 0;
        String[] emptyFieldNames = new String[3];
        for (boolean isEmptyReturn : isEmptyReturns) {
            if (isEmptyReturn) {
                if (emptyFields < 3) {
                    if (i == 0) {
                        if (ageNotBirthDate) emptyFieldNames[emptyFields] = "age";
                        else emptyFieldNames[emptyFields] = "birth date";
                    }
                    if (i == 1) emptyFieldNames[emptyFields] = "shoe size";
                    if (i == 2) emptyFieldNames[emptyFields] = "height";
                    if (i == 3) emptyFieldNames[emptyFields] = "name";
                }
                emptyFields++;
            }
            i++;
        }

        switch (emptyFields) {
            case 1:
                snackBarString = getString(R.string.one_field_empty, emptyFieldNames[0]);
                break;
            case 2:
                snackBarString = getString(R.string.two_fields_empty, emptyFieldNames[0], emptyFieldNames[1]);
                break;
            case 3:
                snackBarString = getString(R.string.three_fields_empty, emptyFieldNames[0], emptyFieldNames[1], emptyFieldNames[2]);
                break;
            default:
                snackBarString = getString(R.string.four_fields_empty);
                break;
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private boolean[] NameValidation(String nameInputString) {

        executeJob = jtf.matcher(nameInputString.trim());
        if (executeJob.matches()) {
            if (((MainActivity)getActivity()).GenerateDemoList()) {
                ClearFocus();
                nameInputField.setText("");
                mAdapter.notifyItemRangeInserted(0, 16);
                return new boolean[]{false, false, false, false};
            }
        }

        if (((MainActivity) getActivity()).getNameOVState()) return new boolean[]{true, false, false, false};

        // Self-descriptive variable captures first name and last name into separate capturing groups
        capturesNameGroups = cfln.matcher(nameInputString.trim());
        boolean nullLastName = !capturesNameGroups.matches();


        // Calculates the occurrences of corresponding character/symbol in the first name
        int dashesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("-", "").length();
        int apostrophesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("\'", "").length();

        // Tests if first name begins or ends "suspiciously"
        matchesInvalidNameBeginOrEnd = minbe.matcher(capturesNameGroups.group(1));
        boolean invalidFirstNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        // Declares variables for validating the last name.
        // The actual validation is wrapped in an if statement
        // to ensure that this validation method works regardless of "the existence" of a last name.
        // "The existence" of a last name -property is stored in the nullLastName variable
        int dashesCountLastName = 0;
        int apostrophesCountLastName = 0;
        boolean invalidLastNameBeginOrEnd = false;
        if (!nullLastName) {

            // Calculates the occurrences of corresponding character/symbol in the first name
            dashesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("-", "").length();
            apostrophesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("\'", "").length();

            // Tests if first name begins or ends "suspiciously"
            matchesInvalidNameBeginOrEnd = minbe.matcher(capturesNameGroups.group(2));
            invalidLastNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        }

        // Some more tests performed on the input as a whole
        matchesInvalidCharacters = mic.matcher(nameInputString);
        matchesTripleCharacters = mtc.matcher(nameInputString);
        matchesDoubleSpecialCharacters = mdsc.matcher(nameInputString);
        boolean invalidCharacters = matchesInvalidCharacters.find();
        boolean tripleCharacters = matchesTripleCharacters.find();
        boolean doubleSpecialCharacters = matchesDoubleSpecialCharacters.find();

        // Comprehensive validation data compressed into a vague suspiciousPatterns bool
        boolean suspiciousPatterns =    tripleCharacters ||
                                        doubleSpecialCharacters ||
                                        dashesCountFirstName >= 2 ||
                                        apostrophesCountFirstName >= 2 ||
                                        invalidFirstNameBeginOrEnd ||
                                        dashesCountLastName >= 4 ||
                                        apostrophesCountLastName >= 4 ||
                                        invalidLastNameBeginOrEnd;

        // The returned array's 0-index - passedValidation - gets its appropriate value
        boolean passedValidation = true;
        if (invalidCharacters || nullLastName || suspiciousPatterns) passedValidation = false;

        return new boolean[] {  passedValidation,
                                nullLastName,
                                invalidCharacters,
                                suspiciousPatterns};
    }

    private boolean[] BirthDateAgeValidation(String birthDateAgeInputString, boolean ageNotBirthDate) {
        // Basically validation is only necessary when the user hasn't flicked the ageYearSwitch
        // ergo when the birthDateAgeInputField is read as a calendar year.
        // The contents of the following if statement make sure that year isn't in the future
        if (!ageNotBirthDate) {
            try {
                birthDateObject = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthDateAgeInputString);
            } catch (ParseException e) {
                try {
                    birthDateObject = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(birthDateAgeInputString);
                } catch (ParseException e1) {
                    try {
                        birthDateObject = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthDateAgeInputString);
                    } catch (ParseException e2) {
                        try {
                            birthDateObject = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(birthDateAgeInputString);
                        } catch (ParseException e3) {
                            return new boolean[]{false, false, true};
                        }
                    }
                }
            }
            if (((MainActivity) getActivity()).getBirthDateOVState()) return new boolean[]{true, false, false};
            return new boolean[]{birthDateObject.getTime() <= new Date().getTime(), !(birthDateObject.getTime() <= new Date().getTime()), false};
        } else return new boolean[]{true, false, false};
    }

    private boolean[] ShoeSizeValidation(String shoeSizeInputString) {
        if (((MainActivity) getActivity()).getShoeSizeOVState()) return new boolean[]{true, false, false};
        int shoeSizeInput = Integer.parseInt(shoeSizeInputString);
        if (shoeSizeInput < MINIMUM_SHOE_SIZE) {
            return new boolean[]{false, true, false};
        } else if (shoeSizeInput > MAXIMUM_SHOE_SIZE) {
            return new boolean[]{false, false, true};
        } else {
            return new boolean[]{true, false, false};
        }
    }

    private boolean[] HeightValidation(String heightInputString) {
        if (((MainActivity) getActivity()).getHeightOVState()) return new boolean[]{true, false, false};
        int heightInput = Integer.parseInt(heightInputString);
        if (heightInput < MINIMUM_HEIGHT) {
            return new boolean[]{false, true, false};
        } else if (heightInput > MAXIMUM_HEIGHT) {
            return new boolean[]{false, false, true};
        } else {
            return new boolean[]{true, false, false};
        }
    }

    private String NameCapitalization(String name) {
        capitalizesNames = nc.matcher(name);
        while (capitalizesNames.find()) {
            int rli = capitalizesNames.end();
            name = name.substring(0, rli - 1) + capitalizesNames.group(1).toUpperCase() + name.substring(rli);
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    // Clears focus from any interactive element by giving it back to the layout as a whole
    private void ClearFocus() {
        rootLayout.requestFocus();
    }
}
