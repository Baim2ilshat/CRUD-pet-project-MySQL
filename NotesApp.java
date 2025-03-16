import java.sql.*;
import java.util.Scanner;

public class NotesApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/notes_db";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";
    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Hello, it's a small app for notes");

        // Establish connection
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Successfully connected to your database!");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            return;
        }

        // User interaction loop
        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("1) Add new note");
            System.out.println("2) Search notes by tag");
            System.out.println("3) Search in notes by text");
            System.out.println("4) Show all notes");
            System.out.println("5) Delete note by ID");
            System.out.println("Type 'e' to exit.");

            String choice = scanner.nextLine();

            if (choice.equals("e")) {
                break;
            }

            switch (choice) {
                case "1":
                    addNote();
                    break;
                case "2":
                    searchByTag();
                    break;
                case "3":
                    searchInNotes();
                    break;
                case "4":
                    showAllNotes();
                    break;
                case "5":
                    deleteNoteById();
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid number (1-5).");
            }
        }

        // Close connection
        System.out.println("Finishing...");
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }

   
    private static void addNote() {
        System.out.println("Type here tag and text of note:");
        String input = scanner.nextLine();
        String[] parts = input.split(" ", 2);

        if (parts.length < 2) {
            System.out.println("Invalid input. Please provide both a tag and a note.");
            return;
        }

        String tag = parts[0];
        String note = parts[1];

        String sql = "INSERT INTO notes (tag, note) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tag);
            stmt.setString(2, note);
            stmt.executeUpdate();
            System.out.println("✅ Note added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding note: " + e.getMessage());
        }
    }

    
    private static void searchByTag() {
        System.out.println("Type here the tag by which you want to search:");
        String tag = scanner.nextLine();

        String sql = "SELECT * FROM notes WHERE tag LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + tag + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                printNoteRow(rs);
                found = true;
            }

            if (!found) System.out.println("No matches found.");
        } catch (SQLException e) {
            System.out.println("❌ Error searching notes: " + e.getMessage());
        }
    }


    private static void searchInNotes() {
        System.out.println("Type here the text you want to search for in notes:");
        String text = scanner.nextLine();

        String sql = "SELECT * FROM notes WHERE note LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + text + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                printNoteRow(rs);
                found = true;
            }

            if (!found) System.out.println("No matches found.");
        } catch (SQLException e) {
            System.out.println("❌ Error searching in notes: " + e.getMessage());
        }
    }

   
    private static void showAllNotes() {
        String sql = "SELECT * FROM notes";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                printNoteRow(rs);
                found = true;
            }

            if (!found) System.out.println("No notes found.");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching notes: " + e.getMessage());
        }
    }

    
    private static void deleteNoteById() {
        System.out.println("Type the ID of the note you want to delete:");
        String input = scanner.nextLine();

        try {
            int noteId = Integer.parseInt(input);

            // Check if the note exists
            String checkSql = "SELECT * FROM notes WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, noteId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("⚠️ No note found with ID " + noteId);
                    return;
                }
            }

            // Delete the note
            String deleteSql = "DELETE FROM notes WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, noteId);
                deleteStmt.executeUpdate();
                System.out.println("✅ Note with ID " + noteId + " has been deleted.");
            }

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input. Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting note: " + e.getMessage());
        }
    }

    // Helper method to print a note row
    private static void printNoteRow(ResultSet rs) throws SQLException {
        System.out.println("ID: " + rs.getInt("id") +
                ", Created At: " + rs.getTimestamp("created_at") +
                ", Tag: " + rs.getString("tag") +
                ", Note: " + rs.getString("note"));
    }
}