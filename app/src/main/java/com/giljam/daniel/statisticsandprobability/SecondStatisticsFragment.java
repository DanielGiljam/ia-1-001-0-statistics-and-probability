package com.giljam.daniel.statisticsandprobability;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class SecondStatisticsFragment extends Fragment {

    /**
     * The linear regression line graph.
     */
    private GraphView graph;

    /**
     * The linear regression line graph's grid labal manager.
     */
    private GridLabelRenderer glr;

    /**
     * The linear regression line graph's viewport.
     */
    private Viewport vp;

    /**
     * The "no data" text that displays if the GraphView is hidden.
     */
    private TextView noData;

    /**
     * Text field for linear regression line formula.
     */
    private TextView linearRegressionLine;

    /**
     * Text field for correlation coefficient.
     */
    private TextView correlationCoefficient;

    @Override
    public View onCreateView(   LayoutInflater inflater,
                                ViewGroup container,
                                Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.second_statistics_fragment, container, false);

        FrameLayout rootLayout = view.findViewById(R.id.second_statistics_fragment_root_layout);
        graph = view.findViewById(R.id.linear_regression_line_graph);
        noData = view.findViewById(R.id.no_data);
        linearRegressionLine = view.findViewById(R.id.linear_regression_line);
        correlationCoefficient = view.findViewById(R.id.correlation_coefficient);

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

        vp = graph.getViewport();

        vp.setXAxisBoundsManual(true);
        vp.setMinX(getResources().getInteger(R.integer.min_shoe_size) - getResources().getInteger(R.integer.extra_min_shoe_size));
        vp.setMaxX(getResources().getInteger(R.integer.max_shoe_size) + getResources().getInteger(R.integer.extra_max_shoe_size));

        vp.setYAxisBoundsManual(true);
        vp.setMinY(getResources().getInteger(R.integer.min_height) - getResources().getInteger(R.integer.extra_min_height));
        vp.setMaxY(getResources().getInteger(R.integer.max_height) + getResources().getInteger(R.integer.extra_max_height));

        glr = graph.getGridLabelRenderer();

        glr.setNumHorizontalLabels((int) (vp.getMaxX(false) - vp.getMinX(false)) / 5);
        glr.setNumVerticalLabels((int) vp.getMaxY(false) / (int) vp.getMinY(false));
        glr.setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) return super.formatLabel(value, true);
                else return super.formatLabel(value, false) + " cm";
            }
        });

        ((MainActivity)getActivity()).RequestRefresh(this);

        return view;
    }

    public void ReceiveCalculations(LinearFunction partOne, double partTwo, List<Double> shoeSizes, List<Double> heights) {
        if (partOne == null || partTwo == NaN) {
            graph.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            linearRegressionLine.setText(R.string.n_a);
            correlationCoefficient.setText(R.string.n_a);
        } else {

            if (graph.getVisibility() == View.GONE) graph.setVisibility(View.VISIBLE);
            if (noData.getVisibility() == View.VISIBLE) noData.setVisibility(View.GONE);

            if (!graph.getSeries().isEmpty()) graph.removeAllSeries();
            if (partOne.getB() < vp.getMinY(false) || partOne.getB() > vp.getMaxY(false)) {
                LineGraphSeries<DataPoint> lineSeries =
                        new LineGraphSeries<>(
                                new DataPoint[]{
                                        new DataPoint(partOne.getX(vp.getMinY(false)), vp.getMinY(false)),
                                        new DataPoint(partOne.getX(vp.getMaxY(false)), vp.getMaxY(false))});
                graph.addSeries(lineSeries);
            } else {
                LineGraphSeries<DataPoint> lineSeries =
                        new LineGraphSeries<>(
                                new DataPoint[]{
                                        new DataPoint(vp.getMinX(false), partOne.getY(vp.getMinX(false))),
                                        new DataPoint(vp.getMaxX(false), partOne.getY(vp.getMaxX(false)))});
                graph.addSeries(lineSeries);
            }

            List<DataPoint> prePreSeries = new ArrayList<>();
            int n;
            if (shoeSizes.size() > heights.size()) n = heights.size();
            else n = shoeSizes.size();
            for (int i = 0; i < n; i++)
                prePreSeries.add(new DataPoint(shoeSizes.get(i), heights.get(i)));
            DataPoint[] preSeries = prePreSeries.toArray(new DataPoint[prePreSeries.size()]);
            PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<>(preSeries);
            pointSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(5);
                    paint.setColor(getResources().getColor(R.color.colorAccent));
                    canvas.drawLine(x - 10, y - 10, x + 10, y + 10, paint);
                    canvas.drawLine(x + 10, y - 10, x - 10, y + 10, paint);
                }
            });
            graph.addSeries(pointSeries);

            String k = new DecimalFormat("#.##").format(partOne.getK());
            String b = new DecimalFormat("#.##").format(partOne.getB());
            if (partOne.getB() > 0) linearRegressionLine.setText(getString(R.string.linear_regression_line_formula_positive_b, k, b));
            else if (partOne.getB() == 0) linearRegressionLine.setText(getString(R.string.linear_regression_line_formula_no_b, k));
            else linearRegressionLine.setText(getString(R.string.linear_regression_line_formula_negative_b, k, b.substring(1)));

            correlationCoefficient.setText(new DecimalFormat("#.####").format(partTwo));
        }
    }
}
