package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;



public class GetImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private String imgFolderPath;
	
	public GetImageServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		try {
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
