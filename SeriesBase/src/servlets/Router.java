package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import business.SeriesControl;
import business.UsersControl;

import exceptions.AccessDeniedException;
import exceptions.ForbiddenException;
import exceptions.NoLoginException;

/**
 * This class is the controller of the seriesbase website. It works like a
 * internet router, receiving the requests from the JSP's files, and then
 * forwarding to another page. According to the request, could be necessary,
 * some work of the business layer of the application, became the role of
 * calling to this class.
 * 
 * @author Edgar Mateus
 * @author Nuno Dias
 * @author Tiago Amaral
 * 
 * @version April 2011
 */
@SuppressWarnings("serial")
@WebServlet("/router")
public class Router extends HttpServlet {

	/**
	 * Process get and post requests.
	 * 
	 * @param req
	 *            HttpRequest
	 * @param resp
	 *            HttpResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String link = req.getParameter("link");
			String seriesAction = req.getParameter("seriesAction");
			String userAction = req.getParameter("userAction");
			if (link != null)
				processLinks(req, resp, link);
			else if (userAction != null)
				processUserActions(req, resp, userAction);
			else if (seriesAction != null)
				processSeriesActions(req, resp, seriesAction);
			else
				throw new ForbiddenException();

		} catch (NoLoginException e) {
			if (req.getSession().getAttribute("tempAddress") == null)
				req.getSession().setAttribute("tempAddress", e.getPageDenied());
			resp.sendRedirect("login.jsp");
		} catch (ForbiddenException e) {
			resp.sendRedirect("error.jsp");
		} catch (AccessDeniedException e) {
			resp.sendRedirect("forbidden.jsp");
		}
	}

	/**
	 * Process all the requests for new pages.
	 */
	private void processLinks(HttpServletRequest req, HttpServletResponse resp,
			String link) throws NoLoginException, ServletException, IOException {
		if (link.equals("userControlPanel")) {
			if (req.getSession().getAttribute("loginID") == null)
				throw new NoLoginException(link);
			else
				resp.sendRedirect("userControlPanel.jsp");

		} else if (link.equals("login")) {
			resp.sendRedirect("login.jsp");
		}
	}

	/**
	 * Process all the requests for the user related actions.
	 * 
	 * @throws ForbiddenException
	 *             When the action or the context is inexistent.
	 * @throws AccessDeniedException
	 *             When the action needs a login.
	 */
	private void processUserActions(HttpServletRequest req,
			HttpServletResponse resp, String action) throws ServletException,
			IOException, ForbiddenException, AccessDeniedException {
		if (action.equals("login")) {
			UsersControl.login(req);
			String tempAddress = (String) req.getSession().getAttribute(
					"tempAddress");
			if (tempAddress != null) {
				resp.sendRedirect(tempAddress);
				req.getSession().removeAttribute("tempAddress");
			} else {
				String referer = req.getHeader("Referer");
				if (referer != null)
					resp.sendRedirect(referer);
				else
					throw new ForbiddenException();
			}
		} else if (action.equals("updateUserData")) {
			int nErrors = UsersControl.updateUserInfo(req);
			if (nErrors == 0)
				req.getSession().setAttribute("updateDone", true);

			resp.sendRedirect("userControlPanel.jsp");
		} else if (action.equals("logout")) {
			if (req.getHeader("Referer") == null)
				throw new AccessDeniedException();
			UsersControl.logout(req.getSession());
			resp.sendRedirect(req.getHeader("Referer"));
		} else if (action.equals("register")) {
			int nErrors = UsersControl.register(req);
			if (nErrors == 0) {
				req.getSession().setAttribute("registerDone", true);
				resp.sendRedirect("login.jsp");
			} else
				resp.sendRedirect("register.jsp");
		}
	}

	/**
	 * Process all the requests for the series related actions.
	 * 
	 * @throws ForbiddenException
	 *             When there are errors.
	 * @throws NoLoginException
	 */
	private void processSeriesActions(HttpServletRequest req,
			HttpServletResponse resp, String action) throws ServletException,
			IOException, ForbiddenException, NoLoginException {
		if (req.getHeader("Referer") == null)
			throw new ForbiddenException();

		try {
			if (action.equals("getAll"))
				SeriesControl.getAllSeries(req);
			else if (action.equals("getById"))
				SeriesControl.getById(req);
			else if (action.equals("getByUserId"))
				SeriesControl.getByUserId(req);
			else if (action.equals("addToFavorites")) {
				SeriesControl.addSeriesToFavorites(req);
				resp.sendRedirect(req.getHeader("Referer"));
			}
		} catch (NumberFormatException e) {
			throw new ForbiddenException();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
}
