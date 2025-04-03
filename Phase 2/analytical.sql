--rankArtists Function
--Rank artists based on number of sessions that listened to a song by the artist
--Handle ties by number of minutes listened
--Parameters: None
--Output: (name of the artist, total times listened to, rank of the artist)

--displayGenreHistory Function
--Finds all songs within a genre that was listened to in the past k months
--Note: 1 month is defined as 30 days counting back starting from the current date
--Parameters: genre:"genre", k:INTEGER
--Output: (songID, title, subtitle)

--dbotifyWrapped Function
--Displays the top k songs with respect to the number of sessions that listened to the song in the past x months
--Note: 1 month is defined as 30 days counting back starting from the current date
--Parameters: k:INTEGER, x:INTEGER
--Output: (songID, number of sessions that listened to the song)

--priceIncrease Function
--Finds the most populated zipcode for each state
--Parameters: None
--Output: (billing state, billing zipcode, number of impacted listeners)

--connectedArtists Function
--Finds a path, if one exists, between artists a1 and a2 with at most 3 hops between them
--Note: A hop is defined as two artists writing the same release together
--Note: If two paths exist between a1 and a2, the shorter path should be returned
--If both paths require the same number of hops, either path may be returned
--Parameters: a1:VARCHAR(30), a2:VARCHAR(30)
--Output: A string representing the path that connects a1 to a2 with all intermediate hops
--Example Output: ‘Brian’ −−−→ ‘Maanya’ −−−→ ‘Vasilis’ −−−→ ‘Pat’
