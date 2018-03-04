package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.Calendar;
import java.util.Date;

class Person {

    private static final long MILLI_SECONDS_TO_YEARS = 31449600000L;
    private static int idCount = 0;

    private String name;
    private String firstName;
    private String lastName;

    private Calendar birthDate;

    private int age;

    private int shoeSize;

    private int height;

    private int id;

    Person(String firstName, String lastName, int age, int shoeSize, int height) {
        name = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        ParseAge(age);
        this.shoeSize = shoeSize;
        this.height = height;
        id = idCount;
        idCount++;
    }

    Person(String firstName, String lastName, Date birthDate, int shoeSize, int height) {
        if (firstName.isEmpty() || lastName.isEmpty()) name = firstName + lastName;
        else name = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        ParseBirthDate(birthDate);
        this.shoeSize = shoeSize;
        this.height = height;
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

    public Calendar getBirthDate() {
        return birthDate;
    }

    public int getAge() {
        return age;
    }

    public int getShoeSize() {
        return shoeSize;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public void RefreshBirthDateAgeInformation(Calendar currentDate) {
        long birthDateAsLong = birthDate.getTime().getTime();
        long currentTimeAsLong = currentDate.getTime().getTime();
        long differenceInTime = currentTimeAsLong - birthDateAsLong;
        long ageAsLong = (differenceInTime - (differenceInTime % MILLI_SECONDS_TO_YEARS)) / MILLI_SECONDS_TO_YEARS;
        age = (int) ageAsLong;
    }

    private void ParseBirthDate(Date birthDate) {
        this.birthDate = Calendar.getInstance();
        this.birthDate.setTime(birthDate);
    }

    private void ParseAge(int age) {
        this.birthDate = Calendar.getInstance();
        this.birthDate.add(Calendar.YEAR, -age);
    }
}
