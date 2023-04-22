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


public class SongServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private String imgFolderPath;
	private String audioFolderPath;
	
	public SongServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		audioFolderPath = context.getInitParameter("audioFolderPath");
		
		try {
			this.connection = ConnectionHandler.getConnection(context);
			this.templateEngine = TemplateHandler.getTemplateEngine(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		
		String songTitle = (String)request.getAttribute("songTitle");
		String genre = (String)request.getAttribute("genre");
		String singer = (String)request.getAttribute("singer");
		String fileImage = (String)request.getAttribute("fileImage");
		String year = (String)request.getAttribute("year");
		
		Part fileAudio = (Part)request.getAttribute("fileAudio");
		Part albumTitle = (Part)request.getAttribute("albumTitle");

	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
