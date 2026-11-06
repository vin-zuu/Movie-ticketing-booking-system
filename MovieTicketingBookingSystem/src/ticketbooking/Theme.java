package ticketbooking;

import java.awt.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.JTableHeader;

// Shared colors and simple styling methods.
// Every window uses this so the whole system looks the same.
public class Theme {

    // Simple light colors (white and grey with one blue color and one soft teal highlight)
    public static final Color BACKGROUND = new Color(243, 245, 247);   // window background
    public static final Color PANEL      = Color.WHITE;                // cards and panels
    public static final Color HEADER     = new Color(52, 84, 114);     // top bar
    public static final Color PRIMARY    = new Color(38, 120, 160);    // main buttons
    public static final Color ACCENT     = new Color(32, 138, 133);    // highlight (soft teal, goes well with blue)
    public static final Color DANGER     = new Color(190, 60, 55);     // delete / cancel buttons
    public static final Color NEUTRAL    = new Color(224, 228, 233);   // plain buttons
    public static final Color LINE       = new Color(208, 213, 220);   // borders
    public static final Color TITLE      = new Color(42, 70, 98);      // headings on light panels
    public static final Color TEXT       = new Color(40, 44, 52);
    public static final Color SOFT_TEXT  = new Color(105, 112, 122);
    public static final Color FIELD      = Color.WHITE;

    // Peso sign
    public static final String PESO = "\u20B1";
    // Shows a number as money, example: ₱250.00
    public static String peso(double amount) {
        long cents = Math.round(amount * 100);   // 250.5 -> 25050
        long pesos = cents / 100;
        long left = cents % 100;

        String centsText = "" + left;
        if (left < 10) {
            centsText = "0" + left;   // 5 -> 05
        }
        return PESO + pesos + "." + centsText;
    }
    // Shows a time (in milliseconds) as date and time only, example: Oct 22, 2026  01:32 PM
    public static String dateTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
        return format.format(new Date(time));
    }
    // Plain text that wraps to the next line by itself (no HTML tags needed)
    public static JTextArea makeNote(String text, int size, Color color) {
        JTextArea note = new JTextArea(text);
        note.setFont(new Font("SansSerif", Font.PLAIN, size));
        note.setForeground(color);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setEditable(false);
        note.setFocusable(false);
        note.setOpaque(false);
        return note;
    }

    // A bold label for dialog windows (no HTML tags needed)
    public static JLabel makeBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    // Creates a label with a font and color
    public static JLabel makeLabel(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        if (bold) {
            label.setFont(new Font("SansSerif", Font.BOLD, size));
        } else {
            label.setFont(new Font("SansSerif", Font.PLAIN, size));
        }
        label.setForeground(color);
        return label;
    }
    // Styles a button (use PRIMARY/ACCENT/DANGER/NEUTRAL for the background)
    public static void styleButton(JButton button, Color background, Color textColor) {
        button.setBackground(background);
        button.setForeground(textColor);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }
    // Styles a text field, combo box or spinner
    public static void styleField(JComponent field) {
        field.setBackground(FIELD);
        field.setForeground(Color.BLACK);
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
    }
    // Styles a table
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setGridColor(LINE);
        table.setSelectionBackground(new Color(204, 236, 233));
        table.setSelectionForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);   // cells cannot be edited

        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 36));
    }
    // Styles the scroll pane that holds a table
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(PANEL);
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE, 1));
    }
    // Top bar used by every window: title on the left, small text on the right
    public static JPanel createHeader(String title, String rightText) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, ACCENT),
                BorderFactory.createEmptyBorder(16, 25, 16, 25)));

        header.add(makeLabel(title, 26, true, Color.WHITE), BorderLayout.WEST);
        header.add(makeLabel(rightText, 15, false, Color.WHITE), BorderLayout.EAST);
        return header;
    }
    // Small information card, example: TOTAL BOOKINGS / 12
    public static JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        card.add(makeLabel(title, 12, true, SOFT_TEXT));
        card.add(makeLabel(value, 24, true, PRIMARY));
        return card;
    }
    // Shows a movie poster picture on a label.
    // If the picture is missing, it shows "NO POSTER" instead.
    public static void showPoster(JLabel label, String posterFile, int width, int height) {
        ImageIcon icon = null;

        if (posterFile != null && !posterFile.trim().isEmpty()) {
            ImageIcon original = new ImageIcon(posterFile.trim());
            if (original.getIconWidth() > 0) {
                Image smallImage = original.getImage().getScaledInstance(
                        width, height, Image.SCALE_SMOOTH);
                icon = new ImageIcon(smallImage);
            }
        }

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(width, height));

        if (icon == null) {
            label.setIcon(null);
            label.setText("NO POSTER");
            label.setForeground(SOFT_TEXT);
        } else {
            label.setIcon(icon);
            label.setText("");
        }
    }
}
