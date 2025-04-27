package src.main.java.org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    private static void addSongToRelease(Connection conn) {
        Scanner sc = new Scanner(System.in);

        try (PreparedStatement st = conn.prepareStatement("SELECT * FROM RELEASES;")){
            List<Integer> releases = new ArrayList<Integer>();
            st.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                System.out.println("ReleaseID: " + rs.getInt("releaseID") + " | title: " + rs.getString("title")
                + " | releaseDate: " + rs.getDate("releaseDATE") + " | label: " + rs.getString("label"));
                releases.add(rs.getInt("releaseID"));
            }

            if(releases.isEmpty()) {
                System.out.println("\nNo Releases have been written");
                sc.close();
                return;
            }

            System.out.println("\nEnter the releaseID you want to add a song to or type -1 to go to the menu: ");
            int releaseID = sc.nextInt();
            sc.nextLine();

            if(releaseID == -1) {
                System.out.println("Returning to menu...");
                sc.close();
                return;
            }
            else if(!releases.contains(releaseID)) {
                System.out.println("Invalid releaseID, returning to menu...");
                sc.close();
                return;
            }
            else {
                int songID = 0;
                while(true) {
                    System.out.println("\nEnter a songID to add to the release or type -1 to go to the menu: ");
                    sc.nextInt();
                    sc.nextLine();

                    if(songID == -1) {
                        System.out.println("Returning to menu...");
                        sc.close();
                        return;
                    }

                    System.out.println("Adding song to release...");
                    CallableStatement cs = conn.prepareCall("{ CALL addSongToRelease(?, ?) }");
                    cs.setQueryTimeout(QUERY_TIMEOUT);
                    cs.setInt(1, releaseID);
                    cs.setInt(2, songID);
                }
            }
        } catch(SQLException e) {
            sc.close();
            handleError(e);
        }
    }

    private static void addSongToPlaylist(Connection conn, String playlistTitle, int listener, int song) {
        try (CallableStatement st = conn.prepareCall("{ CALL addSongToPlaylist(?, ?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, playlistTitle);
            st.setInt(2, listener);
            st.setInt(3, song);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void startSession(Connection conn, int listenerID, Timestamp startTime) {
        try (CallableStatement st = conn.prepareCall("{ CALL startSession(?, ?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setInt(1, listenerID);
            st.setTimestamp(2, startTime);
            st.execute();
        } catch(SQLException e) {
            handleError(e);
        }
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
            System.out.println("MENU OPTIONS");
            System.out.println( "1: Connect to DB\n" +
                                "2: Add a release\n" +
                                "3: Create a new listener\n" +
                                "4: Create a playlist\n" +
                                "5: Add an artist to a release\n" +
                                "6: Add song(s) to a release\n" +
                                "7: Add a song to a playlist\n" +
                                "8: Start a session\n" + 
                                "9: Listen to a song or multiple songs\n" + 
                                "10: Listen to a playlist\n" +
                                "11: End a session\n" +
                                "12: Delete a listener\n" +
                                "13: Clear a listener's listening history\n" +
                                "14: Remove a song\n" +
                                "15: Delete a playlist or all playlists\n" + 
                                "16: Find all of a listener's playlists that have a song with a specific genre\n" +
                                "17: Search for songs with a title or subtitle that contain a pattern\n" +
                                "18: Find all of an artist's songs that are part of a release\n" +
                                "19: Display a listener's listening history between two dates\n" +
                                "20: Rank of artists\n" +
                                "21: Display songs with a genre that have been listened to in a specified number of months\n" +
                                "22: DBotify Wrapped\n" +
                                "23: Impact of price increases on most populous zipcode in each state\n" +
                                "24: Finds a path between two artists that are at most 3 hops away\n" +
                                "25: Exit\n");
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
                    addSongToRelease(conn);
                    break;
                case 7:
                    System.out.println("Adding song to playlist...\n");

                    System.out.println("Enter the title of the playlist: ");
                    String songPlaylistTitle = sc.nextLine();
                    System.out.println("Enter the listenerID of the listener: ");
                    int songListenerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the songID of the song: ");
                    int songID = sc.nextInt();
                    sc.nextLine();

                    addSongToPlaylist(conn, songPlaylistTitle, songListenerID, songID);
                    break;
                case 8:
                    System.out.println("Starting a new session...\n");

                    System.out.println("Enter the listenerID of the listener: ");
                    int startListenerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the starting timestamp of the session in the format YYYY-MM-DD HH24:MI:SS : ");
                    Timestamp startTimestamp = Timestamp.valueOf(sc.nextLine());

                    startSession(conn, startListenerID, startTimestamp);
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