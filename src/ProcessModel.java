import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
//TODO phase 1 test shows sorted processes, calls kill task, prints processes again
public class ProcessModel {
    private HashMap<Integer, ProcessInfo> processInfoHashMap;
    private ArrayList<Integer> compareIDs, oldEntries;
    private int sortType;
    public ProcessModel() {
        processInfoHashMap = new HashMap<>();
        compareIDs = new ArrayList<>();
        oldEntries = new ArrayList<>();
        sortType = 0;
        try {
            refreshProcesses();
        }catch (InterruptedException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void refreshProcesses() throws InterruptedException, IOException {
        //Credit to Stepan Yakovenko, stackoverflow, for Windows process retrieval code
        compareIDs.clear();
        Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
        Scanner sc = new Scanner(process.getInputStream());
        if (sc.hasNextLine()) sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\",\"");
            String processName = parts[0].substring(1);
            String numBytes = parts[4].substring(0,parts[4].length()-1);
            int processID = Integer.parseInt(parts[1]);
            if (processInfoHashMap.containsKey(processID))
                processInfoHashMap.get(processID).processBytes = numBytes;
            else processInfoHashMap.put(processID,new ProcessInfo(processName, numBytes));
            compareIDs.add(processID);
        }
        process.waitFor();
        //End credit
        deleteOldProcesses();
        sortProcesses(sortType);
    }

    public void killProcess(int processID) {
        try {
            Runtime.getRuntime().exec("taskkill /F /PID "+processID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortProcesses(int sortType) {
        switch (sortType) {
            case 0:
                sortByInt();
                break;
            case 1:
                sortByName();
                break;
            default:
                sortByBytes();
        }
    }
    private void sortByInt() {

    }
    private void sortByName() {

    }
    private void sortByBytes() {

    }
    public ProcessInfo getProcessInfo(int id) { return processInfoHashMap.get(id);  }
    public int[] getKeys() {
        int[] keys = new int[processInfoHashMap.size()];
        int index = 0;
        for (int i : processInfoHashMap.keySet())
            keys[index++] = i;
        return keys;
    }
    public int getSize() { return processInfoHashMap.size(); }
    public void setSortType(int sortType) { this.sortType = sortType; }

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