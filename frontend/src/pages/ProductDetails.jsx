import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
    getProduct,
    getProductListings
} from "../services/api";

function ProductDetails() {
    const { id } = useParams();

    const [product, setProduct] = useState(null);
    const [listings, setListings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadProduct() {
            try {
                setLoading(true);
                setError("");

                const [productData, listingData] = await Promise.all([
                    getProduct(id),
                    getProductListings(id)
                ]);

                setProduct(productData);
                setListings(listingData);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }

        loadProduct();
    }, [id]);

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-slate-50">
                <p className="text-slate-500">Loading product...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-slate-50">
                <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-red-700">
                    {error}
                </div>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <p>Product not found.</p>
            </div>
        );
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

                    <Link
                        to="/seller"
                        className="text-sm font-medium text-slate-700 hover:text-orange-600"
                    >
                        Seller Dashboard
                    </Link>

                </div>
            </nav>

            <main className="mx-auto max-w-7xl px-6 py-10">

                {/* Back */}
                <Link
                    to="/"
                    className="text-sm font-medium text-slate-500 hover:text-orange-600"
                >
                    ← Back to products
                </Link>

                {/* Product information */}
                <section className="mt-6 rounded-2xl border border-slate-200 bg-white p-8">

                    <div className="grid gap-8 md:grid-cols-[220px_1fr]">

                        <div className="flex h-52 items-center justify-center rounded-xl bg-slate-100">
                            <span className="text-7xl">🧱</span>
                        </div>

                        <div>

                            <p className="text-sm font-semibold uppercase tracking-wide text-orange-600">
                                {product.category}
                            </p>

                            <h1 className="mt-2 text-3xl font-bold text-slate-900">
                                {product.name}
                            </h1>

                            <p className="mt-4 max-w-2xl leading-7 text-slate-600">
                                {product.description}
                            </p>

                            <div className="mt-6 flex flex-wrap gap-3">

                                <span className="rounded-full bg-slate-100 px-4 py-2 text-sm text-slate-600">
                                    Unit: {product.unit}
                                </span>

                                <span className="rounded-full bg-orange-50 px-4 py-2 text-sm font-medium text-orange-700">
                                    {listings.length} available seller
                                    {listings.length !== 1 ? "s" : ""}
                                </span>

                            </div>

                        </div>

                    </div>

                </section>

                {/* Sellers */}
                <section className="mt-10">

                    <div className="mb-5">
                        <h2 className="text-2xl font-bold text-slate-900">
                            Available Sellers
                        </h2>

                        <p className="mt-1 text-slate-500">
                            Compare seller prices, stock and minimum order quantities.
                        </p>
                    </div>

                    {listings.length === 0 ? (

                        <div className="rounded-xl border border-slate-200 bg-white p-10 text-center">
                            <div className="text-4xl">📦</div>

                            <h3 className="mt-4 font-semibold text-slate-900">
                                Currently unavailable
                            </h3>

                            <p className="mt-2 text-sm text-slate-500">
                                No approved sellers are currently offering this product.
                            </p>
                        </div>

                    ) : (

                        <div className="grid gap-5 lg:grid-cols-2">

                            {listings.map((listing) => (

                                <div
                                    key={listing.id}
                                    className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm"
                                >

                                    <div className="flex items-start justify-between">

                                        <div>
                                            <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
                                                Seller
                                            </p>

                                            <h3 className="mt-1 text-xl font-bold text-slate-900">
                                                {listing.sellerName}
                                            </h3>
                                        </div>

                                        <span className="rounded-full bg-green-50 px-3 py-1 text-xs font-semibold text-green-700">
                                            {listing.status}
                                        </span>

                                    </div>

                                    <div className="mt-6">

                                        <p className="text-sm text-slate-500">
                                            Price
                                        </p>

                                        <p className="mt-1 text-3xl font-bold text-slate-900">
                                            ₹{listing.price}
                                            <span className="ml-1 text-sm font-normal text-slate-500">
                                                / {product.unit}
                                            </span>
                                        </p>

                                    </div>

                                    <div className="mt-6 grid grid-cols-2 gap-4">

                                        <div className="rounded-lg bg-slate-50 p-4">
                                            <p className="text-xs text-slate-500">
                                                Available stock
                                            </p>

                                            <p className="mt-1 text-lg font-semibold text-slate-900">
                                                {listing.stock}
                                            </p>
                                        </div>

                                        <div className="rounded-lg bg-slate-50 p-4">
                                            <p className="text-xs text-slate-500">
                                                Minimum order
                                            </p>

                                            <p className="mt-1 text-lg font-semibold text-slate-900">
                                                {listing.minimumOrderQuantity}
                                            </p>
                                        </div>

                                    </div>

                                </div>

                            ))}

                        </div>

                    )}

                </section>

            </main>
        </div>
    );
}

export default ProductDetails;