const API_BASE_URL = "http://localhost:8080/api";

export async function getProducts({
    search = "",
    category = "",
    page = 0,
    size = 12,
    sortBy = "name",
    direction = "asc",
} = {}) {
    const params = new URLSearchParams();

    if (search.trim()) {
        params.set("search", search.trim());
    }

    if (category) {
        params.set("category", category);
    }

    params.set("page", page);
    params.set("size", size);
    params.set("sort", `${sortBy},${direction}`);

    const response = await fetch(
        `${API_BASE_URL}/products?${params.toString()}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to fetch products");
    }

    return data;
}

export async function getProduct(id) {
    const response = await fetch(
        `${API_BASE_URL}/products/${id}`
    );

    if (!response.ok) {
        throw new Error("Failed to fetch product");
    }

    return response.json();
}

export async function getProductListings(productId) {
    const response = await fetch(
        `${API_BASE_URL}/products/${productId}/listings`
    );

    if (!response.ok) {
        throw new Error("Failed to fetch seller listings");
    }

    return response.json();
}

export async function getSellers() {
    const response = await fetch(
        `${API_BASE_URL}/sellers`
    );

    if (!response.ok) {
        throw new Error("Failed to fetch sellers");
    }

    return response.json();
}

export async function getSellerListings(sellerId) {
    const response = await fetch(
        `${API_BASE_URL}/seller/listings`,
        {
            headers: {
                "X-Seller-Id": sellerId
            }
        }
    );

    if (!response.ok) {
        throw new Error("Failed to fetch seller listings");
    }

    return response.json();
}

export async function createListing(
    sellerId,
    listing
) {
    const response = await fetch(
        `${API_BASE_URL}/seller/listings`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-Seller-Id": sellerId
            },
            body: JSON.stringify(listing)
        }
    );

    if (!response.ok) {
        const error = await response.json();
        throw new Error(
            error.message || "Failed to create listing"
        );
    }

    return response.json();
}

export async function updateListing(
    sellerId,
    listingId,
    listing
) {
    const response = await fetch(
        `${API_BASE_URL}/seller/listings/${listingId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-Seller-Id": sellerId
            },
            body: JSON.stringify(listing)
        }
    );

    if (!response.ok) {
        const error = await response.json();
        throw new Error(
            error.message || "Failed to update listing"
        );
    }

    return response.json();
}

export async function stopListing(
    sellerId,
    listingId
) {
    const response = await fetch(
        `${API_BASE_URL}/seller/listings/${listingId}/stop`,
        {
            method: "PATCH",
            headers: {
                "X-Seller-Id": sellerId
            }
        }
    );

    if (!response.ok) {
        const error = await response.json();
        throw new Error(
            error.message || "Failed to stop listing"
        );
    }

    return response.json();
}

export async function adminLogin(username, password) {
    const response = await fetch(`${API_BASE_URL}/admin/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            password,
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Invalid admin credentials");
    }

    return data;
}

export async function getAdminSellers(token) {
    const response = await fetch(`${API_BASE_URL}/admin/sellers`, {
        headers: {
            "X-Admin-Token": token,
        },
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to fetch sellers");
    }

    return data;
}

export async function approveSeller(token, sellerId) {
    const response = await fetch(
        `${API_BASE_URL}/admin/sellers/${sellerId}/approve`,
        {
            method: "PATCH",
            headers: {
                "X-Admin-Token": token,
            },
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to approve seller");
    }

    return data;
}

export async function rejectSeller(token, sellerId, reason) {
    const response = await fetch(
        `${API_BASE_URL}/admin/sellers/${sellerId}/reject`,
        {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "X-Admin-Token": token,
            },
            body: JSON.stringify({
                reason,
            }),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to reject seller");
    }

    return data;
}

export async function getAdminProducts(token) {
    const response = await fetch(`${API_BASE_URL}/products`);

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to fetch products");
    }

    return data;
}

export async function createProduct(token, product) {
    const response = await fetch(`${API_BASE_URL}/products`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-Admin-Token": token,
        },
        body: JSON.stringify(product),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to create product");
    }

    return data;
}

export async function updateProduct(token, productId, product) {
    const response = await fetch(
        `${API_BASE_URL}/products/${productId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-Admin-Token": token,
            },
            body: JSON.stringify(product),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Failed to update product");
    }

    return data;
}