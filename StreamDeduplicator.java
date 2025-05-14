import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public class StreamDeduplicator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar StreamDeduplicator.jar <file_or_directory_path>");
            return;
        }

        String inputPath = args[0];
        Path path = Paths.get(inputPath);
        
        if (!Files.exists(path)) {
            System.err.println("Error: The specified path does not exist.");
            return;
        }
        
        // Create output directory if it doesn't exist
        Path outputDirectory = Paths.get("output");
        try {
            if (!Files.exists(outputDirectory)) {
                Files.createDirectory(outputDirectory);
            }
        } catch (IOException e) {
            System.err.println("Error creating output directory: " + e.getMessage());
            return;
        }
        
        try {
            List<Path> filesToProcess = new ArrayList<>();
            
            // Check if the input is a directory or a single file
            if (Files.isDirectory(path)) {
                // Process all JSON files in the directory
                System.out.println("Processing directory: " + inputPath);
                
                try (Stream<Path> paths = Files.walk(path, 1)) {
                    filesToProcess = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".json"))
                        .collect(Collectors.toList());
                }
                
                if (filesToProcess.isEmpty()) {
                    System.out.println("No JSON files found in the specified directory.");
                    return;
                }
                
                System.out.println("Found " + filesToProcess.size() + " JSON file(s).");
            } else if (Files.isRegularFile(path)) {
                // Process single file
                if (!path.toString().toLowerCase().endsWith(".json")) {
                    System.err.println("Error: The specified file is not a JSON file.");
                    return;
                }
                filesToProcess.add(path);
                System.out.println("Processing single file: " + path.getFileName());
            } else {
                System.err.println("Error: The path is neither a file nor a directory.");
                return;
            }
            
            // Process each file
            for (Path jsonFile : filesToProcess) {
                String fileName = jsonFile.getFileName().toString();
                System.out.println("Processing file: " + fileName);
                
                try {
                    String inputJson = new String(Files.readAllBytes(jsonFile));
                    String outputJson = Deduplicator.processStreams(inputJson);
                    
                    // Create output file name based on the input file name
                    String outputFileName = getOutputFileName(fileName);
                    Path outputPath = outputDirectory.resolve(outputFileName);
                    
                    // Write output to file
                    Files.write(outputPath, outputJson.getBytes());
                    System.out.println("  Processed: " + fileName + " -> " + outputPath);
                } catch (Exception e) {
                    System.err.println("  Error processing file " + fileName + ": " + e.getMessage());
                }
            }
            
            System.out.println("All files processed. Output files saved to the 'output' directory.");
        } catch (IOException e) {
            System.err.println("Error reading path: " + e.getMessage());
        }
    }
    
    /**
     * Generates an appropriate output file name based on the input file name
     */
    private static String getOutputFileName(String inputFileName) {
        // Remove .json extension if present
        String baseName = inputFileName.toLowerCase().endsWith(".json") 
            ? inputFileName.substring(0, inputFileName.length() - 5) 
            : inputFileName;
            
        // Return with _output suffix and json extension
        return baseName + "_output.json";
    }
}

/**
 * StreamDeduplicator class with the core deduplication logic
 */
class Deduplicator {
    public static String processStreams(String inputJson) {
        JSONArray sections = new JSONArray(inputJson);
        
        // Step 1: Extract section data and keep track of top 3 streams seen so far
        Set<String> top3Streams = new HashSet<>();
        List<Map<String, Object>> processedSections = new ArrayList<>();
        
        // First pass: Identify duplication in top 3 positions
        for (int i = 0; i < sections.length(); i++) {
            JSONObject section = sections.getJSONObject(i);
            String sectionId = section.optString("sectionID", "");
            JSONArray sectionData = section.getJSONArray("sectionData");
            
            // Extract streamer IDs for this section
            List<String> streamerIds = new ArrayList<>();
            for (int j = 0; j < sectionData.length(); j++) {
                JSONObject streamer = sectionData.getJSONObject(j);
                streamerIds.add(streamer.getString("streamerID"));
            }
            
            // Store section info for processing
            Map<String, Object> sectionInfo = new HashMap<>();
            sectionInfo.put("sectionID", sectionId);
            sectionInfo.put("streamerIds", streamerIds);
            processedSections.add(sectionInfo);
        }
        
        // Second pass: Fix duplications
        for (Map<String, Object> section : processedSections) {
            @SuppressWarnings("unchecked")
            List<String> streamerIds = (List<String>) section.get("streamerIds");
            
            // Process the top 3 positions (or fewer if the section has fewer streams)
            int topLimit = Math.min(3, streamerIds.size());
            
            // Keep track of streams that need to be swapped
            List<Integer> duplicatesInTop3 = new ArrayList<>();
            for (int i = 0; i < topLimit; i++) {
                String streamerId = streamerIds.get(i);
                if (top3Streams.contains(streamerId)) {
                    duplicatesInTop3.add(i);
                } else {
                    top3Streams.add(streamerId);
                }
            }
            
            // Find replacement streams from positions beyond top 3
            if (!duplicatesInTop3.isEmpty() && streamerIds.size() > 3) {
                int replacementIndex = 3; // Start looking from the 4th position
                
                for (int dupIndex : duplicatesInTop3) {
                    // Find a stream not in the top3 set to swap with
                    while (replacementIndex < streamerIds.size()) {
                        String replacementStream = streamerIds.get(replacementIndex);
                        if (!top3Streams.contains(replacementStream)) {
                            // Swap positions
                            String duplicate = streamerIds.get(dupIndex);
                            streamerIds.set(dupIndex, replacementStream);
                            streamerIds.set(replacementIndex, duplicate);
                            
                            // Add the new stream to top3 set
                            top3Streams.add(replacementStream);
                            replacementIndex++;
                            break;
                        }
                        replacementIndex++;
                    }
                }
            }
        }
        
        // Create output JSON
        JSONObject output = new JSONObject();
        for (Map<String, Object> section : processedSections) {
            String sectionId = (String) section.get("sectionID");
            @SuppressWarnings("unchecked")
            List<String> streamerIds = (List<String>) section.get("streamerIds");
            output.put(sectionId, new JSONArray(streamerIds));
        }
        
        return output.toString(2);
    }
}