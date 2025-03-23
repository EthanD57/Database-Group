DROP SCHEMA IF EXISTS dbotify CASCADE;
CREATE SCHEMA dbotify;
SET SCHEMA 'dbotify';

CREATE DOMAIN occupation AS VARCHAR(15) CHECK (VALUE IN ('Singer', 'Composer', 'Editor', 'Publisher', 'Songwriter', 'Instrumentalist'));
CREATE DOMAIN genre AS VARCHAR(11) CHECK (VALUE IN ('Pop', 'Country', 'Electronic', 'Hip Hop', 'Jazz', 'Punk', 'Rock', 'Heavy Metal', 'Soul'));
CREATE DOMAIN state as CHAR(2) CHECK (VALUE IN ('AL', 'AK', 'AS', 'AZ', 'AR', 'CA', 'CO',
                                                'CT', 'DE', 'OF', 'DC', 'FL', 'GA', 'GU',
                                                'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY',
                                                'LA', 'ME', 'MD', 'MA', 'MI', 'MN', 'MS',
                                                'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM',
                                                'NY', 'NC', 'ND', 'IS', 'MP', 'OH', 'OK',
                                                'OR', 'PA', 'PR', 'RI', 'SC', 'SD', 'TN',
                                                'TX', 'UT', 'VT', 'VA', 'VI', 'WA', 'WV', 'WI', 'WY'));

DROP TABLE IF EXISTS SONGS;
DROP TABLE IF EXISTS ARTISTS;
DROP TABLE IF EXISTS GENRES;
DROP TABLE IF EXISTS RELEASES;
DROP TABLE IF EXISTS LISTENERS;
DROP TABLE IF EXISTS SESSIONS;
DROP TABLE IF EXISTS PLAYLISTS;
DROP TABLE IF EXISTS WRITES;
DROP TABLE IF EXISTS INCLUDES;
DROP TABLE IF EXISTS LISTENS_TO;
DROP TABLE IF EXISTS CONTAINS;


--Added Constraints
--title can not be NULL and must have a minimum length of 1
--Duration must be positive
CREATE TABLE SONGS (
    songID INTEGER,
    title VARCHAR(30) NOT NULL,
    subtitle VARCHAR(50),
    duration INTERVAL,
    PRIMARY KEY (songID),
    CHECK (duration > '00:00:00'),
    CHECK (LENGTH(title) >= 1)

);

--Added Constraints
--name can not be NULL and must have a minimum length of 1
--artistID can not be NULL
CREATE TABLE ARTISTS (
    name VARCHAR(30) NOT NULL,
    artistID INTEGER NOT NULL,
    biography VARCHAR(250),
    occupation "occupation",
    city VARCHAR(50),
    country VARCHAR(50),
    PRIMARY KEY (name),
    UNIQUE (artistID),
    CHECK (LENGTH(name) >= 1)

);

CREATE TABLE GENRES (
    genre "genre",
    song INTEGER,
    PRIMARY KEY (genre, song),
    FOREIGN KEY (song) REFERENCES SONGS(songID) ON UPDATE CASCADE ON DELETE CASCADE
);

--Added Constraints
--title can not be NULL and must have a minimum length of 1
--releaseDate can not be NULL
CREATE TABLE RELEASES (
    releaseID SERIAL,
    title VARCHAR(50) NOT NULL,
    releaseDATE DATE NOT NULL,
    label VARCHAR(30),
    PRIMARY KEY (releaseID),
    CHECK (LENGTH(title) >= 1)
);

--Added Constraints
--profileName can not be NULL and must have a minimum length of 1
--billingStreet can not be NULL and must have a minimum length of 1
--billingCity can not be NULL and must have a minimum length of 1
--billingState must be in domain state
--billingZipcode can not be NULL
--email can not be NULL
--email must follow email REGEX (something @ something . something)
--billingZipcode must be 5 digits (0-9)
CREATE TABLE LISTENERS (
    listenerID SERIAL,
    profileName VARCHAR(30) NOT NULL,
    billingStreet VARCHAR(50) NOT NULL,
    billingCity VARCHAR(50) NOT NULL,
    billingState "state",
    billingZipcode CHAR(5) NOT NULL,
    email VARCHAR(30) NOT NULL,
    PRIMARY KEY (listenerID),
    UNIQUE (email),
    CHECK (email LIKE '%@%.%'),
    CHECK (billingZipcode LIKE '^\d{5}$'),
    CHECK (LENGTH(profileName) >= 1),
    CHECK (LENGTH(billingStreet) >= 1),
    CHECK (LENGTH(billingCity) >= 1)
);

--Added Constraints
--startTime can not be NULL
--endTime can be NULL
--listener can not be NULL
--endTime must be greater than startTime
CREATE TABLE SESSIONS (
    sessionID SERIAL,
    startTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    endTime TIMESTAMP DEFAULT NULL,
    listener INTEGER NOT NULL,
    PRIMARY KEY (sessionID),
    FOREIGN KEY (listener) REFERENCES LISTENERS(listenerID) ON UPDATE CASCADE ON DELETE CASCADE,
    CHECK (startTime < endTime)
);

--Added Constraints
--dateOfCreation can not be NULL
CREATE TABLE PLAYLISTS (
    title VARCHAR(30),
    listener INTEGER,
    dateOfCreation DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (title, listener),
    FOREIGN KEY (listener) REFERENCES LISTENERS(listenerID) ON UPDATE CASCADE ON DELETE CASCADE,
    CHECK (LENGTH(title) >= 1)
);

CREATE TABLE WRITES (
    artist VARCHAR(30),
    release INTEGER,
    PRIMARY KEY (artist, release),
    FOREIGN KEY (artist) REFERENCES ARTISTS(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (release) REFERENCES RELEASES(releaseID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE INCLUDES (
    release INTEGER,
    song INTEGER,
    PRIMARY KEY (release, song),
    FOREIGN KEY (release) REFERENCES RELEASES(releaseID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (song) REFERENCES SONGS(songID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE LISTENS_TO (
    session INTEGER,
    song INTEGER,
    PRIMARY KEY (session, song),
    FOREIGN KEY (session) REFERENCES SESSIONS (sessionID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (song) REFERENCES SONGS(songID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE CONTAINS (
    playlistTitle VARCHAR(30),
    playlistCreator INTEGER,
    song INTEGER,
    PRIMARY KEY (playlistTitle, playlistCreator, song),
    FOREIGN KEY (playlistTitle, playlistCreator) REFERENCES PLAYLISTS(title, listener) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (song) REFERENCES SONGS(songID) ON UPDATE CASCADE ON DELETE CASCADE
);