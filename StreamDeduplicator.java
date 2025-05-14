import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class StreamDeduplicator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar StreamDeduplicator.jar <input_file_path>");
            return;
        }

        try {
            String inputJson = new String(Files.readAllBytes(Paths.get(args[0])));
            String outputJson = processStreams(inputJson);
            
            // Write output to a file
            Files.write(Paths.get("output.json"), outputJson.getBytes());
            System.out.println("Processing complete! Output written to output.json");
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

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