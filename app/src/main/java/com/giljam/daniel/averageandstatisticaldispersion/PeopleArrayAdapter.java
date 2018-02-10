package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeopleArrayAdapter<T> extends ArrayAdapter<T> {

    private static Pattern primitivePattern = Pattern.compile("(.+) \\(birthyear or age: (\\d+)\\)");
    private static Matcher primitiveMatcher;

    private int resource;

    public PeopleArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view;
        if (convertView == null) view = inflater.inflate(resource, parent, false);
        else view = convertView;
        primitiveMatcher = primitivePattern.matcher((String)getItem(position));
        primitiveMatcher.matches();
        TextView personName = view.findViewById(R.id.person_name);
        TextView personYearAge = view.findViewById(R.id.person_year_age);
        personName.setText(primitiveMatcher.group(1));
        personYearAge.setText(primitiveMatcher.group(2));
        return view;
    }
}
