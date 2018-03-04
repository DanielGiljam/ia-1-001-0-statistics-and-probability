package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

enum SortingMode {
    ORIGINAL,
    NAME,
    AGE,
    SHOE_SIZE,
    HEIGHT;

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
            case SHOE_SIZE:
                string = "incremental order (by shoe size)";
                break;
            case HEIGHT:
                string = "incremental order (by height)";
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
    private static Comparator<Person> sortByShoeSize = new SortByShoeSize();
    private static Comparator<Person> sortByHeight = new SortByHeight();

    private static List<Person> people;

    PersonDataManagement() {
        activeSortingMode = SortingMode.ORIGINAL;
        people = new ArrayList<>();
    }

    private void RefreshPeopleBirthDateAgeInformation() {
        Calendar currentDate = Calendar.getInstance();
        for (Person person : people) {
            person.RefreshBirthDateAgeInformation(currentDate);
        }
    }

    private void BackgroundSortPeople() {
        RefreshPeopleBirthDateAgeInformation();
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
            case SHOE_SIZE:
                Collections.sort(people, sortByShoeSize);
                break;
            case HEIGHT:
                Collections.sort(people, sortByHeight);
                break;
        }
    }

    public List<Person> getPeople() {
        return people;
    }

    public int getAmountOfPeople() {
        return people.size();
    }

    public List<Double> getPeopleData() {
        List<Double> peopleData = new ArrayList<>();
        for (Person person : people) {
            peopleData.add((double) person.getAge());
        }
        return peopleData;
    }

    public int WhereIs(Person person) {
        return people.indexOf(person);
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

    public static int compareNames(Person person1, Person person2) {
        String name1 = person1.getLastName() + person1.getFirstName();
        name1 = name1.replace("-", "");
        name1 = name1.replace("\'", "");
        name1 = name1.toLowerCase();
        String name2 = person2.getLastName() + person2.getFirstName();
        name2 = name2.replace("-", "");
        name2 = name2.replace("\'", "");
        name2 = name2.toLowerCase();
        return name1.compareToIgnoreCase(name2);
    }

    private static class SortById implements  Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return person1.getId() - person2.getId();
        }

    }

    private static class SortByName implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return compareNames(person1, person2);
        }

    }

    private static class SortByAge implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            if (!(person2.getBirthDate().get(Calendar.YEAR) == person1.getBirthDate().get(Calendar.YEAR)))
                return person2.getBirthDate().get(Calendar.YEAR) - person1.getBirthDate().get(Calendar.YEAR);
            else {
                if (!(person2.getBirthDate().get(Calendar.MONTH) == person1.getBirthDate().get(Calendar.MONTH)))
                    return person2.getBirthDate().get(Calendar.MONTH) - person1.getBirthDate().get(Calendar.MONTH);
                else {
                    if (!(person2.getBirthDate().get(Calendar.DATE) == person1.getBirthDate().get(Calendar.DATE)))
                        return person2.getBirthDate().get(Calendar.DATE) - person1.getBirthDate().get(Calendar.DATE);
                    else
                        return compareNames(person1, person2);
                }
            }
        }

    }

    private static class SortByShoeSize implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            if (person1.getShoeSize() == person2.getShoeSize()) {
                return compareNames(person1, person2);
            } else
                return person1.getShoeSize() - person2.getShoeSize();
        }

    }

    private static class SortByHeight implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            if (person1.getHeight() == person2.getHeight()) {
                return compareNames(person1, person2);
            } else
                return person1.getHeight() - person2.getHeight();
        }

    }
}
