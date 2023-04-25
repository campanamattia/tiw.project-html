package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;
import java.util.ArrayList;

public class PlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private String imgFolderPath;
	
	public PlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		try {
			this.connection = ConnectionHandler.getConnection(context);
			this.templateEngine = TemplateHandler.getTemplateEngine(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//this page must be accessible only by selecting a playlist in the home page,
	//by the buttons in the playlist page
	// or through a doPost of EditPlaylistServlet (using ForwardPlaylist).
	//Doing so, the parameters in the session have been already checked
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		SongDAO songDAO = new SongDAO(this.connection);
		String userName = (String)session.getAttribute("user");
		String error = null;
		
		//taking attributes from session
		String playlistName = (String)session.getAttribute("playlistName");
		String playlistError = (String)session.getAttribute("playlistError");
		int lowerBound = session.getAttribute("lowerBound") != null ? (Integer)session.getAttribute("lowerBound") : 0;
		
		//removing attributes from the session because they identify that the page has been loaded in the intended way 
		session.removeAttribute("playlistName");
		session.removeAttribute("playlistError");
		session.removeAttribute("lowerBound");
		
		//taking all the user's songs that are not in the playlist
		ArrayList<Song> notInPlaylistSongs = null;
		try {
			notInPlaylistSongs = songDAO.getSongsNotInPlaylist(playlistName, userName);
			if(notInPlaylistSongs == null || notInPlaylistSongs.isEmpty()) {
				if(playlistError == null) playlistError = "You have no more songs to add";
				else playlistError += "\nYou have no more songs to add";
			}
		}
		catch(SQLException e) {
			error = "Database error: Unable to load your playlist";
		}
		
		//if an error occurred, the user will be redirected to the home page
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//taking all the user's songs contained in the playlist
		ArrayList<Song> allSongs = null;
		try {
			allSongs = songDAO.getSongTitleAndImg(playlistName, userName);
		}
		catch(SQLException e) {
			error = "Database error: Unable to load your playlist";
		}
		
		//if an error occurred, the user will be redirected to the home page
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//taking only the songs to render on the page
		ArrayList<Song> songs = new ArrayList<Song>();
		for(int i=0; i<5 && error == null; i++) {
			try {
				songs.add(allSongs.get(lowerBound + i));
			}
			catch(IndexOutOfBoundsException e) {
				error = "Section not found";
			}
		}
		
		//if songs is empty the user is trying to load asection of the playlist in which there are no songs
		if(songs.isEmpty()) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//setting the correct path for the images
		for(Song s : songs) {
			s.getAlbum().setFileImage(this.imgFolderPath + userName + "_" + s.getAlbum().getFileImage());
		}
		
		for(Song s : notInPlaylistSongs) {
			s.getAlbum().setFileImage(this.imgFolderPath + userName + "_" + s.getAlbum().getFileImage());
		}
		
		//starting to prepare the presentation of the page
		ctx.setVariable("playlistName", playlistName);
		if(playlistError != null) ctx.setVariable("playlistError", playlistError);
		ctx.setVariable("songs", songs);
		ctx.setVariable("notInPlaylistSongs", notInPlaylistSongs);
		ctx.setVariable("lowerBound", lowerBound);
		if(lowerBound == 0) ctx.setVariable("previousButton", 0);
		else  ctx.setVariable("previousButton", 1);
		if(lowerBound + 5 < allSongs.size()) ctx.setVariable("nextButton", 1);
		else ctx.setVariable("nextButton", 0);
		
		templateEngine.process("/WEB-INF/playlist.html", ctx, response.getWriter());	
	}
		
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
