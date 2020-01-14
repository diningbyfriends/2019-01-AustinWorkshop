from gremlin_python.process.anonymous_traversal import traversal
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.graph_traversal import __


import json

to_string = json.dumps

class App(object):    
    def connectToDatabase(self):
        return DriverRemoteConnection('ws://db:8182/gremlin', 'g')

    def getGraphTraversalSource(self, cluster):
        return traversal().withRemote(cluster)

    def displayMenu(self, g):
        option = int(self.showMenu())
        if option==0:
            exit()
        elif option==1:
            print(self.getPerson(g))
        elif option==2:
            print(self.getFriends(g))
        elif option==3:
            print(self.getFriendsOfFriends(g))
        else:
            print("Sorry, please enter valid Option")
        self.displayMenu(g)

    def showMenu(self):
        print("Main Menu:")
        print("--------------")
        print("1) Find person by name")
        print("2) Find a person's friends")
        print("3) Find the friends of the person's friends")
        print("0) Quit")
        print("--------------")
        print("Enter your choice:")
        return raw_input();

    def getPerson(self, g):
        name = raw_input("Enter the first name of for the person to find:");
        vertex = g.V().has("person", "first_name", name).next();
        return vertex

    def getFriends(self, g):
        name=raw_input("Enter the name of the person to get their friends: ");
        friends = g.V().has("person", "first_name", name).\
            both("friends").\
            dedup().\
            values("first_name").toList();
        return '\n'.join(friends);

    def getFriendsOfFriends(self, g):
        name = raw_input("Enter the name of the person to get their friends: ");
        friends = g.V().has("person", "first_name", name).repeat(__.both("friends") ).times(2).dedup().values("first_name").toList();
        return '\n'.join(friends);

def main():
    app = App()
    cluster = app.connectToDatabase()
    g = app.getGraphTraversalSource(cluster)
    app.displayMenu(g)

if __name__ == "__main__":
    main()