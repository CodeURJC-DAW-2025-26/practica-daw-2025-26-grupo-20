import { useLoaderData, Link, useNavigate } from "react-router";
import { useEffect, useState } from "react";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import { API_BASE_URL } from "../config";

// Interfaces
interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  totalPrice: number;
  productImageUrl?: string;
}

interface CartSummary {
  items: CartItem[];
  subtotal: number;
  tax: number;
  total: number;
  itemCount: number;
  totalUnits: number;
  hasDiscount?: boolean;
  discountInfo?: string;
  discountAmount?: number;
}

// Loader function (runs on the client side with React Router)
export async function clientLoader({ request }: { request: Request }) {
  const response = await fetch("/api/v1/cart", {
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  });
  if (response.status === 401) return { isUnauthorized: true };
  if (!response.ok) return { cart: null };
  const apiCart = await response.json();
  const parsePrice = (priceStr: any): number => {
    if (typeof priceStr === 'number') return priceStr;
    return Number(priceStr?.toString().replace('€', '').trim()) || 0;
  };
  const cart: CartSummary = {
    items: apiCart.items.map((item: any) => ({
      id: item.id,
      productId: item.productId,
      productName: item.name,
      productPrice: parsePrice(item.unitPrice),
      quantity: item.quantity,
      totalPrice: parsePrice(item.lineTotal),
      productImageUrl: item.imageUrl,
    })),
    subtotal: parsePrice(apiCart.subtotal),
    tax: parsePrice(apiCart.tax),
    total: parsePrice(apiCart.total),
    itemCount: apiCart.itemCount,
    totalUnits: apiCart.totalUnits || 0,
    hasDiscount: apiCart.hasDiscount || false,
    discountInfo: apiCart.discountInfo,
    discountAmount: apiCart.discountAmount,
  };
  return { cart };
}

export default function Cart() {
  const { cart: initialCart, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();
  const isLogged = useAuthStore(state => state.isLogged);
  const setItemCount = useCartStore(state => state.setItemCount);

  // Main state variables
  const [cart, setCart] = useState<CartSummary | null>(initialCart || null);
  const [branches, setBranches] = useState<any[]>([]);
  const [selectedBranchId, setSelectedBranchId] = useState<string>("");
  const [branchDescription, setBranchDescription] = useState<string>("");
  const [paymentMethod, setPaymentMethod] = useState<string>("payPal"); // 'payPal' or 'creditCard'
  const [isProcessing, setIsProcessing] = useState(false);
  const [toastMessage, setToastMessage] = useState<{ text: string; type: "success" | "error" } | null>(null);
  const [loadingItems, setLoadingItems] = useState(true);
  const [loadingBranches, setLoadingBranches] = useState(true);

  // Sync global cart count
  useEffect(() => {
    if (cart) {
      setItemCount(cart.totalUnits);
    }
  }, [cart, setItemCount]);

  // Redirect if not authenticated
  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
  }, [isUnauthorized, isLogged, navigate]);

  // Load cart items (same as original loadCartItems)
  const loadCartItems = async () => {
    setLoadingItems(true);
    try {
      const response = await fetch("/api/v1/cart", { credentials: "include" });
      if (!response.ok) throw new Error("Error loading cart");
      const data = await response.json();
      const parsePrice = (p: any) => Number(p?.toString().replace('€', '').trim()) || 0;
      const newCart: CartSummary = {
        items: data.items.map((item: any) => ({
          id: item.id,
          productId: item.productId,
          productName: item.name,
          productPrice: parsePrice(item.unitPrice),
          quantity: item.quantity,
          totalPrice: parsePrice(item.lineTotal),
          productImageUrl: item.imageUrl,
        })),
        subtotal: parsePrice(data.subtotal),
        tax: parsePrice(data.tax),
        total: parsePrice(data.total),
        itemCount: data.itemCount,
        totalUnits: data.totalUnits || 0,
        hasDiscount: data.hasDiscount,
        discountInfo: data.discountInfo,
        discountAmount: data.discountAmount,
      };
      setCart(newCart);
      updateSummaryDOM(newCart); // update summary spans
    } catch (error) {
      console.error(error);
      showToast("Error loading cart", "error");
    } finally {
      setLoadingItems(false);
    }
  };

  // Update order summary DOM elements (so IDs match Mustache)
  const updateSummaryDOM = (cartData: CartSummary) => {
    const subtotalEl = document.getElementById("cart-subtotal");
    const taxEl = document.getElementById("tax-amount");
    const totalEl = document.getElementById("cart-total");
    const discountRow = document.getElementById("discountRow");
    const discountAmountEl = document.getElementById("discount-amount");
    if (subtotalEl) subtotalEl.textContent = `${cartData.subtotal.toFixed(2)}€`;
    if (taxEl) taxEl.textContent = `${cartData.tax.toFixed(2)}€`;
    if (totalEl) totalEl.textContent = `${cartData.total.toFixed(2)}€`;
    if (cartData.hasDiscount && discountRow && discountAmountEl) {
      discountRow.style.display = "flex";
      discountAmountEl.textContent = `-${cartData.discountInfo || cartData.discountAmount?.toFixed(2)+"€"}`;
    } else if (discountRow) {
      discountRow.style.display = "none";
    }
  };

  // Load branches (branches)
