# Protelis demo application

This project demonstrates a minimal application making use of Protelis.

Protelis requires Java 8+.  This project has been set up to run in Eclipse with Maven, and should be able to be 
directly imported and executed if you have these set up.

## Contents:

* src/main/java:
 * HelloMain.java: entry point to run the demo on a simple simulated network
 * SimpleDevice.java: devices hosting a ProtelisVM and network interface
 * CachingNetworkManager.java: network interface for simulation, which simply records the 
 	most recent values sent to neighbors and received from neighbors.
 * IntegerUID.java: utility class for numerical device identifiers
* src/main/protelis:
 * hello.pt: Protelis program to be executed
* src/test/java:
 * HelloTest.java: runs HelloMain.java and compares output to HelloTest.txt
* src/test/resources:
 * HelloTest.txt: expected output from running HelloMain.java
* pom.xml: Maven configuration of the project
* README.md: this file

## To run:

To run normally, execute "HelloMain"

In response, you should see text output matching:
	src/test/resources/HelloTest.txt
	
There is also a JUnit test "HelloTest"
that runs "HelloMain" and performs this comparison automatically.
