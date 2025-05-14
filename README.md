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


## How to Execute in Linux Environment

1. **Install Required Software**:
   ```
   sudo apt update
   sudo apt install maven default-jdk
   ```

2. **Build and Run the Project**:
   ```
   cd 17Live_Solution
   ./setup.sh
   ```
3. **Run the example input json"**:
    ```
    java -jar target/17Live_Solution-1.0-SNAPSHOT-jar-with-dependencies.jar input.json
    ```
4. **Run the testcases"**:
    ```
    java -jar target/17Live_Solution-1.0-SNAPSHOT-jar-with-dependencies.jar test_cases
    ```
5. **Check the Results**:
   After running the application, check the `output` directory for the processed results.


# Test Cases and Edge Case Analysis

This section outlines the test cases developed for the 17Live stream deduplication solution and analyzes potential edge cases where the algorithm might face challenges.

## Test Cases Overview

We've designed four comprehensive test cases to validate our solution:

1. **Normal Case** - Tests the basic functionality using the sample input from the assignment
2. **Small Sections** - Tests handling of sections with fewer than 3 streams
3. **Extensive Duplication** - Tests behavior when many sections have the same top streams
4. **Not Enough Unique Streams** - Tests the failure case when there aren't enough unique streams

## Edge Case: Not Enough Unique Streams

The most significant edge case occurs when there aren't enough unique streams available to resolve all duplications. This happens when:

1. Multiple sections have identical top streams
2. There aren't enough streams beyond position 3 that can be used as replacements

**Example:** Three sections all have the same 3 streams in their top positions, with only one unique stream each beyond position 3.

**Current Algorithm Behavior:** 
- The algorithm will swap as many duplicates as possible
- Some duplications will inevitably remain in the top 3 positions
- The algorithm prioritizes earlier sections, so the first section will maintain its original top 3

**Impact on User Experience:**
Users will still see some duplicate streams in the top positions across different sections.

## Suggested Improvements

To handle this edge case better, we could implement:

1. **Intelligent Prioritization:**
   - Analyze which sections would benefit most from deduplication
   - Prioritize swaps that maximize unique streams across all sections

2. **Visual Distinction:**
   - When unavoidable duplicates exist, add visual markers to help users identify them
   - Use different layouts or highlight unique aspects of the duplicated streams

3. **Section Rotation:**
   - Periodically rotate which sections get priority for unique streams
   - Ensure that all sections get opportunities to showcase unique content

4. **User Preference Learning:**
   - Track which sections users interact with most
   - Prioritize those sections for unique stream allocation

## GitHub Link of Project: 

https://github.com/RishavAich511/17Live_Solution
