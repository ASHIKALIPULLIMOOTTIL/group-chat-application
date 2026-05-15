import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class ClientGUI extends JFrame {

    private static final Color BG          = new Color(15, 15, 25);
    private static final Color PANEL_BG    = new Color(24, 24, 40);
    private static final Color CARD_BG     = new Color(32, 32, 56);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color ACCENT2     = new Color(139, 92, 246);
    private static final Color TEXT_MAIN   = new Color(226, 232, 240);
    private static final Color TEXT_DIM    = new Color(100, 116, 139);
    private static final Color MY_MSG_BG   = new Color(99, 102, 241);
    private static final Color OTHER_MSG_BG= new Color(36, 36, 60);
    private static final Color JOIN_COLOR  = new Color(52, 211, 153);
    private static final Color LEAVE_COLOR = new Color(248, 113, 113);
    private static final Color SERVER_COL  = new Color(251, 191, 36);
    private static final Color PM_BG       = new Color(120, 53, 15);

    private final JPanel chatPanel = new JPanel();
    private JScrollPane chatScroll;
    private final JTextField inputField = new JTextField();
    private final DefaultListModel<String> onlineModel = new DefaultListModel<>();
    private final JList<String> onlineList = new JList<>(onlineModel);
    private final JComboBox<String> pmCombo = new JComboBox<>();
    private final JLabel statusLabel = new JLabel("● Connecting…");

    private Client client;
    private String username;

    public ClientGUI() { showLoginDialog(); }

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

        JButton joinBtn = styledButton("Connect →", ACCENT, Color.WHITE, 14);
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
            if (client.connect()) { dialog.dispose(); buildMainUI(); }
            else JOptionPane.showMessageDialog(dialog,
                    "Could not connect to " + host + ":" + port,
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
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
        field.setBackground(CARD_BG); field.setForeground(TEXT_MAIN);
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
        setSize(900, 640);
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
        JButton dcBtn = styledButton("Disconnect", new Color(127, 29, 29), new Color(254, 202, 202), 11);
        dcBtn.addActionListener(e -> { client.disconnect(); System.exit(0); });
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false); left.add(title);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(userLabel); right.add(statusLabel); right.add(dcBtn);
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildCenterPanel() {
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        chatScroll = new JScrollPane(chatPanel);
        chatScroll.setBorder(BorderFactory.createEmptyBorder());
        chatScroll.getViewport().setBackground(BG);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(PANEL_BG);
        rightPanel.setPreferredSize(new Dimension(190, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)));

        JLabel onlineLabel = new JLabel("Online Users");
        onlineLabel.setForeground(TEXT_DIM);
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        onlineList.setBackground(PANEL_BG); onlineList.setForeground(TEXT_MAIN);
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
        pmCombo.setBackground(CARD_BG); pmCombo.setForeground(TEXT_MAIN);
        pmCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pmCombo.addItem("— Select User —");
        JButton pmBtn = styledButton("Send Private", ACCENT2, Color.WHITE, 11);
        pmBtn.addActionListener(e -> sendPrivate());
        pmPanel.add(pmLabel); pmPanel.add(pmCombo); pmPanel.add(pmBtn);

        rightPanel.add(onlineLabel, BorderLayout.NORTH);
        rightPanel.add(onlineScroll, BorderLayout.CENTER);
        rightPanel.add(pmPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, rightPanel);
        split.setDividerLocation(680);
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
        inputField.setBackground(CARD_BG); inputField.setForeground(TEXT_MAIN);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        inputField.addActionListener(e -> sendMessage());
        JButton sendBtn = styledButton("Send ➤", ACCENT, Color.WHITE, 13);
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
                    addSystemMessage(reason, LEAVE_COLOR);
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
                addBubble(mine ? "You" : msg.getSender(),
                        msg.getContent(), msg.getTimestamp(),
                        mine ? MY_MSG_BG : OTHER_MSG_BG, mine);
                break;
            case PRIVATE:
                boolean isMine = msg.getSender().equals(username);
                String lbl = isMine ? "🔒 You → " + msg.getReceiver() : "🔒 " + msg.getSender() + " → You";
                addBubble(lbl, msg.getContent(), msg.getTimestamp(), PM_BG, isMine);
                break;
            case JOIN:   addSystemMessage("⟶ " + msg.getContent(), JOIN_COLOR);  break;
            case LEAVE:  addSystemMessage("⟵ " + msg.getContent(), LEAVE_COLOR); break;
            case SERVER: addSystemMessage("ℹ " + msg.getContent(), SERVER_COL);  break;
        }
    }

    /** Adds a rounded chat bubble aligned LEFT (received) or RIGHT (sent). */
    private void addBubble(String sender, String text, String time, Color bubbleColor, boolean isMe) {
        // Full-width row
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

        // Rounded bubble
        JPanel bubble = new RoundedPanel(18, bubbleColor);
        bubble.setLayout(new BorderLayout(0, 4));
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel nameLabel = new JLabel(sender);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLabel.setForeground(isMe ? new Color(199, 210, 254) : new Color(125, 211, 252));

        JTextArea msgText = new JTextArea(text);
        msgText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgText.setForeground(Color.WHITE);
        msgText.setOpaque(false);
        msgText.setEditable(false);
        msgText.setLineWrap(true);
        msgText.setWrapStyleWord(true);
        msgText.setFocusable(false);
        msgText.setBorder(null);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(180, 180, 210));
        timeLabel.setHorizontalAlignment(isMe ? SwingConstants.RIGHT : SwingConstants.LEFT);

        bubble.add(nameLabel, BorderLayout.NORTH);
        bubble.add(msgText,   BorderLayout.CENTER);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        // Constrain bubble width
        int bubbleWidth = Math.min(440, Math.max(120, text.length() * 9 + 80));
        bubble.setPreferredSize(new Dimension(bubbleWidth, bubble.getPreferredSize().height + 46));

        // Align: sent → RIGHT, received → LEFT
        JPanel aligner = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 4, 0));
        aligner.setOpaque(false);
        aligner.add(bubble);

        row.add(aligner, BorderLayout.CENTER);
        chatPanel.add(row);
        chatPanel.revalidate();
        scrollToBottom();
    }

    private void addSystemMessage(String text, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl.setForeground(color);
        row.add(lbl);
        chatPanel.add(row);
        chatPanel.revalidate();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = chatScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
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
                "Private message to " + target + ":",
                "Private Message", JOptionPane.PLAIN_MESSAGE);
        if (text != null && !text.trim().isEmpty())
            client.sendPrivate(target, text.trim());
    }

    private JButton styledButton(String label, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(label);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Panel with anti-aliased rounded corners. */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;
        RoundedPanel(int radius, Color color) {
            this.radius = radius; this.color = color;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
