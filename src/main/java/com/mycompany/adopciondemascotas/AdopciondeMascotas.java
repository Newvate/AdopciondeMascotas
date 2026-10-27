package com.mycompany.adopciondemascotas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Servidor web pequeño para la demostración de adopciones. */
public class AdopciondeMascotas {
    private static final String PETS = """
        [
          {"id":1,"codigo":"HF-001","nombre":"Max","tipo":"Perro","raza":"Labrador Retriever","edad":"2 años","edadMeses":24,"ubicacion":"Lima","sexo":"Macho","tamano":"Mediano","descripcion":"Cariñoso, juguetón y listo para acompañarte en todas tus aventuras.","imagen":"https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=900&q=85"},
          {"id":2,"codigo":"HF-002","nombre":"Luna","tipo":"Gato","raza":"Doméstico de pelo corto","edad":"1 año","edadMeses":12,"ubicacion":"Lima","sexo":"Hembra","tamano":"Pequeño","descripcion":"Una gatita tranquila y curiosa que disfruta las siestas al sol.","imagen":"https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=900&q=85"},
          {"id":3,"codigo":"HF-003","nombre":"Rocky","tipo":"Perro","raza":"Mestizo","edad":"3 años","edadMeses":36,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Grande","descripcion":"Noble y activo. Le encanta pasear y jugar con su familia.","imagen":"https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=900&q=85"},
          {"id":4,"codigo":"HF-004","nombre":"Milo","tipo":"Gato","raza":"Doméstico de pelo corto","edad":"6 meses","edadMeses":6,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Pequeño","descripcion":"Pequeño explorador, dulce y muy sociable.","imagen":"https://images.unsplash.com/photo-1519052537078-e6302a4968d4?auto=format&fit=crop&w=900&q=85"},
          {"id":5,"codigo":"HF-005","nombre":"Nala","tipo":"Perro","raza":"Golden Retriever","edad":"4 años","edadMeses":48,"ubicacion":"Lima","sexo":"Hembra","tamano":"Mediano","descripcion":"Amable y serena; sueña con tener un hogar para siempre.","imagen":"https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=900&q=85"},
          {"id":6,"codigo":"HF-006","nombre":"Simba","tipo":"Gato","raza":"Doméstico de pelo corto","edad":"2 años","edadMeses":24,"ubicacion":"Lima","sexo":"Macho","tamano":"Mediano","descripcion":"Un compañero afectuoso que siempre está cerca para darte cariño.","imagen":"https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&w=900&q=85"},
          {"id":7,"codigo":"HF-007","nombre":"Maya","tipo":"Perro","raza":"Beagle","edad":"1 año","edadMeses":12,"ubicacion":"Lima","sexo":"Hembra","tamano":"Mediano","descripcion":"Alegre y curiosa, disfruta los paseos y jugar en familia.","imagen":"https://images.unsplash.com/photo-1505628346881-b72b27e84530?auto=format&fit=crop&w=900&q=85"},
          {"id":8,"codigo":"HF-008","nombre":"Toby","tipo":"Perro","raza":"Bulldog francés","edad":"2 años","edadMeses":24,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Pequeño","descripcion":"Un compañero tranquilo y cariñoso que disfruta estar cerca de las personas.","imagen":"https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=85"},
          {"id":9,"codigo":"HF-009","nombre":"Kira","tipo":"Perro","raza":"Pastor alemán","edad":"3 años","edadMeses":36,"ubicacion":"Lima","sexo":"Hembra","tamano":"Grande","descripcion":"Inteligente y leal, busca una familia activa con quien compartir aventuras.","imagen":"https://images.unsplash.com/photo-1561037404-61cd46aa615b?auto=format&fit=crop&w=900&q=85"},
          {"id":10,"codigo":"HF-010","nombre":"Coco","tipo":"Perro","raza":"Poodle","edad":"6 meses","edadMeses":6,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Pequeño","descripcion":"Un cachorro juguetón y sociable, listo para conocer su nuevo hogar.","imagen":"https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=900&q=85"},
          {"id":11,"codigo":"HF-011","nombre":"Loki","tipo":"Perro","raza":"Husky siberiano","edad":"4 años","edadMeses":48,"ubicacion":"Lima","sexo":"Macho","tamano":"Grande","descripcion":"Activo y amistoso, será feliz con una familia que disfrute salir a pasear.","imagen":"https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=900&q=85"}
        ]
        """;

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/mascotas", exchange -> respond(exchange, 200, "application/json; charset=utf-8", PETS));
        server.createContext("/api/contacto", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                respond(exchange, 405, "application/json; charset=utf-8", "{\"mensaje\":\"Método no permitido\"}");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                respond(exchange, 400, "application/json; charset=utf-8", "{\"mensaje\":\"Completa tus datos\"}");
                return;
            }
            respond(exchange, 201, "application/json; charset=utf-8", "{\"mensaje\":\"¡Gracias! Recibimos tu mensaje y pronto nos comunicaremos contigo.\"}");
        });
        server.createContext("/", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                respond(exchange, 405, "text/plain; charset=utf-8", "Método no permitido");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            String contentType = switch (path) {
                case "/index.html" -> "text/html; charset=utf-8";
                case "/styles.css" -> "text/css; charset=utf-8";
                case "/app.js" -> "application/javascript; charset=utf-8";
                default -> null;
            };
            if (contentType == null) {
                respond(exchange, 404, "text/plain; charset=utf-8", "No encontrado");
                return;
            }
            try (var in = AdopciondeMascotas.class.getResourceAsStream("/public" + path)) {
                if (in == null) { respond(exchange, 404, "text/plain; charset=utf-8", "No se encontró el recurso."); return; }
                byte[] bytes = in.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                exchange.sendResponseHeaders(200, bytes.length);
                try (var out = exchange.getResponseBody()) { out.write(bytes); }
            } catch (IOException e) { respond(exchange, 500, "text/plain; charset=utf-8", "Error al cargar el recurso."); }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Huellas está disponible en http://localhost:" + port);
    }

    private static void respond(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var out = exchange.getResponseBody()) { out.write(bytes); }
    }
}
