# Comment Service Plugin for Atlassian (Jira/Confluence)

This is a lightweight RESTful comment service implemented as an Atlassian plugin using the Atlassian Java SDK.
It allows storing, retrieving, and filtering comments in memory.

---

## Features

- Add new comments
- Retrieve comments by `issueId` or `author`
- JSON-based REST API
- In-memory data storage using `ConcurrentHashMap`
- Jackson for JSON serialization
- SLF4J for logging
- Full JUnit test coverage

---

## How to Build & Run

### Prerequisites

- Java 11
- Atlassian SDK installed (`atlas-version` to verify)
- Maven installed (`mvn -v`)

### Build the Plugin
```bash
atlas-package
```

### Run in Jira
```bash
atlas-run
```

### Access the Plugin
You can access the comment service via the following URL:
```plaintext
http://localhost:2990/jira/rest/comment-service/1.0/comment
```

### API Usage
#### Add a Comment
- Request
```bash
- curl -X POST http://localhost:2990/jira/rest/comment-service/1.0/comment \
  -H "Content-Type: application/json" \
  -d '{"issueId": "ISSUE-123", "author": "alice", "message": "Looks good!"}'
 ```
- Response
```bash
{
  "id": "1",
  "issueId": "ISSUE-123",
  "author": "alice",
  "message": "Looks good!"
}
 ```

#### Get Comments by Issue ID
- Request
```bash
- curl -X GET "http://localhost:2990/jira/rest/comment-service/1.0/comment?issueId=ISSUE-123"
```

#### Get Comments by Author
- Request
```bash
- curl -X GET "http://localhost:2990/jira/rest/comment-service/1.0/comment?author=alice"
```

### Run tests using Maven directly
```bash
atlas-mvn test
```