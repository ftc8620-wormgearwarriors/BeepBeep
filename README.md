# BeepBeep - FTC RoadRunner Simulator
## Introduciton
BeepBeep is a simulator for First Tech Challenge teams that are utilizing android studio and RoadRunner tool chains.  It is intended to aid in quick path development.  It is NOT a physics simulator,

## Motivation
In previous seasons we have developed roadrunner paths using other simulators.  All of these required  porting or modifying our code from the simulator into the robot.   We would end up with difference in these version that made it difficult to keep the simulation and the physical robot synchronized.  
The primary goal of BeepBeep is to modularize the trajectory code so that the exact same code is run on the simulator or the robot.  No longer do we need to maintain two versions of our paths!

## Acknowledgements
We want to thank all of the others that have worked to provide tools for FTC teams.  Specifically Road Runner.  Additionally we learned a lot from using and then reviewing Noah Bres’s MeepMeep.  In fact, the name BeepBeep was picked as a nod of respect to MeepMeep!

## Architecture
We strongly believe users of BeepBeep should modify it and make it fit their needs.  To encourage this we are sharing BeepBeep as source rather than a library.  Please have fun with it. If you make it do something cool let us know!    BeepBeep is installed into your project as a submodule.    The submodule contains
BeepBeepCore this is the low level simulator code that interfaces with your trajectories
BeepBeepWin this is a windows executable and our current preferred interface due to compile and run speeds
BeepBeepApp this is an android app that can be installed on other devices.  It is useful, but when developing trajectories this is run in the Android Studio Device simulator.  Compile, launch an run times were less that ideal on some of our older machines so we transitions to the Windows executable above.
In addition to the BeepBeep submodule you will need a folder in your project that holds the trajectories that will be run by the simulator and the physical robot code.  THis folder is accessible to the BeepBeep and TeamCode making it simple to develop paths in the simulator and then quickly deploy to the physical robot with zero code changes!

## Prerequiste
BeepBeep depends on the Action class in RoadRunner.  Please review the RR documentation for a better understanding of actions and VSequentialAction
https://rr.brott.dev/docs/v1-0/actions/
A full understanding of Roadrunner is encouraged, please read all of their documentation.  BeepBeep examples show how to mirror trajectories from the Red to Blue side of the field allowing you to only have to maintain trajectories for the one side of the field.  When you find yourself needing to tweak the numbers between what should be identical paths, it is most likely an error in your RR setup/tunning or an incorrect start position for how you are physically placing your robot on the field.  We find RR accurate enough to run the same code on both sides of the field!

## Installation:
