package com.mycompany.adopciondemascotas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Servidor web pequeño para la demostración de adopciones. */
public class AdopciondeMascotas {
    private static final String PETS = """
        [
          {"id":1,"nombre":"Max","tipo":"Perro","edad":"2 años","edadMeses":24,"ubicacion":"Lima","sexo":"Macho","tamano":"Mediano","descripcion":"Cariñoso, juguetón y listo para acompañarte en todas tus aventuras.","imagen":"https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=900&q=85"},
          {"id":2,"nombre":"Luna","tipo":"Gato","edad":"1 año","edadMeses":12,"ubicacion":"Lima","sexo":"Hembra","tamano":"Pequeño","descripcion":"Una gatita tranquila y curiosa que disfruta las siestas al sol.","imagen":"https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=900&q=85"},
          {"id":3,"nombre":"Rocky","tipo":"Perro","edad":"3 años","edadMeses":36,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Grande","descripcion":"Noble y activo. Le encanta pasear y jugar con su familia.","imagen":"https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=900&q=85"},
          {"id":4,"nombre":"Milo","tipo":"Gato","edad":"6 meses","edadMeses":6,"ubicacion":"Huánuco","sexo":"Macho","tamano":"Pequeño","descripcion":"Pequeño explorador, dulce y muy sociable.","imagen":"https://images.unsplash.com/photo-1519052537078-e6302a4968d4?auto=format&fit=crop&w=900&q=85"},
          {"id":5,"nombre":"Nala","tipo":"Perro","edad":"4 años","edadMeses":48,"ubicacion":"Lima","sexo":"Hembra","tamano":"Mediano","descripcion":"Amable y serena; sueña con tener un hogar para siempre.","imagen":"https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=900&q=85"},
          {"id":6,"nombre":"Simba","tipo":"Gato","edad":"2 años","edadMeses":24,"ubicacion":"Lima","sexo":"Macho","tamano":"Mediano","descripcion":"Un compañero afectuoso que siempre está cerca para darte cariño.","imagen":"https://images.unsplash.com/photo-151猫?auto=format&fit=crop&w=900&q=85"}
        ]
        """.replace("photo-151猫", "photo-1533738363-b7f9aef128ce");

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
            try (var in = AdopciondeMascotas.class.getResourceAsStream("/public/index.html")) {
                if (in == null) { respond(exchange, 500, "text/plain; charset=utf-8", "No se encontró la página web."); return; }
                respond(exchange, 200, "text/html; charset=utf-8", new String(in.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) { respond(exchange, 500, "text/plain; charset=utf-8", "Error al cargar la página."); }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Hogar Fiel está disponible en http://localhost:" + port);
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
