package ticketbooking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

// Small helpers so every screen of the system can use a simple JOptionPane
// (no big windows). Used by Booking Records, Movie Management,
// Account Security, Sales Report and Ticket Booking.
public class Popup {

    // Shows a menu of buttons. Returns the number of the button that was
    // clicked (0 = first button), or -1 if the window was closed.
    public static int choose(Component parent, String title,
                             String message, String[] buttons) {
        JPanel box = withHeader(title, plainText(message));
        return JOptionPane.showOptionDialog(parent, box, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, buttons, buttons[0]);
    }
    // Shows a drop-down list. Returns the number of the chosen item
    // (0 = first item), or -1 if the user cancelled.
    public static int pick(Component parent, String title,
                           String message, String[] items) {
        Object chosen = JOptionPane.showInputDialog(parent, message, title,
                JOptionPane.QUESTION_MESSAGE, null, items, items[0]);

        if (chosen == null) {
            return -1;
        }
        for (int i = 0; i < items.length; i++) {   // linear search
            if (items[i] == chosen) {   // the dialog gives back the same item from the list
                return i;
            }
        }
        return -1;
    }
    // Shows long text (a table or a list) in a scrolling message box
    public static void showText(Component parent, String title, String text) {
        JOptionPane.showMessageDialog(parent, withHeader(title, textBox(text)),
                title, JOptionPane.PLAIN_MESSAGE);
    }
    // Same as showText, but with a row of cards (or anything else) above the text
    public static void showReport(Component parent, String title,
                                  JComponent top, String text) {
        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.add(top, BorderLayout.NORTH);
        body.add(textBox(text), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(parent, withHeader(title, body),
                title, JOptionPane.PLAIN_MESSAGE);
    }
    // Same as showText, but with buttons under the text (like the menu).
    // Returns the number of the clicked button, or -1 if the window was closed.
    public static int showTextWithButtons(Component parent, String title,
                                          String text, String[] buttons) {
        JPanel box = withHeader(title, textBox(text));
        return JOptionPane.showOptionDialog(parent, box, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, buttons, buttons[0]);
    }
    // Same idea as showTextWithButtons, but the list is shown in a JTable
    // (real rows and columns) with a small note under it (example: the total).
    // widths = the width of every column. Returns the number of the clicked
    // button, or -1 if the window was closed.
    public static int showTableWithButtons(Component parent, String title,
                                           String[] columns, Object[][] rows,
                                           int[] widths, String footer,
                                           String[] buttons) {
        return showTableWithButtons(parent, title, null, columns, rows,
                widths, footer, buttons);
    }
    // Same, with something above the table (example: a row of cards or a note)
    public static int showTableWithButtons(Component parent, String title,
                                           JComponent top, String[] columns,
                                           Object[][] rows, int[] widths,
                                           String footer, String[] buttons) {
        JPanel box = withHeader(title, tableBody(top, columns, rows, widths, footer));
        return JOptionPane.showOptionDialog(parent, box, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, buttons, buttons[0]);
    }
    // A table with only an OK button (used by the Sales Report)
    public static void showTable(Component parent, String title, JComponent top,
                                 String[] columns, Object[][] rows,
                                 int[] widths, String footer) {
        JOptionPane.showMessageDialog(parent,
                withHeader(title, tableBody(top, columns, rows, widths, footer)),
                title, JOptionPane.PLAIN_MESSAGE);
    }
    // The inside of a table box: [top] + the JTable in a scroll pane + [footer]
    private static JPanel tableBody(JComponent top, String[] columns,
                                    Object[][] rows, int[] widths, String footer) {
        JTable table = new JTable(rows, columns);
        Theme.styleTable(table);

        int totalWidth = 0;
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            totalWidth = totalWidth + widths[i];
        }

        JScrollPane scroll = new JScrollPane(table);
        Theme.styleScrollPane(scroll);
        scroll.setPreferredSize(new Dimension(Math.max(900, totalWidth), 380));

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        if (top != null) {
            body.add(top, BorderLayout.NORTH);
        }
        body.add(scroll, BorderLayout.CENTER);
        body.add(Theme.makeLabel(footer, 14, true, Theme.TEXT), BorderLayout.SOUTH);
        return body;
    }
    // Asks for one line of text. Returns the trimmed text, or null if cancelled.
    public static String ask(Component parent, String title, String message) {
        String typed = JOptionPane.showInputDialog(parent, message, title,
                JOptionPane.QUESTION_MESSAGE);
        if (typed == null) {
            return null;
        }
        return typed.trim();
    }
    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }
    public static void warn(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }
    public static boolean yes(Component parent, String title, String message) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    // Wraps the content of a pop-up box. The title is only shown in the
    // title of the window (frame), so there is no extra bar inside the box.
    public static JPanel withHeader(String title, JComponent body) {
        JPanel box = new JPanel(new BorderLayout(0, 10));
        box.setBackground(Theme.BACKGROUND);
        box.add(body, BorderLayout.CENTER);
        return box;
    }
    // The scrolling text box used by showText and showTextWithButtons
    private static JScrollPane textBox(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBackground(Theme.PANEL);
        area.setForeground(Theme.TEXT);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(900, 380));
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE, 1));
        return scroll;
    }
    // A few lines of plain text for the small boxes
    private static JTextArea plainText(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 15));
        area.setForeground(Theme.TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        return area;
    }
    // Cuts a long text so the columns of a table stay straight
    public static String fit(String text, int width) {
        if (text.length() > width) {
            return text.substring(0, width - 1) + "~";
        }
        return text;
    }
}
