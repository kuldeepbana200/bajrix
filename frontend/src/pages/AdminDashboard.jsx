import { useCallback, useEffect, useState } from "react";

import {
  adminLogin,
  getAdminSellers,
  approveSeller,
  rejectSeller,
  getAdminProducts,
  createProduct,
  updateProduct,
} from "../services/api";

export default function AdminDashboard() {
  const [token, setToken] = useState(
    localStorage.getItem("bajrix_admin_token"),
  );

  const [activeTab, setActiveTab] = useState("sellers");

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [sellers, setSellers] = useState([]);
  const [products, setProducts] = useState([]);

  const [loginLoading, setLoginLoading] = useState(false);

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [filter, setFilter] = useState("ALL");

  const [showProductForm, setShowProductForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  const loadSellers = useCallback(async function loadSellers(adminToken) {
    if (!adminToken) return;

    try {
      const data = await getAdminSellers(adminToken);
      setSellers(data);
    } catch (err) {
      setError(err.message);
    }
  }, []);

  const loadProducts = useCallback(async function loadProducts() {
    try {
      const data = await getAdminProducts();

      // Supports both paginated and non-paginated API responses.
      setProducts(data.content || data);
    } catch (err) {
      setError(err.message);
    }
  }, []);

  useEffect(() => {
    if (!token) return;

    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadSellers(token);
    loadProducts();
  }, [loadProducts, loadSellers, token]);

  async function handleLogin(e) {
    e.preventDefault();

    setLoginLoading(true);
    setError("");

    try {
      const data = await adminLogin(username, password);

      localStorage.setItem("bajrix_admin_token", data.token);

      setToken(data.token);
      setUsername("");
      setPassword("");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoginLoading(false);
    }
  }

  async function handleApprove(sellerId) {
    setError("");
    setMessage("");

    try {
      await approveSeller(token, sellerId);
      setMessage("Seller approved successfully.");
      await loadSellers(token);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleReject(sellerId) {
    const reason = window.prompt("Enter the reason for rejecting this seller:");

    if (!reason || !reason.trim()) {
      return;
    }

    setError("");
    setMessage("");

    try {
      await rejectSeller(token, sellerId, reason.trim());

      setMessage("Seller rejected successfully.");
      await loadSellers(token);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleProductSubmit(form) {
    setError("");
    setMessage("");

    try {
      if (editingProduct) {
        await updateProduct(token, editingProduct.id, form);
        setMessage("Product updated successfully.");
      } else {
        await createProduct(token, form);
        setMessage("Product created successfully.");
      }

      setShowProductForm(false);
      setEditingProduct(null);

      await loadProducts();
    } catch (err) {
      setError(err.message);
    }
  }

  function handleLogout() {
    localStorage.removeItem("bajrix_admin_token");

    setToken(null);
    setSellers([]);
    setProducts([]);
    setMessage("");
    setError("");
  }

  const filteredSellers =
    filter === "ALL"
      ? sellers
      : sellers.filter((seller) => seller.status === filter);

  const pendingCount = sellers.filter(
    (seller) => seller.status === "PENDING",
  ).length;

  const approvedCount = sellers.filter(
    (seller) => seller.status === "APPROVED",
  ).length;

  const rejectedCount = sellers.filter(
    (seller) => seller.status === "REJECTED",
  ).length;

  if (!token) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
        <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-sm">
          <div className="mb-8">
            <div className="mb-3 text-sm font-semibold text-gray-500">
              BAJRIX ADMIN
            </div>

            <h1 className="text-3xl font-bold text-gray-900">Admin Login</h1>

            <p className="mt-2 text-sm text-gray-500">
              Sign in to manage the marketplace.
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Username
              </label>

              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="admin"
                required
                className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none focus:border-gray-900"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Password
              </label>

              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter admin password"
                required
                className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none focus:border-gray-900"
              />
            </div>

            {error && (
              <div className="rounded-xl bg-red-50 p-4 text-sm text-red-700">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loginLoading}
              className="w-full rounded-xl bg-gray-900 px-6 py-3.5 font-semibold text-white hover:bg-gray-800 disabled:opacity-60"
            >
              {loginLoading ? "Signing in..." : "Sign In"}
            </button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="border-b bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              BajriX Admin
            </p>

            <h1 className="text-2xl font-bold text-gray-900">
              Marketplace Management
            </h1>
          </div>

          <button
            onClick={handleLogout}
            className="rounded-xl border border-gray-300 px-4 py-2 text-sm font-medium hover:bg-gray-50"
          >
            Logout
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8">
        {message && (
          <div className="mb-6 rounded-xl bg-green-50 p-4 text-sm text-green-700">
            {message}
          </div>
        )}

        {error && (
          <div className="mb-6 rounded-xl bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}

        {/* Main tabs */}

        <div className="mb-8 flex gap-2 rounded-xl bg-white p-2 shadow-sm">
          <button
            onClick={() => setActiveTab("sellers")}
            className={`rounded-lg px-5 py-2.5 text-sm font-semibold ${
              activeTab === "sellers"
                ? "bg-gray-900 text-white"
                : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            Sellers
          </button>

          <button
            onClick={() => setActiveTab("products")}
            className={`rounded-lg px-5 py-2.5 text-sm font-semibold ${
              activeTab === "products"
                ? "bg-gray-900 text-white"
                : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            Products
          </button>
        </div>

        {/* SELLERS */}

        {activeTab === "sellers" && (
          <>
            <div className="mb-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <StatCard title="Total Sellers" value={sellers.length} />
              <StatCard title="Pending" value={pendingCount} />
              <StatCard title="Approved" value={approvedCount} />
              <StatCard title="Rejected" value={rejectedCount} />
            </div>

            <div className="rounded-2xl bg-white shadow-sm">
              <div className="border-b px-6 py-5">
                <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                  <div>
                    <h2 className="text-lg font-semibold">
                      Seller Applications
                    </h2>

                    <p className="mt-1 text-sm text-gray-500">
                      Review and manage seller registrations.
                    </p>
                  </div>

                  <div className="flex gap-2">
                    {["ALL", "PENDING", "APPROVED", "REJECTED"].map(
                      (status) => (
                        <button
                          key={status}
                          onClick={() => setFilter(status)}
                          className={`rounded-lg px-3 py-2 text-xs font-semibold ${
                            filter === status
                              ? "bg-gray-900 text-white"
                              : "bg-gray-100 text-gray-600"
                          }`}
                        >
                          {status}
                        </button>
                      ),
                    )}
                  </div>
                </div>
              </div>

              {filteredSellers.length === 0 ? (
                <div className="p-10 text-center text-gray-500">
                  No sellers found.
                </div>
              ) : (
                <div className="divide-y">
                  {filteredSellers.map((seller) => (
                    <SellerCard
                      key={seller.id}
                      seller={seller}
                      onApprove={handleApprove}
                      onReject={handleReject}
                    />
                  ))}
                </div>
              )}
            </div>
          </>
        )}

        {/* PRODUCTS */}

        {activeTab === "products" && (
          <ProductManagement
            products={products}
            showForm={showProductForm}
            setShowForm={setShowProductForm}
            editingProduct={editingProduct}
            setEditingProduct={setEditingProduct}
            onSubmit={handleProductSubmit}
          />
        )}
      </main>
    </div>
  );
}

function StatCard({ title, value }) {
  return (
    <div className="rounded-2xl bg-white p-5 shadow-sm">
      <p className="text-sm text-gray-500">{title}</p>

      <p className="mt-2 text-3xl font-bold text-gray-900">{value}</p>
    </div>
  );
}

function ProductManagement({
  products,
  showForm,
  setShowForm,
  editingProduct,
  setEditingProduct,
  onSubmit,
}) {
  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Products</h2>

          <p className="mt-1 text-sm text-gray-500">
            Manage the marketplace product catalog.
          </p>
        </div>

        <button
          onClick={() => {
            setEditingProduct(null);
            setShowForm(true);
          }}
          className="rounded-xl bg-gray-900 px-5 py-3 text-sm font-semibold text-white hover:bg-gray-800"
        >
          + Add Product
        </button>
      </div>

      {showForm && (
        <ProductForm
          product={editingProduct}
          onSubmit={onSubmit}
          onCancel={() => {
            setShowForm(false);
            setEditingProduct(null);
          }}
        />
      )}

      <div className="grid gap-5 md:grid-cols-2 lg:grid-cols-3">
        {products.map((product) => (
          <div key={product.id} className="rounded-2xl bg-white p-6 shadow-sm">
            <div className="mb-4 flex items-start justify-between">
              <div>
                <h3 className="text-lg font-bold text-gray-900">
                  {product.name}
                </h3>

                <span className="mt-2 inline-block rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold text-gray-600">
                  {product.category}
                </span>
              </div>

              <span className="text-xs text-gray-400">#{product.id}</span>
            </div>

            <p className="mb-5 min-h-12 text-sm text-gray-500">
              {product.description || "No description provided."}
            </p>

            <div className="flex items-center justify-between border-t pt-4">
              <span className="text-sm font-medium text-gray-600">
                Unit: {product.unit}
              </span>

              <button
                onClick={() => {
                  setEditingProduct(product);
                  setShowForm(true);
                }}
                className="rounded-lg border border-gray-300 px-3 py-2 text-sm font-semibold hover:bg-gray-50"
              >
                Edit
              </button>
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="rounded-2xl bg-white p-12 text-center text-gray-500">
          No products available.
        </div>
      )}
    </div>
  );
}

function ProductForm({ product, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    name: product?.name || "",
    description: product?.description || "",
    category: product?.category || "",
    unit: product?.unit || "",
  });

  const [saving, setSaving] = useState(false);

  function handleChange(e) {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();

    setSaving(true);

    try {
      await onSubmit(form);
    } finally {
      setSaving(false);
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="mb-8 rounded-2xl bg-white p-6 shadow-sm"
    >
      <h3 className="mb-6 text-lg font-bold">
        {product ? "Edit Product" : "Add Product"}
      </h3>

      <div className="grid gap-5 md:grid-cols-2">
        <Input
          label="Product Name"
          name="name"
          value={form.name}
          onChange={handleChange}
          required
        />

        <Input
          label="Category"
          name="category"
          value={form.category}
          onChange={handleChange}
          required
        />

        <Input
          label="Unit"
          name="unit"
          value={form.unit}
          onChange={handleChange}
          required
        />

        <div />

        <div className="md:col-span-2">
          <label className="mb-2 block text-sm font-medium text-gray-700">
            Description
          </label>

          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            rows={4}
            className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none focus:border-gray-900"
            placeholder="Describe the product..."
          />
        </div>
      </div>

      <div className="mt-6 flex gap-3">
        <button
          type="submit"
          disabled={saving}
          className="rounded-xl bg-gray-900 px-5 py-3 text-sm font-semibold text-white hover:bg-gray-800 disabled:opacity-60"
        >
          {saving ? "Saving..." : product ? "Update Product" : "Create Product"}
        </button>

        <button
          type="button"
          onClick={onCancel}
          className="rounded-xl border border-gray-300 px-5 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-50"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}

function Input({ label, ...props }) {
  return (
    <div>
      <label className="mb-2 block text-sm font-medium text-gray-700">
        {label}
      </label>

      <input
        {...props}
        className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none focus:border-gray-900"
      />
    </div>
  );
}

function SellerCard({ seller, onApprove, onReject }) {
  const statusClasses = {
    APPROVED: "bg-green-100 text-green-700",
    PENDING: "bg-yellow-100 text-yellow-700",
    REJECTED: "bg-red-100 text-red-700",
  };

  return (
    <div className="p-6">
      <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
        <div className="flex-1">
          <div className="flex flex-wrap items-center gap-3">
            <h3 className="text-lg font-semibold">{seller.name}</h3>

            <span
              className={`rounded-full px-3 py-1 text-xs font-semibold ${
                statusClasses[seller.status]
              }`}
            >
              {seller.status}
            </span>
          </div>

          <div className="mt-5 grid gap-4 text-sm sm:grid-cols-2 lg:grid-cols-3">
            <Detail label="Contact Person" value={seller.contactPerson} />
            <Detail label="Email" value={seller.email} />
            <Detail label="Phone" value={seller.phone} />
            <Detail label="Business Type" value={seller.businessType} />
            <Detail label="City" value={seller.city} />
            <Detail label="State" value={seller.state} />
            <Detail label="Pincode" value={seller.pincode} />
            <Detail label="GSTIN" value={seller.gstin || "Not provided"} />

            <div className="sm:col-span-2 lg:col-span-3">
              <Detail label="Address" value={seller.address} />
            </div>

            {seller.rejectionReason && (
              <div className="sm:col-span-2 lg:col-span-3">
                <Detail
                  label="Rejection Reason"
                  value={seller.rejectionReason}
                />
              </div>
            )}
          </div>
        </div>

        <div className="flex shrink-0 gap-2">
          {seller.status !== "APPROVED" && (
            <button
              onClick={() => onApprove(seller.id)}
              className="rounded-xl bg-green-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-green-700"
            >
              Approve
            </button>
          )}

          {seller.status !== "REJECTED" && (
            <button
              onClick={() => onReject(seller.id)}
              className="rounded-xl bg-red-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-red-700"
            >
              Reject
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div>
      <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
        {label}
      </p>

      <p className="mt-1 wrap-break-word text-gray-700">{value || "—"}</p>
    </div>
  );
}
