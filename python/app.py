from gremlin_python.process.anonymous_traversal import traversal
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.graph_traversal import __

import json

to_string = json.dumps

class App(object):    
    def connect_to_database(self):
        return DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')

    def get_graph_traversal_source(self, cluster):
        return traversal().withRemote(cluster)

    def display_menu(self, g):
        option = int(self.show_menu())
        if option == 0:
            exit()
        elif option == 1:
            print(self.get_person(g))
        elif option == 2:
            print(self.get_friends(g))
        elif option == 3:
            print(self.get_friends_of_friends(g))
        else:
            print("Sorry, please enter valid Option")
        self.display_menu(g)

    def show_menu(self):
        print("Main Menu:")
        print("--------------")
        print("1) Find person by name")
        print("2) Find a person's friends")
        print("3) Find the friends of the person's friends")
        print("0) Quit")
        print("--------------")
        print("Enter your choice:")
        return raw_input();

    def get_person(self, g):
        name = raw_input("Enter the first name of for the person to find:");
        vertex = g.V().has("person", "first_name", name).next();
        return vertex

    def get_friends(self, g):
        name = raw_input("Enter the name of the person to get their friends: ");
        friends = g.V().has("person", "first_name", name).\
            both("friends").\
            dedup().\
            values("first_name").toList();
        return '\n'.join(friends);

    def get_friends_of_friends(self, g):
        name = raw_input("Enter the name of the person to get their friends: ");
        friends = g.V().has("person", "first_name", name).repeat(__.both("friends") ).times(2).dedup().values("first_name").toList();
        return '\n'.join(friends);

def main():
    app = App()
    cluster = app.connect_to_database()
    g = app.get_graph_traversal_source(cluster)
    app.display_menu(g)

if __name__ == "__main__":
    main()