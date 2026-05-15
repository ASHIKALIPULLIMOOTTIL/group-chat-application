import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServerGUI extends JFrame {

    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JTextArea logArea   = new JTextArea();
    private final JLabel statusLabel  = new JLabel("● Server Running on port " + Server.PORT);

    private static final Color BG       = new Color(18, 18, 30);
    private static final Color PANEL_BG = new Color(28, 28, 46);
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color TEXT_MAIN= new Color(226, 232, 240);
    private static final Color TEXT_DIM = new Color(148, 163, 184);
    private static final Color SUCCESS  = new Color(52, 211, 153);

    public ServerGUI() {
        buildUI();
        startServer();
    }

    private void buildUI() {
        setTitle("Chat Server — Port " + Server.PORT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT));

        JLabel title = new JLabel("⚡ ChatApp Server");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);

        statusLabel.setForeground(SUCCESS);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        header.add(title);
        header.add(Box.createHorizontalStrut(20));
        header.add(statusLabel);
        add(header, BorderLayout.NORTH);

        // Log area
        logArea.setEditable(false);
        logArea.setBackground(BG);
        logArea.setForeground(new Color(163, 230, 53));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1), " Server Log ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), TEXT_DIM));
        logScroll.getViewport().setBackground(BG);

        // User list
        JList<String> userList = new JList<>(userListModel);
        userList.setBackground(PANEL_BG);
        userList.setForeground(TEXT_MAIN);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setFixedCellHeight(32);

        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(180, 0));
        userScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1), " Online Users ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), TEXT_DIM));
        userScroll.getViewport().setBackground(PANEL_BG);

        JPanel center = new JPanel(new BorderLayout(8, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        center.add(logScroll, BorderLayout.CENTER);
        center.add(userScroll, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(PANEL_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ACCENT));

        JButton clearBtn = new JButton("Clear Log");
        clearBtn.setBackground(ACCENT);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> logArea.setText(""));
        footer.add(clearBtn);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append("[" + time + "] " + text + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void startServer() {
        Server server = new Server();
        server.setListener(new Server.ServerListener() {
            @Override public void onLog(String message) { appendLog(message); }
            @Override public void onClientConnected(String username) {
                SwingUtilities.invokeLater(() -> {
                    if (!userListModel.contains("👤 " + username))
                        userListModel.addElement("👤 " + username);
                });
            }
            @Override public void onClientDisconnected(String username) {
                SwingUtilities.invokeLater(() -> userListModel.removeElement("👤 " + username));
            }
        });
        new Thread(server::start, "server-thread").start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
