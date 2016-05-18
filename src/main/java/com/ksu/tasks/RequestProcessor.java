package com.ksu.tasks;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
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
            if (!file.exists()) {
                writeNotFound(socket.getOutputStream());
            } else {
                String contentType = Files.probeContentType(file.toPath());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> requestBody = new ArrayList<>(10);
        while (true) {
            String s = br.readLine();
            requestBody.add(s);
            if(s == null || s.trim().length() == 0) {
                break;
            }
        }
        String[] head = requestBody.get(0).split(" ");
        Request request = new Request();
        request.setMethod(head[0]);
        request.setUrl(head[1]);
        return request;
    }

    private void writeNotFound(OutputStream os) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n";
        os.write(response.getBytes());
        os.flush();
    }

    private void writeOk(OutputStream os, String s) throws Throwable {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Server: YarServer/2009-09-09\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + s.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + s;
        os.write(result.getBytes());
        os.flush();
    }



}
