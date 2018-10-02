#!/bin/bash

javac ./paper/pkg1_simulator/*.java
javac ./paper/pkg1_simulator/queue/*.java
javac ./paper/pkg1_simulator/regularApp/*.java
javac ./paper/pkg1_simulator/scalableMiddleware/*.java
javac ./paper/pkg1_simulator/XMPPMessage/*.java
javac ./scalableApp/*.java
java    paper.pkg1_simulator.Paper1_simulator
