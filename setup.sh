#!/bin/bash

# Check if StreamDeduplicator.java exists
if [ ! -f "StreamDeduplicator.java" ]; then
    echo "Error: StreamDeduplicator.java not found in the current directory."
    exit 1
fi

echo "Setting up project structure..."

# Create input.json file with the example data from the assignment
cat > input.json << 'EOL'
[
  {
    "sectionData": [
      {"streamerID": "db5309b4-053b-4013-9309-5e0e6a7d0a2a"},
      {"streamerID": "75d95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "75d95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "9b345f78-1973-498d-a06e-935d5c83aae5"},
      {"streamerID": "691f160b-131e-4572-b727-e7d88993428f"},
      {"streamerID": "918d3470-23aa-414e-b81d-28bee67c0c27"},
      {"streamerID": "1a946d91-4855-4006-ba39-f8cf905a9ae2"},
      {"streamerID": "75abdb3f-a74f-49ae-8ac3-ecb632c0622d"},
      {"streamerID": "63727510-7d63-4d54-6391-4b86c697819a"},
      {"streamerID": "938af20d-cf69-4756-a9fc-d25bb6f673c8"}
    ],
    "lokalisedKey": "streamers_daily_life_updates",
    "sectionID": "Streamers' Daily Life Updates"
  },
  {
    "mlDynamicLabel": true,
    "labelID": "",
    "sectionData": [
      {"streamerID": "4c2b41d1-f4ce-438e-a0f5-317b7cbf5c3a"},
      {"streamerID": "a352f686-ee93-430a-9e27-9fce686138f5"},
      {"streamerID": "75d95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "9b345f78-1973-498d-a06e-935d5c83aae5"},
      {"streamerID": "75d95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "691f160b-131e-4572-b727-e7d88993428f"},
      {"streamerID": "d76d85cd-6fae-42f7-61e1-b2dffa3564a4"},
      {"streamerID": "04244baf-30b7-4287-9d08-536781f0d345"},
      {"streamerID": "938af20d-cf69-4756-a9fc-d25bb6f673c8"},
      {"streamerID": "e6a3d4d9-b7c4-461b-8521-8ee975245855"}
    ],
    "sectionID": "Social Media Trends & Influencers"
  },
  {
    "mlDynamicLabel": true,
    "sectionData": [
      {"streamerID": "db53e9b4-053b-4013-9309-5e0e6a7d0a2a"},
      {"streamerID": "75d95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "75e95031-72e7-4bee-be92-75494e1530ff"},
      {"streamerID": "9b3465f78-1973-498d-a06e-935d5c83aae5"},
      {"streamerID": "691f162b-131e-4572-b727-e7d88993428f"},
      {"streamerID": "918d3470-23aa-414e-b81d-28bee67c0c27"},
      {"streamerID": "1a946d91-4855-4006-ba39-f8cf905a9ae2"},
      {"streamerID": "75abd13f-a74f-49ae-8ac3-ecb632c0622d"},
      {"streamerID": "63727510-7d63-4d54-6391-4b86c697819a"},
      {"streamerID": "9387f20d-cf69-4756-a9fc-d25bb6f673c8"}
    ],
    "lokalisedKey": "music",
    "sectionID": "music"
  }
]
EOL

# Create test_cases directory if it doesn't exist
mkdir -p test_cases

# Create normal_case.json
cat > test_cases/normal_case.json << 'EOL'
[
    {
      "sectionData": [
        {"streamerID": "a"},
        {"streamerID": "b"},
        {"streamerID": "c"},
        {"streamerID": "d"},
        {"streamerID": "e"},
        {"streamerID": "f"},
        {"streamerID": "g"},
        {"streamerID": "h"},
        {"streamerID": "i"},
        {"streamerID": "j"}
      ],
      "lokalisedKey": "streamers_daily_life_updates",
      "sectionID": "Streamers' Daily Life Updates"
    },
    {
      "mlDynamicLabel": true,
      "labelID": "",
      "sectionData": [
        {"streamerID": "a"},
        {"streamerID": "b"},
        {"streamerID": "d"},
        {"streamerID": "e"},
        {"streamerID": "f"},
        {"streamerID": "g"},
        {"streamerID": "h"},
        {"streamerID": "i"},
        {"streamerID": "j"},
        {"streamerID": "k"}
      ],
      "sectionID": "Social Media Trends & Influencers"
    },
    {
      "mlDynamicLabel": true,
      "sectionData": [
        {"streamerID": "c"},
        {"streamerID": "d"},
        {"streamerID": "e"},
        {"streamerID": "f"},
        {"streamerID": "g"},
        {"streamerID": "h"},
        {"streamerID": "i"},
        {"streamerID": "j"},
        {"streamerID": "k"},
        {"streamerID": "l"}
      ],
      "lokalisedKey": "music",
      "sectionID": "music"
    }
]
EOL

# Create small_sections.json
cat > test_cases/small_sections.json << 'EOL'
[
    {
        "sectionData": [
            {"streamerID": "a"},
            {"streamerID": "b"}
        ],
        "sectionID": "0"
    },
    {
        "sectionData": [
            {"streamerID": "c"},
            {"streamerID": "a"}
        ],
        "sectionID": "1"
    },
    {
        "sectionData": [
            {"streamerID": "b"},
            {"streamerID": "c"}
        ],
        "sectionID": "2"
    }
]
EOL

# Create extensive_duplication.json
cat > test_cases/extensive_duplication.json << 'EOL'
[
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "d"},
      {"streamerID": "e"}
    ],
    "sectionID": "Section1"
  },
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "f"},
      {"streamerID": "g"}
    ],
    "sectionID": "Section2"
  },
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "h"},
      {"streamerID": "i"}
    ],
    "sectionID": "Section3"
  },
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "j"},
      {"streamerID": "k"}
    ],
    "sectionID": "Section4"
  }
]
EOL

# Create not_enough_unique.json
cat > test_cases/not_enough_unique.json << 'EOL'
[
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "d"}
    ],
    "sectionID": "Section1"
  },
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "e"}
    ],
    "sectionID": "Section2"
  },
  {
    "sectionData": [
      {"streamerID": "a"},
      {"streamerID": "b"},
      {"streamerID": "c"},
      {"streamerID": "f"}
    ],
    "sectionID": "Section3"
  }
]
EOL


# Create a simple pom.xml file for Maven
cat > pom.xml << 'EOL'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>17Live_Solution</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20231013</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>.</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>StreamDeduplicator</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
EOL

# Checking if Java and Maven are installed
echo "Checking prerequisites..."
if ! command -v javac &> /dev/null; then
    echo "Java compiler not found! Installing OpenJDK..."
    sudo apt update
    sudo apt install -y default-jdk
fi

if ! command -v mvn &> /dev/null; then
    echo "Maven not found! Installing Maven..."
    sudo apt update
    sudo apt install -y maven
fi

echo "Building the project..."
mvn clean package

echo "Setup complete!"
echo ""
echo "You can now run the example input json with:"
echo "java -jar target/17Live_Solution-1.0-SNAPSHOT-jar-with-dependencies.jar input.json"
echo ""
echo "You can now run the testcases with:"
echo "java -jar target/17Live_Solution-1.0-SNAPSHOT-jar-with-dependencies.jar test_cases"