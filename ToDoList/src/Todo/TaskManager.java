package Todo;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.swing.DefaultListModel;

public class TaskManager {
    private List<String> tasks;
    private String filename;

    public TaskManager(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + password).getBytes());
        this.filename = encoded + ".txt";
        this.tasks = new ArrayList<>();
        loadTasks();
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(line);
            }
        } catch (IOException e) {
            // File not found is acceptable, it means no tasks yet
        }
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void addTask(String task) throws IOException {
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(String task) throws IOException {
        tasks.remove(task);
        saveTasks();
    }

    public void updateTask(DefaultListModel<String> taskListModel) throws IOException {
        tasks.clear();
        for (int i = 0; i < taskListModel.getSize(); i++) {
            tasks.add(taskListModel.getElementAt(i));
        }
        saveTasks();
    }

    private void saveTasks() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
        }
    }
}
