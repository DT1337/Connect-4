# Connect 4

**Connect 4** is an implementation of the classic Connect 4 game with a JavaFX frontend for the client.  
This distributed system is built on a server-client architecture where the server manages the game board,  
and clients communicate with the server via sockets.

![image](https://github.com/DT1337/Connect-4/blob/960a153207bd4e27734a5d23ef687dd684dcfb60/Connect4Client/src/main/resources/de/hsw/images/preview.png)

***
### Start Playing Right Away

Download the following files and run them via:

```bash
java -jar <jar_name>
```

**Client**: [Connect4Client.jar](https://github.com/DT1337/Connect-4/blob/main/Executables/Connect4Client.jar)  
**Server**: [Connect4Server.jar](https://github.com/DT1337/Connect-4/blob/main/Executables/Connect4Server.jar)
***
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
- Apache Maven 3.9.4 or later
- optional: JavaFX Runtime

## Usage

Clone this repository:

```bash
git clone https://github.com/DT1337/Connect-4 && cd Connect-4
```

### Client

#### Running the Client with Maven

```bash
cd Connect4Client && mvn clean javafx:run
```

#### Running the Client JAR

1. Build the JAR using Maven:

```bash
cd Connect4Client && mvn clean package
```

2. Run the JAR file:

```bash
java -jar target/Connect4Client-1.0-SNAPSHOT.jar
```

### Server

#### Running the Server JAR

1. Build the JAR using Maven:

```bash
cd Connect4Server && mvn clean package
```

2. Run the JAR file:

```bash
java -jar target/Connect4Server-1.0-SNAPSHOT.jar
```

