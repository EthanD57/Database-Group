SET SCHEMA 'dbotify';

-- Trigger 1: checkActiveSessions
-- Prevents starting a new session if listener already has an active session
CREATE OR REPLACE FUNCTION checkActiveSessions()
RETURNS TRIGGER AS
$$
DECLARE
    active_session_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO active_session_count
    FROM SESSIONS
    WHERE listener = NEW.listener AND endTime IS NULL;

    IF active_session_count > 0 THEN
        RAISE EXCEPTION 'The listener already has an ongoing listening session. Please end the current session before starting a new listening session.'
        USING ERRCODE = 'P0001';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_multiple_active_sessions
BEFORE INSERT ON SESSIONS
FOR EACH ROW
EXECUTE FUNCTION checkActiveSessions();

-- Trigger 2: addRecommendedPlaylist
-- Adds a recommended playlist after ending a session
CREATE OR REPLACE FUNCTION addRecommendedPlaylist()
RETURNS TRIGGER AS
$$
DECLARE
    top_songs INTEGER[];
    existing_recommended_playlist BOOLEAN;
    current_song INTEGER;
BEGIN
    -- Only trigger when endTime is set to non-null
    IF NEW.endTime IS NOT NULL THEN
        -- Check if 'DBotify Recommended' playlist already exists
        SELECT EXISTS(
            SELECT 1
            FROM PLAYLISTS
            WHERE title = 'DBotify Recommended' AND listener = NEW.listener
        ) INTO existing_recommended_playlist;

        -- Find top 5 most listened songs for this listener
        SELECT ARRAY(
                       SELECT song
                       FROM listens_to lt
                           JOIN sessions s ON lt.session = s.sessionid
                       WHERE s.listener = NEW.listener
                       GROUP BY song
                       ORDER BY COUNT(*) DESC, song
                       LIMIT 5
               ) INTO top_songs;

        -- If playlist doesn't exist, create it
        IF NOT existing_recommended_playlist THEN
            INSERT INTO PLAYLISTS (title, listener)
            VALUES ('DBotify Recommended', NEW.listener);
        ELSE
            -- Remove existing recommended songs
            DELETE FROM CONTAINS
            WHERE playlistTitle = 'DBotify Recommended'
              AND playlistCreator = NEW.listener;
        END IF;

        -- Add top songs to recommended playlist
        FOREACH current_song IN ARRAY top_songs LOOP
            INSERT INTO CONTAINS (playlistTitle, playlistCreator, song)
            VALUES ('DBotify Recommended', NEW.listener, current_song);
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_add_recommended_playlist
AFTER UPDATE ON sessions
FOR EACH ROW
EXECUTE FUNCTION addRecommendedPlaylist();