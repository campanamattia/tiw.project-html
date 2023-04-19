package it.polimi.tiw.playlist.beans;


public class Song{
	private int id;
    private String title;
    private String genre;
    private String fileAudio;
    private String user;
    private Album album;

    public Song(){
        this.id = 0;
        this.title = null;
        this.genre = null;
        this.fileAudio = null;
        this.user = null;
        this.album = null;
    }

    public Song(int id, String title, String genre, String fileAudio, String user, Album album){
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.fileAudio = fileAudio;
        this.user = user;
        this.album = album;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getGenre() {
        return genre;
    }

    public void setFileAudio(String fileAudio) {
        this.fileAudio = fileAudio;
    }
    public String getFileAudio() {
        return fileAudio;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public String getUser() {
        return user;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
    public Album getAlbum() {
        return album;
    }
    
}