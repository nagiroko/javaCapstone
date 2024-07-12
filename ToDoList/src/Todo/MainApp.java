package Todo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainApp extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AccountManager accountManager;

    public MainApp() {
        setTitle("Login/Create Account");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            accountManager = new AccountManager("accounts.txt");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while initializing the account manager.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login");

        createAccountButton.addActionListener(new CreateAccountListener());
        loginButton.addActionListener(new LoginListener());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(createAccountButton);
        panel.add(loginButton);

        add(panel);
    }

    private class CreateAccountListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(MainApp.this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (username.contains(",") || password.contains(",")) {
                JOptionPane.showMessageDialog(MainApp.this, "Username and Password cannot contain ,.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (accountManager.accountExists(username)) {
                    JOptionPane.showMessageDialog(MainApp.this, "Account already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    accountManager.createAccount(username, password);
                    JOptionPane.showMessageDialog(MainApp.this, "Account created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MainApp.this, "An error occurred while creating the account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(MainApp.this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (accountManager.authenticate(username, password)) {
                JOptionPane.showMessageDialog(MainApp.this, "Login successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new HomeWindow(username, password);
                dispose();
            } else {
                JOptionPane.showMessageDialog(MainApp.this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}
