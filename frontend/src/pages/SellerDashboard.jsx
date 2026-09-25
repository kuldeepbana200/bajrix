import { useEffect, useState } from "react";
import { Link, Route } from "react-router-dom";
import {
  getSellers,
  getProducts,
  getSellerListings,
  createListing,
  updateListing,
  stopListing,
} from "../services/api";
import SellerRegistration from "./SellerRegisteration";

function SellerDashboard() {
  const [sellers, setSellers] = useState([]);
  const [products, setProducts] = useState([]);
  const [listings, setListings] = useState([]);

  const [sellerId, setSellerId] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showForm, setShowForm] = useState(false);

  const [editingListingId, setEditingListingId] = useState(null);

  const [editForm, setEditForm] = useState({
    price: "",
    stock: "",
    minimumOrderQuantity: "",
  });

  const [form, setForm] = useState({
    productId: "",
    price: "",
    stock: "",
    minimumOrderQuantity: "",
  });

  async function loadInitialData() {
    try {
      setLoading(true);

      const [sellerData, productData] = await Promise.all([
        getSellers(),
        getProducts(),
      ]);

      setSellers(sellerData);
      setProducts(productData.content);

      if (sellerData.length > 0) {
        const firstApprovedSeller = sellerData.find(
          (seller) => seller.status === "APPROVED",
        );

        if (firstApprovedSeller) {
          setSellerId(String(firstApprovedSeller.id));
        }
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadListings(id) {
    if (!id) return;

    try {
      setError("");

      const data = await getSellerListings(id);

      setListings(data);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => {
    loadInitialData();
  }, []);

  useEffect(() => {
    if (sellerId) {
      loadListings(sellerId);
    }
  }, [sellerId]);

  function handleFormChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function handleCreateListing(event) {
    event.preventDefault();

    try {
      setError("");
      setSuccess("");

      await createListing(Number(sellerId), {
        productId: Number(form.productId),
        price: Number(form.price),
        stock: Number(form.stock),
        minimumOrderQuantity: Number(form.minimumOrderQuantity),
      });

      setSuccess("Listing created successfully.");

      setForm({
        productId: "",
        price: "",
        stock: "",
        minimumOrderQuantity: "",
      });

      setShowForm(false);

      await loadListings(sellerId);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleStopListing(listingId) {
    if (
      !window.confirm("Are you sure you want to stop selling this product?")
    ) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await stopListing(Number(sellerId), listingId);

      setSuccess("Listing stopped successfully.");

      await loadListings(sellerId);
    } catch (err) {
      setError(err.message);
    }
  }

  function startEditing(listing) {
    setEditingListingId(listing.id);

    setEditForm({
      price: listing.price,
      stock: listing.stock,
      minimumOrderQuantity: listing.minimumOrderQuantity,
    });

    setError("");
    setSuccess("");
  }

  function handleEditChange(event) {
    setEditForm({
      ...editForm,
      [event.target.name]: event.target.value,
    });
  }

  async function handleUpdateListing(event) {
    event.preventDefault();

    try {
      setError("");
      setSuccess("");

      await updateListing(Number(sellerId), editingListingId, {
        price: Number(editForm.price),
        stock: Number(editForm.stock),
        minimumOrderQuantity: Number(editForm.minimumOrderQuantity),
      });

      setSuccess("Listing updated successfully.");

      setEditingListingId(null);

      await loadListings(sellerId);
    } catch (err) {
      setError(err.message);
    }
  }

  function cancelEditing() {
    setEditingListingId(null);

    setEditForm({
      price: "",
      stock: "",
      minimumOrderQuantity: "",
    });
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <p className="text-slate-500">Loading seller dashboard...</p>
      </div>
    );
  }

  const currentSeller = sellers.find(
    (seller) => String(seller.id) === sellerId,
  );

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Navbar */}
      <nav className="border-b bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <Link to="/" className="text-2xl font-bold text-slate-900">
            Bajri<span className="text-orange-600">X</span>
          </Link>

          <Link
            to="/"
            className="text-sm font-medium text-slate-600 hover:text-orange-600"
          >
            ← Marketplace
          </Link>
        </div>
      </nav>

      <main className="mx-auto max-w-7xl px-6 py-10">
        {/* Header */}
        <div className="flex flex-col gap-5 md:flex-row md:items-end md:justify-between">
          <div className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase tracking-wide text-orange-600">
                Seller Portal
              </p>

              <h1 className="mt-2 text-3xl font-bold text-slate-900">
                Seller Dashboard
              </h1>

              <p className="mt-2 text-slate-500">
                Manage your products, pricing and inventory.
              </p>

              <Link
                to="/seller/register"
                className="mt-4 inline-block text-sm font-semibold text-orange-600 hover:text-orange-700"
              >
                Become a Seller →
              </Link>
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-700">
                Current seller
              </label>

              <select
                value={sellerId}
                onChange={(event) => setSellerId(event.target.value)}
                className="min-w-64 rounded-lg border border-slate-300 bg-white px-4 py-3 outline-none focus:border-orange-500"
              >
                {sellers.map((seller) => (
                  <option key={seller.id} value={seller.id}>
                    {seller.name} — {seller.status}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Seller selector */}
          <div>
            <label className="mb-2 block text-sm font-medium text-slate-700">
              Current seller
            </label>

            <select
              value={sellerId}
              onChange={(event) => setSellerId(event.target.value)}
              className="min-w-64 rounded-lg border border-slate-300 bg-white px-4 py-3 outline-none focus:border-orange-500"
            >
              {sellers.map((seller) => (
                <option key={seller.id} value={seller.id}>
                  {seller.name} — {seller.status}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Seller status */}
        {currentSeller && (
          <div className="mt-8 flex items-center justify-between rounded-xl border border-slate-200 bg-white p-5">
            <div>
              <p className="text-sm text-slate-500">Seller account</p>

              <p className="mt-1 font-semibold text-slate-900">
                {currentSeller.name}
              </p>
            </div>

            <span
              className={`rounded-full px-3 py-1 text-xs font-semibold ${
                currentSeller.status === "APPROVED"
                  ? "bg-green-50 text-green-700"
                  : currentSeller.status === "PENDING"
                    ? "bg-yellow-50 text-yellow-700"
                    : "bg-red-50 text-red-700"
              }`}
            >
              {currentSeller.status}
            </span>
          </div>
        )}

        {/* Messages */}
        {error && (
          <div className="mt-6 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}

        {success && (
          <div className="mt-6 rounded-lg border border-green-200 bg-green-50 p-4 text-sm text-green-700">
            {success}
          </div>
        )}

        {/* Actions */}
        <div className="mt-10 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-900">My Listings</h2>

            <p className="mt-1 text-sm text-slate-500">
              {listings.length} listing
              {listings.length !== 1 ? "s" : ""}
            </p>
          </div>

          {currentSeller?.status === "APPROVED" && (
            <button
              onClick={() => setShowForm(!showForm)}
              className="rounded-lg bg-slate-900 px-5 py-3 text-sm font-medium text-white hover:bg-slate-800"
            >
              {showForm ? "Cancel" : "+ Add Listing"}
            </button>
          )}
        </div>

        {/* Create listing */}
        {showForm && (
          <form
            onSubmit={handleCreateListing}
            className="mt-6 rounded-xl border border-slate-200 bg-white p-6"
          >
            <h3 className="text-lg font-semibold text-slate-900">
              Add Product Listing
            </h3>

            <div className="mt-5 grid gap-5 md:grid-cols-2">
              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700">
                  Product
                </label>

                <select
                  name="productId"
                  value={form.productId}
                  onChange={handleFormChange}
                  required
                  className="w-full rounded-lg border border-slate-300 px-4 py-3"
                >
                  <option value="">Select product</option>

                  {products.map((product) => (
                    <option key={product.id} value={product.id}>
                      {product.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700">
                  Price
                </label>

                <input
                  name="price"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={form.price}
                  onChange={handleFormChange}
                  required
                  placeholder="390.00"
                  className="w-full rounded-lg border border-slate-300 px-4 py-3"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700">
                  Stock
                </label>

                <input
                  name="stock"
                  type="number"
                  min="0"
                  value={form.stock}
                  onChange={handleFormChange}
                  required
                  placeholder="500"
                  className="w-full rounded-lg border border-slate-300 px-4 py-3"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-slate-700">
                  Minimum Order Quantity
                </label>

                <input
                  name="minimumOrderQuantity"
                  type="number"
                  min="1"
                  value={form.minimumOrderQuantity}
                  onChange={handleFormChange}
                  required
                  placeholder="10"
                  className="w-full rounded-lg border border-slate-300 px-4 py-3"
                />
              </div>
            </div>

            <button
              type="submit"
              className="mt-6 rounded-lg bg-orange-600 px-6 py-3 font-medium text-white hover:bg-orange-700"
            >
              Create Listing
            </button>
          </form>
        )}

        {/* Listings */}
        <div className="mt-6 space-y-4">
          {listings.length === 0 ? (
            <div className="rounded-xl border border-slate-200 bg-white p-10 text-center">
              <div className="text-4xl">📦</div>

              <h3 className="mt-4 font-semibold text-slate-900">
                No listings yet
              </h3>

              <p className="mt-2 text-sm text-slate-500">
                Add a product to start selling.
              </p>
            </div>
          ) : (
            listings.map((listing) => (
              <div
                key={listing.id}
                className="rounded-xl border border-slate-200 bg-white p-6"
              >
                {editingListingId === listing.id ? (
                  /* EDIT MODE */

                  <form onSubmit={handleUpdateListing}>
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wide text-orange-600">
                          Editing Listing
                        </p>

                        <h3 className="mt-1 text-xl font-bold text-slate-900">
                          {listing.productName}
                        </h3>
                      </div>

                      <span className="rounded-full bg-green-50 px-3 py-1 text-xs font-semibold text-green-700">
                        {listing.status}
                      </span>
                    </div>

                    <div className="mt-6 grid gap-5 md:grid-cols-3">
                      <div>
                        <label className="mb-2 block text-sm font-medium text-slate-700">
                          Price
                        </label>

                        <input
                          name="price"
                          type="number"
                          min="0.01"
                          step="0.01"
                          value={editForm.price}
                          onChange={handleEditChange}
                          required
                          className="w-full rounded-lg border border-slate-300 px-4 py-3 outline-none focus:border-orange-500"
                        />
                      </div>

                      <div>
                        <label className="mb-2 block text-sm font-medium text-slate-700">
                          Stock
                        </label>

                        <input
                          name="stock"
                          type="number"
                          min="0"
                          value={editForm.stock}
                          onChange={handleEditChange}
                          required
                          className="w-full rounded-lg border border-slate-300 px-4 py-3 outline-none focus:border-orange-500"
                        />
                      </div>

                      <div>
                        <label className="mb-2 block text-sm font-medium text-slate-700">
                          Minimum Order Quantity
                        </label>

                        <input
                          name="minimumOrderQuantity"
                          type="number"
                          min="1"
                          value={editForm.minimumOrderQuantity}
                          onChange={handleEditChange}
                          required
                          className="w-full rounded-lg border border-slate-300 px-4 py-3 outline-none focus:border-orange-500"
                        />
                      </div>
                    </div>

                    <div className="mt-6 flex gap-3">
                      <button
                        type="submit"
                        className="rounded-lg bg-orange-600 px-5 py-2.5 text-sm font-medium text-white hover:bg-orange-700"
                      >
                        Save Changes
                      </button>

                      <button
                        type="button"
                        onClick={cancelEditing}
                        className="rounded-lg border border-slate-300 px-5 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50"
                      >
                        Cancel
                      </button>
                    </div>
                  </form>
                ) : (
                  /* NORMAL MODE */

                  <>
                    <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wide text-orange-600">
                          {listing.status}
                        </p>

                        <h3 className="mt-1 text-xl font-bold text-slate-900">
                          {listing.productName}
                        </h3>

                        <p className="mt-1 text-sm text-slate-500">
                          Listing #{listing.id}
                        </p>
                      </div>

                      <div className="grid grid-cols-3 gap-8">
                        <div>
                          <p className="text-xs text-slate-500">Price</p>

                          <p className="mt-1 font-bold text-slate-900">
                            ₹{listing.price}
                          </p>
                        </div>

                        <div>
                          <p className="text-xs text-slate-500">Stock</p>

                          <p className="mt-1 font-bold text-slate-900">
                            {listing.stock}
                          </p>
                        </div>

                        <div>
                          <p className="text-xs text-slate-500">MOQ</p>

                          <p className="mt-1 font-bold text-slate-900">
                            {listing.minimumOrderQuantity}
                          </p>
                        </div>
                      </div>
                    </div>

                    {listing.status === "ACTIVE" && (
                      <div className="mt-5 flex gap-3 border-t border-slate-100 pt-5">
                        <button
                          onClick={() => startEditing(listing)}
                          className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
                        >
                          Edit
                        </button>

                        <button
                          onClick={() => handleStopListing(listing.id)}
                          className="rounded-lg border border-red-200 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50"
                        >
                          Stop Selling
                        </button>
                      </div>
                    )}
                  </>
                )}
              </div>
            ))
          )}
        </div>
      </main>
    </div>
  );
}

export default SellerDashboard;
