package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.Calendar;

class Person {

    private String firstName;
    private String lastName;

    private Calendar birthyear;
    private int age;

    Person(String firstName, String lastName, int yearAge, boolean ageNotYear) {
        this.firstName = firstName;
        this.lastName = lastName;
        if (ageNotYear) ParseAge(yearAge);
        else ParseBirthYear(yearAge);
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    private void ParseBirthYear(int birthyear) {
        this.birthyear = Calendar.getInstance();
        this.birthyear.set(Calendar.YEAR, birthyear);
    }

    private void ParseAge(int age) {
        this.birthyear = Calendar.getInstance();
        int year = this.birthyear.get(Calendar.YEAR);
        this.birthyear.set(Calendar.YEAR, year - age);
    }
}
