import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DoMeLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int loggedInUserId;

    public DoMeLogin() {
        setTitle("Do:Me Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(220, 255, 220));
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 255, 153));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(" Do:Me ", JLabel.CENTER);
        titleLabel.setFont(new Font("Monotype Corsiva", Font.BOLD, 70));
        titleLabel.setForeground(new Color(0, 150, 136)); // Teal color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Login", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 150, 136));
        loginButton.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(loginButton, gbc);

        JLabel registerLabel = new JLabel("Don’t have an account? Register", JLabel.CENTER);
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        mainPanel.add(registerLabel, gbc);

        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new DoMeRegister().setVisible(true);
                dispose(); // Close the login window
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (authenticateUser(usernameField.getText(), new String(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(DoMeLogin.this, "Login successful!", "Login Status", JOptionPane.INFORMATION_MESSAGE);
                        SwingUtilities.invokeLater(() -> {
                            new ToDoListApp(loggedInUserId).setVisible(true);
                        });
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(DoMeLogin.this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private boolean authenticateUser(String username, String password) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT id FROM tableoop WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();
        boolean userExists = resultSet.next();
        if (userExists) {
            loggedInUserId = resultSet.getInt("id");
        }
        resultSet.close();
        statement.close();
        connection.close();
        return userExists;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DoMeLogin().setVisible(true);
        });
    }
}

class DoMeRegister extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public DoMeRegister() {
        setTitle("Do:Me Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(220, 255, 220));
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 255, 153));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(" Do:Me ", JLabel.CENTER);
        titleLabel.setFont(new Font("Monotype Corsiva", Font.BOLD, 70));
        titleLabel.setForeground(new Color(0, 150, 136));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Register", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);

        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 150, 136));
        registerButton.setForeground(Color.WHITE);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(registerButton, gbc);

        JLabel loginLabel = new JLabel("Already have an account? Login", JLabel.CENTER);
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        mainPanel.add(loginLabel, gbc);

        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new DoMeLogin().setVisible(true);
                dispose();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(DoMeRegister.this, "Passwords do not match!", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (registerUser(username, password)) {
                        JOptionPane.showMessageDialog(DoMeRegister.this, "Registration successful!", "Registration Status", JOptionPane.INFORMATION_MESSAGE);
                        new DoMeLogin().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(DoMeRegister.this, "Registration failed. Username may already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private boolean registerUser(String username, String password) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "INSERT INTO tableoop (username, password) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        int rowsAffected = statement.executeUpdate();
        statement.close();
        connection.close();
        return rowsAffected > 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DoMeRegister().setVisible(true);
        });
    }
}