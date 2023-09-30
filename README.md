# Connect 4

**Connect 4 Distributed System** is an implementation of the classic Connect 4 game with a JavaFX frontend for the client. This distributed system is built on a server-client architecture where the server manages the game board, and clients communicate with the server via sockets.

![image](https://github.com/DT1337/Connect-4/assets/92855706/b68c984d-72f2-49fa-bb40-a125e422d9ee)

## Table of Contents

- [Prerequisites](#prerequisites)
- [Usage](#usage)
  - [Client](#client)
    - [Running the Client with Maven](#running-the-client-with-maven)
    - [Running the Client JAR](#running-the-client-jar)
  - [Server](#server)
    - [Running the Server JAR](#running-the-server-jar)

## Prerequisites

Before you can run the Connect 4 application, you need to have the following software installed on your machine:

- Java Development Kit (JDK) 17 or later

## Usage

Clone this repository:

```bash
git clone https://github.com/DT1337/Connect4
cd Connect4
```

### Client

#### Running the Client with Maven

```bash
cd Connect4Client
mvn clean javafx:run
```

#### Running the Client JAR

1. Build the JAR using Maven:

```bash
cd Connect4Client
mvn clean package
```

2. Run the JAR file:

```bash
java -jar target/Connect4Client-1.0-SNAPSHOT.jar
```

### Server

#### Running the Server JAR

1. Build the JAR using Maven:

```bash
cd Connect4Server
mvn clean package
```

2. Run the JAR file:

```bash
java -jar target/Connect4Server-1.0-SNAPSHOT.jar
```

