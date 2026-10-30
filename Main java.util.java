
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// Interface
interface Borrowable {
    boolean borrowItem();
    boolean returnItem();
}

// Custom Exception
class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String message) {
        super(message);
    }
}

// Abstract Class
abstract class LibraryItem implements Borrowable {
    private String id;
    private String title;
    private boolean borrowed;

    public LibraryItem() {
        this("UNKNOWN", "Untitled", false);
    }

    public LibraryItem(String id, String title, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.borrowed = borrowed;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public abstract String getDetails();
}

// Book Class
class Book extends LibraryItem {
    public static final String CATEGORY = "BOOK";
    private String author;

    public Book() {
        super();
        author = "Unknown Author";
    }

    public Book(String id, String title, String author,
                boolean borrowed) {
        super(id, title, borrowed);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean borrowItem() {
        if (isBorrowed()) {
            return false;
        }

        setBorrowed(true);
        return true;
    }

    @Override
    public boolean returnItem() {
        if (!isBorrowed()) {
            return false;
        }

        setBorrowed(false);
        return true;
    }

    @Override
    public String getDetails() {
        return getId() + " | " + getTitle() + " | "
                + author + " | "
                + (isBorrowed() ? "Borrowed" : "Available")
                + " | " + CATEGORY;
    }
}

// Main GUI
public class Book_2 extends JFrame {

    private final List<LibraryItem> catalog = new ArrayList<>();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField;
    private final TableRowSorter<DefaultTableModel> sorter;

    public Book_2() {
        setTitle("Advanced School Library System");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Sample Books
        catalog.add(new Book(
                "B001", "The Hobbit", "J.R.R. Tolkien", false));
        catalog.add(new Book(
                "B002", "Charlotte's Web", "E.B. White", false));
        catalog.add(new Book(
                "B003", "Wonder", "R.J. Palacio", false));

        // Header
        JLabel heading = new JLabel(
                "ADVANCED SCHOOL LIBRARY SYSTEM",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        heading.setForeground(new Color(30, 70, 120));
        heading.setBorder(BorderFactory.createEmptyBorder(
                20, 10, 15, 10));
        add(heading, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "Book ID", "Title", "Author", "Status", "Category"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(
                5, 15, 5, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);

        JButton searchButton = new JButton("Search");
        JButton viewAllButton = new JButton("View All");

        searchPanel.add(new JLabel("Search ID or Title:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);

        // Action Buttons
        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 5));

        JButton borrowButton = new JButton("Borrow Book");
        JButton returnButton = new JButton("Return Book");
        JButton modulesButton = new JButton("System Modules");
        JButton exitButton = new JButton("Exit");

        borrowButton.setBackground(new Color(45, 130, 75));
        borrowButton.setForeground(Color.WHITE);

        returnButton.setBackground(new Color(40, 100, 180));
        returnButton.setForeground(Color.WHITE);

        actionPanel.add(borrowButton);
        actionPanel.add(returnButton);
        actionPanel.add(modulesButton);
        actionPanel.add(exitButton);

        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        searchButton.addActionListener(e -> searchBooks());

        searchField.addActionListener(e -> searchBooks());

        viewAllButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        borrowButton.addActionListener(e -> processBorrow());

        returnButton.addActionListener(e -> processReturn());

        modulesButton.addActionListener(e -> showModules());

        exitButton.addActionListener(e -> {
            dispose();
        });
    }

    // Display books in the table
    private void refreshTable() {
        model.setRowCount(0);

        for (LibraryItem item : catalog) {
            String author = "Unknown";

            if (item instanceof Book) {
                author = ((Book) item).getAuthor();
            }

            model.addRow(new Object[]{
                item.getId(),
                item.getTitle(),
                author,
                item.isBorrowed() ? "Borrowed" : "Available",
                Book.CATEGORY
            });
        }
    }

    // Find a book by ID or title
    private LibraryItem findItem(String query)
            throws ItemNotFoundException {

        for (LibraryItem item : catalog) {
            if (item.getId().equalsIgnoreCase(query)
                    || item.getTitle().equalsIgnoreCase(query)) {
                return item;
            }
        }

        throw new ItemNotFoundException(
                "Book not found: " + query);
    }

    // Get selected book from the table
    private LibraryItem getSelectedBook() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            String query = searchField.getText().trim();

            if (!query.isEmpty()) {
                try {
                    return findItem(query);
                } catch (ItemNotFoundException ex) {
                    JOptionPane.showMessageDialog(
                            this, ex.getMessage(),
                            "Not Found", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            }

            JOptionPane.showMessageDialog(
                    this, "Please select a book from the table.",
                    "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String id = model.getValueAt(modelRow, 0).toString();

        try {
            return findItem(id);
        } catch (ItemNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return null;
        }
    }

    // Borrow Book
    private void processBorrow() {
        LibraryItem item = getSelectedBook();

        if (item == null) {
            return;
        }

        if (item.borrowItem()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Successfully borrowed:\n"
                    + item.getTitle() + " (" + item.getId() + ")",
                    "Borrow Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This book is already borrowed.",
                    "Notice",
                    JOptionPane.WARNING_MESSAGE);
        }

        refreshTable();
    }

    // Return Book
    private void processReturn() {
        LibraryItem item = getSelectedBook();

        if (item == null) {
            return;
        }

        if (item.returnItem()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Successfully returned:\n"
                    + item.getTitle() + " (" + item.getId() + ")",
                    "Return Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This book has not been borrowed.",
                    "Notice",
                    JOptionPane.WARNING_MESSAGE);
        }

        refreshTable();
    }

    // Search by ID or title
    private void searchBooks() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(query),
                            0, 1));
        }
    }

    // Traditional Array demonstration
    private void showModules() {
        String[] modules = {
            "Core Library Framework",
            "Polymorphic Book Subsystem",
            "Exception Handling Handler"
        };

        StringBuilder result = new StringBuilder(
                "ACTIVE SYSTEM MODULES\n\n");

        for (int i = 0; i < modules.length; i++) {
            result.append("Module [")
                  .append(i)
                  .append("]: ")
                  .append(modules[i])
                  .append("\n");
        }

        JOptionPane.showMessageDialog(
                this, result.toString(),
                "System Modules",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Book_2 app = new Book_2();
            app.setVisible(true);
        });
    }
}
