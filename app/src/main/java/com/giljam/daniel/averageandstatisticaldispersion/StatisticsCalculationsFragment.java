package com.giljam.daniel.averageandstatisticaldispersion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class StatisticsCalculationsFragment extends Fragment {

    /**
     * Text field for minimum value.
     */
    private TextView minimum;

    /**
     * Text field for maximum value.
     */
    private TextView maximum;

    /**
     * Text field for average value.
     */
    private TextView average;

    /**
     * Text field for standard deviation value.
     */
    private TextView standardDeviation;

    /**
     * Text field for lower quartile value.
     */
    private TextView lowerQuartile;

    /**
     * Text field for median value.
     */
    private TextView median;

    /**
     * Text field for higher quartile value.
     */
    private TextView upperQuartile;

    /**
     * Text field for the interquartile range value.
     */
    private TextView interQuartileRange;

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
        View view = inflater.inflate(R.layout.fragment_statistics_calculations, container, false);

        minimum = view.findViewById(R.id.minimum);
        maximum = view.findViewById(R.id.maximum);
        average = view.findViewById(R.id.average);
        standardDeviation = view.findViewById(R.id.standard_deviation);
        lowerQuartile = view.findViewById(R.id.lower_quartile);
        median = view.findViewById(R.id.median);
        upperQuartile = view.findViewById(R.id.upper_quartile);
        interQuartileRange = view.findViewById(R.id.interquartile_range);

        return view;
    }

    public void ReceiveCalculations(double[] stats) {
        if (stats == null) {
            minimum.setText(getString(R.string.n_a));
            maximum.setText(getString(R.string.n_a));
            average.setText(getString(R.string.n_a));
            standardDeviation.setText(getString(R.string.n_a));
            lowerQuartile.setText(getString(R.string.n_a));
            median.setText(getString(R.string.n_a));
            upperQuartile.setText(getString(R.string.n_a));
            interQuartileRange.setText(getString(R.string.n_a));
        } else {
            minimum.setText(new DecimalFormat("#").format(stats[0]));
            maximum.setText(new DecimalFormat("#").format(stats[1]));
            average.setText(new DecimalFormat("#.##").format(stats[2]));
            standardDeviation.setText(new DecimalFormat("#.##").format(stats[3]));
            lowerQuartile.setText(new DecimalFormat("#.#").format(stats[4]));
            median.setText(new DecimalFormat("#.#").format(stats[5]));
            upperQuartile.setText(new DecimalFormat("#.#").format(stats[6]));
            interQuartileRange.setText(new DecimalFormat("#.#").format(stats[7]));
        }
    }
}
