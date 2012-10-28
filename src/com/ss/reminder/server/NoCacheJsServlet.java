package com.ss.reminder.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.ss.reminder.server.util.ServerUtil;

@SuppressWarnings("serial")
public class NoCacheJsServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(NoCacheJsServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF8");
		resp.addHeader("Pragma", "no-cache");
		resp.addHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		resp.addHeader("Cache-Control", "must-revalidate");
		resp.addHeader("Expires", "Mon, 8 Aug 2006 10:00:00 GMT");
		resp.setContentType("application/x-javascript");

		String sp = req.getServletPath();
		String prefix = ServerUtil.isDev() ? "/" : "/WEB-INF";

		String fileUrl = prefix + sp;

		ServletContext ctx = getServletContext();
		InputStream in = null;
		try {
			log.log(Level.FINE, "Getting nocache.js from url: " + fileUrl);
			in = ctx.getResourceAsStream(fileUrl);
			ServletOutputStream out = resp.getOutputStream();
			IOUtils.copy(in, out);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
