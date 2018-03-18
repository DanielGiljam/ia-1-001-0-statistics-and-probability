package com.giljam.daniel.statisticsandprobability;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class FirstStatisticsFragment extends Fragment {

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

    @Override
    public View onCreateView(   LayoutInflater inflater,
                                ViewGroup container,
                                Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.first_statistics_fragment, container, false);

        FrameLayout rootLayout = view.findViewById(R.id.first_statistics_fragment_root_layout);
        minimum = view.findViewById(R.id.minimum);
        maximum = view.findViewById(R.id.maximum);
        average = view.findViewById(R.id.average);
        standardDeviation = view.findViewById(R.id.standard_deviation);
        lowerQuartile = view.findViewById(R.id.lower_quartile);
        median = view.findViewById(R.id.median);
        upperQuartile = view.findViewById(R.id.upper_quartile);
        interQuartileRange = view.findViewById(R.id.interquartile_range);

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

        ((MainActivity)getActivity()).RequestRefresh(this);

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
            minimum.setText(new DecimalFormat(getString(R.string.integer_format)).format(stats[0]));
            maximum.setText(new DecimalFormat(getString(R.string.integer_format)).format(stats[1]));
            average.setText(new DecimalFormat(getString(R.string.medium_float_format)).format(stats[2]));
            standardDeviation.setText(new DecimalFormat(getString(R.string.medium_float_format)).format(stats[3]));
            lowerQuartile.setText(new DecimalFormat(getString(R.string.short_float_format)).format(stats[4]));
            median.setText(new DecimalFormat(getString(R.string.short_float_format)).format(stats[5]));
            upperQuartile.setText(new DecimalFormat(getString(R.string.short_float_format)).format(stats[6]));
            interQuartileRange.setText(new DecimalFormat(getString(R.string.short_float_format)).format(stats[7]));
        }
    }
}
