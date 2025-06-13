# Order Processing System – HTTP API Testing Guide

## 🔧 Prerequisites

- Java 17+
- Maven or Gradle (for building the Spring Boot project)
- IntelliJ IDEA or Visual Studio Code (recommended)
- Spring Boot App must be running on: `http://localhost:8080`

---

## 🚀 HTTP API Testing

You can test the system using an `.http` file in IntelliJ or VS Code.

### 🔹 Create HTTP Test File

Create a file named:

```
order-test.http
```

Place it inside the root directory or `src/test/resources/`.

---

### 📩 HTTP Request Snippets

#### 1. Register a User

```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "userId": "user1",
  "name": "John Doe"
}
```

---

#### 2. Place an Order

```http
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "userId": "user1",
  "items": [
    { "itemId": "item1", "quantity": 1 },
    { "itemId": "item2", "quantity": 2 }
  ]
}
```

---

#### 3. Get Orders by User

```http
GET http://localhost:8080/api/orders/user/user1
```

---

#### 4. Get Specific Order

```http
GET http://localhost:8080/api/orders/{orderId}
```

Replace `{orderId}` with the actual order ID from step 3 response.

---

#### 5. Cancel Order

```http
POST http://localhost:8080/api/orders/cancel/{orderId}
```

Only works if the order is in `PENDING` state.

---

## 🧪 Running the Requests

### IntelliJ IDEA
- Open `order-test.http`
- Click the **Run icon** above each request
- OR press **Ctrl+Enter (Cmd+Enter)**

### Visual Studio Code
- Install **REST Client Extension**
- Open `order-test.http`
- Click **“Send Request”** above each request

---

## ✅ Output

- Kafka lifecycle events and order updates are logged in the console
- Each HTTP request gives a structured `ApiResponse` JSON
