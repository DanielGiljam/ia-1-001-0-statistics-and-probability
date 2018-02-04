package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.ArrayList;
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

    private static Comparator<Person> sortByName = new SortByName();
    private static Comparator<Person> sortByAge = new SortByAge();

    private static List<Person> people;

    private static List<Person> originalOrderBackup;

    PersonDataManagement() {
        activeSortingMode = SortingMode.ORIGINAL;
        people = new ArrayList<>();
        originalOrderBackup = new ArrayList<>();
    }

    private void BackgroundSortPeople() {
        people = originalOrderBackup;
        switch (activeSortingMode) {
            case NAME:
                Collections.sort(people, sortByName);
                break;
            case AGE:
                Collections.sort(people, sortByAge);
                break;
        }
    }

    public List<Person> getPeople() {
        return people;
    }

    public void CollectPeople(List<Person> people) {
        originalOrderBackup.addAll(people);
        BackgroundSortPeople();
    }

    public void DeletePeople(int index) {
        if (index - 1 >= 0 && index - 1 <= people.size()) {
            originalOrderBackup.remove(index - 1);
            BackgroundSortPeople();
        } else {
            System.out.println("No person at index " + index + ".  (No changes)");
        }
    }

    public void DeletePeople(Person person, int iterator) {
        int occurrences = Collections.frequency(people, person);
        if (occurrences > 0 && iterator <= occurrences) {
            for (int i = 0; i < iterator; i++) {
                originalOrderBackup.remove(person);
            }
            BackgroundSortPeople();
        } else System.out.println("Cannot delete " + person + " " + iterator + " times. (No changes)");
    }

    public void ClearPeople() {
        if (!people.isEmpty()) {
            people.clear();
            originalOrderBackup.clear();
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

    private static class SortByName implements Comparator<Person> {

        // Overriding the compare method to sort the age
        public int compare(Person person1, Person person2) {
            return person1.getLastName().compareToIgnoreCase(person2.getLastName());
        }

    }

    private static class SortByAge implements Comparator<Person> {

        // Overriding the compare method to sort the age
        public int compare(Person person1, Person person2) {
            return person1.getAge() - person2.getAge();
        }

    }
}
