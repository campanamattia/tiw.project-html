package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;
import it.polimi.tiw.playlist.utils.EditType;
import java.util.ArrayList;

public class EditPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public EditPlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			this.templateEngine = TemplateHandler.getTemplateEngine(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//this page must be accessible only through the CreatePlaylistServlet and ModifyPlaylistServlet doPost methods
	//or through a failed doPost of this Servlet
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//taking attributes from session
		String playlistName = (String)session.getAttribute("playlistName");
		EditType editType = (EditType)session.getAttribute("editType");
		
		//if someone tries to load the page directly typing the url he will be redirected to the home page
		if(playlistName == null || editType == null  || playlistName.isEmpty() ) {
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//removing attributes from the session because they identify that the page has been loaded through the correct doPost methods 
		session.removeAttribute("playlistName");
		session.removeAttribute("editType");
		
		//starting to prepare the presentation of the page
		ctx.setVariable("playlistName", playlistName);
		ctx.setVariable("editType", editType.toString());
		String userName = (String)session.getAttribute("user");
		
		ArrayList<Song> songs = null;
		String error = null;
		
		//taking the songs that the user can add from the database
		if(editType.equals(EditType.CREATE)) {
			try {
				songs = new SongDAO(this.connection).getSongsbyUser(userName);
				if(songs == null || songs.isEmpty()) {
					error = "Upload a song before creating a playlist";
				}
				else ctx.setVariable("songs", songs);
			}
			catch(SQLException e) {
				error = "Database error: Unable to load your songs";
			}
		}
		if(editType.equals(EditType.MODIFY)) {
			try {
				songs = new SongDAO(this.connection).getSongsNotInPlaylist(playlistName, userName);
				if(songs == null || songs.isEmpty()) {
					error = "You have no more songs to add";
				}
				else ctx.setVariable("songs", songs);
			}
			catch(SQLException e) {
				error = "Database error: Unable to load your songs";
			}
		}
		if(error != null) ctx.setVariable("error", error);
		
		templateEngine.process("/WEB-INF/editPlaylist.html", ctx, response.getWriter());	
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		String userName = (String)session.getAttribute("user");
		String playlistError = null; // will be sent to the playlist page
		String error = null; //will be sent to the home page
		
		//taking playlistName and editType parameters
		String playlistName = request.getParameter("playlistName");
		EditType editType = null;
		if( request.getParameter("editType") == "CREATE") editType = EditType.CREATE;
		if( request.getParameter("editType") == "MODIFY") editType = EditType.MODIFY;
		
		//checking whether playlistName and editType parameters are valid or not
		if(playlistName == null || playlistName.isEmpty() || editType == null) {
			error = "Something went wrong";
		}
		if(error == null) {
			
			if(editType.equals(EditType.CREATE)) {
				try {
					if( playlistName.length() > 50 || new PlaylistDAO(this.connection).taken(playlistName,userName) ) {
						error = "Something went wrong";
					}
				}
				catch(SQLException e) {
					error = "Database error, try again";
				}
			}
			
			if(editType.equals(EditType.MODIFY)) {
				try {
					if( !(new PlaylistDAO(this.connection).belongTo(playlistName,userName)) ) {
						error = "Something went wrong";
					}
				}
				catch(SQLException e) {
					error = "Database error, try again";
				}
			}
		}
		
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//taking the selected songs
		SongDAO songDAO = new SongDAO(this.connection);
		ArrayList<Integer> songToAdd = new ArrayList<Integer>();
		
		if(editType.equals(EditType.CREATE)) {
			try {
				int maxSize = songDAO.getNumOfSongsbyUser(userName);
				for(Integer i=0; i<maxSize;i++) {
					String song = request.getParameter("song"+i.toString());
					if(song != null) { //This song has been chosen
						Integer songId = Integer.parseInt(song);
						if(songDAO.belongTo(songId, userName) ) {
							songToAdd.add(songId);
						}
					}
				}
				if(songToAdd.isEmpty()) {
					playlistError = "You must select at least one song";
				}
			}
			catch(SQLException e) {
				playlistError = "Database error, try again";
			}
			catch(NumberFormatException e1) {
				error = "Something went wrong";
			}
		}
		
		if(editType.equals(EditType.MODIFY)) {
			try {
				String song = request.getParameter("song");
				if(song != null) {
					Integer songId = Integer.parseInt(song);
					if(songDAO.belongTo(songId, userName) ) {
						songToAdd.add(songId);
					}
				}
				if(songToAdd.isEmpty()) {
					playlistError = "No song selected";
				}
			}
			catch(SQLException e) {
				playlistError = "Database error, try again";
			}
			catch(NumberFormatException e1) {
				error = "Something went wrong";
			}
		}
		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
