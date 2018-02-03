package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PersonDataManagement {

    private static boolean sortFlip; // TODO: make sorting functionality that supports multiple (more than two) kinds of sorting orders!

    private static List<Person> people;

    private static List<Person> originalOrderBackup;

    PersonDataManagement() {
        sortFlip = false;
        people = new ArrayList<>();
        originalOrderBackup = new ArrayList<>();
    }

    private void BackgroundSortPeople() {
        if (sortFlip) {
            people = originalOrderBackup;
            // Collections.sort(people);
        } else {
            originalOrderBackup = people;
            // Collections.sort(originalOrderBackup);
        }
    }

    public List<Person> getPeople() {
        return people;
    }

    public void CollectPeople(List<Person> people) {
        if (sortFlip) {
            PersonDataManagement.originalOrderBackup.addAll(people);
        } else {
            PersonDataManagement.people.addAll(people);
        }
        BackgroundSortPeople();
        PrintPeople();
    }

    public void DeletePeople(int index) {
        if (!(index - 1 >= 0) && !(index - 1 <= people.size())) {
            System.out.println("No person at index " + index + ".");
        }
        if (sortFlip) {
            originalOrderBackup.remove(index - 1);
        } else {
            people.remove(index - 1);
        }
        BackgroundSortPeople();
    }

    public void DeletePeople(Person person, int iterator) {
        int occurrences = Collections.frequency(people, person);
        if (occurrences > 0 && iterator <= occurrences) {
            if (sortFlip) {
                for (int i = 0; i < iterator; i++) {
                    originalOrderBackup.remove(person);
                }
            } else {
                for (int i = 0; i < iterator; i++) {
                    people.remove(person);
                }
            }
            BackgroundSortPeople();
        } else System.out.println("Cannot delete " + person + " " + iterator + " times.");
    }

    public void ClearPeople() {
        if (!people.isEmpty()) {
            people.clear();
            originalOrderBackup.clear();
        }
    }

    private void SortPeople() {
        people = originalOrderBackup;
        sortFlip = !sortFlip;
        if (sortFlip) {
            PrintPeople("Your list of people, sorted:");
        } else {
            PrintPeople("Your list of people, unsorted:");
        }
    }

    public void SortPeople(Boolean bool) {
        if (sortFlip != bool) {
            people = originalOrderBackup;
            sortFlip = !sortFlip;
        }
        if (sortFlip) {
            PrintPeople("Your list of people, sorted:");
        } else {
            PrintPeople("Your list of people, unsorted:");
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
}
