import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ProcessView extends JFrame {
    JButton button;
    JTable table;
    static int MIN_ROWS = 100, COL_NUM = 3;
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

        //Table and ScrollPane
        table = new JTable(MIN_ROWS,COL_NUM);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(450,368));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(0).setHeaderValue("ID");
        table.getColumnModel().getColumn(1).setHeaderValue("Process Name");
        table.getColumnModel().getColumn(2).setHeaderValue("Memory Usage");

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
    public JButton getButton() { return button; }
    public JTable getTable() { return table; }
    public JTableHeader getTableHeader() { return table.getTableHeader(); }
    public void clearTable() {
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < COL_NUM; j++)
                table.getModel().setValueAt("", i, j);
        }
    }
}
