import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import ProductDetails from "./pages/ProductDetails";
import SellerDashboard from "./pages/SellerDashboard";
import SellerRegistration from "./pages/SellerRegisteration";
import AdminDashboard from "./pages/AdminDashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products/:id" element={<ProductDetails />} />\
        <Route path="/seller" element={<SellerDashboard />} />
        <Route path="/seller/register" element={<SellerRegistration />} />
        <Route path="/admin" element={<AdminDashboard />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
