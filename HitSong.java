package en.diana.gr1067.hw;

public class HitSong extends Song
{
    long totalStreams;

    public HitSong(Genre genre, int durationSeconds, String artist, String title, long totalStreams)
    {
        super(genre, durationSeconds, artist, title);
        this.totalStreams = totalStreams;
    }

    @Override
    public void printInfo()
    {
        if(totalStreams >= STREAM_TRESHOLD)
        {
            System.out.print(super.getTitle() + " | " + super.getArtist() + " | " + super.getDurationSeconds() + "s | " + super.getGenre() + " | " + "Streams: " + totalStreams + " [HIT]");
        }
        else
            System.out.print(super.getTitle() + " | " + super.getArtist() + " | " + super.getDurationSeconds() + "s | " + super.getGenre() + " | " + "Streams: " + totalStreams);
    }
}
