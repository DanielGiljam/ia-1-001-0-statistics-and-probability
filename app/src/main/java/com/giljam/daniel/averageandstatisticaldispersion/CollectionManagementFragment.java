package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionManagementFragment extends Fragment {

    // Patterns needed for validating the name input string

    private static final Pattern mic =
            Pattern.compile(    "[^a-zàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿŒœŠšŸƒ' \\-]",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern mtc =
            Pattern.compile(    "([a-zàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿŒœŠšŸƒ])\\n{2}",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern mdsc =
            Pattern.compile(    "([\'\\-])\\n",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern minbe =
            Pattern.compile(    "\\A(-. *)|(. * -)\\z",
                                Pattern.CASE_INSENSITIVE);
    private static final Pattern cfln =
            Pattern.compile(    "\\A(\\S+)(?:\\s+(\\S+))*\\z",
                                Pattern.CASE_INSENSITIVE);

    // Matcher variables for more efficiently matching patterns

    private static Matcher matchesInvalidCharacters;        // goes with mic
    private static Matcher matchesTripleCharacters;         // goes with mtc
    private static Matcher matchesDoubleSpecialCharacters;  // goes with mdsc
    private static Matcher matchesInvalidNameBeginOrEnd;    // goes with minbe
    private static Matcher capturesNameGroups;        // goes with cfln

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
     * The {@link ListView} that will display the data converted by an {@link ArrayAdapter}.
     */
    private ListView mListView;

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
                    AddPerson(view);
                    return true;
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
                        yearAgeInputField.setImeOptions(    EditorInfo.IME_ACTION_NEXT +
                                                            EditorInfo.IME_FLAG_NAVIGATE_NEXT +
                                                            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    else
                        yearAgeInputField.setImeOptions(    EditorInfo.IME_ACTION_DONE +
                                                            EditorInfo.IME_FLAG_NAVIGATE_NEXT +
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
                    AddPerson(view);
                    return true;
                }
                return false;
            }
        });

        // Set up listener for the yearAgeSwitch
        // so that its state specifies how the input in yearAgeInputField should be interpreted
        // TODO: progress the yearAgeSwitch beyond cosmetic
        yearAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                yearAgeInputField.setHint(R.string.age_input_field_text);
            } else {
                yearAgeInputField.setHint(R.string.year_input_field_text);
            }
            }
        });

        // Set up listener for the addButton
        // so that it trigger addPerson method
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPerson(addButton);
            }
        });

        // Set up listener for the sortButton
        // so that the list is sorted accordingly to the sortButton's state
        // TODO: progress the sortButton beyond cosmetic
        sortButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sortButton.setTextOn(getString(R.string.name_sort_text));
                } else {
                    if (sortButton.getTextOn().equals(getString(R.string.name_sort_text))) {
                        sortButton.setChecked(true);
                        sortButton.setTextOn(getString(R.string.age_sort_text));
                    }
                }
            }
        });

        // Set up the ListView with the array adapter
        mListView = view.findViewById(R.id.people);
        mListView.setAdapter(MainActivity.mArrayAdapter);

        return view;
    }

    private void AddPerson(View view) {

        // Fetches whatever is in the EditText input fields
        String nameInputString = nameInputField.getText().toString();
        String yearAgeInputString = yearAgeInputField.getText().toString();

        // If the validation of the input is successful,
        // ergo the FieldValidation method returns true,
        // then the method continues,
        // else the addPerson method ends here
        if (FieldValidation(view, nameInputString, yearAgeInputString, yearAgeSwitch.isChecked())) {
            // TODO: add actual content to the addPerson method!
            nameInputField.setText("");
            yearAgeInputField.setText("");
        }
    }

    private boolean FieldValidation(View view, String nameInputString, String yearAgeInputString, boolean ageNotYear) {

        // Checks whether the input fields where filled out or not
        boolean nameEmpty = nameInputString.isEmpty();
        boolean yearAgeEmpty = yearAgeInputString.isEmpty();

        // If any input field was left empty,
        // then the EmptyFieldProtocol method takes care of notifying this to the user
        // and this method returns false, as the validation wasn't successful
        if (nameEmpty || yearAgeEmpty) {
            EmptyFieldProtocol(view, nameEmpty, yearAgeEmpty, ageNotYear);
            return false;
        }
        boolean[] nameValidationReport = NameValidation(nameInputString);
        boolean yearAgeValidationReport = YearAgeValidation(yearAgeInputString, ageNotYear);
        if (nameValidationReport[0] && yearAgeValidationReport) {
            return true;
        } else {
            InvalidInputProtocol(view, nameValidationReport, yearAgeValidationReport);
        }
        // TODO: add actual content to the FieldValidation method!
        return true;
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

        Snackbar.make(      getActivity().findViewById(R.id.main_layout),
                            snackBarString,
                            Snackbar.LENGTH_SHORT)
                .show();
    }

    private boolean[] NameValidation(String nameInputString) {

        capturesNameGroups = cfln.matcher(nameInputString);

        int dashesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("-", "").length();
        int apostrophesCountFirstName = capturesNameGroups.group(1).length() - capturesNameGroups.group(1).replace("\'", "").length();

        matchesInvalidNameBeginOrEnd = minbe.matcher(capturesNameGroups.group(1));
        boolean invalidFirstNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();

        boolean nullLastName = false;
        int dashesCountLastName = 0;
        int apostrophesCountLastName = 0;
        boolean invalidLastNameBeginOrEnd = false;

        if (capturesNameGroups.group(2) != null) {

            dashesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("-", "").length();
            apostrophesCountLastName = capturesNameGroups.group(2).length() - capturesNameGroups.group(2).replace("\'", "").length();

            matchesInvalidNameBeginOrEnd = minbe.matcher(capturesNameGroups.group(2));
            invalidLastNameBeginOrEnd = matchesInvalidNameBeginOrEnd.matches();
        } else nullLastName = true;

        matchesInvalidCharacters = mic.matcher(nameInputString);
        matchesTripleCharacters = mtc.matcher(nameInputString);
        matchesDoubleSpecialCharacters = mdsc.matcher(nameInputString);

        boolean invalidCharacters = matchesInvalidCharacters.matches();
        boolean tripleCharacters = matchesTripleCharacters.matches();
        boolean doubleSpecialCharacters = matchesDoubleSpecialCharacters.matches();

        boolean suspiciousPatterns =    tripleCharacters ||
                                        doubleSpecialCharacters ||
                                        dashesCountFirstName >= 2 ||
                                        apostrophesCountFirstName >= 2 ||
                                        invalidFirstNameBeginOrEnd ||
                                        dashesCountLastName >= 4 ||
                                        apostrophesCountLastName >= 4 ||
                                        invalidLastNameBeginOrEnd;

        boolean passedValidation = true;
        if (invalidCharacters || nullLastName || suspiciousPatterns) passedValidation = false;

        return new boolean[] {  passedValidation,
                                invalidCharacters,
                                nullLastName,
                                suspiciousPatterns};
    }

    private boolean YearAgeValidation(String yearAgeInputString, boolean ageNotYear) {
        if (!ageNotYear) {
            int yearInput = Integer.parseInt(yearAgeInputString);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return yearInput <= currentYear;
        }
    }

    private void InvalidInputProtocol(View view, boolean[] nameValidationReport, boolean yearAgeValidationReport) {
        String snackBarString = "";

        String invalidCharacters = "";
        String nullLastName = "";
        String suspiciousPatterns = "";

        String invalidYear = "";

        if (nameValidationReport[1]) invalidCharacters = getString(R.string.invalid_characters_in_name);
        if (nameValidationReport[2]) nullLastName = getString(R.string.no_last_name);
        if (nameValidationReport[3]) suspiciousPatterns = getString(R.string.suspicious_patterns_in_name);

        if (!yearAgeValidationReport) invalidYear = getString(R.string.invalid_year);

        if (!invalidCharacters.isEmpty()) snackBarString += invalidCharacters + "\n";
        if (!nullLastName.isEmpty()) snackBarString += nullLastName + "\n";
        if (!suspiciousPatterns.isEmpty()) snackBarString += suspiciousPatterns + "\n";

        if (!invalidYear.isEmpty()) snackBarString += invalidYear + "\n";
    }

    // Show the on-screen keyboard
    private void ShowKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    // Clears focus from any interactive element by giving it back to the layout as a whole TODO: see if there's a workaround regarding the duplicate "hide on-screen keyboard" code
    private void ClearFocus(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        rootLayout.requestFocus();
    }
}
