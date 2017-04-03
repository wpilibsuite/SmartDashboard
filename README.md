# SmartDashboard Project

[![Build Status](https://travis-ci.org/wpilibsuite/SmartDashboard.svg?branch=master)](https://travis-ci.org/wpilibsuite/SmartDashboard)

Welcome to the WPILib project. This repository contains the SmartDashboard project.

- [WPILib Mission](#wpilib-mission)
- [Getting SmartDashboard](#getting-smartdashboard)
- [Building SmartDashboard](#building-smartdashboard)
- [Contributing to WPILib](#contributing-to-wpilib)

## WPILib Mission

The WPILib Mission is to enable FIRST teams to focus on writing game-specific software rather than on hardware details - "raise the floor, don't lower the ceiling". We try to enable teams with limited programming knowledge and/or mentor experience to do as much as possible, while not hampering the abilities of teams with more advanced programming capabilities. We support Kit of Parts control system components directly in the library. We also strive to keep parity between major features of each language (Java, C++, and NI's LabVIEW), so that teams aren't at a disadvantage for choosing a specific programming language. WPILib is an open-source project, licensed under the BSD 3-clause license. You can find a copy of the license [here](BSD_License_for_WPILib_code.txt).

# Getting SmartDashboard

The latest release build of SmartDashboard can be found on [WPI's server](http://first.wpi.edu/FRC/roborio/maven/release/edu/wpi/first/wpilib/SmartDashboard/).

## Requirements
- [JRE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

# Building SmartDashboard

Building SmartDashboard is very straightforward. SmartDashboard uses Gradle to compile.

## Requirements
- [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## Running

To run SmartDashboard navigate to the `smartdashboard` directory and use the command `./gradlew :run`.

## Building

To build Smart Dashboard navigate to the `smartdashboard` directory and use the command `./gradlew shadowjar`. The runnable jar is `build\libs\SmartDashboard-all.jar`.

# Contributing to WPILib

See [CONTRIBUTING.md](CONTRIBUTING.md).
