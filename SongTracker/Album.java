package en.diana.gr1067.hw;

import java.util.List;
import java.util.HashSet;   // importing the hashset class

public class Album
{
    String albumTitle;
    int releaseYear;
    HashSet<Song> songs = new HashSet<>();

    // how do we how how not allow duplicates
    // Set 	HashSet, TreeSet, LinkedHashSet 	Collection of unique elements
    // Use Set classes when you need to store unique values only.
    // A HashSet is a collection of elements where every element is unique.
    // It is part of the java.util package and implements the Set interface.


    public Album(HashSet<Song> songs, int releaseYear, String albumTitle)
    {
        this.releaseYear = releaseYear;
        this.albumTitle = albumTitle;
        //this.songs = songs;
        setSongs(songs);
    }

    public String getAlbumTitle()
    {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle)
    {
        this.albumTitle = albumTitle;
    }

    public int getReleaseYear()
    {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear)
    {
        this.releaseYear = releaseYear;
    }

    // the standard java way: defensive copying
//    public HashSet<Song> getSongs()
//    {
//        return new HashSet<>(this.songs);
//    }
//
//    public void setSongs(HashSet<Song> songs)
//    {
//        // creating a new set to prevent the user to keep a reference of our internal data
//        if(songs!=null)
//        {
//            this.songs = new HashSet<>(songs);
//        }
//        else
//        {
//            this.songs = new HashSet<>();   // good practice to avoid null collections!!
//        }
//    }

// the c++ way: true deep copying
// If you want total protection—meaning you duplicate the
// HashSet and you duplicate every single Song inside of it
// you need a true deep copy
    public HashSet<Song> getSongs()
    {
        HashSet<Song> deepCopiedSet = new HashSet<>();
//        for(int i = 0 ; i< this.songs.size(); i++)
//        {
//            Song song = this.songs.get(i);
//            // do something
//        }

        for(Song song : this.songs)     // Enhanced For Loop
        {
            // we allocate a brand new Song object for the brand new set
            deepCopiedSet.add(new Song(song));
        }
        return deepCopiedSet;
    }

    public void setSongs(HashSet<Song> songs)
    {
        this.songs = new HashSet<>();
        if(songs!=null)
        {
            for(Song song : songs)
            {
                // allocate brand new Song object for our internal set
                this.songs.add(new Song(song));
            }
        }
    }

    public void addSong(Song s)    // adds a song to the album's collection
    {
        if(s != null)
        {
            // using the copy ctor to add a deep copy
            this.songs.add(new Song(s));
        }
    }

    public Album cloneAlbum()
    {
        // calling this -> triggers setSongs() method
        // which automatically handles the deep copying of the HashSet!
        return new Album(this.songs, this.releaseYear, this.albumTitle);
    }
}
