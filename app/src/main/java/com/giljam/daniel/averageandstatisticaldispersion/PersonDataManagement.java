package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

enum SortingMode {
    ORIGINAL,
    NAME,
    AGE;

    String string() {
        String string = "";
        switch (this) {
            case ORIGINAL:
                string = "original order";
                break;
            case NAME:
                string = "alphabetical order (by last name)";
                break;
            case AGE:
                string = "incremental order (by age)";
                break;
        }
        return string;
    }
}

class PersonDataManagement {

    private static SortingMode activeSortingMode;

    private static Comparator<Person> sortById = new SortById();
    private static Comparator<Person> sortByName = new SortByName();
    private static Comparator<Person> sortByAge = new SortByAge();

    private static List<Person> people;

    private static List<String> primitiveList;

    private static boolean primitiveAgeNotYear;

    PersonDataManagement() {
        activeSortingMode = SortingMode.ORIGINAL;
        people = new ArrayList<>();
        primitiveList = new ArrayList<>();
        primitiveAgeNotYear = false;
    }

    private void RefreshPeopleYearAgeInformation() {
        Calendar currentDate = Calendar.getInstance();
        for (Person person : people) {
            person.RefreshYearAgeInformation(currentDate);
        }
    }

    private void BackgroundSortPeople() {
        RefreshPeopleYearAgeInformation();
        switch (activeSortingMode) {
            case ORIGINAL:
                Collections.sort(people, sortById);
                break;
            case NAME:
                Collections.sort(people, sortByName);
                break;
            case AGE:
                Collections.sort(people, sortByAge);
                break;
        }
        PreparePrimitiveList();
    }

    private void PreparePrimitiveList() {
        primitiveList.clear();
        for (Person person : people) {
            if (primitiveAgeNotYear) primitiveList.add(person.getName() + " (birthyear or age: " + person.getAge() + ")");
            else primitiveList.add(person.getName() + " (birthyear or age: " + person.getBirthYear() + ")");
        }
    }

    public List<Integer> getPeopleData() {
        List<Integer> peopleData = new ArrayList<>();
        for (Person person : people) {
            peopleData.add(person.getAge());
        }
        return peopleData;
    }

    public List<String> getPrimitiveList() {
        return primitiveList;
    }

    public void TriggerRefresh(boolean ageNotYear) {
        primitiveAgeNotYear = ageNotYear;
        BackgroundSortPeople();
    }

    public void CollectPeople(List<Person> people) {
        PersonDataManagement.people.addAll(people);
        BackgroundSortPeople();
    }

    public void DeletePeople(int index) {
        if (index - 1 >= 0 && index - 1 <= people.size()) {
            people.remove(index);
            BackgroundSortPeople();
        } else {
            System.out.println("No person at index " + index + ".  (No changes)");
        }
    }

    public void ClearPeople() {
        if (!people.isEmpty()) {
            people.clear();
        } else {
            System.out.println("Your list of people was already empty. (No changes)");
        }
    }

    public void SortPeople(SortingMode sortingMode) {
        if (activeSortingMode != sortingMode) {
            activeSortingMode = sortingMode;
            BackgroundSortPeople();
            System.out.println("Sorting mode set to " + sortingMode.string() + ".");
        } else {
            System.out.println("The sorting mode was already set to " + sortingMode.string() + ".");
        }
    }

    private void PrintPeople(String header) {
        if (!header.isEmpty()) System.out.println("\n" + header + "\n");
        for (int i = 0; i < people.size(); i++) {
            System.out.println("\t" + (i + 1) + ". " + people.get(i));
        }
    }

    public void PrintPeople() {
        System.out.println("\nYour list of people:\n");
        for (int i = 0; i < people.size(); i++) {
            System.out.println("\t" + (i + 1) + ". " + people.get(i));
        }
    }

    private static class SortById implements  Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return person1.getId() - person2.getId();
        }

    }

    private static class SortByName implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return person1.getLastName().compareToIgnoreCase(person2.getLastName());
        }

    }

    private static class SortByAge implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return person1.getAge() - person2.getAge();
        }

    }
}
