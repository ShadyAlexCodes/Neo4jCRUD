package io.github.xanderendre;

import org.neo4j.driver.exceptions.Neo4jException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    static Database database = new Database();

    public static ArrayList<Person> createPerson(ArrayList<String> lineToRead) {
        ArrayList<Person> people = new ArrayList<Person>();

        for (String line : lineToRead) {
            Person person = new Person(line);
            people.add(person);
        }

        return people;
    }

    public static ArrayList<String> readLines() {
        String fileName = "data\\people\\output.csv";
        File file = new File(fileName);
        String lineToRead = "";
        ArrayList<String> lines = new ArrayList<String>();

        if (file.exists()) {
            // Reads text from an input string
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while ((lineToRead = bufferedReader.readLine()) != null) {
                    lines.add(lineToRead);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("The file " + file + " was not found!");
        }
        return lines;
    }

    //createReportsToRelationship
    public static void readRelationshipLines(String fileInput) {
        String fileName = "data\\people\\" + fileInput + ".csv";
        File file = new File(fileName);

        String lineToRead = "";
        if (file.exists()) {
            // Reads text from an input string
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while ((lineToRead = br.readLine()) != null) {
                    String[] employee = lineToRead.split(",");
                    database.createReportsToRelationship(employee[0], employee[1], fileInput);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("The file " + file + " was not found!");
        }

    }

    public void run() throws Exception {
        boolean quit = false;

        while (!quit) {
            int selection = Util.getMenuSelection("Main Menu", new String[]{"Create User", "Read Users", "Edit Users", "Delete Users", "Import Data", "Advanced"}, true);
            switch (selection) {
                case 0 -> quit = true;
                case 1 -> createUser();
                case 2 -> readUsers();
                case 3 -> editUser();
                case 4 -> deleteUsers();
                case 5 -> importAllData();
                case 6 -> advanced();
            }
        }
    }

    public void readUsers() {
        boolean quit = false;

        while (!quit) {
            int selection = Util.getMenuSelection("Read Users", new String[]{"Read All Users", "Read 'X' number of Users", "Read specific User", "Read for field and value"}, true);
            switch (selection) {
                case 0 -> quit = true;
                case 1 -> database.readUserData();
                case 2 -> database.readQuantityOfUser();
                case 3 -> database.readLastNameOfUser();
                case 4 -> database.readForFieldAndValue();

            }
        }
    }

    public void editUser() {
        boolean quit = false;
        String id = Util.getString("Enter the user's id:");

        while (!quit) {
            int selection = Util.getMenuSelection("Updating a User", new String[]{"Update a User's First Name", "Update a User's Last name", "Update a User's Hire Year"}, true);
            switch (selection) {
                case 0 -> quit = true;
                case 1 -> Database.editField(id, "FirstName");
                case 2 -> Database.editField(id, "LastName");
                case 3 -> Database.editField(id, "HireYear");
            }
        }
    }

    public void deleteUsers() {
        boolean quit = false;

        while (!quit) {
            int selection = Util.getMenuSelection("Delete", new String[]{"Delete All Users"}, true);
            switch (selection) {
                case 0 -> quit = true;
                case 1 -> database.deleteAllUsers();
                case 2 -> database.deleteSpecificUser();
            }
        }
    }

    public void advanced() {
        boolean quit = false;

        while (!quit) {
            int selection = Util.getMenuSelection("Advanced Options", new String[]{"Create an Index", "Delete an index", "Query an Index"}, true);
            switch (selection) {
                case 0 -> quit = true;
                case 1 -> database.createIndex();
                case 2 -> database.deleteIndex();
                case 3 -> database.queryIndex();
                //  case 1 -> deleteAUser();
                // case 2 -> deleteEntireCollection();
                /// case 3 -> editYearHired(person);
            }
        }
    }

    public void createUser() {
        String firstName = Util.getString("What is their first name");
        String lastName = Util.getString("What is their last name?");
        int yearHired = Util.getInteger("What year were they hired?");

        Person person = new Person(firstName, lastName, yearHired);

        database.createUserData(person);
    }

    public void importAllData() {
        long startTime = System.nanoTime();

        ArrayList<String> people = readLines();
        ArrayList<Person> aryPeople = createPerson(people);

        database.insertAllUserData(aryPeople);
        readRelationshipLines("friendships");
        readRelationshipLines("reportsTo");

        long endTime = System.nanoTime();

        System.out.println("Elapsed Time in nano seconds: " + (endTime - startTime));
    }
}
