package com.ksu.tasks;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestProcessor implements Runnable {

    private static final String WEBAPP_DIRECTORY = "webapps";
    private static final String NOT_FOUND_RESPONSE = "HTTP/1.1 404 Not Found\n";
    private Socket socket;

    public RequestProcessor(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void run() {
        try {
            Request request = parseRequest(socket.getInputStream());
            request.getMethod();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> requestBody = new ArrayList<String>(10);
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

}
