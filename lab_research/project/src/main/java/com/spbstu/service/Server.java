package com.spbstu.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.spbstu.dbo.Request;
import com.spbstu.dbo.User;
import com.spbstu.facade.Facade;
import com.spbstu.facade.FacadeImpl;
import com.spbstu.service.jsono.UserDto;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/request", new GetHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен");
    }

    static class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map <String,String> params = Server.queryToMap(httpExchange.getRequestURI().getQuery());
            try {
                Gson gson = new Gson();
                List<Request> requests = Server.getRequests(params.get("login"), params.get("pass"));
                response.append(gson.toJson(requests));
            } catch (Exception e) {
                response.append("error");
            }
            Server.writeResponse(httpExchange, response.toString());
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(bytes);
        os.close();
    }


    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private static List<Request> getRequests(String login, String pass) throws Exception {
        Facade facade = new FacadeImpl();
        try {
            facade.auth(login, pass);
            List<Request> requests = facade.getRequestsForClient();
            return requests;
        } finally {
            facade.logout();
        }
    }
}
