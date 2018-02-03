package com.giljam.daniel.averageandstatisticaldispersion;

class Person {

    private String firstName;
    private String lastName;

    private int birthyear;
    private int age;

    Person(String firstName, String lastName, int yearsOld, boolean b) {
        this.firstName = firstName;
        this.lastName = lastName;
        if (b) {
            age = yearsOld;
            CalculateBirthDate();
        } else {
            birthyear = yearsOld;
            CalculateAge();
        }
    }

    private void CalculateAge() {
        // TODO: make CalculateAge -method!
    }

    private void CalculateBirthDate() {
        // TODO: make CalculateBirthDate -method!
    }

}
