package com.giljam.daniel.statisticsandprobability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PeopleDataFacilitator {

    private static Matcher personUnitRecognizer;

    private SortPeopleBy activeSortingMode;

    private final Comparator<Person> sortById = new SortById();
    private final Comparator<Person> sortByName = new SortByName();
    private final Comparator<Person> sortByAge = new SortByAge();
    private final Comparator<Person> sortByShoeSize = new SortByShoeSize();
    private final Comparator<Person> sortByHeight = new SortByHeight();

    private final List<Person> people = new ArrayList<>();

    PeopleDataFacilitator(SortPeopleBy activeSortingMode) {
        this.activeSortingMode = activeSortingMode;
    }

    ReadWriteReport ReadFromCSV(File csvFile, boolean merge) {

        Scanner scanner;
        try {
            scanner = new Scanner(csvFile).useDelimiter("\n");
        } catch (FileNotFoundException e) {
            return ReadWriteReport.FILE_NOT_FOUND;
        }

        ReadWriteReport returnValue = ReadWriteReport.SUCCESSFUL;
        List<Person> peopleFromCSV = new ArrayList<>();
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (!next.isEmpty()) {
                try {
                    peopleFromCSV.add(ExtractPersonData(next));
                } catch (ParseException e) {
                    returnValue = ReadWriteReport.PARTIALLY_SUCCESSFUL;
                }
            }
        }

        scanner.close();

        if (peopleFromCSV.isEmpty()) return ReadWriteReport.FILE_WAS_EMPTY;

        if (!people.isEmpty()) {
            if (merge) {
                people.addAll(peopleFromCSV);
            } else {
                people.clear();
                people.addAll(peopleFromCSV);
            }
        } else {
            people.addAll(peopleFromCSV);
        }
        BackgroundSortPeople();
        return returnValue;
    }

    ReadWriteReport WriteToCSV(File csvFile, boolean withActiveSorting) {

        if (csvFile.exists() && csvFile.isFile()) {
            csvFile.delete();
        } try {
            csvFile.createNewFile();
        } catch (IOException e) {
            return ReadWriteReport.IO_EXCEPTION;
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(csvFile);
        } catch (FileNotFoundException e) {
            return ReadWriteReport.FILE_NOT_FOUND;
        }

        SortPeopleBy activeSortingModeBackup = activeSortingMode;
        if (!withActiveSorting) {
            activeSortingMode = SortPeopleBy.ORIGINAL;
            BackgroundSortPeople();
        }

        List<String> fileContentsList = new ArrayList<>();
        for (Person person : people)
            fileContentsList.add(PreparePersonData(person));
        if (activeSortingMode == SortPeopleBy.ORIGINAL)
            Collections.reverse(fileContentsList);

        StringBuilder fileContents = new StringBuilder();
        for (String personData : fileContentsList)
            fileContents.append(personData).append("\n");

        if (!withActiveSorting) {
            activeSortingMode = activeSortingModeBackup;
            BackgroundSortPeople();
        }

        try {
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
            return ReadWriteReport.SUCCESSFUL;
        } catch (IOException e) {
            return ReadWriteReport.IO_EXCEPTION;
        }
    }

    List<Person> GetPeople() {
        return people;
    }

    List<Double> GetPeopleAgeData() {
        List<Double> peopleAgeData = new ArrayList<>();
        for (Person person : people) peopleAgeData.add((double) person.getAge());
        return peopleAgeData;
    }

    List<Double> GetPeopleShoeSizeData() {
        List<Double> peopleShoeSizeData = new ArrayList<>();
        for (Person person : people) peopleShoeSizeData.add((double) person.getShoeSize());
        return peopleShoeSizeData;
    }

    List<Double> GetPeopleHeightData() {
        List<Double> peopleHeightData = new ArrayList<>();
        for (Person person : people) peopleHeightData.add((double) person.getHeight());
        return peopleHeightData;
    }

    SortPeopleBy GetActiveSortingMode() {
        return activeSortingMode;
    }

    int GetAmountOfPeople() {
        return people.size();
    }

    int WhereIs(Person person) {
        return people.indexOf(person);
    }

    void AddPerson(Person person) {
        people.add(person);
        BackgroundSortPeople();
    }

    void AddPeople(List<Person> people) {
        this.people.addAll(people);
        BackgroundSortPeople();
    }

    void RemovePerson(int index) {
        people.remove(index);
        BackgroundSortPeople();
    }

    void ClearPeople() {
        people.clear();
    }

    void SortPeople(SortPeopleBy sortingMode) {
        activeSortingMode = sortingMode;
        BackgroundSortPeople();
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

    private void RefreshPeopleBirthDateAgeInformation() {
        Calendar currentDate = Calendar.getInstance();
        for (Person person : people) {
            person.RefreshBirthDateAgeInformation(currentDate);
        }
    }

    private Person ExtractPersonData(String personData) throws ParseException {
        if (personUnitRecognizer == null)
            personUnitRecognizer = Pattern.compile( "\\A\"(.*)\",\\s*\"(.*)\",\\s*\"(.*)\",\\s*\"(.*)\",\\s*\"(.*)\"\\z",
                                                    Pattern.CASE_INSENSITIVE)
                                        .matcher(personData);
        else personUnitRecognizer.reset(personData);
        if (!personUnitRecognizer.matches()) {
            personUnitRecognizer.lookingAt();
            throw new ParseException(personData, personUnitRecognizer.end());
        }
        String firstName;
        if (personUnitRecognizer.group(1).isEmpty()) throw new ParseException(personData, personUnitRecognizer.start(1));
        firstName = personUnitRecognizer.group(1);
        String lastName;
        if (personUnitRecognizer.group(2).isEmpty()) throw new ParseException(personData, personUnitRecognizer.start(2));
        lastName = personUnitRecognizer.group(2);
        Date birthDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(personUnitRecognizer.group(3));
        int shoeSize;
        try {
            shoeSize = Integer.parseInt(personUnitRecognizer.group(4));
        } catch (NumberFormatException e) {
            throw new ParseException(personUnitRecognizer.group(4), 0);
        }
        int height;
        try {
            height = Integer.parseInt(personUnitRecognizer.group(5));
        } catch (NumberFormatException e) {
            throw new ParseException(personUnitRecognizer.group(5), 0);
        }
        return new Person(firstName, lastName, birthDate, shoeSize, height);
    }

    private String PreparePersonData(Person person) {
        StringBuilder personData = new StringBuilder();

        String start = "\"";
        String interSection = "\", \"";
        String end = "\"";

        personData.append(start);
        personData.append(person.getFirstName()).append(interSection);
        personData.append(person.getLastName()).append(interSection);

        String birthDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(person.getBirthDate().getTime());
        personData.append(birthDate).append(interSection);

        personData.append(person.getShoeSize()).append(interSection);
        personData.append(person.getHeight());
        personData.append(end);

        return personData.toString();
    }

    private int CompareNames(Person person1, Person person2) {
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

    private class SortById implements  Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return person2.getId() - person1.getId();
        }

    }

    private class SortByName implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            return CompareNames(person1, person2);
        }

    }

    private class SortByAge implements Comparator<Person> {

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
                        return CompareNames(person1, person2);
                }
            }
        }

    }

    private class SortByShoeSize implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            if (person1.getShoeSize() == person2.getShoeSize()) {
                return CompareNames(person1, person2);
            } else
                return person1.getShoeSize() - person2.getShoeSize();
        }

    }

    private class SortByHeight implements Comparator<Person> {

        public int compare(Person person1, Person person2) {
            if (person1.getHeight() == person2.getHeight()) {
                return CompareNames(person1, person2);
            } else
                return person1.getHeight() - person2.getHeight();
        }

    }
}

