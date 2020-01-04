package org.diningbyfriends;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.List;
import java.util.Scanner;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.both;

/**
 * DiningByFriends - Console Application
 */
public class App {
    public static void main(String[] args) {
        Cluster cluster = connectToDatabase();
        System.out.println("using cluster connection: " + cluster.toString());
        GraphTraversalSource g = getGraphTraversalSource(cluster);
        System.out.println("using GraphTraversalSource: " + g.toString());

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
                System.out.println(getFriends(g));
                break;
            case 3:
                // Find Friends of Friends
                System.out.println(getFriendsOfFriends(g));
                break;
            default:
                System.out.println("Sorry, please enter valid Option");
            }
        }

        System.out.println("Exiting DiningByFriends, Bye!");
    }

    private static int showMenu() {
        int option = -1;
        Scanner keyboard = new Scanner(System.in);
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("--------------");
        System.out.println("1) Find person by name");
        System.out.println("2) Find a person's friends");
        System.out.println("3) Find the friends of the person's friends");
        System.out.println("0) Quit");
        System.out.println("--------------");
        System.out.println("Enter your choice:");
        option = keyboard.nextInt();
        keyboard.close();
        return option;
    }

    public static String getPerson(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the first name of for the person to find:");
        String name = keyboard.nextLine();
        keyboard.close();

        Vertex vertex = g.V().has("person", "first_name", name).next();

        return vertex.toString();
    }

    private static String getFriends(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to get their friends: ");
        String name = keyboard.nextLine();
        keyboard.close();

        List<Object> friends = g.V().has("person", "first_name", name).both("friends").dedup().values("first_name")
                .toList();

        return StringUtils.join(friends, System.lineSeparator());
    }

    private static String getFriendsOfFriends(GraphTraversalSource g) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the person to get their friends: ");
        String name = keyboard.nextLine();
        keyboard.close();

        List<Object> friends = g.V().has("person", "first_name", name).repeat(both("friends")).times(2).dedup()
                .values("first_name").toList();

        return StringUtils.join(friends, System.lineSeparator());
    }

    private static GraphTraversalSource getGraphTraversalSource(Cluster cluster) {
        return traversal().withRemote(DriverRemoteConnection.using(cluster));
    }

    public static Cluster connectToDatabase() {
        Cluster.Builder builder = Cluster.build();
        builder.addContactPoint("localhost");
        builder.port(8182);

        return builder.create();
    }
}
