# ScaFi Web Blocks README

## Overview

ScaFi Web Blocks is an online playground designed to facilitate agile experimentation with ScaFi using block programming. ScaFi itself is a framework that enables the writing of aggregated code in an intuitive and agile manner. This platform aims to lower the learning curve for mastering this programming paradigm by leveraging block programming, particularly Blockly.

The project consists of two main parts:
- A web page, which includes ScaFi Web and ScaFi Blocks for more detailed information.
- A remote compilation service that translates ScaFi code into executable JavaScript.

## Getting Started

### Launching the Project

There are two ways to launch the project: by compiling and running it locally or by using Docker. We recommend the latter for ease of setup.

#### Using Docker

To run the project using Docker, execute the following command:

```
docker run -d -p 8080:8080 gianlucaaguzzi:scafi-blocks:latest
```

#### Using SBT

If you prefer to compile the project locally, ensure you have the following requirements:

- Node 16
- JDK 8

Then, execute the following command in your terminal:

```
sbt runService
```

This command compiles the system, producing the web page and the compilation server.

In both cases, you should be able to access the project's main page.

### Playing with the Tools

The web page is organized as follows:

- On the left, you have the area where you can write your code.
- On the right, you can control the simulation.

#### Blockly Functionality

You can write your program using drag and drop in this section. Blockly blocks are categorized for ease of use, and you can drag and drop them into the workspace. After writing your code, you can view its textual version.

#### ScaFi Web Functionality

Once your application is ready, you can load your program by clicking "load." This sends the script to the server, compiles it, and prepares it for execution by the simulator.

With the simulator, you can change and move nodes. Each node also has sensors and actuators that can be turned on and off by selecting the nodes and clicking on "sensor" to change their states.

### Examples

#### Example 1: Gradient

This example demonstrates an aggregate calculation to determine the distance from a source node (with a value set to true) to all other nodes. ScaFi Web Blocks can also change the color of nodes based on a program's output, such as using HSL to color gradients.

#### Example 2: Channel

The channel is a pattern in AC that connects a source area and a destination. This program can be created using ScaFi Web Blocks.

#### Example 3: Basic Movement

Nodes can also be moved using a specific block called "X".

Feel free to experiment with combining blocks and observing the results in the current screen.

