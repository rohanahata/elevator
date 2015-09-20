rm *.class
javac -cp .:junit-4.11.jar:hamcrest-core-1.3.jar:hamcrest-library-1.3.jar ElevatorControlSystem.java Simulator.java Elevator.java TestRunner.java Simulator.java
java -cp .:junit-4.11.jar:hamcrest-core-1.3.jar:hamcrest-library-1.3.jar TestRunner
java -cp .:junit-4.11.jar:hamcrest-core-1.3.jar:hamcrest-library-1.3.jar Simulator