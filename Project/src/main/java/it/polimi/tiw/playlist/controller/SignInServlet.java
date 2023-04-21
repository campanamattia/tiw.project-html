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


@WebServlet("/Sign In")
public class SignInServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public SignInServlet() {
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
				if(new UserDAO(this.connection).authentication(userName, password)) {
					HttpSession session = request.getSession(true);
					if(session.isNew())
						session.setAttribute("user", userName);
				} else {
					error+="Wrong UserName or Password";
				}
			} catch (SQLException e) {
				error+=e.toString();
			}
		}
		if(!error.equals("")) {
			String path = "/sign-in.html";
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