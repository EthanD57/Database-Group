SET SCHEMA 'dbotify';

--addRelease Function
--Parameters: title:VARCHAR(30), releaseDate:DATE, label:VARCHAR(30)
CREATE OR REPLACE FUNCTION addRelease(title VARCHAR(30), releaseDate DATE, label VARCHAR(30))
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO RELEASES (title, releaseDATE, label) 
    VALUES (title, releaseDate, label);
END;
$$ LANGUAGE plpgsql;

--newListener Function
--Parameters: profileName:VARCHAR(30), billingStreet:VARCHAR(50), billingCity:VARCHAR(50), 
--billingState:"state", billingZipcode:CHAR(5), email:VARCHAR(30)
CREATE OR REPLACE FUNCTION newListener(profileName VARCHAR(30), billingStreet VARCHAR(50), billingCity VARCHAR(50), 
billingState "state", billingZipcode CHAR(5), email VARCHAR(30))
    RETURN VOID AS
$$
BEGIN
    INSERT INTO LISTENERS (profileName, billingStreet, billingCity, billingState, billingZipcode, email)
    VALUES (profileName, billingStreet, billingCity, billingState, billingZipcode, email);
END;
$$ LANGUAGE plpgsql;

--createPlaylist function
--Parameters: title:VARCHAR(30), listener:INTEGER
CREATE OR REPLACE FUNCTION createPlaylist(title VARCHAR(30), listener INTEGER)
    RETURN VOID AS
$$
BEGIN
    INSERT INTO PLAYLISTS (title, listener)
    VALUES (title, listener);
END;
$$ LANGUAGE plpgsql;

--addArtistToRelease
--Parameters: artist:VARCHAR(30), release:INTEGER
CREATE OR REPLACE FUNCTION addArtistToRelease(artist VARCHAR(30), release INTEGER)
    RETURN VOID AS
$$
BEGIN
    INSERT INTO WRITES (artist, release)
    VALUES(artist, release);
END;
$$ LANGUAGE plpgsql;

--addSongToRelease Function
--Parameters: song:INTEGER, release:INTEGER
CREATE OR REPLACE FUNCTION addArtistToRelease(song INTEGER, release INTEGER)
    RETURN VOID AS
$$
BEGIN
    INSERT INTO INCLUDES (song, release)
    VALUES (song, release);
END;
$$ LANGUAGE plpgsql;

--addSongToPlaylist Function
--Parameters: playlistTitle:VARCHAR(30), playlistCreator:INTEGER, song:INTEGER
CREATE OR REPLACE FUNCTION addSongToPlaylist(playlistTitle VARCHAR(30), playlistCreator INTEGER, song INTEGER)
    RETURN VOID AS
$$
BEGIN
    INSERT INTO CONTAINS (playlistTitle, playlistCreator, song)
    VALUES (playlistTitle, playlistCreator, song);
END;
$$ LANGUAGE plpgsql;

--startSession Function
--Parameters: listener:INTEGER, startTime:TIMESTAMP
CREATE OR REPLACE FUNCTION startSession(listener INTEGER, startTime TIMESTAMP)
    RETURN INTEGER AS
$$
DECLARE
    new_sessionID INTEGER;
BEGIN
    INSERT INTO SESSIONS (listener, startTime)
    VALUES (listener, startTime)
    RETURNING sessionID INTO new_sessionID;

    RETURN new_sessionID;
END;
$$ LANGUAGE plpgsql;

--listenToSong Function
--Parameters: song:INTEGER, listener:INTEGER
CREATE OR REPLACE FUNCTION listenToSong(song INTEGER, listener INTEGER)
    RETURN VOID AS
$$
DECLARE
    active_sessionID INTEGER;
BEGIN
    SELECT sessionID INTO active_sessionID
    FROM SESSIONS
    WHERE SESSIONS.listener = listener AND SESSIONS.endTime IS NULL;
    ORDER BY SESSIONS.startTime DESC
    LIMIT 1;

    IF active_sessionID IS NOT NULL
    THEN
        INSERT INTO LISTENS_TO (session, song)
        VALUES (active_sessionID, song);
    ELSE
        active_sessionID := startSession(listener, CURRENT_TIMESTAMP);
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
    RETURN VOID AS
$$
DECLARE
    active_sessionID INTEGER;
BEGIN
    SELECT sessionID INTO active_sessionID
    FROM SESSIONS
    WHERE SESSIONS.listener = listener AND SESSIONS.endTime IS NULL;
    ORDER BY SESSIONS.startTime DESC
    LIMIT 1;

    IF active_sessionID IS NOT NULL
    THEN
        UPDATE 
    ELSE
        RAISE EXCEPTION "No active sessions for %", 
        SELECT profileName FROM LISTENERS WHERE LISTENERS.listenerID = listener
        USING ERRCODE = 'P0002';
    END IF;
END;
$$ LANGUAGE plpgsql;

--deleteListener Function
--Delete listener from LISTENERS
--Parameters: listenerID:INTEGER
CREATE OR REPLACE FUNCTION deleteListener(listenerID INTEGER)
    RETURN VOID AS
$$
BEGIN
    DELETE FROM LISTENERS
    WHERE LISTENERS.listenerID = listenerID;
END;
$$ LANGUAGE plpgsql;

--clearListeningHistory Function
--Remove all of the listener's sessions and songs listened to during each session
--Parameters: listenerID:INTEGER
CREATE OR REPLACE FUNCTION clearListeningHistory(listenerID INTEGER)
    RETURN VOID AS
$$
DECLARE
    session_record RECORD;
BEGIN
    FOR session_record IN
        SELECT sessionID FROM SESSIONS
        WHERE SESSIONS.listener = listenerID
    LOOP
        DELETE FROM SESSIONS
        WHERE SESSIONS.sessionID = session_record.sessionID;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

--removeSong Function
--Parameters: songID:INTEGER
CREATE OR REPLACE FUNCTION removeSong(songID INTEGER)
    RETURN VOID AS
$$
BEGIN
    DELETE FROM SONGS
    WHERE SONGS.songID = songID;
END;
$$ LANGUAGE plpgsql;

--deletePlaylist Function
--Parameters: title:VARCHAR(30), listener:INTEGER
CREATE OR REPLACE FUNCTION deletePlaylist(title VARCHAR(30), listener INTEGER)
    RETURN VOID AS
$$
BEGIN
    DELETE FROM PLAYLISTS
    WHERE PLAYLISTS.title = title AND PLAYLISTS.listener = listener;
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