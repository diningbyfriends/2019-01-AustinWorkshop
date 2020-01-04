using System;
using Gremlin.Net.Driver;
using Gremlin.Net.Driver.Remote;
using Gremlin.Net.Process.Traversal;
using static Gremlin.Net.Process.Traversal.AnonymousTraversalSource;


namespace csharp
{
    class Program
    {
        static void Main(string[] args)
        {
            var cluster = ConnectToDatabase();
            Console.WriteLine("using cluster connection: " + cluster.ToString());
            GraphTraversalSource g = GetGraphTraversalSource(cluster);
            Console.WriteLine("using GraphTraversalSource: " + g.ToString());

            DisplayMenu(g);

            cluster.Dispose();
            Environment.Exit(0);
        }

        private static void DisplayMenu(GraphTraversalSource g)
        {
            int option = -1;
            while (option != 0)
            {
                option = ShowMenu();
                switch (option)
                {
                    case 0:
                        break;
                    case 1:
                        // Find Person
                        Console.WriteLine("Found person: " + GetPerson(g));
                        break;
                    case 2:
                        // Find Friends
                        Console.WriteLine(GetFriends(g));
                        break;
                    case 3:
                        // Find Friends of Friends
                        Console.WriteLine(GetFriendsOfFriends(g));
                        break;
                    default:
                        Console.WriteLine("Sorry, please enter valid Option");
                        break;
                }
            }

            Console.WriteLine("Exiting DiningByFriends, Bye!");
        }

        private static int ShowMenu()
        {
            int option = -1;
            Console.WriteLine();
            Console.WriteLine("Main Menu:");
            Console.WriteLine("--------------");
            Console.WriteLine("1) Find person by name");
            Console.WriteLine("2) Find a person's friends");
            Console.WriteLine("3) Find the friends of the person's friends");
            Console.WriteLine("0) Quit");
            Console.WriteLine("--------------");
            Console.WriteLine("Enter your choice:");
            if (int.TryParse(Console.ReadLine(), out option))
            {
                return option;
            }
            else
            {
                return ShowMenu();
            }
        }

        public static String GetPerson(GraphTraversalSource g)
        {
            Console.WriteLine("Enter the first name of for the person to find:");
            String name = Console.ReadLine();

            var vertex = g.V().
                    Has("person", "first_name", name).
                    Next();

            return vertex.ToString();
        }

        static String GetFriends(GraphTraversalSource g)
        {
            Console.WriteLine("Enter the name of the person to get their friends: ");
            String name = Console.ReadLine();

            var friends = g.V()
                    .Has("person", "first_name", name)
                    .Both("friends")
                    .Dedup()
                    .Values<String>("first_name")
                    .ToList();

            return String.Join("\n\r", friends);
        }

        private static String GetFriendsOfFriends(GraphTraversalSource g)
        {
            Console.WriteLine("Enter the name of the person to get their friends: ");
            String name = Console.ReadLine();

            var friends = g.V().Has("person", "first_name", name).
                    Repeat(
                            __.Both("friends")
                          ).Times(2).Dedup().
                    Values<String>("first_name").
                    ToList();

            return String.Join("\n\r", friends);
        }

        private static GraphTraversalSource GetGraphTraversalSource(DriverRemoteConnection cluster)
        {
            return Traversal().WithRemote(cluster);
        }

        public static DriverRemoteConnection ConnectToDatabase()
        {
            return new DriverRemoteConnection(new GremlinClient(new GremlinServer("localhost", 8182)));
        }
    }
}
