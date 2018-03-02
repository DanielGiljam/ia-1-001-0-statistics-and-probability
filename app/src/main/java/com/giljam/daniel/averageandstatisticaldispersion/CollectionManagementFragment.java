package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
     * Input field for person birthyear.
     */
    private EditText birthDateAgeInputField;

    /**
     * Switch to toggle between the birthyear input field and the age input field.
     */
    private Switch birthDateAgeSwitch;

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
        birthDateAgeInputField = view.findViewById(R.id.year_age_input_field);
        birthDateAgeSwitch = view.findViewById(R.id.year_age_switch);
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
                    if (birthDateAgeInputField.getText().toString().isEmpty())
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
        // so that the enter key's action is the same as the addButton's if the birthDateAgeInputField already has been filled out
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
                    if (nameInputField.getText().toString().isEmpty())
                        birthDateAgeInputField.setImeOptions(   EditorInfo.IME_ACTION_PREVIOUS +
                                                                EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS +
                                                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        birthDateAgeInputField.setImeOptions(   EditorInfo.IME_ACTION_DONE +
                                                                EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS +
                                                                EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in birthDateAgeInputField
        // so that the enter key's action is the same as the addButton's if the nameInputField already has been filled out
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
                    if (birthDateAgeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
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
        // so that it trigger addPerson method
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
                if (b) {
                    ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.NAME);
                    sortButton.setTextOn(getString(R.string.name_sort_text));
                } else {
                    if (sortButton.getTextOn().equals(getString(R.string.name_sort_text))) {
                        ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.AGE);
                        sortButton.setChecked(true);
                        sortButton.setTextOn(getString(R.string.age_sort_text));
                    } else {
                        ((MainActivity)getActivity()).ChangeSortingMode(SortingMode.ORIGINAL);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    private void PreAddPerson(View view) {

        // Fetches whatever is in the EditText input fields
        String nameInputString = nameInputField.getText().toString();
        String birthDateAgeInputString = birthDateAgeInputField.getText().toString();

        // If the validation of the input is successful,
        // ergo the FieldValidation method returns true,
        // then the method continues,
        // else the addPerson method ends here

        if (FieldValidation(view, nameInputString, birthDateAgeInputString, birthDateAgeSwitch.isChecked())) {
            String firstName = NameCapitalization(capturesNameGroups.group(1));
            String lastName = NameCapitalization(capturesNameGroups.group(2));
            Person person;
            if (!birthDateAgeSwitch.isChecked())
                person = new Person(firstName, lastName, birthDateObject, 0, 0);
            else
                person = new Person(firstName, lastName, Integer.parseInt(birthDateAgeInputString), 0, 0);
            ClearFocus();
            nameInputField.setText("");
            birthDateAgeInputField.setText("");
            ((MainActivity)getActivity()).AddPerson(person);
            mAdapter.notifyDataSetChanged();
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

    private boolean FieldValidation(View view, String nameInputString, String birthDateAgeInputString, boolean ageNotBirthDate) {

        // Checks whether the input fields where filled out or not
        boolean nameEmpty = nameInputString.isEmpty();
        boolean birthDateAgeEmpty = birthDateAgeInputString.isEmpty();

        // Creating the validation reports with preset base values
        boolean[] nameValidationReport = new boolean[]{true, false, false, false};
        boolean[] birthDateAgeValidationReport = new boolean[]{true, false};

        // If any input field was left empty,
        // then the following code block (if statement) shuts down the validation process
        // and notifies the user appropriately
        if (nameEmpty || birthDateAgeEmpty) {

            // Any filled in field containing invalid input is considered more urgent than empty fields.
            // The following if- else if statement takes care of notifying the user in a case like that
            if (!nameEmpty) {

                nameValidationReport = NameValidation(nameInputString);
                if (!nameValidationReport[0]) InvalidNameInputProtocol(view, nameValidationReport);

            } else if (!birthDateAgeEmpty) {

                birthDateAgeValidationReport = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);
                if (!birthDateAgeValidationReport[0]) InvalidBirthDateAgeInputProtocol(view, birthDateAgeValidationReport);
            }

            // If both fields where left empty or if a single filled in field was valid,
            // then the EmptyFieldProtocol method takes care of notifying the user about empty fields
            if (nameValidationReport[0] && birthDateAgeValidationReport[0]) EmptyFieldProtocol(view, nameEmpty, birthDateAgeEmpty, ageNotBirthDate);

            return false;
        }

        // If the validation process makes it all the way here,
        // it means that both fields where filled in with some kind of input.
        // That input is evaluated in the following lines
        nameValidationReport = NameValidation(nameInputString);
        birthDateAgeValidationReport = BirthDateAgeValidation(birthDateAgeInputString, ageNotBirthDate);

        // If the validation reports are positive then the validation process returns true,
        // else the InvalidInputProtocol method handles notifying the user appropriately and the validation process returns false
        if (nameValidationReport[0] && birthDateAgeValidationReport[0]) {
            return true;
        } else {
            InvalidInputProtocol(view, nameValidationReport, birthDateAgeValidationReport);
            return false;
        }
    }

    private boolean[] NameValidation(String nameInputString) {

        // Self-descriptive variable captures first name and last name into separate capturing groups
        capturesNameGroups = cfln.matcher(nameInputString.trim());
        capturesNameGroups.matches();


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
        boolean nullLastName = false;
        int dashesCountLastName = 0;
        int apostrophesCountLastName = 0;
        boolean invalidLastNameBeginOrEnd = false;
        if (capturesNameGroups.group(2) != null) {

            // Calculates the occurrences of corresponding character/symbol in the first name
            dashesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("-", "").length();
            apostrophesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("\'", "").length();

            // Tests if first name begins or ends "suspiciously"
            matchesInvalidNameBeginOrEnd = minbe.matcher(capturesNameGroups.group(2));
            invalidLastNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        } else nullLastName = true;

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
                            return new boolean[] {false, false, true};
                        }
                    }
                }
            }
            return new boolean[] {birthDateObject.getTime() <= new Date().getTime(), !(birthDateObject.getTime() <= new Date().getTime()), false};
        } else return new boolean[]{true, false, false};
    }

    private void EmptyFieldProtocol(View view, boolean nameEmpty, boolean birthDateAgeEmpty, boolean ageNotBirthDate) {
        String snackBarString;

        // The following if- and else if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor

        if (view == nameInputField) {

            if (nameEmpty) {
                nameInputField.requestFocus();
            } else {
                birthDateAgeInputField.requestFocus();
            }

        } else if (view == birthDateAgeInputField) {

            if (birthDateAgeEmpty) {
                birthDateAgeInputField.requestFocus();
            } else {
                nameInputField.requestFocus();
            }

        }

        // The following if-, else if- and else statement takes care of
        // showing the appropriate message to the user

        if (nameEmpty && birthDateAgeEmpty) {

            if (ageNotBirthDate) snackBarString = getString(R.string.name_and_age_empty);
            else snackBarString = getString(R.string.name_and_birth_date_empty);

        } else if (birthDateAgeEmpty) {

            if (ageNotBirthDate) snackBarString = getString(R.string.age_empty);
            else snackBarString = getString(R.string.birth_date_empty);

        } else {

            snackBarString = getString(R.string.name_empty);
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidInputProtocol(View view, boolean[] nameValidationReport, boolean[] birthDateAgeValidationReport) {
        String snackBarString = "";

        // Fetching string resources
        String suspiciousPatterns = getString(R.string.suspicious_patterns_in_name);
        String invalidCharacters = getString(R.string.invalid_characters_in_name);
        String nullLastName = getString(R.string.no_last_name);
        String invalidDate = getString(R.string.invalid_date);
        String unknownDateFormat = getString(R.string.unknown_date_format);

        // The following if-, else if- and else statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor.
        // The order of the nested if statements implies the prioritization
        // according to which the messages will be shown (the subsequent if statement overrides the preceding).

        if (view == nameInputField) {

            if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
            if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;
            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;

            if (nameValidationReport[1] || nameValidationReport[2] || nameValidationReport[3]) {
                nameInputField.requestFocus();
            } else {
                birthDateAgeInputField.requestFocus();
            }

        } else if (view == birthDateAgeInputField) {

            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;
            if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
            if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;

            if (birthDateAgeValidationReport[1] || birthDateAgeValidationReport[2]) {
                birthDateAgeInputField.requestFocus();
            } else {
                nameInputField.requestFocus();
            }

        } else {

            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;
            if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
            if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidNameInputProtocol(View view, boolean[] nameValidationReport) {
        if (!MainActivity.generatedDemoList) {
            executeJob = jtf.matcher(nameInputField.getText());
            if (executeJob.matches()) {
                ClearFocus();
                nameInputField.setText("");
                ((MainActivity) getActivity()).GenerateDemoList();
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
        String snackBarString = "";

        String suspiciousPatterns = getString(R.string.suspicious_patterns_in_name);
        String invalidCharacters = getString(R.string.invalid_characters_in_name);
        String nullLastName = getString(R.string.no_last_name);

        if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
        if (nameValidationReport[2]) snackBarString = invalidCharacters;
        if (nameValidationReport[1]) snackBarString = nullLastName;

        // The following if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor
        if (view == birthDateAgeInputField) nameInputField.requestFocus();

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidBirthDateAgeInputProtocol(View view, boolean[] birthDateAgeValidationReport) {
        String snackBarString = "";

        String invalidDate = getString(R.string.invalid_date);
        String unknownDateFormat = getString(R.string.unknown_date_format);

        if (birthDateAgeValidationReport[1]) snackBarString = invalidDate;
        if (birthDateAgeValidationReport[2]) snackBarString = unknownDateFormat;

        // The following if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor
        if (view == nameInputField) birthDateAgeInputField.requestFocus();

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    // Clears focus from any interactive element by giving it back to the layout as a whole
    private void ClearFocus() {
        rootLayout.requestFocus();
    }
}
