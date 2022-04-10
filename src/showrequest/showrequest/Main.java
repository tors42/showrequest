package showrequest;

class Main {

    public static void main(String[] args) throws Exception {
        String portString = System.getenv("PORT");
        if (portString == null) portString = "8000";

        int port = Integer.parseInt(portString);

        var httpServer = com.sun.net.httpserver.HttpServer.create(
                new java.net.InetSocketAddress(
                    "0.0.0.0",
                    port),
                /*backlog*/ 0);

        httpServer.createContext("/", exchange -> {

            String requestLine = "%s %s %s".formatted(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI(),
                    exchange.getProtocol());

            String flattenedHeaders = String.join("\n",
                    exchange.getRequestHeaders().entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream()
                        .map(value -> Main.stripSensitive(entry.getKey(), value))
                        )
                    .toList()
                    );

            String body = new String(exchange.getRequestBody().readAllBytes());

            String requestData = body.isBlank() ?
                "%s%n%s".formatted(requestLine, flattenedHeaders) :
                "%s%n%s%n%n%s".formatted(requestLine, flattenedHeaders, body);

            byte[] bytes = requestData.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();

            System.out.println(requestData);
        });

        httpServer.start();
    }

    static String stripSensitive(String header, String value) {
        String stripped = switch(header.toLowerCase()) {
            case "authorization" -> value.split(" ").length == 2 ? "%s ***".formatted(value.split(" ")[0]) : "Not on format \"<scheme> ***\"";
            case "x-forwarded-for" -> "***";
            default -> value;
        };
        return "%s: %s".formatted(header, stripped);
    }
}
