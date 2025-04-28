package Phase_4;
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

        try{

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?currentSchema=dbotify\n", props);

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

    private static void listenToSong(Connection conn, int ListernerID) { //9
        Scanner sc = new Scanner(System.in);
        try(PreparedStatement checkSt = conn.prepareStatement("SELECT COUNT(*) FROM SONGS;")){
            checkSt.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSt.executeQuery();
            r.next();
            if(r.getInt(1) == 0){
                System.out.println("No songs to listen to.");
                return;
            }
            while(true){
                System.out.println("\nEnter a songID to listen to or type -1 to go to the menu: ");
                int songID = sc.nextInt();
                sc.nextLine();
                if(songID == -1) {
                    System.out.println("Returning to menu...");
                    sc.close();
                    return;
                }
                PreparedStatement songSt = conn.prepareStatement("SELECT * FROM SONGS WHERE ID = ?;");
                songSt.setQueryTimeout(QUERY_TIMEOUT);
                songSt.setInt(1, songID);
                ResultSet songR = songSt.executeQuery();
                if(!songR.next()){
                    System.out.println("Inputed invalid songID: " + songID + ". Please try again.");
                    continue;
                }
                CallableStatement s = conn.prepareCall("{ CALL listenToSong(?, ?) }");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setInt(1, songID);
                s.setInt(2, ListernerID);
                s.execute();
                System.out.println("Successfully added song " + songID + "to session.");
            }
        } catch(SQLException e){
            sc.close();
            handleError(e);
        }
    }

    private static void listenToPlaylist(Connection conn, int endID) { //10
        Scanner sc = new Scanner(System.in);
        try(PreparedStatement checkSt = conn.prepareStatement("SELECT title FROM PLAYLISTS WHERE listener = ?;")){
            checkSt.setQueryTimeout(QUERY_TIMEOUT);
            checkSt.setInt(1, endID);
            ResultSet r_1 = checkSt.executeQuery();
            if (!r_1.next()) {
                System.out.println("No playlists to listen to");
                return;
            }
            ResultSet r_2 = checkSt.executeQuery();
            while(r_2.next()){
                String playlist = r_2.getString("title");
                System.out.println("Do you want to listen to your " + playlist + " playlist?");
                String reply = "";
                while(true){
                    reply = sc.nextLine().trim();
                    if (reply.equalsIgnoreCase("Yes") || reply.equalsIgnoreCase("No") || reply.equalsIgnoreCase("Exit")) {
                        break;
                    }else{
                        System.out.println("Not valid response, enter yes, no, or exit.");
                    }
                }
                if (reply.equalsIgnoreCase("Yes")) {
                    CallableStatement s = conn.prepareCall("{ CALL listenToPlaylist(?, ?) }");
                    s.setQueryTimeout(QUERY_TIMEOUT);
                    s.setString(1, playlist);
                    s.setInt(2, endID);
                    s.execute();
                    System.out.println("Successfully listened to " + playlist + ".");
                } else if (reply.equals("Exit")) {
                    System.out.println("Returning to menu...");
                    sc.close();
                    return;
                }
            }
            System.out.println("That was the last playlist. Returning to menu...");
            sc.close();
            return;

        }catch(SQLException e){
            sc.close();
            handleError(e);
        }

    }

    private static void endSession(Connection conn, int endID) { //11
        Scanner sc = new Scanner(System.in);
        String profile= "";
        try(PreparedStatement checkSt = conn.prepareStatement("SELECT profileName FROM LISTENERS WHERE listenerID = ?")){
            checkSt.setQueryTimeout(QUERY_TIMEOUT);
            checkSt.setInt(1, endID);
            ResultSet r_1 = checkSt.executeQuery();
            if (!r_1.next()) {
                System.out.println("Not a valid listener.");
                sc.close();
                return;
            }
            profile = r_1.getString("profileName");
            CallableStatement st = conn.prepareCall("{ CALL endSession(?) }");
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setInt(1, endID);
            st.execute();
            System.out.println("Session ended successfully for " + profile + ".");
        }catch(SQLException e){
            if(e.getSQLState().equals("P0002")){
                System.out.println("No active sessions for " + profile + ".");
            }else{
                sc.close();
                handleError(e);
            }
        }
    }

    private static void deleteListener(Connection conn, int removeID) { //12
        try (CallableStatement st = conn.prepareCall("{ CALL deleteListener(?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, removeID);
            st.execute();
            System.out.println("Successfully removed listener.");
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void clearListeningHistory(Connection conn, int clearID) { //13
        try (CallableStatement st = conn.prepareCall("{ CALL clearListeningHistory(?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setString(1, clearID);
            st.execute();
            System.out.println("Successfully cleared history of listener.");
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void removeSong(Connection conn, int removeSongID) { //14
        try (CallableStatement st = conn.prepareCall("{ CALL removeSong(?) }")){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setInt(1, removeSongID);
            st.execute();
            System.out.println("Successfully removed song.");
        } catch(SQLException e) {
            handleError(e);
        }
    }

    private static void deletePlaylist(Connection conn, int deletePlaylistListenerID, boolean all) { //15
        Scanner sc = new Scanner(System.in);
        try (PreparedStatement st = conn.prepareStatement("SELECT title FROM PLAYLISTS WHERE listener = ?");){
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setInt(1, deletePlaylistListenerID);
            ResultSet r1 = st.executeQuery();
            if (!r1.next()) {
                System.out.println("No playlists to remove");
                sc.close();
                return;
            }
            ResultSet r2 = st.executeQuery();
            if(all){
                while(r2.next()){
                    CallableStatement allSt = conn.prepareCall("{ CALL deletePlaylist(?, ?) }");
                    allSt.setQueryTimeout(QUERY_TIMEOUT);
                    allSt.setString(1, r2.getString("title"));
                    allSt.setInt(2, deletePlaylistListenerID);
                    allSt.execute();
                }
                System.out.println("Successfully deleted all playlists.");
            }
            else{
                System.out.println("Playlists:");
                while (r2.next()) {
                    System.out.println(r2.getString("title"));
                }
                System.out.println("Choose a playlist to delete:");
                String deletePlaylistTitle = sc.nextLine();
                sc.nextLine();
                CallableStatement oneSt = conn.prepareCall("{ CALL deletePlaylist(?, ?) }");
                oneSt.setQueryTimeout(QUERY_TIMEOUT);
                oneSt.setString(1, deletePlaylistTitle);
                oneSt.setInt(2, deletePlaylistListenerID);
                oneSt.execute();
                System.out.println("Successfully deleted one playlist.");
            }
        } catch(SQLException e) {
            sc.close();
            handleError(e);
        }
    }

    private static void listPlaylistsWithGenre(Connection conn, int listenerID, String genre) { //16
        try (CallableStatement st = conn.prepareCall("SELECT * FROM listPlaylistsWithGenre(?, ?)")) {
            st.setQueryTimeout(QUERY_TIMEOUT);
            st.setInt(1, listenerID);
            st.setString(2, genre);
            ResultSet r = st.executeQuery();
            System.out.println("Displaying playlists with " + genre +" genre");
            int count = 1;
            while(r.next()){
                System.out.println(count + ":" + r.getString("playlistTitle") + ", " + r.getInt("playlistListener") + ", " + r.getDate("dateOfCreation"));
                count++;
            }
        }catch(SQLException e) {
            handleError(e);
        }
    }
 //------------------------------------------------------------
    private static void searchSongs(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM SONGS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No songs available to search");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter a song title or subtitle (up to 30 characters) to search for, or type -1 to go to the menu: ");
                System.out.println("For Search Help, type 'help'");
                String searchTerm = input.nextLine();

                if(searchTerm.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }

                if(searchTerm.equalsIgnoreCase("help")) {
                    System.out.println("\nSearch finds exact matches for words unless '%' is used to indicate fuzzy matches.");
                    System.out.println("'%' indicates where there may be extra characters.");
                    System.out.println("You can use % in cases such as: 'The%' and '%C' to find fuzzy matches like 'The Beetles' and 'AC/DC'.");
                    continue;
                }

                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM searchSongs(?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setString(1, searchTerm.substring(0, Math.min(30, searchTerm.length())));
                boolean hasResults = s.execute();
                if(!hasResults) {
                    System.out.println("No results found.");
                    continue;
                }
                //Print out results
                ResultSet searchResults = s.getResultSet();
                while(searchResults.next()){
                    System.out.println(
                            "SongID: " + searchResults.getInt("songID") +
                            " | Title: " + searchResults.getString("title") +
                            " | Subtitle: " + searchResults.getString("subtitle") +
                            " | Duration: " + searchResults.getString("duration"));
                }
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void lookupArtist(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM ARTISTS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No artists available to search");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter a artist's name (up to 30 characters) to show all songs by them, or type -1 to go to the menu: ");
                String searchTerm = input.nextLine();

                if(searchTerm.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }

                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM lookupArtist(?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setString(1, searchTerm.substring(0, Math.min(30, searchTerm.length())));
                boolean hasResults = s.execute();
                if(!hasResults) {
                    System.out.printf("No results found for artist '%s'", searchTerm);
                    continue;
                }
                //Print out results
                ResultSet searchResults = s.getResultSet();
                while(searchResults.next()){
                    System.out.println(
                            "SongID: " + searchResults.getInt("songID") +
                                    " | Title: " + searchResults.getString("title") +
                                    " | Subtitle: " + searchResults.getString("subtitle") +
                                    " | Duration: " + searchResults.getString("duration"));
                }
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void displayListeningHistory(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM SESSIONS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Listening History available to display");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter a start date, end date, and listener ID to show listening history, or type -1 to go to the menu: ");
                System.out.println("Format: YYYY-MM-DD YYYY-MM-DD <integer>");
                String searchString = input.nextLine();

                if(searchString.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }
                String[] splitSearchString = searchString.split(" ");
                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM displayListeningHistory(?, ?, ?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setDate(1, Date.valueOf(splitSearchString[0]));
                s.setDate(2, Date.valueOf(splitSearchString[1]));
                s.setInt(3, Integer.parseInt(splitSearchString[2]));
                boolean hasResults = s.execute();
                if(!hasResults) {
                    System.out.println("No results found.");
                    continue;
                }
                //Print out results
                ResultSet searchResults = s.getResultSet();
                while(searchResults.next()){
                    System.out.println(
                            "SongID: " + searchResults.getInt("songID") +
                                    " | Title: " + searchResults.getString("title") +
                                    " | Subtitle: " + searchResults.getString("subtitle") +
                                    " | Duration: " + searchResults.getString("duration"));
                }
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void rankArtists(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM ARTISTS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Artists to Rank");
                return;
            }
            checkSongs.close();

            PreparedStatement s = conn.prepareStatement("SELECT * FROM rankArtists()");
            s.setQueryTimeout(QUERY_TIMEOUT);
            s.execute();

            //Print out results
            ResultSet searchResults = s.getResultSet();
            while(searchResults.next()){
                System.out.println(
                                "Artist Name: " + searchResults.getString("artistName") +
                                " | Listens: " + searchResults.getInt("timesListened") +
                                " | Rank: " + searchResults.getInt("rank"));
            }
            s.close();
            System.out.println("Press enter to return to the menu...");
            input.nextLine();
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void displayGenreHistory(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM SONGS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Songs Available to Display");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter a Genre and Time (T) in Months to Search For All Songs of That Genre That Were Listened to In 'T' Months, or type -1 to go to the menu: ");
                System.out.println("Genres: Pop, Country, Electronic, Hip Hop, Jazz, Punk, Rock, Heavy Metal, Soul");
                String searchString = input.nextLine();

                if(searchString.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }
                String[] splitSearchString = searchString.split(" ");
                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM displayGenreHistory(?, ?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setString(1, splitSearchString[0]);
                s.setInt(2, Integer.parseInt(splitSearchString[1]));
                boolean hasResults = s.execute();
                if(!hasResults) {
                    System.out.println("This genre was not listened to in the past " + splitSearchString[1] + " months." );
                    continue;
                }
                //Print out results
                ResultSet searchResults = s.getResultSet();
                while(searchResults.next()){
                    System.out.println(
                                    "SongID: " + searchResults.getInt("songID") +
                                    " | Title: " + searchResults.getString("title") +
                                    " | Subtitle: " + searchResults.getString("subtitle"));
                }
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void dbotifyWrapped(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM SONGS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Songs Available to Display");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter Two Numbers, X and Y. This Function Returns the Top X Songs in the Past Y Months. Type -1 to go to the menu: ");
                String searchString = input.nextLine();

                if(searchString.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }
                String[] splitSearchString = searchString.split(" ");
                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM dbotifywrapped(?, ?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setInt(2, Integer.parseInt(splitSearchString[0]));
                s.setInt(1, Integer.parseInt(splitSearchString[1]));
                boolean hasResults = s.execute();
                if(!hasResults) {
                    System.out.println("No Songs Listened to in the Past" + splitSearchString[1] + " months." );
                    continue;
                }
                //Print out results
                ResultSet searchResults = s.getResultSet();
                while(searchResults.next()){
                    System.out.println(
                                    "SongID: " + searchResults.getInt("songID") +
                                    " | Sessions: " + searchResults.getString("sessioncount"));
                }
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void priceIncrease(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM LISTENERS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Listeners Available to Display");
                return;
            }
            checkSongs.close();

            PreparedStatement s = conn.prepareStatement("SELECT * FROM priceIncrease()");
            s.setQueryTimeout(QUERY_TIMEOUT);
            s.execute();

            //Print out results
            ResultSet searchResults = s.getResultSet();
            while(searchResults.next()){
                System.out.println(
                                "Billing State: " + searchResults.getString("billingstate") +
                                " | Billing ZipCode: " + searchResults.getInt("billingzipcode") +
                                " | Number of Impacted Listeners: " + searchResults.getInt("impactedlisteners"));
            }
            s.close();
            System.out.println("Press enter to return to the menu...");
            input.nextLine();
        } catch(SQLException e){
            handleError(e);
        }
    }

    private static void connectedArtists(Connection conn, Scanner input) {
        if (conn == null) {
            System.out.println("No database connection! \n");
            return;
        }
        try {
            PreparedStatement checkSongs = conn.prepareStatement("SELECT * FROM ARTISTS");
            checkSongs.setQueryTimeout(QUERY_TIMEOUT);
            ResultSet r = checkSongs.executeQuery();
            if(!r.next()){
                System.out.println("No Artists Available to Display");
                return;
            }
            checkSongs.close();
            do{
                System.out.println("\nEnter Two Artist Names. Type -1 to go to the menu: ");
                System.out.println("This Function Attempts to Find Artist Connections (They are on the Same Release) Between Two Artists Within 3 Releases.");
                String searchString = input.nextLine();

                if(searchString.equals("-1")) {
                    System.out.println("Returning to menu...");
                    return;
                }
                String[] splitSearchString = searchString.split(" ");
                boolean found = false;
                PreparedStatement s = conn.prepareStatement("SELECT * FROM connectedArtists(?, ?)");
                s.setQueryTimeout(QUERY_TIMEOUT);
                s.setString(1, splitSearchString[0].substring(0, Math.min(30,  splitSearchString[0].length())));
                s.setString(2, splitSearchString[1].substring(0, Math.min(30,  splitSearchString[1].length())));
                s.execute();
                ResultSet searchResults = s.getResultSet();
                System.out.println(searchResults);
                s.close();
            } while (true);
        } catch(SQLException e){
            handleError(e);
        }
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

                    if(conn != null) {
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
                    System.out.println("\nEnter Listener ID of the Listener:");
                    int ListenerID = sc.nextInt();
                    sc.nextLine();
                    listenToSong(conn, ListenerID);
                    break;
                    case 10:
                    System.out.println("\nEnter Listener ID of the Listener:");
                    int listenToPlaylistID = sc.nextInt();
                    sc.nextLine();
                    listenToPlaylist(conn, listenToPlaylistID);
                    break;
                case 11:
                    System.out.println("\nEnter Listener ID of the Listener to end session:");
                    int endSessionID = sc.nextInt();
                    sc.nextLine();
                    endSession(conn, endSessionID);
                    break;
                case 12:
                    System.out.println("\nEnter Listener ID of the Listener to remove:");
                    int removeListenerID = sc.nextInt();
                    sc.nextLine();
                    deleteListener(conn, removeListenerID);
                    break;
                case 13:
                    System.out.println("\nEnter Listener ID of the Listener to clear history:");
                    int clearListeningHistoryID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("\nConfirm history clear. (Yes/No)");
                    String confirm = sc.nextLine().trim();
                    sc.nextLine();
                    if(confirm.equalsIgnoreCase("Yes"))
                    {
                        clearListeningHistory(conn, clearListeningHistoryID);
                    }else {
                        System.out.println("Listening History was not cleared");
                    }
                        break;
                case 14:
                    System.out.println("\nEnter song ID of the song to remove:");
                    int removeSongID = sc.nextInt();
                    sc.nextLine();
                    removeSong(conn, removeSongID);
                    break;
                case 15:
                    System.out.println("\nEnter Listener ID of the listener that wishes to delete playlist(s):");
                    int deletePlaylistListenerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("\nDelete all or a single playlist?(All/Single)");
                    String allorone = sc.nextLine().trim();
                    sc.nextLine();
                    if(allorone.equalsIgnoreCase("All"))
                    {
                        System.out.println("\nConfime delete all?(Yes/No)");
                        String confirmation = sc.nextLine().trim();
                        sc.nextLine();
                        if(confirmation.equalsIgnoreCase("Yes"))
                        {
                            deletePlaylist(conn, deletePlaylistListenerID, true);
                        }else {
                            System.out.println("No playlists were removed.");
                        }
                    }else if(confirmation.equalsIgnoreCase("Single")) {
                        deletePlaylist(conn, deletePlaylistListenerID, false);
                    }else{
                        System.out.println("Not a valid response.");
                    }
                    break;
                case 16:
                    System.out.println("Enter listener ID of the listener: ");
                    int listPlaylistsWithGenreListenerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the genre (Pop, Country, Electronic, Hip Hop, Jazz, Punk, Rock, Heavy Metal, Soul): ");
                    String listPlaylistsWithGenreGenre = sc.nextLine().trim();
                    sc.nextLine();
                    listPlaylistsWithGenre(conn, listPlaylistsWithGenreListenerID, listPlaylistsWithGenreGenre);
                    break;
                case 17:
                    // TODO: modify for function
                    searchSongs(conn, sc);
                    break;
                case 18:
                    // TODO: modify for function
                    lookupArtist(conn, sc);
                    break;
                case 19:
                    // TODO: modify for function
                    displayListeningHistory(conn, sc);
                    break;
                case 20:
                    // TODO: modify for function
                    rankArtists(conn, sc);
                    break;
                case 21:
                    // TODO: modify for function
                    displayGenreHistory(conn, sc);
                    break;
                case 22:
                    // TODO: modify for function
                    dbotifyWrapped(conn,sc);
                    break;
                case 23:
                    // TODO: modify for function
                    priceIncrease(conn, sc);
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