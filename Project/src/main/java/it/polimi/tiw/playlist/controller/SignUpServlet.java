package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.UserDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;


@WebServlet("/Sign Up")
public class SignUpServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public SignUpServlet() {
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
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String error = "";
		
		if(userName == null || password == null || userName.isEmpty() || password.isEmpty())
			error += "Missing parameters;";
		else {
			try {
				if(! new UserDAO(this.connection).registration(userName, password))
					error+="UserName already taken";
			} catch (SQLException e) {
				error+= e.toString();
			}
		}
		if(!error.equals("")) {
			String path = "/sign-up.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("error", error);
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} else {
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
  }
}
