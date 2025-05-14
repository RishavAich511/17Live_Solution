# 17Live Stream Deduplication Solution

This document explains the algorithm and implementation of the Java solution for the 17Live stream deduplication problem.

## Algorithm Explanation

The algorithm follows these key steps:

1. **Parse the input JSON** to extract sections and their streamer IDs.

2. **Two-pass processing**:
   - First pass: Extract all sections and their streamer IDs
   - Second pass: Process each section to resolve duplications

3. **Deduplication Process**:
   - Keep a global set of streams that appear in the top 3 positions of any section
   - For each section, check if any of its top 3 streams already exist in this global set
   - If a duplicate is found, swap it with a stream from a position beyond the top 3 that hasn't appeared in any top 3 positions
   - Add the replacement stream to the global top 3 set

4. **Generate the output** in the required JSON format

## Time & Space Complexity

- **Time Complexity**: O(n), where n is the total number of streams across all sections
- **Space Complexity**: O(n) for storing stream IDs and section data

## Optimization Highlights

1. **Minimal Changes**: The algorithm only swaps positions when necessary and maintains the original order as much as possible

2. **No Section Reordering**: Preserves the original section order from the recommendation system

3. **Efficient Duplicate Detection**: Uses a HashSet for O(1) lookups when checking for duplicates

4. **Smart Swapping**: Swaps duplicate streams with streams that haven't appeared in any top 3 positions yet

## Potential Edge Cases & Limitations

1. **Not Enough Non-duplicate Streams**: If a section has duplicate streams in its top 3 but not enough unique streams below to swap with, the algorithm will handle it gracefully but may not be able to completely remove all duplicates.

2. **Complete Overlap**: In extreme cases where all sections have the same top streams, perfect deduplication isn't possible while maintaining the constraints.

## How to Execute in Ubuntu with VSCode

1. **Install Required Software**:
   ```
   sudo apt update
   sudo apt install maven default-jdk
   ```

2. **Create a Project Directory and Files**:
   - Run the `setup.sh` script provided to set up the project structure and files
   - Make the script executable: `chmod +x setup.sh`
   - Run the script: `./setup.sh`

3. **Open the Project in VSCode**:
   ```
   code 17Live_Solution
   ```

4. **Build and Run the Project**:
   ```
   cd 17Live_Solution
   mvn clean package
   java -jar target/17Live_Solution-1.0-SNAPSHOT-jar-with-dependencies.jar input.json
   ```

5. **Check the Results**:
   After running the application, check the `output.json` file for the processed results.