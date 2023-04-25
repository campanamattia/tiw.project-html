package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.beans.Playlist;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;
import java.util.ArrayList;

public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public HomeServlet() {
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
	
	//method that prepares the presentation of the home page
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String userName = (String)session.getAttribute("user");
		
		//taking from the database all the playlists of the user
		String playlistListError = null;
		ArrayList<Playlist> playlists = null;
		try {
			playlists = new PlaylistDAO(this.connection).allPlaylists(userName);
			if(playlists == null || playlists.isEmpty()) {
				playlistListError = "You have no playlists saved on your account";
			}
			else {
				ctx.setVariable("playlists", playlists);
			}
		}
		catch(SQLException e) {
			playlistListError = "Database error: Unable to load your palylists";
		}
		
		//taking from the database all the songs of the user
		String playlistError = null;
		ArrayList<Song> songs = null;
		
		try {
			songs = new SongDAO(this.connection).getSongsbyUser(userName);
			if(songs == null || songs.isEmpty()) {
				playlistError = "Upload a song before creating a playlist";
			}
			else ctx.setVariable("songs", songs);
		}
		catch(SQLException e) {
			playlistError = "Database error: Unable to load your songs";
		}
		
		//taking errors coming from the selection of a playlist in the home page
		if(session.getAttribute("playlistListError") != null) {
			if(playlistListError == null) playlistListError = (String)session.getAttribute("playlistListError");
			else playlistListError = (String)session.getAttribute("playlistListError") + "\n" + playlistListError;
			session.removeAttribute("generalError");
		}
		if(playlistListError != null) ctx.setVariable("playlistListError", playlistListError);
		
		//taking the errors coming from the create playlist form in the home page
		if(session.getAttribute("playlistError") != null) {
			if(playlistError == null) playlistError = (String)session.getAttribute("playlistError");
			else playlistError = (String)session.getAttribute("playlistError") + "\n" + playlistError;
			session.removeAttribute("playlistError");
		}
		if(playlistError != null) ctx.setVariable("playlistError", playlistError);
		
		//taking the errors coming from the create song form in the home page
		if(session.getAttribute("songError") != null) {
			ctx.setVariable("songError", (String)session.getAttribute("songError"));
			session.removeAttribute("songError");
		}
		
		//taking the errors coming from other pages
		if(session.getAttribute("generalError") != null) {
			ctx.setVariable("generalError", (String)session.getAttribute("generalError"));
			session.removeAttribute("generalError");
		}
		
		templateEngine.process("/WEB-INF/home.html", ctx, response.getWriter());
	}
	
	//method that sends the user to the selected playlist page
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		
		String playlistName = request.getParameter("playlistName");
		String userName = (String)session.getAttribute("user");
		String playlistListError = null;
		
		//checking if the playlist name is valid
		try {
			if(playlistName == null || playlistName.isEmpty() || !(new PlaylistDAO(this.connection).belongTo(playlistName, userName)) ) {
				playlistListError = "Playlist not found";
			}
		} catch (SQLException e) {
			playlistListError = "Database error, try again";
		}
		
		//if an error occurred, the home page will be reloaded
		if(playlistListError != null) {
			session.setAttribute("playlistListError", playlistListError);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//forward to the playlist page
		session.setAttribute("playlistName", playlistName);
		String path = servletContext.getContextPath() + "/Playlist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
 	}
	
}