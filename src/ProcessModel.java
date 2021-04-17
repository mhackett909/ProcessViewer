import java.io.IOException;
import java.util.*;

/**
 * A model that holds data for the process viewer.
 */
public class ProcessModel {
    private HashMap<Integer, ProcessInfo> processInfoHashMap;
    private ArrayList<Integer> compareIDs, oldEntries;

    /**
     * Creates a new model that holds data for the viewer.
     * Contains a map of all running processes and pertinent information.
     */
    public ProcessModel() {
        processInfoHashMap = new HashMap<>();
        compareIDs = new ArrayList<>();
        oldEntries = new ArrayList<>();
    }

    //Public Methods
    /**
     * Retrieves a list of all running processes using a ProcessBuilder.
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void refreshProcesses() throws InterruptedException, IOException {
        compareIDs.clear();
        //Credit to Stepan Yakovenko, stackoverflow, for Windows process retrieval code
        Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
        Scanner sc = new Scanner(process.getInputStream());
        while (sc.hasNextLine()) { processLine(sc.nextLine()); }
        process.waitFor();
        deleteOldProcesses();
    }

    /**
     * Splits up the raw data received from the Scanner running
     * on the ProcessBuilder. Saves the process id, name, and memory
     * usage (kilobytes).
     *
     * @param line The line to be processed
     */
    public void processLine(String line) {
        String[] parts = line.split("\",\"");
        String processName = parts[0].substring(1);
        String numBytes = parts[4].substring(0,parts[4].length()-1);
        int processID = Integer.parseInt(parts[1]);
        if (processInfoHashMap.containsKey(processID))
            processInfoHashMap.get(processID).setProcessBytes(numBytes);
        else processInfoHashMap.put(processID,
                new ProcessInfo(processName, numBytes));
        compareIDs.add(processID);
    }

    /**
     * Kills the selected process using a Windows system runtime call.
     *
     * @param processID The ID of the process to be killed.
     */
    public void killProcess(int processID) {
        try {
            Runtime.getRuntime().exec("taskkill /F /PID "+processID);
        }catch (IOException e) { e.printStackTrace(); }
    }

    //Private Methods
    /**
     * Deletes processes from the HashMap if they are no longer running.
     * Avoids concurrent modification.
     */
    private void deleteOldProcesses() {
        oldEntries.clear();
        for (int id : processInfoHashMap.keySet()) {
            if (!compareIDs.contains(id)) oldEntries.add(id);
        }
        for (int id : oldEntries) processInfoHashMap.remove(id);
    }

    /**
     * Returns an integer array containing the HashMap's keyset.
     */
    public int[] getKeys() {
        int[] keys = new int[processInfoHashMap.size()];
        int index = 0;
        for (int i : processInfoHashMap.keySet()) keys[index++] = i;
        return keys;
    }

    /**
     * Retrieves the number of running processes.
     *
     * @return The size of the HashMap
     */
    public int getSize() { return processInfoHashMap.size(); }

    /**
     * Returns information about the process.
     *
     * @param id The process ID
     * @return A ProcessInfo object
     */
    public ProcessInfo getProcessInfo(int id) { return processInfoHashMap.get(id);  }

   /*
    public static void main(String[] args) {
        //Phase 1 test code
        ProcessModel model = new ProcessModel();
        try {
            model.refreshProcesses();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        int[] keys = model.getKeys();
        System.out.print("Number of processes: "+model.getSize());
        for (int i = 0; i < model.getSize(); i++) {
            if (i % 10 == 0) System.out.println();
            else System.out.print("| ");
            System.out.print(keys[i]+" ");
            System.out.print(model.getProcessInfo(keys[i]).getProcessName()+" ");
        }
    }
    */
}

/**
 * Contains the name and memory usage of each process.
 */
class ProcessInfo {
    private String processName;
    private String processBytes;

    ProcessInfo(String processName, String processBytes) {
        this.processName = processName;
        this.processBytes = processBytes;
    }
    public String getProcessName() { return processName; }
    public String getProcessBytes() { return processBytes; }
    public void setProcessBytes(String processBytes) {
        this.processBytes = processBytes;
    }
}