import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessController {
    ProcessModel processModel;
    ProcessView processView;
    DefaultTableModel tableModel;

    ProcessController() {
        processModel = new ProcessModel();
        processView = new ProcessView("Windows Process Viewer");
        processView.getButton().addActionListener(e -> {
            int row = processView.getTable().getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                processModel.killProcess(id);
            }
        });
        //Timer for the following, every X seconds
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() { refreshModelAndView(); }
        };
        timer.schedule(task,0,1000);
    }
    
    private void refreshModelAndView() {
        try {
            processModel.refreshProcesses();
        }catch (InterruptedException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        processView.clearTable();
        int processSize = processModel.getSize();
        int tableSize = processView.getTable().getRowCount();
        tableModel =  (DefaultTableModel) processView.getTable().getModel();
        while (processSize > tableSize) {
            tableModel.addRow(new Object[]{"", "", ""});
            tableSize++;
        }
        while (tableSize > processSize) {
            tableModel.removeRow(--tableSize);
        }
        int[] keys = processModel.getKeys();
        Arrays.sort(keys);
        for (int i = 0; i < processSize; i++) {
            tableModel.setValueAt(keys[i], i, 0);
            ProcessInfo info = processModel.getProcessInfo(keys[i]);
            tableModel.setValueAt(info.processName, i, 1);
            tableModel.setValueAt(info.processBytes, i, 2);
        }
        processView.selectLastIndex();
    }
}
