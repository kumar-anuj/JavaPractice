# `OllamaAssistant` — Detailed Explanation

[`OllamaAssistant.java`](./OllamaAssistant.java) is a small Java client that sends a question to a locally running Ollama server and returns Ollama's HTTP response.

## 1. Overall functionality

The class performs the following workflow:

1. Creates a reusable Java HTTP client.
2. Accepts a user question.
3. Escapes characters that could break the JSON request.
4. Builds an Ollama chat request.
5. Sends the request to `http://localhost:11434/api/chat`.
6. Waits for the complete model response.
7. Validates the HTTP status code.
8. Returns Ollama's raw JSON response.

The execution flow is:

```text
OllamaAssistant.main()
        |
        v
assistant.ask(question)
        |
        v
Build JSON request
        |
        v
POST http://localhost:11434/api/chat
        |
        v
Ollama generates a response
        |
        v
Java receives JSON
        |
        v
Java prints the response
```

## 2. Package declaration

```java
package com.practice.general.basic;
```

This places the class in the `com.practice.general.basic` package. The source file is stored in the matching directory:

```text
src/com/practice/general/basic/OllamaAssistant.java
```

Other Java classes can import it with:

```java
import com.practice.general.basic.OllamaAssistant;
```

## 3. Imported classes

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
```

These are built-in Java classes:

- `URI` represents the Ollama endpoint.
- `HttpClient` sends HTTP requests.
- `HttpRequest` describes the outgoing request.
- `HttpResponse` contains the server response.
- `Duration` defines connection and request timeouts.

The implementation uses Java's standard HTTP API, so an external HTTP client library is not required.

## 4. Ollama endpoint

```java
private static final String OLLAMA_URL =
        "http://localhost:11434/api/chat";
```

This constant identifies Ollama's local chat API:

```text
http://localhost
```

The request is sent to the same computer running the Java application.

```text
:11434
```

Port `11434` is Ollama's default server port.

```text
/api/chat
```

This is Ollama's chat endpoint.

The constant is `private`, `static`, and `final`:

- `private`: only this class can access it.
- `static`: it belongs to the class rather than an individual object.
- `final`: it cannot be reassigned.

## 5. HTTP client

```java
private final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
```

This creates one reusable HTTP client when an `OllamaAssistant` object is constructed.

### Connection timeout

```java
.connectTimeout(Duration.ofSeconds(10))
```

The client waits up to 10 seconds while attempting to connect to Ollama. This is different from the five-minute timeout configured on the individual request.

Reusing one client is more efficient than constructing a new client for every question.

## 6. The `ask` method

```java
public String ask(String question) throws Exception
```

This method sends one question to Ollama and returns the response body as a string.

The `question` parameter contains the user's prompt. The method declares `throws Exception`, so connection, timeout, URI, and interruption errors are propagated to the caller.

For production code, it would be better to handle more specific exceptions rather than declaring the broad `Exception` type.

## 7. Escaping the question

```java
String escapedQuestion = question
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\r", "\\r")
        .replace("\n", "\\n");
```

The question is inserted manually into a JSON string, so special characters must be escaped.

### Backslashes

```java
.replace("\\", "\\\\")
```

Converts a single backslash into two backslashes. This prevents file paths or other backslashes from being interpreted as invalid JSON escape sequences.

### Double quotes

```java
.replace("\"", "\\\"")
```

Escapes quotation marks inside the question. For example:

```text
What is a "class"?
```

becomes JSON-safe text containing `\"`.

### Carriage returns and newlines

The final two replacements convert carriage returns and newline characters into JSON escape sequences so that a multi-line question remains valid JSON.

### Limitation

Manual JSON escaping is fragile. Other control characters, such as tabs, may require additional handling. A JSON library such as Jackson is safer for production code.

## 8. JSON request body

```java
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
```

The Java text block creates a readable multi-line JSON string. Text blocks are supported by Java 15 and later; Java 21 supports them.

### Model

```json
"model": "llama3.2"
```

This tells Ollama which installed model should answer the question. The name must match an installed model:

```powershell
ollama list
```

If the installed model is tagged as `llama3.2:1b`, the Java request should use:

```json
"model": "llama3.2:1b"
```

### System message

```json
{
  "role": "system",
  "content": "You are a helpful assistant inside a Java application."
}
```

The system message defines the assistant's behavior and gives the model context before the user question.

### User message

```json
{
  "role": "user",
  "content": "%s"
}
```

