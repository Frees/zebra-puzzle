## Kotlin Developer Test Task

Your task is to develop a Kotlin program to solve the Zebra puzzle
(https://en.wikipedia.org/wiki/Zebra_Puzzle) [zebra-puzzle.md](zebra-puzzle.md).

The program should read the puzzle conditions from an input file in JSON format and
output the solution(s) in JSON format.

The program needs to adhere to the classical rules of the Zebra puzzle. Still, it should
also be flexible enough to provide multiple solutions if the conditions are changed and
variations are allowed.

We appreciate if you write your program following clean code principles and the code is
covered with unit tests to ensure the program's functionality and reliability.
You may use any libraries, frameworks, or tools you are familiar with to accomplish this
task

## Running the Application

To run the application with the sample input file and output to the console:
```bash
./gradlew run --args="sample-input.json"
```

## Solution Algorithm

The solution to the Zebra puzzle is implemented using a constraint satisfaction algorithm with the following key components:

1. **Constraint Propagation**: The algorithm applies constraints to reduce the possible values for each attribute in each house. This is done iteratively until no more reductions are possible.

2. **Backtracking**: When constraint propagation alone cannot solve the puzzle, the algorithm makes an assumption (assigns a specific attribute to a house) and continues solving. If this leads to a contradiction, it backtracks and tries a different assumption.

3. **Constraint Types**: The algorithm handles several types of constraints:
   - SameHouse: Two attributes must be in the same house
   - NextTo: Two attributes must be in adjacent houses
   - PositionIs: An attribute must be in a specific position
   - RightOf: One attribute must be in a house to the right of another attribute
