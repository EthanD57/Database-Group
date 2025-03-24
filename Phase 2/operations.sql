SET SCHEMA 'dbotify';

--addRelease Function
--Add a new release
--Parameters: title:VARCHAR(30), releaseDate:DATE, label:VARCHAR(30)
CREATE OR REPLACE FUNCTION addRelease(title VARCHAR(30), releaseDate DATE, label VARCHAR(30))
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO RELEASES (title, releaseDATE, label)
    VALUES ($1, $2, $3);
END;
$$ LANGUAGE plpgsql;

--newListener Function
--Add a new listener
--Parameters: profileName:VARCHAR(30), billingStreet:VARCHAR(50), billingCity:VARCHAR(50), 
--billingState:"state", billingZipcode:CHAR(5), email:VARCHAR(30)
CREATE OR REPLACE FUNCTION newListener(profileName VARCHAR(30), billingStreet VARCHAR(50), billingCity VARCHAR(50), 
billingState "state", billingZipcode CHAR(5), email VARCHAR(30))
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO LISTENERS (profileName, billingStreet, billingCity, billingState, billingZipcode, email)
    VALUES ($1, $2, $3, $4, $5, $6);
END;
$$ LANGUAGE plpgsql;

--createPlaylist function
--Create a playlist with a particular listener
--Parameters: title:VARCHAR(30), listener:INTEGER
CREATE OR REPLACE FUNCTION createPlaylist(title VARCHAR(30), listener INTEGER)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO PLAYLISTS (title, listener)
    VALUES ($1, $2);
END;
$$ LANGUAGE plpgsql;

--addArtistToRelease
--Add an artist to a release
--Parameters: artist:VARCHAR(30), release:INTEGER
CREATE OR REPLACE FUNCTION addArtistToRelease(artist VARCHAR(30), release INTEGER)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO WRITES (artist, release)
    VALUES($1, $2);
END;
$$ LANGUAGE plpgsql;

--addSongToRelease Function
--Add a song to a release
--Parameters: song:INTEGER, release:INTEGER
CREATE OR REPLACE FUNCTION addArtistToRelease(song INTEGER, release INTEGER)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO INCLUDES (song, release)
    VALUES ($1, $2);
END;
$$ LANGUAGE plpgsql;

--addSongToPlaylist Function
--Add a song to a playlist with a particular creator
--Parameters: playlistTitle:VARCHAR(30), playlistCreator:INTEGER, song:INTEGER
CREATE OR REPLACE FUNCTION addSongToPlaylist(playlistTitle VARCHAR(30), playlistCreator INTEGER, song INTEGER)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO CONTAINS (playlistTitle, playlistCreator, song)
    VALUES ($1, $2, $3);
END;
$$ LANGUAGE plpgsql;

--startSession Function
--Start a session for a particular listener at the specified start time
--Parameters: listener:INTEGER, startTime:TIMESTAMP
CREATE OR REPLACE FUNCTION startSession(listener INTEGER, startTime TIMESTAMP)
    RETURNS INTEGER AS
$$
DECLARE
    new_sessionID INTEGER;
BEGIN
    INSERT INTO SESSIONS (listener, startTime)
    VALUES ($1, $2)
    RETURNING sessionID INTO new_sessionID;

    RETURN new_sessionID;
END;
$$ LANGUAGE plpgsql;

--listenToSong Function
--Parameters: song:INTEGER, listener:INTEGER
CREATE OR REPLACE FUNCTION listenToSong(song INTEGER, listener INTEGER)
    RETURNS VOID AS
$$
DECLARE
    active_sessionID INTEGER;
BEGIN
    SELECT sessionID INTO active_sessionID
    FROM SESSIONS
    WHERE SESSIONS.listener = $2 AND SESSIONS.endTime IS NULL
    ORDER BY SESSIONS.startTime DESC
    LIMIT 1;

    IF active_sessionID IS NOT NULL
    THEN
        INSERT INTO LISTENS_TO (session, song)
        VALUES (active_sessionID, $1);
    ELSE
        active_sessionID := startSession(listener, CURRENT_TIMESTAMP::timestamp);
        INSERT INTO LISTENS_TO (session, song)
        VALUES (active_sessionID, song);
    END IF;
END;
$$ LANGUAGE plpgsql;

--listenToPlaylist Function
--Parameters: playlistTitle:VARCHAR(30), listener:INTEGER


--endSession Function
--End the listener's active session by updating endTime to the current timestamp
--If an active session does not exist, throw a no_data_found error
--Parameters: listener:INTEGER
CREATE OR REPLACE FUNCTION endSession(listener INTEGER)
    RETURNS VOID AS
$$
DECLARE
    active_sessionID INTEGER;
    listener_profile VARCHAR(30);
BEGIN
    SELECT sessionID INTO active_sessionID
    FROM SESSIONS
    WHERE SESSIONS.listener = $1 AND SESSIONS.endTime IS NULL
    ORDER BY SESSIONS.startTime DESC
    LIMIT 1;

    IF active_sessionID IS NOT NULL
    THEN
        UPDATE SESSIONS
        SET endTime = CURRENT_TIMESTAMP::timestamp
        WHERE SESSIONS.sessionID = active_sessionID;
    ELSE
        SELECT profileName INTO listener_profile
        FROM LISTENERS
        WHERE LISTENERS.listenerID = listener;
        RAISE EXCEPTION 'No active sessions for %', listener_profile
        USING ERRCODE = 'P0002';
    END IF;
END;
$$ LANGUAGE plpgsql;

--deleteListener Function
--Delete listener from LISTENERS
--Parameters: listenerID:INTEGER
CREATE OR REPLACE FUNCTION deleteListener(listenerID INTEGER)
    RETURNS VOID AS
$$
BEGIN
    DELETE FROM LISTENERS
    WHERE LISTENERS.listenerID = $1;
END;
$$ LANGUAGE plpgsql;

--clearListeningHistory Function
--Remove the listener's sessions and songs listened to during each session
--Parameters: listenerID:INTEGER
CREATE OR REPLACE FUNCTION clearListeningHistory(listenerID INTEGER)
    RETURNS VOID AS
$$
DECLARE
    session_record RECORD;
BEGIN
    FOR session_record IN
        SELECT sessionID FROM SESSIONS
        WHERE SESSIONS.listener = $1
    LOOP
        DELETE FROM SESSIONS
        WHERE SESSIONS.sessionID = session_record.sessionID;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

--removeSong Function
--Remove a song and all dependencies of that song
--Parameters: songID:INTEGER
CREATE OR REPLACE FUNCTION removeSong(songID INTEGER)
    RETURNS VOID AS
$$
BEGIN
    DELETE FROM SONGS
    WHERE SONGS.songID = $1;
END;
$$ LANGUAGE plpgsql;

--deletePlaylist Function
--Delete a playlist and all dependencies of that playlist
--Parameters: title:VARCHAR(30), listener:INTEGER
CREATE OR REPLACE FUNCTION deletePlaylist(title VARCHAR(30), listener INTEGER)
    RETURNS VOID AS
$$
BEGIN
    DELETE FROM PLAYLISTS
    WHERE PLAYLISTS.title = $1 AND PLAYLISTS.listener = $2;
END;
$$ LANGUAGE plpgsql;

--listPlaylistsWithGenre Function
--Parameters: listenerID:INTEGER, genre: "genre"

--searchSongs Function
--Parameters: pattern:VARCHAR(32)

--lookupArtist Function
--Parameters: artist:VARCHAR(30)

--displayListeningHistory Function
--Parameters: x:DATE, y:DATE, listenerID:INTEGER