import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Stack;

public class StudyPlannerUltimate extends JFrame {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final Stack<Task> deletedTasks = new Stack<>();
    private final JPanel taskPanel = new JPanel();
    private boolean darkMode = false;

    public StudyPlannerUltimate() {
        showSplash();
    }

    private void showSplash() {
        JFrame splash = new JFrame();
        splash.setSize(500, 400);
        splash.getContentPane().setBackground(new Color(230, 245, 255));
        splash.setLayout(new BorderLayout());

        JLabel title = new JLabel(" StudyPlanner Ultimate", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(60, 90, 180));

        JLabel subtitle = new JLabel("Your Smart Study Companion ", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBackground(new Color(230, 245, 255));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        String[] features = {
                "Priority Colors for Better Focus",
                " Mark & Track Task Completion",
                " Search & Filter Tasks Easily",
                " Toggle Dark/Light Mode Instantly"
        };
        for (String f : features) {
            JLabel lbl = new JLabel(f, JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            center.add(lbl);
        }

        JButton start = createButton(" Start Planning Now!", new Color(72, 201, 176), e -> {
            splash.dispose();
            createMainUI();
        });

        splash.add(title, BorderLayout.NORTH);
        splash.add(center, BorderLayout.CENTER);
        splash.add(subtitle, BorderLayout.SOUTH);
        splash.add(start, BorderLayout.PAGE_END);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
    }

   
    private void createMainUI() {
        setTitle("StudyPlanner Ultimate");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addBtn = createButton(" Add Task", new Color(60, 179, 113), e -> addTask());
        JButton doneBtn = createButton(" Mark Done", new Color(0, 150, 255), e -> markTaskDone());
        JButton deleteBtn = createButton(" Delete", new Color(220, 53, 69), e -> deleteTask());
        JButton undoBtn = createButton(" Undo Delete", Color.MAGENTA, e -> undoDelete());
        JButton statsBtn = createButton(" Stats", new Color(123, 104, 238), e -> showStats());
        JButton searchBtn = createButton(" Search", new Color(255, 165, 0), e -> searchTasks());

        JButton darkBtn = new JButton(" Dark Mode");
        darkBtn.setBackground(Color.DARK_GRAY);
        darkBtn.setForeground(Color.WHITE);
        darkBtn.setFocusPainted(false);
        darkBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        darkBtn.addActionListener(e -> toggleDarkMode(darkBtn));

        topPanel.add(addBtn);
        topPanel.add(doneBtn);
        topPanel.add(deleteBtn);
        topPanel.add(undoBtn);
        topPanel.add(statsBtn);
        topPanel.add(searchBtn);
        topPanel.add(darkBtn);

        add(topPanel, BorderLayout.NORTH);


        taskPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        taskPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(taskPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
        setLocationRelativeTo(null);
    }


    private void addTask() {
        JTextField title = new JTextField();
        JTextField subject = new JTextField();
        JTextField date = new JTextField(LocalDate.now().plusDays(1).toString());
        JComboBox<String> priority = new JComboBox<>(new String[]{" High", "Medium", " Low"});

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Title:"));
        panel.add(title);
        panel.add(new JLabel("Subject:"));
        panel.add(subject);
        panel.add(new JLabel("Due Date:"));
        panel.add(date);
        panel.add(new JLabel("Priority:"));
        panel.add(priority);

        int res = JOptionPane.showConfirmDialog(this, panel, "Add New Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            Task t = new Task(title.getText(), subject.getText(),
                    LocalDate.parse(date.getText()), priority.getSelectedIndex() + 1);
            tasks.add(t);
            refreshTasks();
        }
    }

    private void markTaskDone() {
        String taskTitle = JOptionPane.showInputDialog(this, "Enter task title to mark as done:");
        if (taskTitle == null) return;
        for (Task t : tasks) {
            if (t.title.equalsIgnoreCase(taskTitle.trim())) {
                t.done = true;
                JOptionPane.showMessageDialog(this, "Task marked as completed!");
                refreshTasks();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Task not found!");
    }

    private void deleteTask() {
        String taskTitle = JOptionPane.showInputDialog(this, "Enter task title to delete:");
        if (taskTitle == null) return;
        for (Task t : tasks) {
            if (t.title.equalsIgnoreCase(taskTitle.trim())) {
                deletedTasks.push(t);
                tasks.remove(t);
                JOptionPane.showMessageDialog(this, "Task deleted!");
                refreshTasks();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Task not found!");
    }

    private void undoDelete() {
        if (!deletedTasks.isEmpty()) {
            tasks.add(deletedTasks.pop());
            JOptionPane.showMessageDialog(this, "Task restored!");
            refreshTasks();
        } else {
            JOptionPane.showMessageDialog(this, "No tasks to undo!");
        }
    }

    private void searchTasks() {
        String query = JOptionPane.showInputDialog(this, "Enter keyword to search:");
        if (query == null || query.isEmpty()) return;

        StringBuilder result = new StringBuilder("Search Results:\n");
        for (Task t : tasks) {
            if (t.title.toLowerCase().contains(query.toLowerCase()) ||
                t.subject.toLowerCase().contains(query.toLowerCase())) {
                result.append(t).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, result.toString());
    }

    private void showStats() {
        long done = tasks.stream().filter(t -> t.done).count();
        long pending = tasks.size() - done;
        String msg = "Statistics:\n" +
                "Total Tasks: " + tasks.size() + "\n" +
                " Completed: " + done + "\n" +
                " Pending: " + pending + "\n";
        JOptionPane.showMessageDialog(this, msg);
    }

   
    private void refreshTasks() {
        taskPanel.removeAll();
        for (Task t : tasks) {
            JPanel card = new JPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(250, 100));
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            card.setBackground(getPriorityColor(t));
            JLabel lbl = new JLabel("<html><b>" + t.title + "</b><br>" +
                    t.subject + "<br> " + t.dueDate + "<br>" +
                    (t.done ? "Completed" : "Pending") + "</html>");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            card.add(lbl, BorderLayout.CENTER);
            taskPanel.add(card);
        }
        taskPanel.revalidate();
        taskPanel.repaint();
    }

    private Color getPriorityColor(Task t) {
        if (t.done) return new Color(220, 220, 220);
        return switch (t.priority) {
            case 1 -> new Color(255, 182, 193);
            case 2 -> new Color(255, 239, 153);
            default -> new Color(178, 255, 178);
        };
    }

    private void toggleDarkMode(JButton btn) {
        darkMode = !darkMode;
        Color bg = darkMode ? new Color(34, 34, 34) : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;
        taskPanel.setBackground(bg);
        getContentPane().setBackground(bg);
        btn.setText(darkMode ? "Light Mode" : " Dark Mode");
        for (Component c : taskPanel.getComponents()) {
            c.setBackground(bg.darker());
            c.setForeground(fg);
        }
        repaint();
    }

   
    private JButton createButton(String text, Color color, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.addActionListener(action);
        return btn;
    }
    static class Task {
        String title, subject;
        LocalDate dueDate;
        int priority;
        boolean done;

        Task(String t, String s, LocalDate d, int p) {
            title = t;
            subject = s;
            dueDate = d;
            priority = p;
            done = false;
        }

        public String toString() {
            return title + " | " + subject + " | Due: " + dueDate + (done ? " ✅" : "");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudyPlannerUltimate::new);
    }
}
