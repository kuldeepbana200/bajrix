# BajriX — Multi-Seller Product Marketplace

A full-stack marketplace for construction and home-building products where multiple sellers can offer the same product with different prices, stock levels, minimum order quantities, and availability.

## Overview

BajriX separates the **Product** from a **Seller Listing**:

- A **Product** is a catalogue item, such as UltraTech PPC Cement 50 kg.
- A **Seller Listing** represents a specific seller offering that product, including price, stock, MOQ, and listing status.

The implementation uses:

- **Frontend:** React + Vite + Tailwind CSS
- **Backend:** Java + Spring Boot
- **Database:** PostgreSQL
- **Database migrations:** Flyway
- **API documentation:** Swagger / OpenAPI
- **Testing:** JUnit 5 + Mockito

Production-grade authentication is intentionally not implemented because it was not required by the challenge. Seller identity is represented through the `X-Seller-Id` request header, while admin operations use a lightweight admin token.

---

## Features

### Buyer

- Browse products
- Search products by name
- Filter by category
- Sort and paginate products
- View product details
- Compare available seller listings for a product
- See seller-specific price, stock, MOQ, and availability
- Only active listings from approved sellers are exposed to buyers

### Seller

- View own listings
- Add a product to their catalogue
- Set price
- Set stock
- Set minimum order quantity
- Update an existing listing
- Stop selling a product
- Backend ownership checks prevent one seller from modifying another seller's listing

### Admin

- Admin login
- View registered sellers
- Approve sellers
- Reject sellers with a reason
- Manage products through protected admin endpoints

---

## Business Rules

The main listing rules are enforced in the backend service layer.

1. Only approved sellers can create listings.
2. A seller cannot create duplicate listings for the same product.
3. Price must be greater than zero.
4. Stock cannot be negative.
5. Minimum order quantity must be greater than zero.
6. Minimum order quantity cannot exceed available stock.
7. Buyers only see `ACTIVE` listings.
8. Buyers only see listings belonging to `APPROVED` sellers.
9. A seller can only update or stop their own listings.
10. Missing sellers, products, and listings produce appropriate API errors.
11. Seller status affects buyer visibility.
12. Listing updates use JPA optimistic locking through `@Version`.

These rules are backed by both service-level validation and PostgreSQL constraints where appropriate.

---

## Architecture

```text
React Frontend
      |
      | REST / JSON
      v
Spring Boot REST API
      |
      +--> Controllers
      |
      +--> Services
      |      |
      |      +--> Business rules
      |
      +--> Repositories
             |
             v
        PostgreSQL
```

### Backend layers

- **Controllers** handle HTTP requests and responses.
- **DTOs** define request/response contracts and validation.
- **Services** contain business rules and authorization boundaries.
- **Repositories** handle database access through Spring Data JPA.
- **Entities** represent database models.
- **Exceptions + GlobalExceptionHandler** provide consistent API errors.
- **Flyway** manages schema migrations.

---

## Database Model

Main tables:

### `products`

Stores catalogue-level product information:

- id
- name
- description
- category
- unit
- created_at

### `sellers`

Stores seller information and approval state:

- id
- name
- status
- business/contact details
- rejection reason
- created_at

Seller status can be:

```text
APPROVED
PENDING
REJECTED
```

### `seller_listings`

Connects sellers with products and stores seller-specific information:

- id
- seller_id
- product_id
- price
- stock
- minimum_order_quantity
- status
- version
- created_at
- updated_at

Listing status can be:

```text
ACTIVE
STOPPED
```

There is a unique constraint on:

```text
(seller_id, product_id)
```

which prevents duplicate seller/product listings.

---

## Concurrency

Seller listings use JPA optimistic locking:

```java
@Version
private Long version;
```

This allows the persistence layer to detect stale updates when multiple processes attempt to modify the same listing.

A sophisticated distributed locking system was intentionally not implemented because the challenge does not require production-scale distributed infrastructure.

---

## API

Important endpoints include:

### Products

```text
GET    /api/products
GET    /api/products/{id}
GET    /api/products/{id}/listings
POST   /api/products
PUT    /api/products/{id}
```

### Seller listings

```text
GET    /api/seller/listings
POST   /api/seller/listings
PUT    /api/seller/listings/{listingId}
PATCH  /api/seller/listings/{listingId}/stop
```

Seller requests use:

```text
X-Seller-Id
```

to represent the currently selected seller.

### Sellers

```text
GET    /api/sellers
GET    /api/sellers/{id}
POST   /api/sellers
```

### Admin Operations

```text
POST   /api/admin/login
POST   /api/admin/logout
GET    /api/admin/sellers
PATCH  /api/admin/sellers/{id}/approve
PATCH  /api/admin/sellers/{id}/reject
```

