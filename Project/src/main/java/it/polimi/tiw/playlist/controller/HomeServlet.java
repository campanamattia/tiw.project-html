package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.beans.Playlist;
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
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		//taking from the database all the playlists of the user
		String userName = (String)session.getAttribute("user");
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
		if(playlistListError != null) ctx.setVariable("playlistListError", playlistListError);
		
		//taking the errors coming from the two forms in the page
		String playlistError = (String)session.getAttribute("playlistError");
		if(playlistError != null) {
			ctx.setVariable("playlistError", playlistError);
			session.removeAttribute("playlistError");
		}
		
		String songError = (String)session.getAttribute("songError");
		if(songError != null) {
			ctx.setVariable("songError", songError);
			session.removeAttribute("songError");
		}
		
		templateEngine.process("/WEB-INF/home.html", ctx, response.getWriter());
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
 	}
	
}