package androidPermissions;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class AnalyzeAndroidPermissions {
    static boolean asc = true;
    static HashSet<String> normal = new HashSet<>();
    static HashSet<String> dangerous = new HashSet<>();
    static HashSet<String> signature = new HashSet<>();
    static HashSet<String> removed = new HashSet<>();
    static HashSet<String> other = new HashSet<>();
    static File[] spyFiles = new File("src/res/spyware").listFiles();
    static File[] popFiles = new File("src/res/popular").listFiles();
    static File permFile = new File("src/res/permissions/allPermissions.txt");
    static File popData = new File("src/res/permissions/popularPermissions.txt");
    static File spyData = new File("src/res/permissions/spywarePermissions.txt");
    static Map<String, List<String>> spyMap = new HashMap<>();
    static Map<String, List<String>> popMap = new HashMap<>();
    static File popCount = new File("src/res/permissions/popPermissionsCount.txt");
    static File spyCount = new File("src/res/permissions/spywarePermissionsCount.txt");

    public static void main(String[] args) {
        try {
            getCategories();
            getPermissions(spyFiles, spyData);
            getPermissions(popFiles, popData);
            countPermissions(spyData, spyMap, spyCount);
            countPermissions(popData, popMap, popCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Reads categorized permissions (normal, dangerous, signature, or removed) and adds to set for use in program
    static void getCategories() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(permFile));
        String line;

        if ((br.readLine()).equals("Dangerous")) {
            while (!(line = br.readLine()).equals("")) {
                dangerous.add(line);
            }
        }
        if ((br.readLine()).equals("Normal")) {
            while (!(line = br.readLine()).equals("")) {
                normal.add(line);
            }
        }
        if ((br.readLine()).equals("Signature")) {
            while (!(line = br.readLine()).equals("")) {
                signature.add(line);
            }
        }
        if ((br.readLine()).equals("Removed")) {
            while (!(line = br.readLine()).equals("")) {
                removed.add(line);
            }
        }
        if ((br.readLine()).equals("Other")) {
            while ((line = br.readLine()) != null) {
                other.add(line);
            }
        }
        br.close();
    }
    // Reads all permissions from set of apps (spyware or popular) and writes to file
    static void getPermissions(File[] files, File txt) throws IOException {
        String line;
        String prefix = "<uses-permission android:name=";
        BufferedWriter bw = new BufferedWriter(new FileWriter(txt));

        for (File file : files) {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                if (line.contains(prefix)) {
                    String[] result = line.split("\"");
                    bw.write(result[1] + "\n");
//                    System.out.println(file + ": " + result[1]);
                }
            }
            br.close();
        }
        bw.close();
    }
    // Counts number of times a permission is listed in file and sorts by category
    static void countPermissions(File txt, Map<String, List<String>> map, File countTxt) throws IOException {
        String line;
        boolean isNormal = false;
        boolean isDangerous = false;
        boolean isSignature = false;
        boolean isRemoved = false;
        boolean isOther = false;
        BufferedReader br = new BufferedReader(new FileReader(txt));
        BufferedWriter bw = new BufferedWriter(new FileWriter(countTxt));

        while ((line = br.readLine()) != null) {
            if (map.containsKey(line)) {
                List<String> countAndType = map.get(line);
                int count = Integer.parseInt(countAndType.get(0)) + 1;
                countAndType.set(0, String.valueOf(count));
                map.put(line, countAndType);
            } else {
                List<String> countAndType = new ArrayList<>();
                if (normal.contains(line)) {
                    countAndType.add("1");
                    countAndType.add("normal");
                    isNormal = true;
                }
                if (!isNormal) {
                    if (dangerous.contains(line)) {
                        countAndType.add("1");
                        countAndType.add("dangerous");
                        isDangerous = true;
                    }
                }
                if (!isNormal && !isDangerous) {
                    if (signature.contains(line)) {
                        countAndType.add("1");
                        countAndType.add("signature");
                        isSignature = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature) {
                    if (removed.contains(line)) {
                        countAndType.add("1");
                        countAndType.add("removed");
                        isRemoved = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature && !isRemoved) {
                    if (other.contains(line)) {
                        countAndType.add("1");
                        countAndType.add("other");
                        isOther = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature && !isRemoved && !isOther) {
                    countAndType.add("1");
                    countAndType.add("unknown");
                }
                map.put(line, countAndType);
                isNormal = false;
                isDangerous = false;
                isSignature = false;
                isRemoved = false;
                isOther = false;
            }
        }
        br.close();

        Map<String, List<String>> sortedSpyMap = sortByComparator(map, asc);

        for (Entry<String, List<String>> entry : sortedSpyMap.entrySet()) {

            bw.write(entry.getValue() + " " + entry.getKey() + "\n");
        }
        bw.close();
    }

    static Map<String, List<String>> sortByComparator(Map<String, List<String>> unsortMap, final boolean order) {
        List<Map.Entry<String, List<String>>> list = new LinkedList<>(unsortMap.entrySet());
        // Sorting the list based on values
        list.sort((o1, o2) -> {
            if (order) {
                return o1.getValue().get(1).compareTo(o2.getValue().get(1));
            } else {
                return o2.getValue().get(1).compareTo(o1.getValue().get(1));
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<String, List<String>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
