package org.diningbyfriends;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.Scanner;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
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
//                case 1:
//                    //Get Person
//                    System.out.println("Person Vertex: " + getPerson(g));
//                    break;
//                case 2:
//                    //Add Person
//                    System.out.println("New person Vertex: " + addPerson(g));
//                    break;
//                case 3:
//                    //Update Person
//                    System.out.println("Update person Vertex: " + updatePerson(g));
//                    break;
//                case 4:
//                    //Delete Person
//                    System.out.println(deletePerson(g));
//                    break;
//                case 5:
//                    //Add Edge
//                    System.out.println(addFriendsEdge(g));
//                    break;
//                case 6:
//                    //Find Friends
//                    System.out.println(getFriends(g));
//                    break;
//                case 7:
//                    //Find Friends of Friends
//                    System.out.println(getFriendsOfFriends(g));
//                    break;
//                case 8:
//                    //findPathBetweenUsers
//                    System.out.println(findPathBetweenPeople(g));
//                    break;
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
        System.out.println("1) Get person Vertex");
        System.out.println("2) Add person Vertex");
        System.out.println("3) Update person Vertex");
        System.out.println("4) Delete person Vertex");
        System.out.println("5) Add friends Edge");
        System.out.println("6) Find your Friends");
        System.out.println("7) Find the Friends of your Friends");
        System.out.println("8) Find the path between two people");
        System.out.println("0) Quit");
        System.out.println("--------------");
        System.out.println("Enter your choice:");
        option = keyboard.nextInt();

        return option;
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
