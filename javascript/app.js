"use strict";
const readline = require('readline-sync');
const gremlin = require('gremlin');
const __ = gremlin.process.statics;
const DriverRemoteConnection = gremlin.driver.DriverRemoteConnection;
const traversal = gremlin.process.AnonymousTraversalSource.traversal;

function main() {
    const cluster = connectToDatabase();
    console.log("using cluster connection: " + cluster.toString());
    const g = getGraphTraversalSource(cluster);
    console.log("using GraphTraversalSource: " + g.toString());

    displayMenu(g);
}

async function displayMenu(g) {
    var option = -1;
    option = showMenu();
    switch (parseInt(option)) {
        case 0:
            console.log("Exiting DiningByFriends, Bye!");
            process.exit(0);
            break;
        case 1:
            // Find Person
            var name = await getPerson(g);
            console.log("Found person: v[" + name.value.id + "]");
            break;
        case 2:
            // Find Friends
            var friends = await getFriends(g);
            friends.forEach(element => {
                console.log(element);
            });
            break;
        case 3:
            // Find Friends of Friends
            var friends = await getFriendsOfFriends(g);
            friends.forEach(element => {
                console.log(element);
            });
            break;
        default:
            console.log("Sorry, please enter valid Option");
            break;
    }
    displayMenu(g);
}

function showMenu() {
    var option = -1;
    console.log();
    console.log("Main Menu:");
    console.log("--------------");
    console.log("1) Find person by name");
    console.log("2) Find a person's friends");
    console.log("3) Find the friends of the person's friends");
    console.log("0) Quit");
    console.log("--------------");
    option = readline.question("Enter your choice:");
    return option;
}

async function getPerson(g) {
    var name = readline.question("Enter the first name of for the person to find:");
    return await g.V().
        has("person", "first_name", name).
        next();
}

async function getFriends(g) {
    var name = readline.question("Enter the name of the person to get their friends: ");

    return await g.V()
        .has("person", "first_name", name)
        .both("friends")
        .dedup()
        .values("first_name")
        .toList();
}

async function getFriendsOfFriends(g) {
    var name = readline.question("Enter the name of the person to get their friends: ");

    return await g.V().has("person", "first_name", name).
        repeat(
            __.both("friends")
        ).times(2).dedup().
        values("first_name").
        toList();
}

function getGraphTraversalSource(cluster) {
    return traversal().withRemote(cluster);
}

function connectToDatabase() {
    return new DriverRemoteConnection('ws://db:8182/gremlin');
}

main();