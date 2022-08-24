package io.github.xanderendre;


import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database implements AutoCloseable {

    private static Driver driver;

    Database() {
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "mypassword";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    public static void insertUserData(ArrayList<Person> people) {
        for (Person person : people) {
            driver.session().run("CREATE (:Employee {EmployeeId: '" + person.getId() + "', FirstName: '" + person.getFirstName() + "', LastName: '" + person.getLastName() + "', HireYear: '" + person.getHireYear() + "'})");
        }
        driver.close();
    }

    private static void createRelationships() {

    }

    @Override
    public void close() throws Exception {

        driver.close();

    }

    public void createUserData(Person person) {
        try (Session session = driver.session()) {

            driver.session().run("CREATE (:Employee {EmployeeId: '" + person.getId() + "', FirstName: '" + person.getFirstName() + "', LastName: '" + person.getLastName() + "', HireYear: '" + person.getHireYear() + "'})");

            session.close();
            close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void readUserData() {
        try (Session session = driver.session()) {
            Result result = driver.session().run("MATCH (person:Employee) RETURN (person)");
/*
            Iterator<Node> javaNodes = result.columnAs("m");
            for (Node node : IteratorUtil.asIterable(javaNodes))
            {
                //parse the node in here
            }
*/

            session.close();
        } catch (Neo4jException e) {
            e.printStackTrace();
        }

        driver.close();

    }

    public void readUserData(List<Person> people) {
        List<Person> allPeople = new ArrayList<Person>();
        for (Person person : people) {
            try {
                driver.session().run("MATCH (person:Employee) RETURN (p)");

            } catch (Neo4jException e) {
                e.printStackTrace();
            }

            driver.close();
        }
    }

/*

    private static MongoCollection<Document> connection() {
        MongoClient client = MongoClients.create("mongodb://localhost:2717");
        MongoDatabase database = client.getDatabase("dbt230");

        return database.getCollection("users");
    }

    public static boolean insertUserData(Person person) {
        Document document = new Document();
        document.append("first_name", person.getFirstName())
                .append("last_name", person.getLastName())
                .append("year_hired", person.getHireYear());

        return connection().insertOne(document).wasAcknowledged();
    }

    public static boolean insertUserData(List<Document> documents) {
        return connection().insertMany(documents).wasAcknowledged();
    }

    public static void readUserData() {
        FindIterable<Document> iterDoc = connection().find();
        Person person = null;
        for (Document document : iterDoc) {
            person = new Person(document.getString("first_name"), document.getString("last_name"), document.getInteger("year_hired"));
            System.out.println(person + "  |  " + document.getString("email"));
        }
    }

    public static void readUserData(int quantity) {
        FindIterable<Document> iterDoc = connection().find().limit(quantity);
        Person person = null;
        for (Document document : iterDoc) {
            person = new Person(document.getString("first_name"), document.getString("last_name"), document.getInteger("year_hired"));
            System.out.println(person + "  |  " + document.getString("email"));
        }
    }

    public static void readUserData(String field, String value) {
        FindIterable<Document> iterDoc = connection().find(eq(field, value));
        for (Document document : iterDoc) {
            System.out.println(document.getString("first_name") + " " + document.getString("last_name") + ": " + document.getString(field));
        }
    }


    public static void readUserData(String field, int value) {
        FindIterable<Document> iterDoc = connection().find(eq(field, value));
        for (Document document : iterDoc) {
            System.out.println(document.getString("first_name") + " " + document.getString("last_name") + ": " + document.getInteger(field));
        }
    }

    public static Person readUserData(String lastName) {
        Document document = connection().find(eq("last_name", lastName)).first();

        if (document != null)
            return new Person(document.getString("first_name"), document.getString("last_name"), document.getInteger("year_hired"));

        return null;
    }

    public static Person createUserData(String lastName) {
        Person person = null;
        Document document = connection().find(eq("last_name", lastName)).first();

        assert document != null;
        person = new Person(document.getString("first_name"), document.getString("last_name"), document.getInteger("year_hired"));

        return person;
    }

    public static boolean editUser(Person person, String field,  String value) {
        UpdateResult document = connection().updateOne(eq("last_name", person.getLastName()), set(field, value));
        System.out.println("Modified Data: " + readUserData(person.getLastName()));
        return document.wasAcknowledged();
    }

    public static boolean editUser(Person person, String field, int value) {
        UpdateResult document = connection().updateOne(eq(field, person.getLastName()), set(field, value));
        System.out.println("Modified Data: " + readUserData(person.getLastName()));
        return document.wasAcknowledged();
    }

    public static boolean deleteUser(Person person) {
        DeleteResult document = connection().deleteOne(eq("last_name", person.getLastName()));
        return document.wasAcknowledged();
    }

    public static void deleteCollection() {
        connection().drop();
    }

    public static boolean updateUser (Person person, String field, String newField, Document data) {
        UpdateResult document = connection().updateOne(eq("last_name", person.getLastName()), addToSet(newField, data));
        System.out.println("Modified Data: " + readUserData(person.getLastName()));
        return document.wasAcknowledged();
    }

    public static boolean updateUsers() {
        Faker faker = new Faker();
        UpdateResult document = connection().updateMany(new Document(), addToSet("address", new Document().append("street", faker.address().streetAddress()).append("city", faker.address().cityName()).append("country", faker.address().country())), new UpdateOptions());
        //System.out.println("Modified Data: " + readUserData(person.getLastName()));
        return document.wasAcknowledged();
    }
*/

}
