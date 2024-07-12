package Todo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;

public class HomeWindow extends JFrame {
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskField;
    private JTextField dateField;
    private TaskManager taskManager;
    private String username;
    private String password;

    public HomeWindow(String username, String password) {
        this.username = username;
        this.password = password;
        this.taskManager = new TaskManager(username, password);

        setTitle("Task Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        taskField = new JTextField();
        dateField = new JTextField();
        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Complete Task");

        addButton.addActionListener(new AddTaskListener());
        deleteButton.addActionListener(new DeleteTaskListener());
        completeButton.addActionListener(new CompleteTaskListener());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Task:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(completeButton);

        loadTasks();

        add(taskScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

private void loadTasks() {
    taskListModel.clear();
    List<String> tasks = taskManager.getTasks();
    for (String task : tasks) {
        taskListModel.addElement(task);
    }
}


    private class AddTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String task = taskField.getText().trim();
            String date = dateField.getText().trim();
            if (!task.isEmpty() && !date.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    Date dueDate = sdf.parse(date);
                    String taskEntry = task + " - " + date;
                    taskManager.addTask(taskEntry);
                    taskListModel.addElement(taskEntry);
                    taskField.setText("");
                    dateField.setText("");
                } catch (ParseException pe) {
                    JOptionPane.showMessageDialog(HomeWindow.this, "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(HomeWindow.this, "An error occurred while adding the task.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(HomeWindow.this, "Task and Date cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                try {
                    taskManager.removeTask(taskListModel.getElementAt(selectedIndex));
                    taskListModel.remove(selectedIndex);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(HomeWindow.this, "An error occurred while deleting the task.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(HomeWindow.this, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CompleteTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                String task = taskListModel.getElementAt(selectedIndex);
                task = "<html><strike>" + task + "</strike></html>";
                taskListModel.set(selectedIndex, task);
                try {
                    taskManager.updateTask(taskListModel);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(HomeWindow.this, "An error occurred while marking the task as completed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(HomeWindow.this, "Please select a task to mark as completed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class TaskCellRenderer extends DefaultListCellRenderer {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String task = (String) value;
            String[] parts = task.split(" - ");
            if (parts.length == 2) {
                try {
                    Date dueDate = sdf.parse(parts[1]);
                    if (new Date().after(dueDate)) {
                        label.setForeground(Color.RED);
                    } else {
                        label.setForeground(Color.BLACK);
                    }
                } catch (ParseException e) {
                    label.setForeground(Color.BLACK);
                }
            }
            return label;
        }
    }
}
