package org.diningbyfriends;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.List;
import java.util.Scanner;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.both;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;

/**
 * DiningByFriends - Console Application
 */
public class App {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println();
        System.out.println("-------------------------------------");
        System.out.println("- Starting DiningByFriends Demo App -");
        System.out.println("-------------------------------------");
        Cluster cluster = connectToDatabase();
        System.out.println("using cluster connection: " + cluster.toString());
        GraphTraversalSource g = getGraphTraversalSource(cluster);
        System.out.println("using GraphTraversalSource: " + g.toString());
        System.out.println();

        displayMenu(g);

        cluster.close();
        System.exit(0);
    }

    private static void displayMenu(GraphTraversalSource g) {
        int option = -1;
        while (option != 0) {
            option = showMenu();
            switch (option) {
            case 0:
                break;
            case 1:
                // Find Person
                System.out.println("Found person: " + getPerson(g));
                break;
            case 2:
                // Find Friends
                System.out.println("Found friends: \n" + getFriends(g));
                break;
            case 3:
                // Find Friends of Friends
                System.out.println("Found friends of friends: \n" + getFriendsOfFriends(g));
                break;
            case 4:
                // Find path between two people
                System.out.println(findPathBetweenPeople(g));
                break;
            case 5:
                // Add person
                System.out.println(addPerson(g));
                break;
            case 6:
                // Update person
                System.out.println(updatePerson(g));
                break;
            case 7:
                // Add Friends Edge between people
                System.out.println(addFriendsEdge(g));
                break;
            case 8:
                // Drop person
                System.out.println(deletePerson(g));
                break;
            default:
                System.out.println("Sorry, please enter valid Option");
            }
        }

        System.out.println("Exiting DiningByFriends, Bye!");
    }

    private static int showMenu() {
        int option = -1;
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("--------------");
        System.out.println("1) Find a person by name");
        System.out.println("2) Find the friends of a person");
        System.out.println("3) Find the friends of the friends of a person");
        System.out.println("4) Find the path between two people");
        System.out.println("5) Add a person");
        System.out.println("6) Change a person’s name");
        System.out.println("7) Connect two people");
        System.out.println("8) Drop a person");
        System.out.println("0) Quit");
        System.out.println("--------------");
        System.out.println("Enter your choice:");
        option = input.nextInt();
        input.nextLine();
        return option;
    }

    private static String getPerson(GraphTraversalSource g) {
        System.out.println("Enter the first name of for the person to find:");
        String name = input.nextLine();

        Vertex vertex = g.V().has("person", "first_name", name).next();

        return vertex.toString();
    }

    private static String getFriends(GraphTraversalSource g) {
        System.out.println("Enter the name of the person to get their friends: ");
        String name = input.nextLine();

        List<Object> friends = g.V().
                has("person", "first_name", name).
                both("friends").values("first_name").
                toList();

        return StringUtils.join(friends, System.lineSeparator());
    }

    private static String getFriendsOfFriends(GraphTraversalSource g) {
        System.out.println("Enter the name of the person to get the friends of their friends: ");
        String name = input.nextLine();

        List<Object> friends = g.V().
                has("person", "first_name", name).
                repeat(
                        both("friends")
                ).times(2).
                dedup().values("first_name").
                toList();

        return StringUtils.join(friends, System.lineSeparator());
    }

    private static String findPathBetweenPeople(GraphTraversalSource g) {
        System.out.println("Enter the name for the person to start with:");
        String fromName = input.nextLine();
        System.out.println("Enter the name for the person to end at:");
        String toName = input.nextLine();

        // Returns a List of Path objects which represent
        // the path between the two person vertices
        List<Path> friends = g.V().has("person", "first_name", fromName).
                until(has("person", "first_name", toName)).
                repeat(
                        both("friends").simplePath()
                ).path().toList();

        return StringUtils.join(friends, "\r\n");
    }

    private static String addPerson(GraphTraversalSource g) {
        System.out.println("Enter the name for the person to add:");
        String name = input.nextLine();

        //This returns a Vertex type
        Vertex newVertex = g.addV("person").property("first_name", name).next();

        return newVertex.toString();
    }

    private static String updatePerson(GraphTraversalSource g) {
        System.out.println("Enter the name for the person to update:");
        String name = input.nextLine();
        System.out.println("Enter the new name for the person:");
        String newName = input.nextLine();

        //This returns a Vertex type
        Vertex vertex = g.V().has("person", "first_name", name).property("first_name", newName).next();
        return vertex.toString();
    }

    private static String addFriendsEdge(GraphTraversalSource g) {
        System.out.println("Enter the name for the person to start the edge at:");
        String fromName = input.nextLine();
        System.out.println("Enter the name for the person to end the edge at:");
        String toName = input.nextLine();

        //This returns an Edge type
        Edge newEdge = g.V().has("person", "first_name", fromName)
                .addE("friends").to(__.V().has("person", "first_name", toName))
                .next();

        return newEdge.toString();
    }

    private static String deletePerson(GraphTraversalSource g) {
        System.out.println("Enter the name for the person to delete:");
        String name = input.nextLine();

        //This returns a count of the vertices dropped
        Long vertexCount = g.V().has("person", "first_name", name).
                sideEffect(__.drop().iterate()).
                count().
                next();

        return vertexCount.toString();
    }

    private static GraphTraversalSource getGraphTraversalSource(Cluster cluster) {
        return traversal().withRemote(DriverRemoteConnection.using(cluster));
    }

    private static Cluster connectToDatabase() {
        Cluster.Builder builder = Cluster.build();
        builder.addContactPoint("localhost");
        builder.port(8182);

        return builder.create();
    }
}
