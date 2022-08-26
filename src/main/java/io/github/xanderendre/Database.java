package io.github.xanderendre;


import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Database implements AutoCloseable {

    private static Driver driver;

    Database() {
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "mypassword";

        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    private static void createRelationships() {

    }

    public static void editField(String id_entered, String field) {
        String employeeBefore = driver.session().run("MATCH (e:Employee {EmployeeId:'" + id_entered + "'}) RETURN e").toString();
        System.out.println(employeeBefore);

        String input = Util.getString("Enter the new " + field + " for the user:");
        try (Session session = driver.session()) {
            driver.session().run("MATCH (e:Employee {EmployeeId:'" + id_entered + "'})" + "SET e." + field + " = '" + input.toUpperCase() + "' " + "RETURN e." + field + " + ', from node ' + id(e)", parameters("message", input));
        }

        System.out.println();
    }

    @Override
    public void close() throws Exception {

        driver.close();

    }

    public void createUserData(Person person) {
        try (Session session = driver.session()) {
            driver.session().run("CREATE (:Employee {EmployeeId: '" + person.getId() + "', FirstName: '" + person.getFirstName() + "', LastName: '" + person.getLastName() + "', HireYear: '" + person.getHireYear() + "'})");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void insertAllUserData(ArrayList<Person> people) {
        try (Session session = driver.session()) {

            for (Person person : people) {
                driver.session().run("CREATE (:Employee {EmployeeId: '" + person.getId() + "', FirstName: '" + person.getFirstName() + "', LastName: '" + person.getLastName() + "', HireYear: '" + person.getHireYear() + "'})");
            }
            System.out.println("Doneee");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createReportsToRelationship(String emp, String boss, String relationship) {
        try (Session session = driver.session()) {
            String input = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee {EmployeeId:'" + emp + "'})" + ", (b:Employee {EmployeeId:'" + boss + "'}) " + "create (e)-[:" + relationship + "]->(b)");

                return result.toString();
            });
        }

    }

    public void readUserData() {
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e) RETURN (e)");

                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });
            System.out.println();
            System.out.println(greeting);
        }
    }

    public void readQuantityOfUser() {
        int input = Util.getInteger("Enter a quantity of users");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e) RETURN (e) LIMIT " + input);

                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });
            System.out.println();
            System.out.println(greeting);
        }
    }

    public void readLastNameOfUser() {
        String input = Util.getString("Enter the users last name: ");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee) WHERE e.LastName = '" + input + "'  RETURN (e)");

                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });
            System.out.println();
            System.out.println(greeting);
        }
    }


    public void readForFieldAndValue() {
        String field = Util.getString("Enter the field you are looking for: ");
        String value = Util.getString("Enter the value you are looking for: ");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee) WHERE e." + field + "='" + value + "'  RETURN (e)");

                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });
            System.out.println();
            System.out.println(greeting);
        }
    }

    public void readUserData(String id_entered) {
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee) WHERE e.EmployeeId = '" + id_entered + "' RETURN (e)");
                return result.single().get(0).toString();
            });
            System.out.println(greeting);
        }
    }

    public void deleteAllUsers() {
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (n) DETACH DELETE n");

                System.out.println("Deleted all records from the database.");
                return true;
            });
        }
    }

    public void deleteSpecificUser() {
        String field = Util.getString("Enter the Employee's Last Name you want to delete");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (n:Employee{LastName: '" + field + "'}) DETACH DELETE n");

                System.out.println("Deleted the record from the database.");
                return true;
            });
        }
    }

    public void createIndex() {
        String nameOfIndex = Util.getString("Enter the name of the index:");
        String indexField = Util.getString("Enter the field you want to index:");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("CREATE INDEX " + nameOfIndex + " FOR (n:Employee) ON (n." + indexField + ")");

                System.out.println("Deleted the record from the database.");
                return true;
            });
        }
    }

    public void queryIndex() {
        try (Session session = driver.session()) {

            System.out.println(session.run("SHOW INDEXES").toString());

            String indexName = Util.getString("Enter the index name:");
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run(":" + indexName);

                System.out.println("Deleted the record from the database.");
                return true;
            });
        }
    }
}


