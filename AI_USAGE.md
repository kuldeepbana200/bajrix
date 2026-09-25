# AI Usage

## Tools Used

I used AI-assisted development tools, primarily ChatGPT, during the BajriX Full-Stack Engineering Challenge.

AI assistance was used as a development aid, not as a replacement for understanding or validating the implementation.

## What AI Was Used For

The primary uses were:

- Planning the application architecture
- Breaking the challenge requirements into implementation tasks
- Discussing the Product vs Seller Listing data model
- Designing REST API structures
- Reviewing Spring Boot service-layer business rules
- Generating and improving unit-test cases
- Debugging Java/Spring Boot compilation and test failures
- Reviewing validation and authorization boundaries
- Suggesting README/documentation structure
- Checking edge cases such as duplicate listings, invalid stock/MOQ, seller status, and listing ownership
- Discussing PostgreSQL indexing and scalability considerations

AI suggestions were always checked against the actual project code and challenge requirements.

## Example of Correcting AI Output

One concrete example involved the `SellerListingServiceTest` tests.

An initial AI-generated test setup used assumptions about repository behavior and method signatures that did not exactly match the implemented service.

After compiling and running the tests, the actual implementation showed that:

```java
createListing(
    Long sellerId,
    Long productId,
    BigDecimal price,
    Integer stock,
    Integer minimumOrderQuantity
)
```

and:

```java
updateListing(
    Long sellerId,
    Long listingId,
    BigDecimal price,
    Integer stock,
    Integer minimumOrderQuantity
)
```

were the actual service signatures.

The tests were then corrected to match the real implementation.

There was also an issue where tests for listing visibility did not stub the product lookup performed by the service. The resulting `ResourceNotFoundException` exposed the mismatch between the test assumption and the actual service flow. The tests were corrected to mock the required product lookup.

Similarly, tests for pending/rejected sellers initially included unnecessary product stubbing even though the service rejects the seller before reaching the product lookup. Those unnecessary stubs were removed.

This was a useful example of treating AI-generated code as a starting point and using compilation/test results and the actual source code as the final authority.

## Development Principle

The important principle followed throughout the project was:

> AI can help generate and review code, but the developer is responsible for understanding, validating, debugging, and modifying the final implementation.

The submitted implementation was therefore checked against the challenge requirements and tested locally rather than blindly accepting generated code.
