import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ToDoListApp extends JFrame {
    private int userId;
    private JFrame mainFrame;
    private JPanel taskPanel;
    private JTextArea completedTaskArea;
    private Map<LocalDate, List<String>> taskHistory;
    private TreeSet<Integer> selectedTimes;
    private List<JCheckBox> allCheckBoxes;
    private JTextField[][] taskFields;
    private String[] categories = {"Work", "School", "Hobby", "Chores", "Optional", "Necessary"};

    public ToDoListApp(int userId) {
        this.userId = userId;
        taskHistory = new HashMap<>();
        selectedTimes = new TreeSet<>();
        allCheckBoxes = new ArrayList<>();
        taskFields = new JTextField[24][1];

        mainFrame = new JFrame("Do:Me - Task List");
        mainFrame.setSize(800, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        JLabel currentTasksLabel = new JLabel("Current Tasks", JLabel.CENTER);
        JLabel completedTasksLabel = new JLabel("Completed Tasks", JLabel.CENTER);

        taskPanel = new JPanel();
        taskPanel.setLayout(new GridLayout(24, 3));
        taskPanel.setBackground(new Color(204, 229, 255));

        for (int i = 0; i < 24; i++) {
            String time = (i % 12 == 0 ? 12 : i % 12) + (i < 12 ? " AM" : " PM");
            JLabel timeLabel = new JLabel(time, JLabel.CENTER);
            timeLabel.setOpaque(true);
            timeLabel.setBackground(new Color(173, 216, 230));
            JTextField taskField = new JTextField("Enter your task here", 20);
            taskField.setForeground(Color.GRAY);

            taskFields[i][0] = taskField;

            taskField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (taskField.getText().equals("Enter your task here")) {
                        taskField.setText("");
                        taskField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (taskField.getText().isEmpty()) {
                        taskField.setText("Enter your task here");
                        taskField.setForeground(Color.GRAY);
                    }
                }
            });

            JCheckBox mergeCheckbox = new JCheckBox();
            int finalI = i;
            mergeCheckbox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (mergeCheckbox.isSelected()) {
                        selectedTimes.add(finalI);
                    } else {
                        selectedTimes.remove(finalI);
                    }
                }
            });

            allCheckBoxes.add(mergeCheckbox);

            JComboBox<String> categoryComboBox = new JComboBox<>(categories);

            JButton doneButton = new JButton("DONE");
            doneButton.setBackground(new Color(255, 182, 193));
            doneButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String task = taskField.getText();
                    String category = (String) categoryComboBox.getSelectedItem();
                    if (!task.isEmpty() && !task.equals("Enter your task here")) {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                        String date = now.format(dateFormatter);
                        String time = (finalI % 12 == 0 ? 12 : finalI % 12) + (finalI < 12 ? " AM" : " PM");

                        String completedTask;
                        if (!selectedTimes.isEmpty()) {
                            int first = selectedTimes.first();
                            int last = selectedTimes.last();
                            String firstTime = (first % 12 == 0 ? 12 : first % 12) + (first < 12 ? " AM" : " PM");
                            String lastTime = (last % 12 == 0 ? 12 : last % 12) + (last < 12 ? " AM" : " PM");
                            completedTask = date + ": " + firstTime + " to " + lastTime + ": " + task + " (" + category + ")\n";
                            selectedTimes.clear();
                        } else {
                            completedTask = date + ": " + time + ": " + task + " (" + category + ")\n";
                        }
                        completedTaskArea.append(completedTask);
                        saveCompletedTaskToFile(completedTask);
                    }
                    taskField.setText("Enter your task here");
                    taskField.setForeground(Color.GRAY);

                    for (JCheckBox checkbox : allCheckBoxes) {
                        checkbox.setSelected(false);
                    }

                    moveFocusToNextTextField(taskField);
                }
            });

            taskPanel.add(timeLabel);
            taskPanel.add(taskField);
            taskPanel.add(mergeCheckbox);
            taskPanel.add(categoryComboBox);
            taskPanel.add(doneButton);
        }

        completedTaskArea = new JTextArea();
        completedTaskArea.setEditable(false);
        JScrollPane completedTaskScrollPane = new JScrollPane(completedTaskArea);

        loadCompletedTasksFromFile();

        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearCompletedTasksButton = new JButton("Clear Completed Tasks");
        clearCompletedTasksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCompletedTasks();
            }
        });

        JButton historyButton = new JButton("History");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistoryPage();
            }
        });

        bottomRightPanel.add(clearCompletedTasksButton);
        bottomRightPanel.add(historyButton);

        JPanel completedTasksPanel = new JPanel(new BorderLayout());
        completedTasksPanel.setBackground(new Color(204, 229, 255));
        completedTasksPanel.add(completedTasksLabel, BorderLayout.NORTH);
        completedTasksPanel.add(completedTaskScrollPane, BorderLayout.CENTER);

        JPanel taskPanelsPanel = new JPanel(new BorderLayout());
        taskPanelsPanel.setBackground(new Color(204, 229, 255));
        taskPanelsPanel.add(currentTasksLabel, BorderLayout.NORTH);
        taskPanelsPanel.add(new JScrollPane(taskPanel), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(taskPanelsPanel);
        splitPane.setRightComponent(completedTasksPanel);
        splitPane.setResizeWeight(0.7);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String formattedDate = currentDate.format(formatter);

        JLabel dateLabel = new JLabel("Today - " + formattedDate, JLabel.CENTER);
        dateLabel.setOpaque(true);
        dateLabel.setBackground(new Color(255, 255, 153));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(mainFrame,
                        "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    mainFrame.dispose();
                    new DoMeLogin().setVisible(true);
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(dateLabel, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(splitPane, BorderLayout.CENTER);
        mainFrame.add(bottomRightPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    private void moveFocusToNextTextField(JTextField currentTextField) {
        int row = -1;
        for (int i = 0; i < 24; i++) {
            if (taskFields[i][0] == currentTextField) {
                row = i;
                break;
            }
        }

        if (row != -1 && row + 1 < 24) {
            final JTextField nextTextField = taskFields[row + 1][0];
            SwingUtilities.invokeLater(() -> {
                nextTextField.requestFocusInWindow();
            });
        }
    }

    private void loadCompletedTasksFromFile() {
        try {
            Path path = Paths.get(getUserSpecificFile());
            if (Files.exists(path)) {
                List<String> tasks = Files.readAllLines(path);
                for (String task : tasks) {
                    completedTaskArea.append(task + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCompletedTaskToFile(String task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserSpecificFile(), true))) {
            writer.write(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCompletedTasksFile() {
        try {
            Path path = Paths.get(getUserSpecificFile());
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCompletedTasks() {
        try {
            String completedTasks = completedTaskArea.getText();
            if (!completedTasks.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserHistoryFile(), true))) {
                    writer.write(completedTasks);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            completedTaskArea.setText("");
            clearCompletedTasksFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHistoryPage() {
        StringBuilder historyContent = new StringBuilder();
        try {
            Path path = Paths.get(getUserHistoryFile());
            if (Files.exists(path)) {
                List<String> tasks = Files.readAllLines(path);
                for (String task : tasks) {
                    historyContent.append(task).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(mainFrame, historyContent.toString(), "Task History", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getUserSpecificFile() {
        return "completed_tasks_user_" + userId + ".txt";
    }

    private String getUserHistoryFile() {
        return "history_tasks_user_" + userId + ".txt";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int userId = 1;
                new ToDoListApp(userId);
            }
        });
    }
}
