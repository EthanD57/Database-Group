import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class DBotify {
    private static int QUERY_TIMEOUT = 30;

    private static Connection connect(String username, String password) {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("escapeSyntaxCallMode", "callIfNoReturn");
        props.setProperty("password", password);

        try(Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", props)) {
            conn.setSchema("dbotify");

            return conn;
        } catch(SQLException e) {
            handleError(e);
            return null;
        }
    }

    private static void addRelease(Connection conn, String title, Date releaseDate, String label) {
        try (CallableStatement st = conn.prepareCall("{ CALL addRelease(?, ?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, title);
            st.setDate(2, releaseDate);
            st.setString(3, label);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void newListener(Connection conn, String profileName, String billingStreet, String billingCity,
    String billingState, String billingZipcode, String email) {
        try (CallableStatement st = conn.prepareCall("{ CALL newListener(?, ?, ?, ?, ?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, profileName);
            st.setString(2, billingStreet);
            st.setString(3, billingCity);
            st.setString(4, billingState);
            st.setString(5, billingZipcode);
            st.setString(6, email);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void createPlaylist(Connection conn, String title, int listener) {
        try (CallableStatement st = conn.prepareCall("{ CALL createPlaylist(?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, title);
            st.setInt(2, listener);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void addArtistToRelease(Connection conn, String artistName, int release) {
        try (CallableStatement st = conn.prepareCall("{ CALL addArtistToRelease(?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, artistName);
            st.setInt(2, release);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
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

    private static void handleError(SQLException err) {
        System.err.println("The following error occurred:");
        System.err.println("Message = " + err.getMessage());
        System.err.println("SQLState = " + err.getSQLState());
        System.err.println("SQL Code = " + err.getErrorCode());
    }
    
    public static void main(String[] args) {
        int menu = -1;
        Connection conn = null;
        Scanner sc = new Scanner(System.in);

        while(menu != 25) {
            menu = sc.nextInt();
            sc.nextLine();

            switch(menu) {
                case 1:
                    System.out.println("Establishing a connection to a DB...\n");

                    System.out.println("Enter your username for the DB: ");
                    String username = sc.nextLine();
                    System.out.println("Enter your password for the DB: ");
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
                    System.out.println("Adding a release...\n");

                    System.out.println("Enter the title of the release: ");
                    String releaseTitle = sc.nextLine();
                    System.out.println("Enter the date of the release in the format YYYY-MM-DD: ");
                    Date releaseDate = Date.valueOf(sc.nextLine());
                    System.out.println("Enter the label name of the release: ");
                    String label = sc.nextLine();

                    addRelease(conn, releaseTitle, releaseDate, label);
                    break;
                case 3:
                    System.out.println("Creating a new listener...\n");

                    System.out.println("Enter the name of the profile: ");
                    String profileName = sc.nextLine();
                    System.out.println("Enter the billing street address: ");
                    String billingStreet = sc.nextLine();
                    System.out.println("Enter the billing city: ");
                    String billingCity = sc.nextLine();
                    System.out.println("Enter the billing state abbreviation (i.e. Pennsylvania = PA): ");
                    String billingState = sc.nextLine();
                    System.out.println("Enter the billing zipcode: ");
                    String billingZipcode = sc.nextLine();
                    System.out.println("Enter the email associated with the profile: ");
                    String profileEmail = sc.nextLine();

                    newListener(conn, profileName, billingStreet, billingCity, billingState, billingZipcode, profileEmail);
                    break;
                case 4:
                    System.out.println("Creating playlist...\n");

                    System.out.println("Enter the title of the playlist: ");
                    String playlistTitle = sc.nextLine();
                    System.out.println("Enter the listenerID of the listener: ");
                    int listenerID = sc.nextInt();
                    sc.nextLine();

                    createPlaylist(conn, playlistTitle, listenerID);
                    break;
                case 5:
                    System.out.println("Adding artist to release...\n");

                    System.out.println("Enter the name of the artist: ");
                    String artistName = sc.nextLine();
                    System.out.println("Enter the releaseID of the release: ");
                    int releaseID = sc.nextInt();
                    sc.nextLine();
                    
                    addArtistToRelease(conn, artistName, releaseID);
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