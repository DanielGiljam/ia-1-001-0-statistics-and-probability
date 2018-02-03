package com.giljam.daniel.averageandstatisticaldispersion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class StatisticsVisualizationsFragment extends Fragment {

    /**
     * Input field that allows user to add items to the collection.
     */
    private EditText nameInputField;

    /**
     * Button to submit input field data.
     */
    private Button addButton;

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
        View view = inflater.inflate(R.layout.fragment_statistics_visualizations, container, false);

        // Set up the input fields and buttons
        nameInputField = view.findViewById(R.id.addField);
        addButton = view.findViewById(R.id.add_button);

        // Set up listener for button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addedNumbersString = nameInputField.getText().toString();
                nameInputField.setText("");
                ((MainActivity) getActivity()).onAdd(addedNumbersString);
            }
        });

        // Set up the ListView with the array adapter.
        mListView = view.findViewById(R.id.people);
        mListView.setAdapter(MainActivity.mArrayAdapter);

        return view;
    }
}
