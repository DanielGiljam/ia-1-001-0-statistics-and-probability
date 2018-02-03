package com.giljam.daniel.averageandstatisticaldispersion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

public class CollectionManagementFragment extends Fragment {

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
    private EditText yearInputField;

    /**
     * Input field for person age.
     */
    private EditText ageInputField;

    /**
     * Switch to toggle between the birthyear input field and the age input field.
     */
    private Switch dateAgeSwitch;

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
        nameInputField = view.findViewById(R.id.name_input_field);
        addButton = view.findViewById(R.id.add_button);
        yearInputField = view.findViewById(R.id.year_input_field);
        ageInputField = view.findViewById(R.id.age_input_field);
        dateAgeSwitch = view.findViewById(R.id.date_age_switch);

        // Set up listener for button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addedNumbersString = nameInputField.getText().toString();
                nameInputField.setText("");
                ((MainActivity) getActivity()).onAdd(addedNumbersString);
            }
        });

        // Set up listener for switch
        dateAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    yearInputField.setVisibility(View.GONE);
                    ageInputField.setVisibility(View.VISIBLE);
                } else {
                    yearInputField.setVisibility(View.VISIBLE);
                    ageInputField.setVisibility(View.GONE);
                }
            }
        });

        // Set up the ListView with the array adapter.
        mListView = view.findViewById(R.id.people);
        mListView.setAdapter(MainActivity.mArrayAdapter);

        return view;
    }
}
