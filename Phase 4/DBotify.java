import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class DBotify {
    private static Connection connect(String username, String password) {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("escapeSyntaxCallMode", "callIfNoReturn");
        props.setProperty("password", password);

        try(Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", props)) {
            conn.setSchema("dbotify");

            return conn;
        } catch(SQLException e) {
            System.err.println("Message = " + e.getMessage());
            System.err.println("SQLState = " + e.getSQLState());
            System.err.println("SQL Code = " + e.getErrorCode());
            return null;
        }
    }

    private static void addRelease() {
        // TODO: write function and modify inputs/outputs
    }

    private static void newListener() {
        // TODO: write function and modify inputs/outputs
    }

    private static void createPlaylist() {
        // TODO: write function and modify inputs/outputs
    }

    private static void addArtistToRelease() {
        // TODO: write function and modify inputs/outputs
    }

    private static void addSongToRelease() {
        // TODO: write function and modify inputs/outputs
    }

    private static void addSongToPlaylist() {
        // TODO: write function and modify inputs/outputs
    }

    private static void startSession() {
        // TODO: write function and modify inputs/outputs
    }

    private static void listenToSong() {
        // TODO: write function and modify inputs/outputs
    }

    private static void listenToPlaylist() {
        // TODO: write function and modify inputs/outputs
    }

    private static void endSession() {
        // TODO: write function and modify inputs/outputs
    }

    private static void deleteListener() {
        // TODO: write function and modify inputs/outputs
    }

    private static void clearListeningHistory() {
        // TODO: write function and modify inputs/outputs
    }

    private static void removeSong() {
        // TODO: write function and modify inputs/outputs
    }

    private static void deletePlaylist() {
        // TODO: write function and modify inputs/outputs
    }

    private static void listPlaylistsWithGenre() {
        // TODO: write function and modify inputs/outputs
    }

    private static void searchSongs() {
        // TODO: write function and modify inputs/outputs
    }

    private static void lookupArtist() {
        // TODO: write function and modify inputs/outputs
    }

    private static void displayListeningHistory() {
        // TODO: write function and modify inputs/outputs
    }

    private static void rankArtists() {
        // TODO: write function and modify inputs/outputs
    }

    private static void displayGenreHistory() {
        // TODO: write function and modify inputs/outputs
    }

    private static void dbotifyWrapped() {
        // TODO: write function and modify inputs/outputs
    }

    private static void priceIncrease() {
        // TODO: write function and modify inputs/outputs
    }

    private static void connectedArtists() {
        // TODO: write function and modify inputs/outputs
    }

    private static void exit() {
        // TODO: write function and modify inputs/outputs
    }
    
    public static void main(String[] args) {
        int menu = -1;
        Connection conn = null;
        Scanner sc = new Scanner(System.in);

        while(menu != 25) {
            menu = sc.nextInt();

            switch(menu) {
                case 1:
                    System.out.println("Enter your username: ");
                    String username = sc.nextLine();
                    System.out.println("Enter your password: ");
                    String password = sc.nextLine();

                    conn = connect(username, password);

                    if(conn == null) {
                        System.out.println("Connection successfully established!");
                    }
                    else {
                        System.out.println("Connection failed.");
                    }
                    break;
                case 2:
                    // TODO: modify for function
                    addRelease();
                    break;
                case 3:
                    // TODO: modify for function
                    newListener();
                    break;
                case 4:
                    // TODO: modify for function
                    createPlaylist();
                    break;
                case 5:
                    // TODO: modify for function
                    addArtistToRelease();
                    break;
                case 6:
                    // TODO: modify for function
                    addSongToRelease();
                    break;
                case 7:
                    // TODO: modify for function
                    addSongToPlaylist();
                    break;
                case 8:
                    // TODO: modify for function
                    startSession();
                    break;
                case 9:
                    // TODO: modify for function
                    listenToSong();
                    break;
                case 10:
                    // TODO: modify for function
                    listenToPlaylist();
                    break;
                case 11:
                    // TODO: modify for function
                    endSession();
                    break;
                case 12:
                    // TODO: modify for function
                    deleteListener();
                    break;
                case 13:
                    // TODO: modify for function
                    clearListeningHistory();
                    break;
                case 14:
                    // TODO: modify for function
                    removeSong();
                    break;
                case 15:
                    // TODO: modify for function
                    deletePlaylist();
                    break;
                case 16:
                    // TODO: modify for function
                    listPlaylistsWithGenre();
                    break;
                case 17:
                    // TODO: modify for function
                    searchSongs();
                    break;
                case 18:
                    // TODO: modify for function
                    lookupArtist();
                    break;
                case 19:
                    // TODO: modify for function
                    displayListeningHistory();
                    break;
                case 20:
                    // TODO: modify for function
                    rankArtists();
                    break;
                case 21:
                    // TODO: modify for function
                    displayGenreHistory();
                    break;
                case 22:
                    // TODO: modify for function
                    dbotifyWrapped();
                    break;
                case 23:
                    // TODO: modify for function
                    priceIncrease();
                    break;
                case 24:
                    // TODO: modify for function
                    connectedArtists();
                    break;
                case 25:
                    // TODO: modify for function
                    exit();
                    break;
                default:
                    System.out.println("Invalid menu selection");
            }
        }
        
        sc.close();
    }
}