package com.ksu.tasks;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RequestProcessor implements Runnable {

	private static final String WEBAPP_DIRECTORY = "webapps";
	private Socket socket;

	public RequestProcessor(Socket socket) throws IOException {
		this.socket = socket;
	}

	public void run() {
		try {
			Request request = parseRequest(socket.getInputStream());
			request.getMethod();
			File file = new File(WEBAPP_DIRECTORY + request.getUrl());
			Path path = file.toPath();
			if (!file.exists()) {
				writeResponse(socket.getOutputStream(), "Not found".getBytes(), 404);
			} else {
				byte[] bytes = Files.readAllBytes(path);
				writeResponse(socket.getOutputStream(), bytes, 200);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private Request parseRequest(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		List<String> requestBody = new ArrayList<>(10);
		while (true) {
			String s = br.readLine();
			requestBody.add(s);
			if (s == null || s.trim().length() == 0) {
				break;
			}
		}
		String[] head = requestBody.get(0).split(" ");
		Request request = new Request();
		request.setMethod(head[0]);
		request.setUrl(head[1]);
		return request;
	}

	private void writeResponse(OutputStream os, byte[] bytes, int status) throws IOException {
		String response = status == 200 ? "HTTP/1.1 200 OK\r\n" : "HTTP/1.1 404 Not found\r\n";
		response += "Server: MyMegaServer/2016-05-17\r\n" +
				"Content-Type: text/html\r\n" +
				"Content-Length: " + bytes.length + "\r\n" +
				"Connection: close\r\n\r\n";
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		result.write(response.getBytes());
		result.write(bytes);
		os.write(result.toByteArray());
		os.flush();
	}


}
