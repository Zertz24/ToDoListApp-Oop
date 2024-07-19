import javax.swing.SwingUtilities;

public class Controller {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoMeLogin().setVisible(true));
    }

    public static void showToDoListApp(int userId) {
        SwingUtilities.invokeLater(() -> new ToDoListApp(userId));
    }
}
