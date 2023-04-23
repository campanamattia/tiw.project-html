package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.EditType;


public class AddPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public AddPlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		SongDAO songDAO = new SongDAO(this.connection);
		PlaylistDAO playlistDAO = new PlaylistDAO(this.connection);
		String userName = (String)session.getAttribute("user");
		String playlistError = null; // will be sent to the playlist page
		String error = null; //will be sent to the home page
		
		//taking playlistName parameter and checking whether it is valid or not
		String playlistName = request.getParameter("playlistName");
		
		if(playlistName == null || playlistName.isEmpty()) {
			error = "Something went wrong";
		}
		
		if(error == null) {
			try {
				if( playlistName.length() > 50 || playlistDAO.taken(playlistName,userName) ) {
					error = "Something went wrong";
				}
			}
			catch(SQLException e) {
				error = "Database error, try again";
			}
		}
		
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//taking the selected songs and checking whether they are valid or not
		ArrayList<Integer> songsToAdd = new ArrayList<Integer>();
		
		try {
			int maxSize = songDAO.getNumOfSongsbyUser(userName);
			for(Integer i=0; i<maxSize;i++) {
				
				String song = request.getParameter("song"+i.toString());
				if(song != null) { //This song has been chosen
					
					Integer songId = Integer.parseInt(song);
					if(songDAO.belongTo(songId, userName) ) {
						songsToAdd.add(songId);
					}
				}
			}
			if(songsToAdd.isEmpty()) {
				playlistError = "You must select at least one song";
			}
		}
		catch(SQLException e) {
			playlistError = "Database error, try again";
		}
		catch(NumberFormatException e1) {
			error = "Something went wrong";
		}
		
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		if(playlistError != null) {
			session.setAttribute("playlistName", playlistName);
			session.setAttribute("editType", EditType.CREATE);
			session.setAttribute("playlistError", playlistError);
			
			String path = servletContext.getContextPath() + "/Playlist";
			RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
			dispatcher.forward(request,response);
		}
		
		try {
			if(playlistDAO.addPlaylistWithSongs(playlistName, userName, new Date(System.currentTimeMillis()), (Integer[])songsToAdd.toArray())) {
				
			}
		} catch(SQLException e) {
			error = "Database error, try again";
		}
		
		if(error != null) {
			session.setAttribute("generalError", error);
		}
		String path = servletContext.getContextPath() + "/Home";
		response.sendRedirect(path);		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
