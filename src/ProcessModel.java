import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
//TODO phase 1 test shows sorted processes, calls kill task, prints processes again
public class ProcessModel {
    private HashMap<Integer, ProcessInfo> processInfoHashMap;
    private ArrayList<Integer> compareIDs, oldEntries;
    public ProcessModel() {
        processInfoHashMap = new HashMap<>();
        compareIDs = new ArrayList<>();
        oldEntries = new ArrayList<>();
        try {
            refreshProcesses();
        }catch (InterruptedException | IOException e) { e.printStackTrace(); }
    }

    public void refreshProcesses() throws InterruptedException, IOException {
        compareIDs.clear();
        //Credit to Stepan Yakovenko, stackoverflow, for Windows process retrieval code
        Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
        Scanner sc = new Scanner(process.getInputStream());
        while (sc.hasNextLine()) { processLine(sc.nextLine()); }
        process.waitFor();
        //End credit
        deleteOldProcesses();
    }
    public void processLine(String line) {
        String[] parts = line.split("\",\"");
        String processName = parts[0].substring(1);
        String numBytes = parts[4].substring(0,parts[4].length()-1);
        int processID = Integer.parseInt(parts[1]);
        if (processInfoHashMap.containsKey(processID))
            processInfoHashMap.get(processID).processBytes = numBytes;
        else processInfoHashMap.put(processID,new ProcessInfo(processName, numBytes));
        compareIDs.add(processID);
    }
    public void killProcess(int processID) {
        try {
            Runtime.getRuntime().exec("taskkill /F /PID "+processID);
        } catch (IOException e) { e.printStackTrace(); }
    }
    public ProcessInfo getProcessInfo(int id) { return processInfoHashMap.get(id);  }
    public int[] getKeys() {
        int[] keys = new int[processInfoHashMap.size()];
        int index = 0;
        for (int i : processInfoHashMap.keySet()) keys[index++] = i;
        return keys;
    }
    public int getSize() { return processInfoHashMap.size(); }

    private void deleteOldProcesses() {
        oldEntries.clear();
        for (int id : processInfoHashMap.keySet()) {
            if (!compareIDs.contains(id)) oldEntries.add(id);
        }
        for (int id : oldEntries) processInfoHashMap.remove(id);
    }
}
class ProcessInfo {
    String processName;
    String processBytes;
    ProcessInfo(String processName, String processBytes) {
        this.processName = processName;
        this.processBytes = processBytes;
    }
}