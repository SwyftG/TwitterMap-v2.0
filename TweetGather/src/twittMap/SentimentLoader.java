package twittMap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SentimentLoader
 */
public class SentimentLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBManager db;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SentimentLoader() {
        super();
		db = new DBManager();
		db.getDirver();
		db.connectAWS();
    }

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		db.shutdown();
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String begin = request.getParameter("beginTime");
		String evaluation = db.getJsonFromAttitude(begin);
		response.setContentType("application/json");
		response.getWriter().println(evaluation);
	}
}
