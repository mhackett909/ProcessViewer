import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A controller class for the Windows Process Viewer.
 */
public class ProcessController {
    ProcessModel processModel;
    ProcessView processView;
    DefaultTableModel tableModel;

    /**
     * Creates a new controller for the process viewer. Using a TimerTask,
     * it constantly retrieves data from the model and uses it to update
     * the view. It also handles input from the user's button clicks.
     */
    ProcessController() {
        processModel = new ProcessModel();
        processView = new ProcessView("Windows Process Viewer");
        tableModel =  (DefaultTableModel) processView.getTable().getModel();
        //Action Listener
        processView.getButton().addActionListener(e -> {
            int row = processView.getTable().getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                processModel.killProcess(id);
            }
        });
        //TimerTask
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() { refreshModelAndView(); }
        };
        timer.schedule(task,0,1000);
    }

    /**
     * Retrieves data from the model and updates the view.
     */
    private void refreshModelAndView() {
        try {
            processModel.refreshProcesses();
        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        processView.clearTable();
        adjustNumRows();
        int[] keys = processModel.getKeys();
        Arrays.sort(keys);
        updateView(keys);
        processView.selectLastID();
    }

    /**
     * Ensures that there are enough rows for all running processes. Removes
     * unused rows.
     */
    private void adjustNumRows() {
        int processSize = processModel.getSize();
        int tableSize = processView.getTable().getRowCount();
        while (processSize > tableSize) {
            tableModel.addRow(new Object[]{"", "", ""});
            tableSize++;
        }
        while (tableSize > processSize) {
            tableModel.removeRow(--tableSize);
        }
    }

    /**
     * Repopulates the view using data from the model.
     *
     * @param keys The process IDs
     */
    private void updateView(int[] keys) {
        for (int i = 0; i < processModel.getSize(); i++) {
            tableModel.setValueAt(keys[i], i, 0);
            ProcessInfo info = processModel.getProcessInfo(keys[i]);
            tableModel.setValueAt(info.getProcessName(), i, 1);
            tableModel.setValueAt(info.getProcessBytes(), i, 2);
        }
    }
}
