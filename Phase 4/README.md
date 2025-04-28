<p> Kameren Jouhal - kaj143 <br>
Katherine Lin - ksl72 <br>
Ethan Defilippi - ecd57 </p>

<p> 
Before running our client, make sure that you open datagrip and run all of the schema and operation files to load the schema into your local postgres instance. Ensure that you run schema.sql, operations.sql, analytical.sql, and trigger.sql in datagrip. <br> <br>

#### To run our client

1. Run mvn clean compile
2. Run mvn package
3. Run java -cp target/DBotify-1.0-SNAPSHOT.jar Phase_4.DBotify

### OR

1. Just open it in intelliJ and hit run (MUCH EASIER)


#### How to Use Our Client
To use our client, type in the number of the operation you want to perform after the menu is displayed to the screen. The options are listed as follows (and are printed in the client when you run the program): <br>
1: Connect to DB <br>
2: Add a release <br>
3: Create a new listener <br>
4: Create a playlist <br>
5: Add an artist to a release <br>
6: Add song(s) to a release <br>
7: Add a song to a playlist <br>
8: Start a session <br>
9: Listen to a song or multiple songs <br>
10: Listen to a playlist <br>
11: End a session <br>
12: Delete a listener <br>
13: Clear a listener's listening history <br>
14: Remove a song <br>
15: Delete a playlist or all playlists <br>
16: Find all of a listener's playlists that have a song with a specific genre <br>
17: Search for songs with a title or subtitle that contain a pattern <br>
18: Find all of an artist's songs that are part of a release <br>
19: Display a listener's listening history between two dates <br>
20: Rank of artists <br>
21: Display songs with a genre that have been listened to in a specified number of months <br>
22: DBotify Wrapped <br>
23: Impact of price increases on most populous zipcode in each state <br>
24: Finds a path between two artists that are at most 3 hops away <br>
25: Exit <br> <br>

After you type one of these options, some instructions could be displayed to the screen prompting for input. For example, when adding an artist to a release, it will ask you for the name of an artist and a release ID. Follow these instructions carefully and input the information that you want for each step.
</p>
