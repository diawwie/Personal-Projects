package en.diana.gr1067.hw;

import java.util.Objects;

enum Genre
{
    POP,
    JAZZ,
    ROCK
}

public class Song implements Printable, Comparable<Song>
{
    private String title;
    private String artist;
    private int durationSeconds;
    private Genre genre;
    public static final int STREAM_TRESHOLD = 1000000; // final = constant din c++

    public Song(Genre genre, int durationSeconds, String artist, String title)
    {
        this.genre = genre;
        this.durationSeconds = durationSeconds;
        this.artist = artist;
        this.title = title;
    }

    // copy ctor
    public Song(Song source)
    {
        this.genre = source.getGenre();
        this.durationSeconds = source.getDurationSeconds();
        this.artist = source.getArtist();
        this.title = source.getTitle();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public int getDurationSeconds()
    {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds)
    {
        this.durationSeconds = durationSeconds;
    }

    public Genre getGenre()
    {
        return genre;
    }

    public void setGenre(Genre genre)
    {
        this.genre = genre;
    }

    // implement equals and hashCode -> tot generate

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
            return false;
        Song song = (Song) o;
        return Objects.equals(getTitle(), song.getTitle()) && Objects.equals(getArtist(), song.getArtist());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getTitle(), getArtist());
    }


    @Override
    public void printInfo()
    {
        System.out.print(title + " | " + artist + " | " + durationSeconds + "s | " + genre);
    }


    @Override
    public int compareTo(Song other)
    {
        // compare the title alphabetically
        int titleComparison = this.title.compareTo(other.getTitle());

        // if the titles are the same, compare by artist
        if(titleComparison == 0)
        {
            return this.artist.compareTo(other.getArtist());
        }

        //otherwise just return the title comparison result
        return titleComparison;
    }
}