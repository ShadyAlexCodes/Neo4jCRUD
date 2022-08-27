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


    public static void editField(String id_entered, String field) {
        System.out.println("User Data Before Edit: ");
        readUserData(id_entered);

        String input = Util.getString("Enter the new " + field + " for the user:");
        try (Session session = driver.session()) {
            String response = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee {EmployeeId:'" + id_entered + "'})" + "SET e." + field + " = '" + input + "' " + "RETURN e");
                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            }).toString();
            System.out.println("Updated: " + response);
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }

        System.out.println("User Data After Edit: ");
        readUserData(id_entered);
        System.out.println();
    }

    public static void readUserData(String id_entered) {
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (e:Employee) WHERE e.EmployeeId = '" + id_entered + "' RETURN (e)");
                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;

            });
            System.out.println(greeting);
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }
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
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
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
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
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
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
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
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }
    }

    public void deleteAllUsers() {
        try (Session session = driver.session()) {

            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (n) DETACH DELETE n");

                System.out.println("Deleted all records from the database.");
                return true;
            });

        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
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
        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }
    }

    public void createIndex() {
        // String nameOfIndex = Util.getString("Enter the name of the index:");
        String indexField = Util.getString("Enter the field you want to index:");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("CREATE INDEX ON :Employee(" + indexField + ")");

                System.out.println("Index Created.");
                return true;
            });

        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }
    }

    public void deleteIndex() {
        // String nameOfIndex = Util.getString("Enter the name of the index:");
        String indexField = Util.getString("Enter the index you want to delete:");
        try (Session session = driver.session()) {
            Object greeting = session.writeTransaction(tx -> {
                Result result = tx.run("DROP INDEX ON :Employee(" + indexField + ")");

                System.out.println("Index Deleted.");
                return true;
            });

        } catch (Exception exception) {
            System.out.println("The query above threw an error.");
        }
    }


    public void queryIndex() {
        try (Session session = driver.session()) {
            Object indexes = session.writeTransaction(tx -> {
                Result result = tx.run("CALL db.indexes");
                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });

            System.out.println(indexes);

            String indexName = Util.getString("Enter the field name:");
            String valueName = Util.getString("Enter the value name:");
            Object query = session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (employee:Employee) WHERE employee." + indexName + " = '" + valueName + "' RETURN employee");
                List<Map<String, Object>> nodeList = new ArrayList<>();

                while (result.hasNext()) {
                    nodeList.add(result.next().fields().get(0).value().asMap());
                }
                return nodeList;
            });

            System.out.println(query);
        } catch (Exception exception) {
            System.out.println("The query above threw an error");
        }
    }
}