Admin-protected endpoints use:

```text
X-Admin-Token
```

---

## Running Locally

### Prerequisites

Install:

- Java 25
- Maven
- PostgreSQL
- Node.js and npm

The project currently uses PostgreSQL directly through pgAdmin/PostgreSQL rather than Docker.

### 1. Create the database

Create a PostgreSQL database:

```text
Database: bajrix
Username: bajrix
Password: bajrix
Host: localhost
Port: 5432
```

Update `backend/src/main/resources/application.properties` if your local credentials differ.

### 2. Start the backend

```bash
cd backend
mvn clean spring-boot:run
```

Backend:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Flyway applies the database migrations automatically.

Sample data is initialized when the database is empty.

### 3. Start the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Then open the Vite URL shown in the terminal.

---

## Testing

The project contains focused service-level tests for the main seller-listing business rules.

Examples include:

- Approved seller listings are visible
- Pending seller listings are hidden
- Stopped listings are hidden
- Seller can update their own listing
- Seller cannot update another seller's listing
- Duplicate seller/product listings are rejected
- Negative prices are rejected
- Negative stock is rejected
- Invalid MOQ is rejected
- MOQ greater than stock is rejected
- Pending/rejected sellers cannot create listings
- Missing sellers/products are handled
- Seller can stop their own listing
- Seller cannot stop another seller's listing
- Already stopped listings remain stopped

Run tests with:

```bash
cd backend
mvn clean test
```

---

## Sample Data

The application includes sample data demonstrating:

- Multiple sellers offering the same product
- Different seller prices
- Different stock quantities
- Different minimum order quantities
- Approved sellers
- Pending sellers
- Rejected sellers

This makes the buyer, seller, and admin workflows testable immediately after startup.

---

## Assumptions

### Seller visibility

Only listings belonging to approved sellers are visible to buyers.

### Listing availability

A stopped listing is not shown to buyers.

### Seller authentication

Production authentication was outside the scope of the challenge. A seller selector/request header is used instead.

### Admin authentication

A lightweight in-memory admin token mechanism is used rather than a complete identity/authentication system.

### Product creation

Products are catalogue entities. Seller-specific commercial information belongs in `seller_listings`.

### Validation

Business-critical validation is enforced in the service layer and supported by database constraints.

---

## Intentionally Not Implemented

The challenge explicitly does not require:

- Payment processing
- Checkout
- Request for Quote workflows
- Production authentication
- Email/SMS
- Mobile applications
- Cloud deployment
- Pixel-perfect UI

Docker was also not used because it is optional and the application runs directly against PostgreSQL.

---

## Scalability Considerations

The challenge mentions a possible future scale of approximately:

- 1,000,000 products
- 100,000 sellers
- 10,000,000 seller listings

The current implementation avoids loading the entire product catalogue for buyer search by using database-backed pagination.

Indexes are present for commonly queried fields such as:

- product name
- product category
- listing product
- listing seller
- listing status

If the system reached millions of records, I would reconsider:

- Full-text/search-engine based product discovery
- Keyset pagination for very deep pages
- Read replicas
- Database partitioning where justified
- Caching for high-read product/catalogue data
- More specialized indexing
- Separate search infrastructure
- Distributed authentication and authorization
- Rate limiting
- Observability and centralized logging
- More robust concurrency and transaction strategies

These would be considered based on actual workload rather than added prematurely.

---

## Error Handling

The backend uses custom exceptions for common application failures:

- `ResourceNotFoundException`
- `BusinessException`

A global exception handler converts these into consistent HTTP responses.

Validation errors are returned as client errors instead of allowing invalid data to reach the database.

---

## Project Structure

```text
BajriX/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bajrix/backend/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── ...
│
├── README.md
└── AI_USAGE.md
```

---

## Improvements With More Time

If additional development time were available, I would prioritize:

1. Stronger authentication and authorization
2. Better frontend loading/error/empty states
3. More integration tests
4. More complete optimistic-locking integration coverage
5. Automated API integration tests
6. Better search for large catalogues
7. Improved admin workflows
8. Production-grade configuration and secret management
9. Observability and structured logging
10. Deployment automation

---

## Challenge Alignment

The implementation focuses on the core engineering requirements:

- Product/listing separation
- Multi-seller catalogue
- Seller-specific pricing and inventory
- Seller ownership boundaries
- Seller approval states
- Validation and business rules
- Search and discovery
- PostgreSQL persistence
- REST API design
- React frontend
- Automated tests
- Optimistic locking consideration
- Documentation and AI usage disclosure
