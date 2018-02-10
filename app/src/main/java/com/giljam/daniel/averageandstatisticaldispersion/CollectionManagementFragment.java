package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.List;
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

    // Matcher variables for more efficiently matching patterns

    private static Matcher matchesInvalidCharacters;        // goes with mic
    private static Matcher matchesTripleCharacters;         // goes with mtc
    private static Matcher matchesDoubleSpecialCharacters;  // goes with mdsc
    private static Matcher matchesInvalidNameBeginOrEnd;    // goes with minbe
    private static Matcher capturesNameGroups;              // goes with cfln
    private static Matcher capitalizesNames;                // goes with nc

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
    private EditText yearAgeInputField;

    /**
     * Switch to toggle between the birthyear input field and the age input field.
     */
    private Switch yearAgeSwitch;

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
    static PeopleAdapter mAdapter;

    /**
     * The {@link ListView} that will display the data converted by an {@link ArrayAdapter}.
     */
    private ListView mListView;

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
        yearAgeInputField = view.findViewById(R.id.year_age_input_field);
        yearAgeSwitch = view.findViewById(R.id.year_age_switch);
        addButton = view.findViewById(R.id.add_button);
        sortButton = view.findViewById(R.id.sort_button);

        // Set up the ListView with the array adapter
        mListView = view.findViewById(R.id.people);
        mListView.setAdapter(MainActivity.mArrayAdapter);

        // Create the adapter that will convert the person list data into "list-displayable" data.
        // mAdapter = new PeopleAdapter(getContext(), MainActivity.pdm.getPeople());

        // Set up the ListView with the array adapter
        // mRecyclerView = view.findViewById(R.id.people);
        // mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        // mRecyclerView.setAdapter(mAdapter);

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
                    if (yearAgeInputField.getText().toString().isEmpty())
                        nameInputField.setImeOptions(   EditorInfo.IME_ACTION_NEXT +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                        EditorInfo. IME_FLAG_NO_EXTRACT_UI);
                    else
                        nameInputField.setImeOptions(   EditorInfo.IME_ACTION_DONE +
                                                        EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                        EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in nameInputField
        // so that the enter key's action is the same as the addButton's if the yearAgeInputField already has been filled out
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

        // Set up listener for when focus changes to yearAgeInputField
        // to trigger script that determines what action the enter key should have
        yearAgeInputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (nameInputField.getText().toString().isEmpty())
                        yearAgeInputField.setImeOptions(    EditorInfo.IME_ACTION_PREVIOUS +
                                                            EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS +
                                                            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        yearAgeInputField.setImeOptions(    EditorInfo.IME_ACTION_DONE +
                                                            EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS +
                                                            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                }
            }
        });

        // Set up listener for enter key when in yearAgeInputField
        // so that the enter key's action is the same as the addButton's if the nameInputField already has been filled out
        yearAgeInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PreAddPerson(view);
                    return true;
                }
                return false;
            }
        });
        yearAgeInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (yearAgeInputField.getImeOptions() == EditorInfo.IME_ACTION_DONE + EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS + EditorInfo.IME_FLAG_NO_EXTRACT_UI) {
                        PreAddPerson(view);
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up listener for the yearAgeSwitch
        // so that its state specifies how the input in yearAgeInputField should be interpreted
        yearAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                ((MainActivity)getActivity()).YearOrAge(true);
                yearAgeInputField.setHint(R.string.age_input_field_text);
            } else {
                ((MainActivity) getActivity()).YearOrAge(false);
                yearAgeInputField.setHint(R.string.year_input_field_text);
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
            }
        });

        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        String yearAgeInputString = yearAgeInputField.getText().toString();

        // If the validation of the input is successful,
        // ergo the FieldValidation method returns true,
        // then the method continues,
        // else the addPerson method ends here
        if (FieldValidation(view, nameInputString, yearAgeInputString, yearAgeSwitch.isChecked())) {
            String firstName = NameCapitalization(capturesNameGroups.group(1));
            String lastName = NameCapitalization(capturesNameGroups.group(2));
            Person person = new Person (firstName, lastName, Integer.parseInt(yearAgeInputString), yearAgeSwitch.isChecked());
            ClearFocus();
            nameInputField.setText("");
            yearAgeInputField.setText("");
            ((MainActivity)getActivity()).AddPerson(person);
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

    private boolean FieldValidation(View view, String nameInputString, String yearAgeInputString, boolean ageNotYear) {

        // Checks whether the input fields where filled out or not
        boolean nameEmpty = nameInputString.isEmpty();
        boolean yearAgeEmpty = yearAgeInputString.isEmpty();

        // Creating the validation reports with preset base values
        boolean[] nameValidationReport = new boolean[]{true, false, false, false};
        boolean yearAgeValidationReport = true;

        // If any input field was left empty,
        // then the following code block (if statement) shuts down the validation process
        // and notifies the user appropriately
        if (nameEmpty || yearAgeEmpty) {

            // Any filled in field containing invalid input is considered more urgent than empty fields.
            // The following if- else if statement takes care of notifying the user in a case like that
            if (!nameEmpty) {

                nameValidationReport = NameValidation(nameInputString);
                if (!nameValidationReport[0]) InvalidInputProtocol(view, nameValidationReport);

            } else if (!yearAgeEmpty) {

                yearAgeValidationReport = YearAgeValidation(yearAgeInputString, ageNotYear);
                if (!yearAgeValidationReport) InvalidInputProtocol(view, yearAgeValidationReport);
            }

            // If both fields where left empty or if a single filled in field was valid,
            // then the EmptyFieldProtocol method takes care of notifying the user about empty fields
            if (nameValidationReport[0] && yearAgeValidationReport) EmptyFieldProtocol(view, nameEmpty, yearAgeEmpty, ageNotYear);

            return false;
        }

        // If the validation process makes it all the way here,
        // it means that both fields where filled in with some kind of input.
        // That input is evaluated in the following lines
        nameValidationReport = NameValidation(nameInputString);
        yearAgeValidationReport = YearAgeValidation(yearAgeInputString, ageNotYear);

        // If the validation reports are positive then the validation process returns true,
        // else the InvalidInputProtocol method handles notifying the user appropriately and the validation process returns false
        if (nameValidationReport[0] && yearAgeValidationReport) {
            return true;
        } else {
            InvalidInputProtocol(view, nameValidationReport, yearAgeValidationReport);
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

    private boolean YearAgeValidation(String yearAgeInputString, boolean ageNotYear) {
        // Basically validation is only necessary when the user hasn't flicked the ageYearSwitch
        // ergo when the yearAgeInputString is read as a calendar year.
        // The contents of the following if statement make sure that year isn't in the future
        if (!ageNotYear) {
            int yearInput = Integer.parseInt(yearAgeInputString);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return yearInput <= currentYear;
        } else return true;
    }

    private void EmptyFieldProtocol(View view, boolean nameEmpty, boolean yearAgeEmpty, boolean ageNotYear) {
        String snackBarString;

        // The following if- and else if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor

        if (view == nameInputField) {

            if (nameEmpty) {
                nameInputField.requestFocus();
            } else {
                yearAgeInputField.requestFocus();
            }

        } else if (view == yearAgeInputField) {

            if (yearAgeEmpty) {
                yearAgeInputField.requestFocus();
            } else {
                nameInputField.requestFocus();
            }

        }

        // The following if-, else if- and else statement takes care of
        // showing the appropriate message to the user

        if (nameEmpty && yearAgeEmpty) {

            if (ageNotYear) snackBarString = getString(R.string.name_and_age_empty);
            else snackBarString = getString(R.string.name_and_year_empty);

        } else if (yearAgeEmpty) {

            if (ageNotYear) snackBarString = getString(R.string.age_empty);
            else snackBarString = getString(R.string.year_empty);

        } else {

            snackBarString = getString(R.string.name_empty);
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidInputProtocol(View view, boolean[] nameValidationReport, boolean yearAgeValidationReport) {
        String snackBarString = "";

        // Fetching string resources
        String suspiciousPatterns = getString(R.string.suspicious_patterns_in_name);
        String invalidCharacters = getString(R.string.invalid_characters_in_name);
        String nullLastName = getString(R.string.no_last_name);
        String invalidYear = getString(R.string.invalid_year);

        // The following if-, else if- and else statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor.
        // The order of the nested if statements implies the prioritization
        // according to which the messages will be shown (the subsequent if statement overrides the preceding).

        if (view == nameInputField) {

            if (!yearAgeValidationReport) snackBarString = invalidYear;
            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;

            if (nameValidationReport[1] || nameValidationReport[2] || nameValidationReport[3]) {
                nameInputField.requestFocus();
            } else {
                yearAgeInputField.requestFocus();
            }

        } else if (view == yearAgeInputField) {

            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;
            if (!yearAgeValidationReport) snackBarString = invalidYear;

            if (!yearAgeValidationReport) {
                yearAgeInputField.requestFocus();
            } else {
                nameInputField.requestFocus();
            }

        } else {

            if (nameValidationReport[3]) snackBarString = suspiciousPatterns;
            if (nameValidationReport[2]) snackBarString = invalidCharacters;
            if (nameValidationReport[1]) snackBarString = nullLastName;
            if (!yearAgeValidationReport) snackBarString = invalidYear;
        }

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidInputProtocol(View view, boolean[] nameValidationReport) {
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
        if (view == yearAgeInputField) nameInputField.requestFocus();

        Snackbar.make(  getActivity().findViewById(R.id.main_layout),
                        snackBarString,
                        Snackbar.LENGTH_SHORT)
                .show();
    }

    private void InvalidInputProtocol(View view, boolean yearAgeValidationReport) {
        String snackBarString = "";

        String invalidYear = getString(R.string.invalid_year);

        if (!yearAgeValidationReport) snackBarString = invalidYear;

        // The following if statement takes care of some fine tuning
        // regarding the choice of what field is focused and given the cursor depending on
        // what field already is in focus/already has the cursor
        if (view == nameInputField) yearAgeInputField.requestFocus();

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
