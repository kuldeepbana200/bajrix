import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getProducts } from "../services/api";

function Home() {
  const [products, setProducts] = useState([]);

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [sortDirection, setSortDirection] = useState("asc");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadProducts() {
    setLoading(true);
    setError("");

    try {
      const data = await getProducts({
        search,
        category,
        page,
        size: 12,
        sortBy: "name",
        direction: sortDirection,
      });

      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to load products:", err);
      setError(err.message || "Failed to load products.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadProducts();
  }, [search, category, page, sortDirection]);

  function handleSearch(event) {
    event.preventDefault();

    // Search is already controlled by state.
    // Reset pagination when a new search is submitted.
    setPage(0);
  }

  function handleCategoryChange(event) {
    setCategory(event.target.value);
    setPage(0);
  }

  function handleSortChange(event) {
    setSortDirection(event.target.value);
    setPage(0);
  }

  function handlePreviousPage() {
    if (page > 0) {
      setPage((currentPage) => currentPage - 1);
    }
  }

  function handleNextPage() {
    if (page < totalPages - 1) {
      setPage((currentPage) => currentPage + 1);
    }
  }

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Navbar */}
      <nav className="border-b bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <Link
            to="/"
            className="text-2xl font-bold tracking-tight text-slate-900"
          >
            Bajri<span className="text-orange-600">X</span>
          </Link>

          <div className="flex items-center gap-6 text-sm">
            <Link
              to="/"
              className="font-medium text-slate-700 hover:text-orange-600"
            >
              Marketplace
            </Link>

            <Link
              to="/seller"
              className="font-medium text-slate-700 hover:text-orange-600"
            >
              Seller Dashboard
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="bg-white">
        <div className="mx-auto max-w-7xl px-6 py-16">
          <div className="max-w-3xl">
            <p className="mb-3 text-sm font-semibold uppercase tracking-wider text-orange-600">
              Construction Marketplace
            </p>

            <h1 className="text-4xl font-bold tracking-tight text-slate-900 sm:text-5xl">
              Find the right materials from trusted local sellers.
            </h1>

            <p className="mt-5 text-lg leading-8 text-slate-600">
              Compare prices, stock and minimum order quantities from multiple
              sellers in one place.
            </p>
          </div>

          {/* Search */}
          <form onSubmit={handleSearch} className="mt-8 flex max-w-3xl gap-3">
            <input
              type="text"
              placeholder="Search cement, steel, sand..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              className="flex-1 rounded-lg border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
            />

            <button
              type="submit"
              className="rounded-lg bg-slate-900 px-6 py-3 font-medium text-white transition hover:bg-slate-800"
            >
              Search
            </button>
          </form>
        </div>
      </section>

      {/* Products */}
      <main className="mx-auto max-w-7xl px-6 py-12">
        {/* Header + Filters */}
        <div className="mb-8 flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Products</h2>

            <p className="mt-1 text-slate-500">
              Browse construction materials available from local sellers.
            </p>
          </div>

          {/* Filters */}
          <div className="flex flex-col gap-3 sm:flex-row">
            <select
              value={category}
              onChange={handleCategoryChange}
              className="rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-500"
            >
              <option value="">All Categories</option>
              <option value="Cement">Cement</option>
              <option value="Steel">Steel</option>
              <option value="Sand">Sand</option>
            </select>

            <select
              value={sortDirection}
              onChange={handleSortChange}
              className="rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-500"
            >
              <option value="asc">Name: A → Z</option>
              <option value="desc">Name: Z → A</option>
            </select>
          </div>
        </div>

        {/* Loading */}
        {loading && (
          <div className="rounded-xl border border-slate-200 bg-white p-10 text-center">
            <p className="text-slate-500">Loading products...</p>
          </div>
        )}

        {/* Error */}
        {error && !loading && (
          <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
            {error}
          </div>
        )}

        {/* Empty State */}
        {!loading && !error && products.length === 0 && (
          <div className="rounded-xl border border-slate-200 bg-white p-10 text-center">
            <div className="text-4xl">🔍</div>

            <h3 className="mt-4 font-semibold text-slate-900">
              No products found
            </h3>

            <p className="mt-2 text-slate-500">
              Try a different search term or category.
            </p>

            <button
              onClick={() => {
                setSearch("");
                setCategory("");
                setPage(0);
              }}
              className="mt-5 rounded-lg bg-slate-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-slate-800"
            >
              Clear Filters
            </button>
          </div>
        )}

        {/* Product Grid */}
        {!loading && !error && products.length > 0 && (
          <>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {products.map((product) => (
                <Link
                  key={product.id}
                  to={`/products/${product.id}`}
                  className="group rounded-xl border border-slate-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:shadow-lg"
                >
                  {/* Product image placeholder */}
                  <div className="mb-5 flex h-32 items-center justify-center rounded-lg bg-slate-100">
                    <span className="text-4xl">🧱</span>
                  </div>

                  {/* Category */}
                  <p className="text-xs font-semibold uppercase tracking-wide text-orange-600">
                    {product.category}
                  </p>

                  {/* Name */}
                  <h3 className="mt-2 text-lg font-semibold text-slate-900 group-hover:text-orange-600">
                    {product.name}
                  </h3>

                  {/* Description */}
                  <p className="mt-2 line-clamp-2 text-sm text-slate-500">
                    {product.description || "No description available."}
                  </p>

                  {/* Footer */}
                  <div className="mt-5 flex items-center justify-between">
                    <span className="text-sm text-slate-500">
                      Unit: {product.unit}
                    </span>

                    <span className="font-medium text-orange-600">View →</span>
                  </div>
                </Link>
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="mt-10 flex items-center justify-center gap-5">
                <button
                  onClick={handlePreviousPage}
                  disabled={page === 0}
                  className="rounded-lg border border-slate-300 bg-white px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                >
                  ← Previous
                </button>

                <span className="text-sm font-medium text-slate-600">
                  Page {page + 1} of {totalPages}
                </span>

                <button
                  onClick={handleNextPage}
                  disabled={page >= totalPages - 1}
                  className="rounded-lg border border-slate-300 bg-white px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                >
                  Next →
                </button>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default Home;
