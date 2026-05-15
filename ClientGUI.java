import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame {

    private static final Color BG          = new Color(15, 15, 25);
    private static final Color PANEL_BG    = new Color(24, 24, 40);
    private static final Color CARD_BG     = new Color(32, 32, 56);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color ACCENT2     = new Color(139, 92, 246);
    private static final Color TEXT_MAIN   = new Color(226, 232, 240);
    private static final Color TEXT_DIM    = new Color(100, 116, 139);
    private static final Color MY_MSG      = new Color(49, 46, 129);
    private static final Color OTHER_MSG   = new Color(30, 41, 59);
    private static final Color JOIN_COLOR  = new Color(52, 211, 153);
    private static final Color LEAVE_COLOR = new Color(248, 113, 113);
    private static final Color SERVER_COL  = new Color(251, 191, 36);

    private final JTextPane chatPane    = new JTextPane();
    private final JTextField inputField = new JTextField();
    private final DefaultListModel<String> onlineModel = new DefaultListModel<>();
    private final JList<String> onlineList = new JList<>(onlineModel);
    private final JComboBox<String> pmCombo = new JComboBox<>();
    private final JLabel statusLabel = new JLabel("● Connecting…");

    private Client client;
    private String username;

    public ClientGUI() {
        showLoginDialog();
    }

    private void showLoginDialog() {
        JDialog dialog = new JDialog((Frame) null, "Join Chat", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(BG);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG);
        form.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        JLabel heading = new JLabel("⚡ Join ChatApp");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(TEXT_MAIN);
        c.gridwidth = 2; c.gridy = 0;
        form.add(heading, c);

        c.gridwidth = 1;
        JTextField nameField = addFormRow(form, "Username:", c, 1);
        JTextField hostField = addFormRow(form, "Server Host:", c, 2);
        hostField.setText("localhost");
        JTextField portField = addFormRow(form, "Port:", c, 3);
        portField.setText("5000");

        JButton joinBtn = new JButton("Connect →");
        joinBtn.setBackground(ACCENT);
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        joinBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        joinBtn.setFocusPainted(false);
        joinBtn.setOpaque(true);
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        c.insets = new Insets(14, 4, 4, 4);
        form.add(joinBtn, c);
        dialog.add(form, BorderLayout.CENTER);

        joinBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String host = hostField.getText().trim();
            int port;
            try { port = Integer.parseInt(portField.getText().trim()); }
            catch (NumberFormatException ex) { return; }
            if (name.isEmpty() || host.isEmpty()) return;

            username = name;
            client = new Client(name, host, port);
            setupClientListener();
            if (client.connect()) {
                dialog.dispose();
                buildMainUI();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Could not connect to " + host + ":" + port,
                        "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        ActionListener submit = e -> joinBtn.doClick();
        nameField.addActionListener(submit);
        hostField.addActionListener(submit);
        portField.addActionListener(submit);

        dialog.setVisible(true);
    }

    private JTextField addFormRow(JPanel panel, String label, GridBagConstraints c, int row) {
        c.gridy = row; c.gridx = 0; c.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, c);

        JTextField field = new JTextField();
        field.setBackground(CARD_BG);
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        c.gridx = 1; c.weightx = 0.7;
        panel.add(field, c);
        return field;
    }

    private void buildMainUI() {
        setTitle("ChatApp — " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { client.disconnect(); }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildInputPanel(), BorderLayout.SOUTH);
        setVisible(true);
        inputField.requestFocusInWindow();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel title = new JLabel("⚡ ChatApp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);

        JLabel userLabel = new JLabel("Logged in as: " + username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_DIM);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(JOIN_COLOR);

        JButton dcBtn = new JButton("Disconnect");
        dcBtn.setBackground(new Color(127, 29, 29));
        dcBtn.setForeground(new Color(254, 202, 202));
        dcBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dcBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        dcBtn.setFocusPainted(false);
        dcBtn.setOpaque(true);
        dcBtn.addActionListener(e -> { client.disconnect(); System.exit(0); });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(userLabel);
        right.add(statusLabel);
        right.add(dcBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildCenterPanel() {
        chatPane.setEditable(false);
        chatPane.setBackground(BG);
        chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane chatScroll = new JScrollPane(chatPane);
        chatScroll.setBorder(BorderFactory.createEmptyBorder());
        chatScroll.getViewport().setBackground(BG);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(PANEL_BG);
        rightPanel.setPreferredSize(new Dimension(190, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)));

        JLabel onlineLabel = new JLabel("Online Users");
        onlineLabel.setForeground(TEXT_DIM);
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        onlineList.setBackground(PANEL_BG);
        onlineList.setForeground(TEXT_MAIN);
        onlineList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        onlineList.setFixedCellHeight(30);

        JScrollPane onlineScroll = new JScrollPane(onlineList);
        onlineScroll.setBorder(BorderFactory.createLineBorder(CARD_BG, 1));
        onlineScroll.getViewport().setBackground(PANEL_BG);

        JPanel pmPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        pmPanel.setOpaque(false);

        JLabel pmLabel = new JLabel("Private Message");
        pmLabel.setForeground(TEXT_DIM);
        pmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        pmCombo.setBackground(CARD_BG);
        pmCombo.setForeground(TEXT_MAIN);
        pmCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pmCombo.addItem("— Select User —");

        JButton pmBtn = new JButton("Send Private");
        pmBtn.setBackground(ACCENT2);
        pmBtn.setForeground(Color.WHITE);
        pmBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pmBtn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        pmBtn.setFocusPainted(false);
        pmBtn.setOpaque(true);
        pmBtn.addActionListener(e -> sendPrivate());

        pmPanel.add(pmLabel);
        pmPanel.add(pmCombo);
        pmPanel.add(pmBtn);

        rightPanel.add(onlineLabel, BorderLayout.NORTH);
        rightPanel.add(onlineScroll, BorderLayout.CENTER);
        rightPanel.add(pmPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, rightPanel);
        split.setDividerLocation(640);
        split.setDividerSize(2);
        split.setBorder(null);
        return split;
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        inputField.setBackground(CARD_BG);
        inputField.setForeground(TEXT_MAIN);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        inputField.addActionListener(e -> sendMessage());

        JButton sendBtn = new JButton("Send ➤");
        sendBtn.setBackground(ACCENT);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        sendBtn.setFocusPainted(false);
        sendBtn.setOpaque(true);
        sendBtn.addActionListener(e -> sendMessage());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);
        return panel;
    }

    private void setupClientListener() {
        client.setListener(new Client.ClientListener() {
            @Override public void onConnected() {
                SwingUtilities.invokeLater(() -> statusLabel.setText("● Connected"));
            }
            @Override public void onDisconnected(String reason) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("✕ Disconnected");
                    statusLabel.setForeground(LEAVE_COLOR);
                    appendSystem(reason, LEAVE_COLOR);
                });
            }
            @Override public void onMessage(Message msg) {
                SwingUtilities.invokeLater(() -> appendMessage(msg));
            }
            @Override public void onUserListUpdated(String[] users) {
                SwingUtilities.invokeLater(() -> {
                    onlineModel.clear();
                    pmCombo.removeAllItems();
                    pmCombo.addItem("— Select User —");
                    for (String u : users) {
                        onlineModel.addElement(u);
                        if (!u.equals(username)) pmCombo.addItem(u);
                    }
                });
            }
        });
    }

    private void appendMessage(Message msg) {
        switch (msg.getType()) {
            case TEXT:
                boolean mine = msg.getSender().equals(username);
                appendBubble(msg.getSender(), msg.getContent(), msg.getTimestamp(),
                        mine ? MY_MSG : OTHER_MSG, mine);
                break;
            case PRIVATE:
                appendPrivate(msg);
                break;
            case JOIN:
                appendSystem("⟶ " + msg.getContent(), JOIN_COLOR);
                break;
            case LEAVE:
                appendSystem("⟵ " + msg.getContent(), LEAVE_COLOR);
                break;
            case SERVER:
                appendSystem("ℹ " + msg.getContent(), SERVER_COL);
                break;
        }
    }

    private void appendBubble(String sender, String text, String time, Color bg, boolean isMe) {
        StyledDocument doc = chatPane.getStyledDocument();
        try {
            SimpleAttributeSet nameStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(nameStyle, isMe ? new Color(165, 180, 252) : new Color(125, 211, 252));
            StyleConstants.setBold(nameStyle, true);
            StyleConstants.setFontSize(nameStyle, 12);
            doc.insertString(doc.getLength(), (isMe ? "You" : sender) + "  ", nameStyle);

            SimpleAttributeSet timeStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(timeStyle, TEXT_DIM);
            StyleConstants.setFontSize(timeStyle, 11);
            doc.insertString(doc.getLength(), time + "\n", timeStyle);

            SimpleAttributeSet msgStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(msgStyle, TEXT_MAIN);
            StyleConstants.setFontSize(msgStyle, 14);
            StyleConstants.setBackground(msgStyle, bg);
            doc.insertString(doc.getLength(), "  " + text + "  \n\n", msgStyle);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {}
    }

    private void appendPrivate(Message msg) {
        StyledDocument doc = chatPane.getStyledDocument();
        try {
            boolean isMe = msg.getSender().equals(username);
            String header = isMe ? "🔒 You → " + msg.getReceiver() : "🔒 " + msg.getSender() + " → You";

            SimpleAttributeSet headerStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(headerStyle, new Color(251, 191, 36));
            StyleConstants.setBold(headerStyle, true);
            StyleConstants.setFontSize(headerStyle, 12);
            doc.insertString(doc.getLength(), header + "  " + msg.getTimestamp() + "\n", headerStyle);

            SimpleAttributeSet msgStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(msgStyle, TEXT_MAIN);
            StyleConstants.setBackground(msgStyle, new Color(120, 53, 15));
            StyleConstants.setFontSize(msgStyle, 14);
            doc.insertString(doc.getLength(), "  " + msg.getContent() + "  \n\n", msgStyle);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {}
    }

    private void appendSystem(String text, Color color) {
        StyledDocument doc = chatPane.getStyledDocument();
        try {
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setForeground(style, color);
            StyleConstants.setItalic(style, true);
            StyleConstants.setFontSize(style, 12);
            doc.insertString(doc.getLength(), text + "\n\n", style);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {}
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        client.sendMessage(text);
        inputField.setText("");
    }

    private void sendPrivate() {
        String target = (String) pmCombo.getSelectedItem();
        if (target == null || target.startsWith("—")) return;
        String text = JOptionPane.showInputDialog(this,
                "Private message to " + target + ":", "Private Message", JOptionPane.PLAIN_MESSAGE);
        if (text != null && !text.trim().isEmpty()) {
            client.sendPrivate(target, text.trim());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
