package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.Calendar;

class Person {

    private static int idCount = 0;

    private String name;
    private String firstName;
    private String lastName;

    private Calendar yearAgeDataOrigin;

    private int birthYear;
    private int age;

    private int id;

    Person(String firstName, String lastName, int yearAge, boolean ageNotYear) {
        name = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        if (ageNotYear) ParseAge(yearAge);
        else ParseBirthYear(yearAge);
        id = idCount;
        idCount++;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public void RefreshYearAgeInformation(Calendar currentDate) {
        birthYear = yearAgeDataOrigin.get(Calendar.YEAR);
        age = currentDate.get(Calendar.YEAR) - birthYear;
    }

    private void ParseBirthYear(int birthYear) {
        this.yearAgeDataOrigin = Calendar.getInstance();
        this.yearAgeDataOrigin.set(Calendar.YEAR, birthYear);
    }

    private void ParseAge(int age) {
        this.yearAgeDataOrigin = Calendar.getInstance();
        int year = this.yearAgeDataOrigin.get(Calendar.YEAR);
        this.yearAgeDataOrigin.set(Calendar.YEAR, year - age);
    }
}
