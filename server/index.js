require('newrelic');
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const port = 3000;

app.use(cors());
app.use(bodyParser.json());

// In-memory product store
const products = [
    {
        id: "1",
        name: "Vintage Camera",
        description: "Classic film camera for enthusiasts. Capture moments with a retro feel.",
        price: 199.99,
        imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80",
        category: "Electronics"
    },
    {
        id: "2",
        name: "Leather Backpack",
        description: "Durable leather backpack for travel and daily commute. Stylish and functional.",
        price: 89.50,
        imageUrl: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&q=80",
        category: "Accessories"
    },
    {
        id: "3",
        name: "Wireless Headphones",
        description: "Noise-cancelling over-ear headphones. Immerse yourself in music.",
        price: 249.00,
        imageUrl: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80",
        category: "Electronics"
    },
    {
        id: "4",
        name: "Running Shoes",
        description: "Lightweight running shoes for daily joggers. Maximum comfort and support.",
        price: 120.00,
        imageUrl: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80",
        category: "Footwear"
    },
    {
        id: "5",
        name: "Smart Watch",
        description: "Track your fitness and notifications on the go. Sleek design.",
        price: 299.99,
        imageUrl: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80",
        category: "Electronics"
    },
    {
        id: "6",
        name: "Coffee Maker",
        description: "Brew the perfect cup every morning. Programmable and easy to clean.",
        price: 45.00,
        imageUrl: "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500&q=80",
        category: "Home"
    }
];

// Logging middleware
app.use((req, res, next) => {
    console.log(`[INFO] ${new Date().toISOString()} - ${req.method} ${req.url}`);
    next();
});

app.get('/products', (req, res) => {
    console.debug(`[DEBUG] Fetching ${products.length} products`);
    // Simulate network delay
    setTimeout(() => {
        res.json(products);
    }, 500);
});

// Get product by ID (with error simulation)
app.get('/products/:id', (req, res) => {
    const id = req.params.id;
    console.log(`[INFO] Fetching product details for ID: ${id}`);

    // Simulate network delay
    setTimeout(() => {
        if (id === '3') {
            console.error(`[ERROR] Simulated backend failure for product ${id}`);
            res.status(500).json({ error: 'Internal Server Error: Failed to fetch product details' });
        } else {
            const product = products.find(p => p.id === id);
            if (product) {
                res.json(product);
            } else {
                res.status(404).json({ error: 'Product not found' });
            }
        }
    }, Math.random() * 1000 + 500);
});

app.post('/checkout', (req, res) => {
    const { cart, total } = req.body;
    console.log(`[INFO] Processing checkout for amount: $${total}`);
    console.debug(`[DEBUG] Cart items: ${JSON.stringify(cart)}`);

    // Simulate processing delay
    setTimeout(() => {
        // Random failure (20% chance)
        if (Math.random() < 0.2) {
            console.error(`[ERROR] Payment failed for amount: $${total}`);
            res.status(500).json({ error: "Payment Gateway Timeout" });
        } else {
            console.log(`[INFO] Payment successful for amount: $${total}`);
            res.json({ success: true, transactionId: "TXN-" + Date.now() });
        }
    }, 1500);
});

app.listen(port, () => {
    console.log(`Reli-Store Server listening on port ${port}`);
});
