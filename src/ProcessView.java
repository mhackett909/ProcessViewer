import javax.swing.*;
import java.awt.*;

/**
 * A GUI for the process viewer.
 */
public class ProcessView extends JFrame {
    JTable table;
    JButton button;
    static int MIN_ROWS = 100, COL_NUM = 3;
    private int lastSelectedID = -1;

    /**
     * Creates a new GUI for the process viewer. Contains a label, table,
     * and button. Extends JFrame.
     *
     * @param title The title of the program
     */
    public ProcessView(String title) {
        //Frame layout
        super(title);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        //JLabel
        JLabel label = new JLabel(title);
        label.setFont(new Font("Verdana",Font.BOLD,20));
        label.setForeground(Color.BLUE);

        //JTable
        table = new JTable(MIN_ROWS,COL_NUM);
        table.setPreferredScrollableViewportSize(new Dimension(450,368));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        //Column width and titles
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(0).setHeaderValue("ID");
        table.getColumnModel().getColumn(1).setHeaderValue("Process Name");
        table.getColumnModel().getColumn(2).setHeaderValue("Memory Usage");

        //JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        //Button
        button = new JButton("Kill Process");
        button.setSize(50,25);

        //Add components to panel, and panel to frame
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(scrollPane);
        panel.add(button);
        add(panel);
        setVisible(true);
    }

    /**
     * Returns the kill process button.
     */
    public JButton getButton() { return button; }

    /**
     * Returns the view's JTable object. Can be used to access
     * the underlying DefaultTableModel.
     */
    public JTable getTable() { return table; }

    /**
     * Clears the table of all values and selections. Saves the currently
     * selected ID before deselecting.
     */
    public void clearTable() {
        int currentRow = table.getSelectedRow();
        //Must ensure the conversion is successful
        try {
            lastSelectedID = (currentRow == -1 ? -1 :
                    (int) table.getModel().getValueAt(currentRow, 0));
        }catch (Exception e) { lastSelectedID = -1; }
        table.clearSelection();
        //Clear rows
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < COL_NUM; j++)
                table.getModel().setValueAt("", i, j);
        }
    }

    /**
     * Searches the table for the last selected ID. Selects
     * the row if it is successful.
     */
    public void selectLastID() {
        if (lastSelectedID == -1) return;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((int) table.getModel().getValueAt(i,0) == lastSelectedID) {
                table.setRowSelectionInterval(i,i);
                break;
            }
        }
    }
}
