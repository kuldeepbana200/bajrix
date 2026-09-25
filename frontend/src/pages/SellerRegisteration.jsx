import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080/api";

export default function SellerRegistration() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    contactPerson: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    state: "",
    pincode: "",
    gstin: "",
    businessType: "",
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  function handleChange(e) {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();

    setLoading(true);
    setMessage("");
    setError("");

    try {
      const response = await fetch(`${API_BASE_URL}/sellers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Unable to submit seller registration");
      }

      setMessage(
        "Registration submitted successfully! Your application is pending admin approval.",
      );

      setForm({
        name: "",
        contactPerson: "",
        email: "",
        phone: "",
        address: "",
        city: "",
        state: "",
        pincode: "",
        gstin: "",
        businessType: "",
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  const inputClass =
    "w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-gray-900 focus:ring-2 focus:ring-gray-900/10";

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-10">
      <div className="mx-auto max-w-3xl">
        <div className="mb-8">
          <button
            onClick={() => navigate("/seller")}
            className="mb-5 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            ← Back to Seller Dashboard
          </button>

          <h1 className="text-3xl font-bold text-gray-900">Become a Seller</h1>

          <p className="mt-2 text-gray-600">
            Register your business to start selling construction materials on
            BajriX.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="rounded-2xl bg-white p-6 shadow-sm sm:p-8"
        >
          <div className="mb-8">
            <h2 className="text-lg font-semibold text-gray-900">
              Business Information
            </h2>

            <p className="mt-1 text-sm text-gray-500">
              Tell us about your business.
            </p>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="sm:col-span-2">
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Business Name *
              </label>

              <input
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="ABC Building Materials"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Contact Person *
              </label>

              <input
                name="contactPerson"
                value={form.contactPerson}
                onChange={handleChange}
                placeholder="Rahul Sharma"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Business Type *
              </label>

              <select
                name="businessType"
                value={form.businessType}
                onChange={handleChange}
                required
                className={inputClass}
              >
                <option value="">Select business type</option>
                <option value="Manufacturer">Manufacturer</option>
                <option value="Wholesaler">Wholesaler</option>
                <option value="Distributor">Distributor</option>
                <option value="Retailer">Retailer</option>
                <option value="Contractor">Contractor</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Email *
              </label>

              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="rahul@example.com"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Phone *
              </label>

              <input
                type="tel"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="9876543210"
                maxLength={10}
                pattern="[0-9]{10}"
                required
                className={inputClass}
              />
            </div>
          </div>

          <div className="my-8 border-t border-gray-200" />

          <div className="mb-6">
            <h2 className="text-lg font-semibold text-gray-900">
              Business Address
            </h2>

            <p className="mt-1 text-sm text-gray-500">
              Where is your business located?
            </p>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="sm:col-span-2">
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Address *
              </label>

              <textarea
                name="address"
                value={form.address}
                onChange={handleChange}
                placeholder="Shop/office address"
                rows={3}
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                City *
              </label>

              <input
                name="city"
                value={form.city}
                onChange={handleChange}
                placeholder="Rourkela"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                State *
              </label>

              <input
                name="state"
                value={form.state}
                onChange={handleChange}
                placeholder="Odisha"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Pincode *
              </label>

              <input
                name="pincode"
                value={form.pincode}
                onChange={handleChange}
                placeholder="769001"
                maxLength={6}
                pattern="[0-9]{6}"
                required
                className={inputClass}
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                GSTIN
              </label>

              <input
                name="gstin"
                value={form.gstin}
                onChange={handleChange}
                placeholder="21ABCDE1234F1Z5"
                className={inputClass}
              />
            </div>
          </div>

          {message && (
            <div className="mt-6 rounded-xl bg-green-50 p-4 text-sm text-green-700">
              {message}
            </div>
          )}

          {error && (
            <div className="mt-6 rounded-xl bg-red-50 p-4 text-sm text-red-700">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="mt-8 w-full rounded-xl bg-gray-900 px-6 py-3.5 font-semibold text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? "Submitting..." : "Submit Seller Application"}
          </button>

          <p className="mt-4 text-center text-xs text-gray-500">
            Your application will remain pending until approved by an admin.
          </p>
        </form>
      </div>
    </div>
  );
}
