package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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

public class CollectionManagementFragment extends Fragment {

    /**
     * Reference to this fragment's root layout/view.
     */
    private FrameLayout rootLayout;

    /**
     * Input field for person name.
     */
    private EditText nameInputField;

    /**
     * Button to submit input field data.
     */
    private Button addButton;

    /**
     * Input field for person birthyear.
     */
    private EditText yearAgeInputField;

    /**
     * Switch to toggle between the birthyear input field and the age input field.
     */
    private Switch yearAgeSwitch;

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
        addButton = view.findViewById(R.id.add_button);
        yearAgeInputField = view.findViewById(R.id.year_age_input_field);
        yearAgeSwitch = view.findViewById(R.id.year_age_switch);
        sortButton = view.findViewById(R.id.sort_button);

        rootLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

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

        // Set up listener for birthyear/age switch
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

        // Set up listener for add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPerson(addButton);
            }
        });

        // Set up listener for sort button
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
        String nameInputString = nameInputField.getText().toString();
        String yearAgeInputString = yearAgeInputField.getText().toString();
        if (FieldValidation(view, nameInputString, yearAgeInputString)) {
            nameInputField.setText("");
            yearAgeInputField.setText("");
        }
    }

    private boolean FieldValidation(View view, String nameInputString, String yearAgeInputString) {

        boolean nameEmpty = nameInputString.isEmpty();
        boolean yearAgeEmpty = yearAgeInputString.isEmpty();

        if (nameEmpty || yearAgeEmpty) {
            EmptyFieldProtocol(view, nameEmpty, yearAgeEmpty);
            return false;
        }
        return true;
    }

    private void EmptyFieldProtocol(View view, boolean nameEmpty, boolean yearAgeEmpty) {
        String snackBarString;

        if (view == nameInputField) {

            if (nameEmpty) {
                nameInputField.requestFocus();
                ShowKeyboard(nameInputField);
            } else {
                yearAgeInputField.requestFocus();
                ShowKeyboard(yearAgeInputField);
            }

        } else if (view == yearAgeInputField) {

            if (yearAgeEmpty) {
                yearAgeInputField.requestFocus();
                ShowKeyboard(yearAgeInputField);
            } else {
                nameInputField.requestFocus();
                ShowKeyboard(nameInputField);
            }

        }

        if (nameEmpty && yearAgeEmpty) {

            if (yearAgeSwitch.isChecked()) snackBarString = getString(R.string.name_and_age_empty);
            else snackBarString = getString(R.string.name_and_year_empty);

        } else if (yearAgeEmpty) {

            if (yearAgeSwitch.isChecked()) snackBarString = getString(R.string.age_empty);
            else snackBarString = getString(R.string.year_empty);

        } else {

            snackBarString = getString(R.string.name_empty);
        }

        Snackbar.make(getActivity().findViewById(R.id.main_layout),
                snackBarString,
                Snackbar.LENGTH_SHORT)

                .show();
    }

    private void ShowKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    private void ClearFocus(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        rootLayout.requestFocus();
    }
}
