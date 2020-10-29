package androidPermissions;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class AnalyzeAndroidPermissions {
    static boolean asc = true;
    static boolean desc = false;
    static HashSet<String> normal = new HashSet<>();
    static HashSet<String> dangerous = new HashSet<>();
    static HashSet<String> signature = new HashSet<>();
    static File[] spyFiles = new File("src/res/spyware").listFiles();
    static File[] popFiles = new File("src/res/popular").listFiles();
    static File normFile = new File("src/res/normalPermissions.txt");
    static File dangFile = new File("src/res/dangerousPermissions.txt");
    static File sigFile = new File("src/res/signaturePermissions.txt");
    static File popData = new File("src/res/popularPermissions.txt");
    static File spyData = new File("src/res/spywarePermissions.txt");
    static Map<String, List<String>> spyMap = new HashMap<String, List<String>>();
    static Map<String, List<String>> popMap = new HashMap<String, List<String>>();
    static File popCount = new File("src/res/popPermissionsCount.txt");
    static File spyCount = new File("src/res/spywarePermissionsCount.txt");

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        try {
            getCategories(normFile, normal);
            getCategories(dangFile, dangerous);
            getCategories(sigFile, signature);
            getPermissions(spyFiles, spyData);
            getPermissions(popFiles, popData);
            countPermissions(spyData, spyMap, spyCount);
            countPermissions(popData, popMap, popCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Reads categorized permissions (normal, dangerous, or signature) and adds to set for use in program
    static void getCategories(File permissions, HashSet set) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(permissions));
        String line;

        while ((line = br.readLine()) != null) {
            set.add(line);
        }
    }
    // Reads all permissions from set of apps (spyware or popular) and writes to file
    static void getPermissions(File[] files, File txt) throws Exception {
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
    static void countPermissions(File txt, Map<String, List<String>> map, File countTxt) throws Exception {
        String line;
        Boolean isNormal = false;
        Boolean isDangerous = false;
        Boolean isSignature = false;
        BufferedReader br = new BufferedReader(new FileReader(txt));
        BufferedWriter bw = new BufferedWriter(new FileWriter(countTxt));

        while ((line = br.readLine()) != null) {
            if (map.containsKey(line)) {
                List<String> countAndType = map.get(line);
                int count = Integer.valueOf(countAndType.get(0)) + 1;
                countAndType.set(0, String.valueOf(count));
                map.put(line, countAndType);
            } else {
                List<String> countAndType = new ArrayList<String>();
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
                    countAndType.add("1");
                    countAndType.add("unknown");
                }
                map.put(line, countAndType);
                isNormal = false;
                isDangerous = false;
                isSignature = false;
            }
        }
        br.close();

        Map<String, List<String>> sortedSpyMap = sortByComparator(map, asc);

        for (Entry entry : sortedSpyMap.entrySet()) {
            bw.write(entry.getValue() + "\t" + entry.getKey() + "\n");
        }
        bw.close();
    }

    static Map<String, List<String>> sortByComparator(Map<String, List<String>> unsortMap, final boolean order) {
        List<Map.Entry<String, List<String>>> list =
                new LinkedList<Map.Entry<String, List<String>>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, List<String>>>() {
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                if (order) {
                    return o1.getValue().get(1).compareTo(o2.getValue().get(1));
                } else {
                    return o2.getValue().get(1).compareTo(o1.getValue().get(1));
                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<String, List<String>> sortedMap = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
