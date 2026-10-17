package com.practice.general.basic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaAssistant {
    private static final String OLLAMA_URL =
            "http://localhost:11434/api/chat";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public OllamaResponse ask(String question) throws Exception {
        System.out.println("In ask");
        String escapedQuestion = question
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");

        String requestJson = """
                {
                  "model": "llama3.2",
                  "messages": [
                    {
                      "role": "system",
                      "content": "You are a helpful assistant inside a Java application."
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "stream": false
                }
                """.formatted(escapedQuestion);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Ollama returned HTTP " + response.statusCode()
                            + ": " + response.body()
            );
        }

        return OllamaResponse.fromJson(response.body());
    }

    public static final class OllamaResponse {
        private final String model;
        private final String createdAt;
        private final Message message;
        private final boolean done;
        private final String doneReason;
        private final long totalDuration;
        private final long loadDuration;
        private final long promptEvalCount;
        private final long promptEvalCachedCount;
        private final long promptEvalDuration;
        private final long evalCount;
        private final long evalDuration;

        private OllamaResponse(
                String model,
                String createdAt,
                Message message,
                boolean done,
                String doneReason,
                long totalDuration,
                long loadDuration,
                long promptEvalCount,
                long promptEvalCachedCount,
                long promptEvalDuration,
                long evalCount,
                long evalDuration) {
            this.model = model;
            this.createdAt = createdAt;
            this.message = message;
            this.done = done;
            this.doneReason = doneReason;
            this.totalDuration = totalDuration;
            this.loadDuration = loadDuration;
            this.promptEvalCount = promptEvalCount;
            this.promptEvalCachedCount = promptEvalCachedCount;
            this.promptEvalDuration = promptEvalDuration;
            this.evalCount = evalCount;
            this.evalDuration = evalDuration;
        }

        public static OllamaResponse fromJson(String json) {
            String messageJson = extractObject(json, "message");
            Message message = new Message(
                    extractString(messageJson, "role"),
                    extractString(messageJson, "content")
            );

            return new OllamaResponse(
                    extractString(json, "model"),
                    extractString(json, "created_at"),
                    message,
                    extractBoolean(json, "done"),
                    extractString(json, "done_reason"),
                    extractLong(json, "total_duration"),
                    extractLong(json, "load_duration"),
                    extractLong(json, "prompt_eval_count"),
                    extractLong(json, "prompt_eval_cached_count"),
                    extractLong(json, "prompt_eval_duration"),
                    extractLong(json, "eval_count"),
                    extractLong(json, "eval_duration")
            );
        }

        public String getModel() {
            return model;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public Message getMessage() {
            return message;
        }

        public boolean isDone() {
            return done;
        }

        public String getDoneReason() {
            return doneReason;
        }

        public long getTotalDuration() {
            return totalDuration;
        }

        public long getLoadDuration() {
            return loadDuration;
        }

        public long getPromptEvalCount() {
            return promptEvalCount;
        }

        public long getPromptEvalCachedCount() {
            return promptEvalCachedCount;
        }

        public long getPromptEvalDuration() {
            return promptEvalDuration;
        }

        public long getEvalCount() {
            return evalCount;
        }

        public long getEvalDuration() {
            return evalDuration;
        }

        private static String extractString(String json, String key) {
            int valueStart = valueStart(json, key);
            if (valueStart < 0 || json.charAt(valueStart) != '"') {
                return null;
            }

            StringBuilder value = new StringBuilder();
            for (int index = valueStart + 1; index < json.length(); index++) {
                char character = json.charAt(index);
                if (character == '"') {
                    return value.toString();
                }
                if (character != '\\') {
                    value.append(character);
                    continue;
                }

                if (++index >= json.length()) {
                    throw new IllegalArgumentException("Invalid JSON escape in field: " + key);
                }

                character = json.charAt(index);
                switch (character) {
                    case '"', '\\', '/' -> value.append(character);
                    case 'b' -> value.append('\b');
                    case 'f' -> value.append('\f');
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case 'u' -> {
                        if (index + 4 >= json.length()) {
                            throw new IllegalArgumentException("Invalid Unicode escape in field: " + key);
                        }
                        String hex = json.substring(index + 1, index + 5);
                        value.append((char) Integer.parseInt(hex, 16));
                        index += 4;
                    }
                    default -> throw new IllegalArgumentException(
                            "Unsupported JSON escape \\" + character + " in field: " + key
                    );
                }
            }

            throw new IllegalArgumentException("Unterminated JSON string for field: " + key);
        }

        private static String extractObject(String json, String key) {
            int objectStart = valueStart(json, key);
            if (objectStart < 0 || json.charAt(objectStart) != '{') {
                throw new IllegalArgumentException("Missing JSON object: " + key);
            }

            int depth = 0;
            boolean escaped = false;
            boolean insideString = false;
            for (int index = objectStart; index < json.length(); index++) {
                char character = json.charAt(index);
                if (insideString) {
                    if (escaped) {
                        escaped = false;
                    } else if (character == '\\') {
                        escaped = true;
                    } else if (character == '"') {
                        insideString = false;
                    }
                    continue;
                }

                if (character == '"') {
                    insideString = true;
                } else if (character == '{') {
                    depth++;
                } else if (character == '}' && --depth == 0) {
                    return json.substring(objectStart, index + 1);
                }
            }

            throw new IllegalArgumentException("Unterminated JSON object: " + key);
        }

        private static boolean extractBoolean(String json, String key) {
            int start = valueStart(json, key);
            if (start < 0) {
                return false;
            }
            if (json.startsWith("true", start)) {
                return true;
            }
            if (json.startsWith("false", start)) {
                return false;
            }
            throw new IllegalArgumentException("Invalid boolean field: " + key);
        }

        private static long extractLong(String json, String key) {
            int start = valueStart(json, key);
            if (start < 0) {
                return 0L;
            }

            int end = start;
            while (end < json.length()
                    && json.charAt(end) != ','
                    && json.charAt(end) != '}') {
                end++;
            }

            return Long.parseLong(json.substring(start, end).trim());
        }

        private static int valueStart(String json, String key) {
            int keyStart = json.indexOf('"' + key + '"');
            if (keyStart < 0) {
                return -1;
            }

            int colon = json.indexOf(':', keyStart + key.length() + 2);
            if (colon < 0) {
                throw new IllegalArgumentException("Missing ':' after JSON field: " + key);
            }

            int valueStart = colon + 1;
            while (valueStart < json.length()
                    && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }
            return valueStart;
        }
    }

    public static final class Message {
        private final String role;
        private final String content;

        private Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("In Main");
        String userQuery = args[0];
        System.out.println("Use Query is:" + userQuery);
        OllamaAssistant assistant = new OllamaAssistant();

        OllamaResponse response = assistant.ask(userQuery);
        System.out.print("Print response===>");

        System.out.println(response.getMessage().getContent());
    }
}
