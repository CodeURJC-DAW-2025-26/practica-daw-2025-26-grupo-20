const NOTIFICATION_DURATION = 3000; // 3 seconds

/**
 * Show notification
 * @param {string} message - The message to display
 * @param {string} type - Type: 'success' or 'error'
 */
function showNotification(message, type = 'success') {
    // Remove existing notification
    const existingNotification = document.querySelector('.cart-notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // Create new notification
    const notification = document.createElement('div');
    notification.className = `cart-notification ${type}`;
    notification.innerHTML = message;

    // Add it to the body
    document.body.appendChild(notification);

    // Show with animation
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    // Hide after duration
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 300);
    }, NOTIFICATION_DURATION);
}

/**
 * Update counter in header
 * @param {number} count - Number of items in cart
 */
function updateCartCounter(count) {
    const cartBadge = document.querySelector('.btn-header .badge, #cartCounter, .cart-badge');
    if (cartBadge) {
        cartBadge.textContent = count;
        cartBadge.style.display = count > 0 ? 'inline-block' : 'none';

        // Animation when updated
        cartBadge.classList.add('counter-pulse');
        setTimeout(() => {
            cartBadge.classList.remove('counter-pulse');
        }, 500);
    }
}

/**
 * Animate cart icon
 */
function animateCartIcon() {
    const cartIcon = document.querySelector('.btn-header .fa-shopping-cart, .cart-icon, .fa-shopping-cart');
    if (cartIcon) {
        cartIcon.classList.add('cart-bounce');
        setTimeout(() => {
            cartIcon.classList.remove('cart-bounce');
        }, 500);
    }
}

/**
 * Set button loading state
 * @param {HTMLElement} button - The button element
 * @param {boolean} isLoading - Whether button is in loading state
 * @param {string} originalHtml - Original button HTML content
 */
function setButtonLoading(button, isLoading, originalHtml = '') {
    if (isLoading) {
        // Save original HTML if not already saved
        if (!button.dataset.originalHtml) {
            button.dataset.originalHtml = button.innerHTML;
        }

        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
        button.disabled = true;
    } else {
        // Restore original HTML
        button.innerHTML = button.dataset.originalHtml || originalHtml;
        button.disabled = false;
        delete button.dataset.originalHtml;
    }
}

// ==================== CSRF FUNCTIONS ====================

/**
 * Get CSRF token from meta tags or cookies
 * @returns {string|null} CSRF token or null if not found
 */
function getCsrfToken() {
    // Look for meta tag
    const metaToken = document.querySelector('meta[name="_csrf"]');
    if (metaToken) return metaToken.getAttribute('content');

    // Look for cookie (Spring Security default)
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'XSRF-TOKEN') return value;
    }

    return null;
}

/**
 * Get CSRF header name
 * @returns {string} CSRF header name
 */
function getCsrfHeader() {
    const metaHeader = document.querySelector('meta[name="_csrf_header"]');
    if (metaHeader) return metaHeader.getAttribute('content');
    return 'X-CSRF-TOKEN'; // Default value
}

/**
 * Prepare headers for fetch requests including CSRF
 * @returns {Object} Headers for fetch
 */
function getFetchHeaders() {
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
    };

    const csrfToken = getCsrfToken();
    const csrfHeader = getCsrfHeader();

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}

// ==================== UTILITY FUNCTIONS ====================

/**
 * Format number as price in euros
 * @param {number} price - Price to format
 * @returns {string} Formatted price (e.g., 12.50€)
 */
function formatPrice(price) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(price).replace('€', '€');
}

/**
 * Get product name from its card element
 * @param {HTMLElement} card - Product card element
 * @returns {string} Product name
 */
function getProductName(card) {
    if (!card) return 'Product';

    const titleSelectors = [
        '.card-title',
        'h5',
        '.product-title',
        '.recommended-info h5',
        '[class*="title"]'
    ];

    for (const selector of titleSelectors) {
        const element = card.querySelector(selector);
        if (element && element.textContent) {
            return element.textContent.trim();
        }
    }

    return 'Product';
}

/**
 * Refresh cart count from server
 */
async function refreshCartCount() {
    try {
        const response = await fetch('/cart/count');
        const data = await response.json();

        if (data.success) {
            updateCartCounter(data.count);
        }
    } catch (error) {
        console.error('Error refreshing cart count:', error);
    }
}

/**
 * Handle "Add to Cart" form submission via AJAX
 * @param {Event} event - The submit event
 */
async function handleAddToCart(event) {
    event.preventDefault();
    event.stopPropagation();

    const form = event.currentTarget; // El form que disparó el evento
    const button = form.querySelector('button[type="submit"]');
    const productName = getProductName(form.closest('.card, .product-shell, .menu-item') || form);

    const formData = new FormData(form);
    const productId = formData.get('productId');
    const quantity = formData.get('qty') || 1;

    // Set button loading state
    setButtonLoading(button, true);

    try {
        const response = await fetch('/cart/add', {
            method: 'POST',
            headers: getFetchHeaders(),
            body: new URLSearchParams({
                productId: productId,
                qty: quantity
            })
        });

        const data = await response.json();

        if (data.success) {
            // Success
            showNotification(`✓ ${productName} añadido al carrito!`, 'success');
            updateCartCounter(data.cartCount);
            animateCartIcon();
        } else {
            // Error from server
            if (data.redirect) {
                window.location.href = data.redirect;
            } else {
                showNotification(`✗ ${data.message}`, 'error');
            }
        }

    } catch (error) {
        console.error('Error:', error);
        showNotification('✗ Error de conexión. Intenta de nuevo.', 'error');

    } finally {
        // Reset button
        setButtonLoading(button, false);
    }
}

/**
 * Initialize all "Add to Cart" AJAX forms
 * @param {string} selector - CSS selector for forms
 */
function initCart(selector = '.ajax-cart-form') {
    document.querySelectorAll(selector).forEach(form => {
        // Prevent duplicate listeners
        if (form.dataset.ajaxInitialized) return;

        form.addEventListener('submit', handleAddToCart);
        form.dataset.ajaxInitialized = 'true';
    });
}

// ==================== STYLES ====================

// Add styles if they don't exist
(function addStyles() {
    if (document.querySelector('#cart-styles')) return;

    const style = document.createElement('style');
    style.id = 'cart-styles';
    style.textContent = `
        /* Notifications */
        .cart-notification {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            background: white;
            color: #333;
            border-radius: 50px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            transform: translateX(120%);
            transition: transform 0.3s ease;
            z-index: 9999;
            font-weight: 500;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
            max-width: 300px;
            word-break: break-word;
        }
        
        .cart-notification.show {
            transform: translateX(0);
        }
        
        .cart-notification.success {
            background: #4CAF50;
            color: white;
        }
        
        .cart-notification.error {
            background: #f44336;
            color: white;
        }
        
        /* Animations */
        .cart-bounce {
            animation: cartBounce 0.5s ease;
        }
        
        @keyframes cartBounce {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.3); color: #4CAF50; }
        }
        
        .counter-pulse {
            animation: counterPulse 0.5s ease;
        }
        
        @keyframes counterPulse {
            0%, 100% { transform: scale(1); background-color: #dc3545; }
            50% { transform: scale(1.2); background-color: #28a745; }
        }
        
        /* Spinner */
        .fa-spinner {
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        /* Button states */
        .btn-add-cart:disabled,
        .btn-recommended-cart:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        /* Cart badge */
        .btn-header .badge,
        #cartCounter,
        .cart-badge {
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            transition: all 0.3s ease;
        }
    `;

    document.head.appendChild(style);
})();