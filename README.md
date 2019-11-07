# Protelis demo application

The goal of this project is project is to show some basic implementations of [Protelis](https://github.com/Protelis/Protelis)
using both Java and Kotlin.

Protelis requires Java 8+.

## Build status
[![Build Status](https://travis-ci.org/lippo97/Protelis-Demo.svg?branch=master)](https://travis-ci.org/lippo97/Protelis-Demo)

## How to implement Protelis

The core or Protelis is the [ProtelisVM](https://static.javadoc.io/org.protelis/protelis/12.0.0/org/protelis/vm/ProtelisVM.html).
It allows to execute a [ProtelisProgram](https://static.javadoc.io/org.protelis/protelis/12.0.0/org/protelis/vm/ProtelisProgram.html)
on a particular device.

A device is represented by an [ExecutionContext](https://static.javadoc.io/org.protelis/protelis/12.0.0/org/protelis/vm/ExecutionContext.html).
It tracks the current state of a device and many properties such as its UID, current time and position in the space. In order to make this demo I extended the existing
[AbstractExecutionContext](https://static.javadoc.io/org.protelis/protelis/12.0.0/org/protelis/vm/impl/AbstractExecutionContext.html)
class, since it already contains some basic functionalities, such as using a
[NetworkManager](https://static.javadoc.io/org.protelis/protelis/12.0.0/org/protelis/vm/NetworkManager.html) to share messages between nodes.

Given the Protelis code:

```protelis
module tutorial:factorial
/*
 * The language is functional, every expression has a return value. In case of multiple statements in a block, the value of the last expression is returned.
 * Comments are C-like, both single and multiline supported.
 * The following defines a new function. If the optional "public" keyword is present, the function will be accessible from outside the module
 */
public def factorial(n) { // Dynamic typing
  if (n <= 1) { 
    1 // No return keyword, no ";" at the end of the last line
  } else { // else is mandatory
    n * factorial(n - 1) // infix operators, recursion
  } 
}
// There is no main function, just write the program at the end (Python-like)
let num = 5; // mandatory ";" for multiline instructions
factorial(5) // Function call

```

An example is provided in the following snippet:


```java
// We extend the AbstractExecutionContext class
public class MyExecutionContext extends AbstractExecutionContext { ... }
public class MyNetworkManager implements NetworkManager { ... }
// Load from a file the protelis program.
ProtelisProgram program = ProtelisLoader.parse("factorial.pt");
// Create a new execution context
MyExecutionContext myExecutionContext = new MyExecutionContext(new MyNetworkManager());
// Create a new virtual machine
ProtelisVM vm = new ProtelisVM(program, myExecutionContext);
// Now we have a working virtual machine which can run the program.
vm.runCycle(); // 120
```

As Protelis is an aggregate programming language it doesn't make much sense that we execute a program in a single node.
We'd like to make a team of nodes running the same program and that's exactly the purpose of this project.

## Contents of the demo

This project includes the following implementations of Protelis:

- `01-java-helloworld`: Java implementation using an emulated network;
- `02-kotlin-helloworld`: Same as above but in Kotlin;
- `03-java-socket`: Java implementation using sockets to make nodes communicate.
- `04-kotlin-socket`: Same as above but in Kotlin.
- `05-java-mqtt`: Java implementation using MQTT protocol to make nodes communicate.
- `06-kotlin-mqtt`: Same as above but in Kotlin.

## Usage

To execute a demo run:

```bash
gradle <subproject>:run
```

## How to import

### IntelliJ IDEA (recommended option)

It is enough to open the project and double click on the main `build.gradle.kts`, everything should load up correctly.

### Eclipse

It is recommended to install the Protelis plugin from the marketplace, then restart Eclipse.

[TODO]: # (Import the project, I didn't manage to do it yet.)