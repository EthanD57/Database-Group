SET SCHEMA 'dbotify';

--rankArtists Function
--Rank artists based on number of sessions that listened to a song by the artist
--Handle ties by number of minutes listened
--Parameters: None
--Output: (name of the artist, total times listened to, rank of the artist)
CREATE OR REPLACE FUNCTION rankArtists()
    RETURNS TABLE (
        artistName VARCHAR(30),
        timesListened BIGINT,
        rank BIGINT
    ) AS
$$
BEGIN
    RETURN QUERY
    SELECT w.artist AS artistName, COUNT(w.artist) AS timesListened,
    RANK() OVER (ORDER BY COUNT(w.artist) DESC, SUM(s.duration) DESC, w.artist DESC) AS rank
    FROM LISTENS_TO AS l
    JOIN INCLUDES AS i ON l.song = i.song
    JOIN WRITES AS w ON i.release = w.release
    JOIN SONGS AS s ON s.songid = l.song
    GROUP BY w.artist;
END;
$$ language plpgsql;

--displayGenreHistory Function
--Finds all songs within a genre that was listened to in the past k months
--Note: 1 month is defined as 30 days counting back starting from the current date
--Parameters: genre:"genre", k:INTEGER
--Output: (songID, title, subtitle)
CREATE OR REPLACE FUNCTION displayGenreHistory(genre "genre", k INTEGER)
    RETURNS TABLE (
        songID INTEGER,
        title VARCHAR(30),
        subtitle VARCHAR(50)
    ) AS
$$
DECLARE
    time_interval INTERVAL;
BEGIN
    time_interval := make_interval(0, 0, 0, 30, 0, 0, 0.0) * $2;
    RETURN QUERY
    SELECT DISTINCT song.songID AS songID, song.title AS title, song.subtitle AS subtitle
    FROM LISTENS_TO AS l
    JOIN SONGS AS song ON l.song = song.songid
    JOIN GENRES AS g ON g.song = song.songID
    JOIN SESSIONS AS session ON session.sessionID = l.session
    WHERE g.genre = $1 AND
    (session.starttime::DATE <= current_date::DATE AND session.starttime::DATE >= current_date::DATE - time_interval)
    AND (session.endtime IS NULL OR
        session.endtime::DATE <= current_date::DATE AND session.endtime::DATE >= current_date::DATE - time_interval);
END;
$$ language plpgsql;

--dbotifyWrapped Function
--Displays the top k songs with respect to the number of sessions that listened to the song in the past x months
--Note: 1 month is defined as 30 days counting back starting from the current date
--Parameters: k:INTEGER, x:INTEGER
--Output: (songID, number of sessions that listened to the song)
CREATE OR REPLACE FUNCTION dbotifyWrapped(x INTEGER, k INTEGER)
    RETURNS TABLE (
        songID INTEGER,
        sessionCount BIGINT
    ) AS
$$
BEGIN
    RETURN QUERY
    SELECT s.songID, COUNT(DISTINCT lt.session) AS sessionCount
    FROM SONGS s
    JOIN LISTENS_TO lt ON s.songID = lt.song
    JOIN SESSIONS sess ON lt.session = sess.sessionID
    WHERE sess.startTime >= CURRENT_DATE - (x * INTERVAL '30 days')
    GROUP BY s.songID
    ORDER BY sessionCount DESC, s.songID
    LIMIT k;
END;
$$ LANGUAGE plpgsql;

--priceIncrease Function
--Finds the most populated zipcode for each state
--Parameters: None
--Output: (billing state, billing zipcode, number of impacted listeners)
CREATE OR REPLACE FUNCTION priceIncrease()
    RETURNS TABLE (
    billingState "state",
    billingZipcode CHAR(5),
    impactedListeners BIGINT
    ) AS
$$
BEGIN
    RETURN QUERY
    WITH zipcode_counts AS (
        SELECT l.billingState, l.billingZipcode, COUNT(*) AS listenerCount
        FROM LISTENERS l
        GROUP BY l.billingState, l.billingZipcode
    ),
    ranked_zipcodes AS (
        SELECT z.billingState, z.billingZipcode, z.listenerCount,
               ROW_NUMBER() OVER (
                   PARTITION BY z.billingState
                   ORDER BY z.listenerCount DESC, z.billingZipcode
               ) AS rnk
        FROM zipcode_counts z
    )
    SELECT r.billingState, r.billingZipcode, r.listenerCount AS impactedListeners
    FROM ranked_zipcodes r
    WHERE r.rnk = 1
    ORDER BY r.billingState;
END;
$$ LANGUAGE plpgsql;

--connectedArtists Function
--Finds a path, if one exists, between artists a1 and a2 with at most 3 hops between them
--Note: A hop is defined as two artists writing the same release together
--Note: If two paths exist between a1 and a2, the shorter path should be returned
--If both paths require the same number of hops, either path may be returned
--Parameters: a1:VARCHAR(30), a2:VARCHAR(30)
--Output: A string representing the path that connects a1 to a2 with all intermediate hops
--Example Output: ‘Brian’ −−−→ ‘Maanya’ −−−→ ‘Vasilis’ −−−→ ‘Pat’
CREATE OR REPLACE FUNCTION connectedArtists(a1 VARCHAR(30), a2 VARCHAR(30))
    RETURNS TEXT
    AS
$$
DECLARE
    path TEXT;
    current_artist VARCHAR(30);
    hop_counter INTEGER;
    max_hops INTEGER := 3;
    visited_artists TEXT[] := ARRAY[a1];
BEGIN
    path := a1;
    current_artist := a1;
    hop_counter := 0;

    WHILE hop_counter < max_hops LOOP
        SELECT w2.artist
        INTO current_artist
        FROM WRITES w
        JOIN WRITES w2 ON w.release = w2.release AND w.artist != w2.artist
        WHERE w.artist = current_artist AND NOT (w2.artist = ANY(visited_artists))
        LIMIT 1;

        IF hop_counter = max_hops THEN
            EXIT;
        END IF;

        path := path || ' → ' || current_artist;
        visited_artists := array_append(visited_artists, current_artist);
        hop_counter := hop_counter + 1;

        IF current_artist = a2 THEN
            RETURN path;
        END IF;
    END LOOP;
    RETURN 'No Path was found between ' || a1 || ' and ' || a2 || ' within 3 hops';
END;
$$ LANGUAGE plpgsql;
