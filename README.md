# **BeepBeep \- FTC RoadRunner Simulator**

## Introduction

BeepBeep is a simulator for First Tech Challenge teams that are utilizing android studio and RoadRunner tool chains.  It is intended to aid in quick path development.  It is NOT a physics simulator,

## Motivation

In previous seasons we have developed roadrunner paths using other simulators.  All of these required  porting or modifying our code from the simulator into the robot.   We would end up with differences in these versions that made it difficult to keep the simulation and the physical robot synchronized.    
The primary goal of BeepBeep is to modularize the trajectory code so that the exact same code is run on the simulator or the robot.  No longer do we need to maintain two versions of our paths\!

## Acknowledgements

We want to thank all of the others that have worked to provide tools for FTC teams.  Specifically Road Runner.  Additionally we learned a lot from using and then reviewing Noah Bres’s MeepMeep.  In fact, the name BeepBeep was picked as a nod of respect to MeepMeep\!

## Architecture

We strongly believe users of BeepBeep should modify it and make it fit their needs.  To encourage this we are sharing BeepBeep as source project rather than a library.  Please have fun with it. If you make it do something cool let us know\!    BeepBeep is installed into your project as a submodule.

### BeepBeepCore

this is the low level simulator code that interfaces with your trajectories

### BeepBeepWin

this is a windows executable and our current preferred interface due to compile and run speeds

### BeepBeepApp

his is an android app that can be installed on other android devices.  It is useful, but when developing trajectories this is run in the Android Studio Device simulator.  Compile, launch and run times were less than ideal on some of our older machines so we transitioned to the Windows executable above.

In addition to the BeepBeep submodule you will need a module in your project that holds the trajectories that will be run by the simulator and the physical robot code.  This module is accessible to the BeepBeep and TeamCode making it simple to develop paths in the simulator and then quickly deploy to the physical robot with zero code changes\!

## Prerequisite

BeepBeep depends on the Action class in RoadRunner.  Please review the RR documentation for a better understanding of actions and VSequentialAction  
[https://rr.brott.dev/docs/v1-0/actions/](https://rr.brott.dev/docs/v1-0/actions/)   
A full understand of Roadrunner is encouraged, please read all of their documentation.  BeepBeep examples show how to mirror trajectories from the Red to Blue side of the field allowing you to only have to maintain trajectories for the one side of the field.  When you find yourself needing to tweak the numbers between what should be identical paths, it is most likely an error in your RR setup/tuning or an incorrect start position for how you are physically placing your robot on the field.  We find RR accurate enough to run the same code on both sides of the field\!


## Installation into project:

1) First you need a project that includes the FTC SDK and RoadRunner. See RR documentation at  [https://rr.brott.dev/docs/v1-0/installation/](https://rr.brott.dev/docs/v1-0/installation/) for details.
2) Make sure this project builds and RR is working
3) Fork BeepBeep into your own repo for easy modification and source control.
4) In your fork of BeepBeep go to \<\>code and copy the URL, use it in next step.
5) Add the BeepBeep Submodule Select the terminal tab in the bottom window and enter the commend:   
   `Git submodule add https://github.com/your-repo/BeepBeep.git`
6) Common version of RoadRunner  To ensure the same version of roadrunner is used by the simulator and the physical robot we need to define the version in a single location.  This is done at the top level of the project and creating a gradile file “build.RoardRunnerCommon.gradle” with the lines:  
   `repositories {`  
   `maven { url = 'https://maven.brott.dev/' }`  
   **`}`**  
   `dependencies {`  
   `implementation 'com.acmerobotics.roadrunner:core:1.0.0-beta3'`  
   `implementation 'com.acmerobotics.roadrunner:actions:1.0.0-beta3'`  
   **`}`**
7) Any module that will use roadrunner should include this line in it’s gradle file (instead of above lines)  
   `apply from: '../../build.RoardRunnerCommon.gradle'`
8) Add a module to store the trajectories.  We named this TranectoryActions.  This module should be at the same level FTCRobotController and TeamCode.
    1) use project view and add to top level.
    2) Create a java library module with the name TrajectoryActions
    3) Change Java Version to “VERSION\_1\_8”
    4) Add add this line to use our global roadrunner version  
       `apply from: '../../build.RoardRunnerCommon.gradle'`
9) Edit settings.gradle (Project Setting)
    1) Add these  lines

       `include ':BeepBeep:BeepBeepApp'`

       `include ':BeepBeep:BeepBeepCore'`

       `include ':BeepBeep:BeepBeepWin'`

       `include ':trajectoryActions'`

10) Synch project with gradle files to make sure project structure is updated.

### 
