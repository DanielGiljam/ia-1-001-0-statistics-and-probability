package com.giljam.daniel.averageandstatisticaldispersion;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private static Pattern primitivePattern = Pattern.compile("(.+) \\(birthyear or age: (\\d+)\\)");
    private static Matcher primitiveMatcher;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView personName;
        public TextView personYearAge;

        public ViewHolder(View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.person_name);
            personYearAge = itemView.findViewById(R.id.person_year_age);
        }
    }

    private Context context;
    private List<Person> people;

    public PeopleAdapter (Context context, List<Person> people) {
        this.context = context;
        this.people = people;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.list_view_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(PeopleAdapter.ViewHolder viewHolder, int position) {
        Person person = people.get(position);
        TextView personName = viewHolder.personName;
        TextView personYearAge = viewHolder.personYearAge;
        personName.setText(person.getName());
        personYearAge.setText(String.format(getContext().getString(R.string.list_view_item_person_year_age), person.getBirthYear(), person.getAge()));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }
}