`.formatted(escapedQuestion)` replaces `%s` with the escaped question.

### Streaming

```json
"stream": false
```

With streaming disabled, Ollama waits until the full response has been generated and then returns one JSON response. This matches the use of `HttpResponse.BodyHandlers.ofString()` later in the code.

With streaming enabled, Ollama would return multiple partial JSON objects and the Java client would need to process them incrementally.

## 9. HTTP request construction

```java
HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(OLLAMA_URL))
        .timeout(Duration.ofMinutes(5))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
        .build();
```

This creates the outgoing HTTP request.

- `URI.create(OLLAMA_URL)` sets the destination.
- `timeout(Duration.ofMinutes(5))` allows up to five minutes for the complete request.
- The `Content-Type` header tells Ollama that the body is JSON.
- `POST(...)` sends the JSON body using HTTP POST.
- `build()` creates the immutable request object.

The five-minute request timeout is important because local model generation can be slow, especially when CPU inference is used or the machine has limited memory.

## 10. Sending the request

```java
HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString()
);
```

`client.send()` is synchronous. The current thread waits until Ollama:

1. Returns a response.
2. Times out.
3. Fails to connect.
4. Is interrupted.

`BodyHandlers.ofString()` reads the complete response body into a `String`.

A successful response is approximately:

```json
{
  "model": "llama3.2",
  "message": {
    "role": "assistant",
    "content": "An interface defines a contract..."
  },
  "done": true
}
```

## 11. HTTP status validation

```java
if (response.statusCode() != 200) {
```

The code expects HTTP status `200 OK`. Any other status is treated as an error.

```java
throw new IllegalStateException(
        "Ollama returned HTTP " + response.statusCode()
                + ": " + response.body()
);
```

The exception includes both the status code and Ollama's response body. This helps diagnose errors such as:

- Ollama is not running.
- The model name is incorrect.
- The request JSON is invalid.
- Ollama encountered an internal error.

## 12. Return value

```java
return response.body();
```

The method currently returns the complete raw JSON response, not only the assistant's generated text.

For example, the caller receives:

```json
{
  "message": {
    "role": "assistant",
    "content": "Generated answer"
  }
}
```

The application would need to parse the JSON and read `message.content` to return only:

```text
Generated answer
```

## 13. The `main` method

```java
public static void main(String[] args) throws Exception {
```

This is the Java entry point.

```java
System.out.println("In Main");
```

Prints a diagnostic message confirming that execution entered `main`.

```java
OllamaAssistant assistant = new OllamaAssistant();
```

Creates an `OllamaAssistant` object and initializes its reusable `HttpClient`.

```java
String response = assistant.ask(
        "Explain the difference between an interface and an abstract class."
);
```

This calls `ask()`, sends the question to Ollama, waits for the response, and stores the raw JSON in `response`.

```java
System.out.println("Print response");
System.out.println(response);
```

These lines print a diagnostic message and then print Ollama's raw JSON response.

## 14. Prerequisites

Ollama must be installed and running:

```powershell
ollama version
ollama list
```

If the Java code uses `llama3.2`, that model name must be installed. If the installed model is `llama3.2:1b`, update the JSON request to use `llama3.2:1b`.

The Ollama server must be reachable at:

```text
http://localhost:11434
```

## 15. Current limitations

### Raw JSON is returned

The class does not extract `message.content`; it returns the entire response body.

### No conversation history

Every call sends only one system message and one user message. Previous questions and answers are not remembered.

For example, the second call does not know the first call unless the application explicitly sends the previous messages again.

### Manual JSON construction

Building JSON with string formatting can become fragile as the request becomes more complex. A JSON library should be used for production code.

### Broad exception handling

`throws Exception` is convenient for a sample, but production code should handle specific exceptions such as connection errors, timeouts, I/O errors, and interruptions.

### Synchronous execution

`client.send()` blocks the calling thread while the model generates its response. A desktop UI or web application may need `sendAsync()` or a background executor to avoid blocking its main thread.

### No authentication

The local Ollama endpoint normally does not require an API key. If Ollama is exposed beyond the local machine, network access control and authentication should be added.

## 16. Suggested next improvement

The most useful improvement is to parse Ollama's JSON response and return only the assistant's text instead of returning the complete raw JSON document. Jackson or another JSON library can read:

```json
{
  "message": {
    "content": "Generated answer"
  }
}
```

and extract:

```text
Generated answer
```
