package Todo;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    private File file;
    private Map<String, String> accounts;

    public AccountManager(String filePath) throws IOException {
        file = new File(filePath);
        accounts = new HashMap<>();
        loadAccounts();
    }

    private void loadAccounts() throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    accounts.put(parts[0], parts[1]);
                }
            }
            reader.close();
        }
    }

    public boolean accountExists(String username) {
        return accounts.containsKey(username);
    }

    public void createAccount(String username, String password) throws IOException {
        accounts.put(username, password);
        saveAccounts();
    }

    public boolean authenticate(String username, String password) {
        return accounts.containsKey(username) && accounts.get(username).equals(password);
    }

    private void saveAccounts() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<String, String> entry : accounts.entrySet()) {
            writer.write(entry.getKey() + "," + entry.getValue());
            writer.newLine();
        }
        writer.close();
    }
}