enum SortPeopleBy {
    ORIGINAL,
    NAME,
    AGE,
    SHOE_SIZE,
    HEIGHT
}

enum ReadWriteReport {
    SUCCESSFUL,
    PARTIALLY_SUCCESSFUL,
    FILE_WAS_EMPTY,
    FILE_NOT_FOUND,
    IO_EXCEPTION
}

class Person {

    private static final long MILLI_SECONDS_TO_YEARS = 31449600000L;
    private static int idCount = 0;

    private int id;

    private String name;
    private String firstName;
    private String lastName;

    private Calendar birthDate;

    private int age;
    private int shoeSize;
    private int height;

    Person(String firstName, String lastName, int age, int shoeSize, int height) {
        SetupName(firstName, lastName);
        ParseAge(age);
        this.shoeSize = shoeSize;
        this.height = height;
        id = idCount;
        idCount++;
    }

    Person(String firstName, String lastName, Date birthDate, int shoeSize, int height) {
        SetupName(firstName, lastName);
        ParseBirthDate(birthDate);
        this.shoeSize = shoeSize;
        this.height = height;
        id = idCount;
        idCount++;
    }

    String getName() {
        return name;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    Calendar getBirthDate() {
        return birthDate;
    }

    int getAge() {
        return age;
    }

    int getShoeSize() {
        return shoeSize;
    }

    int getHeight() {
        return height;
    }

    int getId() {
        return id;
    }

    void RefreshBirthDateAgeInformation(Calendar currentDate) {
        long birthDateAsLong = birthDate.getTime().getTime();
        long currentTimeAsLong = currentDate.getTime().getTime();
        long differenceInTime = currentTimeAsLong - birthDateAsLong;
        long ageAsLong = (differenceInTime - (differenceInTime % MILLI_SECONDS_TO_YEARS)) / MILLI_SECONDS_TO_YEARS;
        age = (int) ageAsLong;
    }

    private void SetupName(String firstName, String lastName) {
        if (firstName.isEmpty() || lastName.isEmpty()) name = firstName + lastName;
        else name = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private void ParseBirthDate(Date birthDate) {
        this.birthDate = Calendar.getInstance();
        this.birthDate.setTime(birthDate);
    }

    // TODO! Find out why some people turn out 0 years old.

    private void ParseAge(int age) {
        this.birthDate = Calendar.getInstance();
        this.birthDate.add(Calendar.YEAR, -age);
    }
}
