package en.diana.gr1067.hw;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map;

import static en.diana.gr1067.hw.Genre.*;   // the * means that we imported everything from there

public class Main
{
    public static void main(String[] args)
    {
//        Song song1 = new Song(POP, 180, "Your Mom", "Lover Baby");
//        Song song2 = new Song(JAZZ, 190, "Your Dad", "Drunk Again");
//        Song song3 = new Song(ROCK, 10, "Your Dad", "Drunk Again");
//
//        System.out.println("Are the two songs the same?");
//        //System.out.println(song1.equals(song2));
//        System.out.println(song2.equals(song3));
//
//        song1.printInfo();

//        HitSong song4 = new HitSong(POP, 360, "Saja Boys", "Soda Pop", 2000000);
//        song4.printInfo();
//        System.out.println();
//        HitSong song5 = new HitSong(JAZZ, 300, "Soul Mother", "Grassy Soda", 200);
//        song5.printInfo();

        // create a hash set to hold the songs
        HashSet<Song> songCatalog = new HashSet<>();

        // create min 4 unique songs (mixing song and hitsong)
        Song song1 = new Song(POP, 180, "Saja Boyz", "Soda Dem0n");
        Song song2 = new Song(JAZZ, 370, "Sukunas Toiletpaper", "Wipping Good");
        HitSong hitSong1 = new HitSong(ROCK, 333, "Biting Metal", "Whiny Boots", 23000000);
        HitSong hitSong2 = new HitSong(ROCK, 356, "Unclean Mane", "Neigh, Bitch", 623879469);

        // making a duplictate of one of the songs, the artist and the title have to be the samne
        Song duplicateSpong = new Song(ROCK, 999, "Saja Boyz", "Soda Dem0n");

        // adding the unique songs in the catalog
        songCatalog.add(song1);
        songCatalog.add(song2);
        songCatalog.add(hitSong1);
        songCatalog.add(hitSong2);

        // printing the size b4 adding the duplicate
        System.out.println("Size of the song catalog before adding the duplicate: " + songCatalog.size());

        // attempting to add the duplicate
        boolean wasAdded = songCatalog.add(duplicateSpong);
        System.out.println("Was the duplicate song added successfully? " + wasAdded);

        // printing the size after attempting to add the duplicate
        System.out.println("Size of the song catalog after attempting to add the duplicate: " + songCatalog.size());

        // we use the songs created previously
        // create 3 albums and add songs (also add duplicates)
        HashSet<Song> album1Songs = new HashSet<>();
        album1Songs.add(song1);
        album1Songs.add(song2);
        Album album1 = new Album(album1Songs, 2020, "Hits of the year 2020");

        HashSet<Song> album2Songs = new HashSet<>();
        album2Songs.add(song2); // duplicate -> also in album 1
        album2Songs.add(hitSong1);
        Album album2 = new Album(album2Songs, 2023, "Rock my Jazz");

        HashSet<Song> album3Songs = new HashSet<>();
        album3Songs.add(song1); // duplicate! also in album1
        album3Songs.add(hitSong1); // duplicate! also in album2
        album3Songs.add(hitSong2);
        Album album3 = new Album(album3Songs, 2026, "Crazy sexy cool");

        // put albums in an array to easily iterate over them
        Album[] allAlbums = { album1, album2, album3 };

        // create the tree map to act as the collision counter
        // key: song, value: integer (the count)
        TreeMap<Song, Integer> songCounter = new TreeMap<>();

        // iterate over every album and every song inside it
        for(Album currentAlbum : allAlbums)
        {
            for(Song currentSong : currentAlbum.getSongs())
            {
                // if the song is NOT yet in the map, insert it with count 1
                if(!songCounter.containsKey(currentSong))
                {
                    songCounter.put(currentSong, 1);
                }
                else    // if the song is already in there
                {
                    int previousCount = songCounter.get(currentSong);
                    songCounter.put(currentSong, previousCount + 1);
                }
            }
        }

        // print the final collection
        System.out.println("----- Song Collision Counter -----");
        for(Map.Entry<Song, Integer> entry : songCounter.entrySet())
        {
            Song s = entry.getKey();
            int count = entry.getValue();
            System.out.println("'" + s.getTitle() + "' by " + s.getArtist() + " - Appears in " + count + " albums(s)");
        }

    }
    // do not write here, it's outside the main method
}