const loadBranches = async () => {
  setLoadingBranches(true);
  try {
    const response = await fetch("/api/v1/cart/branches", { credentials: "include" });
    const data = await response.json();
    
    // The backend directly returns the branches array
    if (Array.isArray(data)) {
      setBranches(data);
      // If you need a current branch, call another endpoint
      // or add an additional endpoint that returns the user's current branch
      const currentBranchResponse = await fetch("/api/v1/cart/branch/current", { 
        credentials: "include" 
      });
      if (currentBranchResponse.ok) {
        const currentBranch = await currentBranchResponse.json();
        setSelectedBranchId(currentBranch.id?.toString() || "");
        setBranchDescription(currentBranch.description || "");
      }
    } else {
      console.error("Unexpected response format:", data);
      showToast("Error loading branches", "error");
    }
  } catch (error) {
    console.error(error);
    showToast("Error loading branches", "error");
  } finally {
    setLoadingBranches(false);
  }
};

  // Change selected branch
const changeBranch = async (branchId: string) => {
  if (!branchId) return;
  try {
    console.log("Changing branch to:", branchId);
    
    const response = await fetch(`/api/v1/cart/branch?branchId=${branchId}`, {
      method: "PUT",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      }
    });
    
    console.log("Response status:", response.status);
    const data = await response.json();
    console.log("Response data:", data);
    
    if (response.ok && data.success) {
      // Update description
      const branch = branches.find(b => b.id.toString() === branchId);
      if (branch?.description) {
        setBranchDescription(branch.description);
      } else {
        setBranchDescription("");
      }
      showToast("Branch updated", "success");
      await loadCartItems();
    } else {
      console.error("Error response:", data);
      showToast(data.message || "Error changing branch", "error");
    }
  } catch (error) {
    console.error("Connection error:", error);
    showToast("Connection error: " + error, "error");
  }
};

  // Update item quantity in cart
  const updateQuantity = async (itemId: number, newQuantity: number) => {
    if (newQuantity < 0) return;
    try {
      const response = await fetch(`/api/v1/cart/items/${itemId}?quantity=${newQuantity}`, {
        method: "PUT",
        credentials: "include",
      });
      const data = await response.json();
      if (data.success) {
        await loadCartItems();
        showToast("Quantity updated", "success");
      } else {
        showToast("Error updating quantity", "error");
      }
    } catch (error) {
      showToast("Connection error", "error");
    }
  };

  // Remove item from cart
  const removeItem = async (itemId: number) => {
    try {
      const response = await fetch(`/api/v1/cart/items/${itemId}`, {
        method: "DELETE",
        credentials: "include",
      });
      const data = await response.json();
      if (data.success) {
        // Optional removal animation
        const itemElement = document.querySelector(`[data-item-id="${itemId}"]`) as HTMLElement;
        if (itemElement) {
          itemElement.style.transition = "opacity 0.3s ease";
          itemElement.style.opacity = "0";
          setTimeout(async () => {
            await loadCartItems();
          }, 300);
        } else {
          await loadCartItems();
        }
        showToast("Product removed", "success");
      } else {
        showToast("Error removing product", "error");
      }
    } catch (error) {
      showToast("Connection error", "error");
    }
  };

  // Process checkout
  const processCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBranchId) {
      showToast("Please select a pickup branch", "error");
      return;
    }
    if (!cart || cart.items.length === 0) {
      showToast("Your cart is empty", "error");
      return;
    }
    setIsProcessing(true);
    try {
      const method = paymentMethod === "payPal" ? "PAYPAL" : "CARD";
      const response = await fetch(`/api/v1/cart/payments?paymentMethod=${method}`, {
        method: "POST",
        credentials: "include",
      });
      const data = await response.json();
      if (response.ok) {
        showToast(data.message || "Order placed successfully!", "success");
        setItemCount(0);
        setTimeout(() => {
          window.location.href = "/new/orders";
        }, 1500);
      } else {
        showToast(data.message || "Error processing payment", "error");
      }
    } catch (error) {
      showToast("Connection error", "error");
    } finally {
      setIsProcessing(false);
    }
  };

  const showToast = (text: string, type: "success" | "error") => {
    setToastMessage({ text, type });
    setTimeout(() => setToastMessage(null), 3000);
  };

  // Expose global functions for compatibility with inline onclicks (optional)
  useEffect(() => {
    (window as any).updateQuantity = updateQuantity;
    (window as any).removeItem = removeItem;
    (window as any).changeBranch = (branchId: string) => changeBranch(branchId);
    (window as any).processCheckout = processCheckout;
  }, []);

  // Initial data loading
  useEffect(() => {
    loadCartItems();
    loadBranches();
  }, []);

  // Loading state
  if (!cart && loadingItems) {
    return (
      <main className="container my-5 cart-container">
        <div className="text-center p-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-3">Loading your cart...</p>
        </div>
      </main>
    );
  }

  // Empty cart state
  if (!cart || cart.items.length === 0) {
    return (
      <main className="legacy-container cart-container">
        <div className="row">
          <div className="col-lg-12">
            <div className="alert alert-info text-center p-5">
              <i className="fas fa-shopping-cart fa-3x mb-3"></i>
              <h4>Your cart is empty</h4>
              <p className="mb-3">Ready to order? Explore our menu</p>
              <Link to="/menu" className="btn btn-primary">
                <i className="fas fa-arrow-left me-2"></i>View Menu
              </Link>
            </div>
          </div>
        </div>
      </main>
    );
  }

  // Main cart view with exact same IDs and classes as the Mustache template
  return (
    <>
      <main className="legacy-container cart-container">
        <div className="row">
          {/* Left Column - Cart Items */}
          <div className="col-lg-8 cart-items-column">
            <h1 className="mb-4">
              <i className="fas fa-shopping-cart me-2"></i>Your Cart
            </h1>

            {/* Cart Items List - loaded dynamically */}
            <div className="cart-items" id="cartItemsContainer">
              {cart.items.map((item) => (
                <div key={item.id} className="cart-item card mb-3" data-item-id={item.id}>
                  <div className="row g-0">
                    <div className="col-md-2 d-flex align-items-center justify-content-center p-3">
                      <img
                        src={item.productImageUrl ? `${API_BASE_URL}${item.productImageUrl}` : `https://via.placeholder.com/300x200?text=${encodeURIComponent(item.productName)}`}
                        className="img-fluid rounded cart-item-image"
                        alt={item.productName}
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = `https://via.placeholder.com/300x200?text=${encodeURIComponent(item.productName)}`;
                        }}
                      />
                    </div>
                    <div className="col-md-8">
                      <div className="card-body">
                        <div className="d-flex justify-content-between align-items-start">
                          <div>
                            <h5 className="cart-item-title mb-1">{item.productName}</h5>
                            <p className="cart-item-price text-muted mb-0">{item.productPrice.toFixed(2)}€ each</p>
                          </div>
                        </div>
                        <div className="d-flex justify-content-between align-items-center mt-3">
                          <div className="quantity-controls">
                            <button
                              className="btn btn-quantity btn-sm"
                              onClick={() => updateQuantity(item.id, item.quantity - 1)}
                              disabled={item.quantity <= 1}
                            >
                              <i className="fas fa-minus"></i>
                            </button>
                            <span className="quantity-display mx-3 fw-bold">{item.quantity}</span>
                            <button
                              className="btn btn-quantity btn-sm"
                              onClick={() => updateQuantity(item.id, item.quantity + 1)}
                            >
                              <i className="fas fa-plus"></i>
                            </button>
                          </div>
                          <div>
                            <span className="item-subtotal me-3 fw-bold">{(item.productPrice * item.quantity).toFixed(2)}€</span>
                            <button className="btn btn-remove btn-sm" onClick={() => removeItem(item.id)}>
                              <i className="fas fa-trash"></i>
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Continue Shopping Button */}
            <div className="mt-4">
              <Link to="/menu" className="btn btn-outline-primary">
                <i className="fas fa-arrow-left me-2"></i>Continue Shopping
              </Link>
            </div>
          </div>

          {/* Right Column - Order Summary */}
          <div className="col-lg-4">
            {/* Order Summary */}
            <div className="order-summary-fixed card" id="orderSummaryFixed">
              <div className="card-header bg-dark text-light">
                <h4 className="mb-0">
                  <i className="fas fa-receipt me-2"></i>Order Summary
                </h4>
              </div>
              <div className="card-body">
                {/* Order Details */}
                <div className="order-details mb-4">
                  <div className="d-flex justify-content-between mb-2">
                    <span>Subtotal:</span>
                    <span id="cart-subtotal">{cart.subtotal.toFixed(2)}€</span>
                  </div>

                  {/* Discount row (hidden if no discount) */}
                  <div
                    className="d-flex justify-content-between mb-2 text-success"
                    id="discountRow"
                    style={{ display: cart.hasDiscount ? "flex" : "none" }}
                  >
                    <span>Discount:</span>
                    <span id="discount-amount">{cart.hasDiscount ? `-${cart.discountInfo || cart.discountAmount?.toFixed(2)+"€"}` : ""}</span>
                  </div>

                  <div className="d-flex justify-content-between mb-3">
                    <span>Tax:</span>
                    <span id="tax-amount">{cart.tax.toFixed(2)}€</span>
                  </div>
                  <hr />
                  <div className="d-flex justify-content-between fw-bold fs-5">
                    <span>Total:</span>
                    <span id="cart-total">{cart.total.toFixed(2)}€</span>
                  </div>
                </div>

                {/* Branch Selector */}
                <div className="branch-selector mb-4">
                  <h6 className="mb-3">
                    <i className="fas fa-store me-2"></i>Pickup branch
                  </h6>

                  {loadingBranches ? (
                    <div className="branch-loading text-center p-3" id="branchLoading">
                      <div className="spinner-border spinner-border-sm text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                      </div>
                      <span className="ms-2">Loading branches...</span>
                    </div>
                  ) : (
                    <div className="branch-list" id="branchList">
                      <select
                        className="form-select"
                        id="branchSelect"
                        value={selectedBranchId}
                        onChange={(e) => changeBranch(e.target.value)}
                      >
                        <option value="">Select a branch</option>
                        {branches.map((branch) => (
                          <option key={branch.id} value={branch.id}>
                            {branch.name}
                          </option>
                        ))}
                      </select>
                      {branchDescription && (
                        <div className="branch-info mt-2 text-muted small" id="branchInfo">
                          <i className="fas fa-info-circle me-1"></i>
                          <span id="branchDescription">{branchDescription}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>

                {/* Payment Methods */}
                <div className="payment-methods mb-4">
                  <h6 className="mb-3">Payment method</h6>
                  <div className="form-check mb-2">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="payPal"
                      checked={paymentMethod === "payPal"}
                      onChange={() => setPaymentMethod("payPal")}
                    />
                    <label className="form-check-label" htmlFor="payPal">
                      <i className="fab fa-cc-paypal me-2"></i>PayPal
                    </label>
                  </div>
                  <div className="form-check mb-2">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="creditCard"
                      checked={paymentMethod === "creditCard"}
                      onChange={() => setPaymentMethod("creditCard")}
                    />
                    <label className="form-check-label" htmlFor="creditCard">
                      <i className="fas fa-credit-card me-2"></i>Credit Card
                    </label>
                  </div>
                </div>

                {/* Checkout Button */}
                <form action="/cart/checkout" method="POST" onSubmit={processCheckout}>
                  <button
                    type="submit"
                    className="btn btn-checkout w-100 py-3"
                    disabled={isProcessing}
                  >
                    {isProcessing ? (
                      <>
                        <i className="fas fa-spinner fa-spin me-2"></i>Processing...
                      </>
                    ) : (
                      <>
                        <i className="fas fa-lock me-2"></i>Proceed to Checkout
                      </>
                    )}
                  </button>
                </form>

                {/* Security Info */}
                <div className="text-center mt-3">
                  <small className="text-muted">
                    <i className="fas fa-shield-alt me-1"></i> 100% secure payment - Your data is protected
                  </small>
                </div>
              </div>
            </div>

            {/* Estimated Delivery */}
            <div className="card mt-3 delivery-info-fixed" id="deliveryInfoFixed">
              <div className="card-body">
                <h6 className="mb-3">
                  <i className="fas fa-shipping-fast me-2"></i>Maximum pickup time
                </h6>
                <p className="mb-2">
                  <i className="fas fa-clock me-2"></i>
                  <span id="delivery-time">24 Hours</span>
                </p>
                <p className="mb-0 text-muted small">
                  <i className="fas fa-info-circle me-1"></i>
                  Enjoy branch discounts!
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Toast Notifications (same structure as Mustache) */}
      <div className="toast-container position-fixed bottom-0 end-0 p-3">
        {toastMessage && (
          <div className="toast show" role="alert" aria-live="assertive" aria-atomic="true">
            <div className={`toast-header ${toastMessage.type === "success" ? "bg-success text-white" : "bg-danger text-white"}`}>
              <i className={`${toastMessage.type === "success" ? "fas fa-check-circle" : "fas fa-exclamation-triangle"} me-2`}></i>
              <strong className="me-auto">{toastMessage.type === "success" ? "Success" : "Error"}</strong>
              <button type="button" className="btn-close btn-close-white" data-bs-dismiss="toast" onClick={() => setToastMessage(null)}></button>
            </div>
            <div className="toast-body">{toastMessage.text}</div>
          </div>
        )}
      </div>
    </>
  );
}