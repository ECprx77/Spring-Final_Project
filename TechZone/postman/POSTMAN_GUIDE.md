# 📮 Postman Collection - TechZone API

## Overview

This Postman collection provides **complete API testing coverage** for the TechZone e-commerce platform. It includes all REST endpoints organized by functionality with automatic token management.

---

## 📦 Files Included

1. **TechZone_API.postman_collection.json** - Main collection with all endpoints
2. **TechZone_Local.postman_environment.json** - Environment variables for local testing
3. **POSTMAN_GUIDE.md** - This documentation file

---

## 🚀 Quick Start

### 1. Import into Postman

**Option A: Import Files**
1. Open Postman
2. Click "Import" button (top left)
3. Drag and drop both JSON files:
   - `TechZone_API.postman_collection.json`
   - `TechZone_Local.postman_environment.json`

**Option B: Import from URL** (if hosted)
1. Click "Import" → "Link"
2. Paste URL to the collection JSON

### 2. Select Environment
1. In top-right corner, select "TechZone Local Environment" from dropdown
2. Verify `base_url` is set to `http://localhost:8080/api`

### 3. Start the Application
```bash
cd TechZone
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./mvnw spring-boot:run
```

Wait for application to start (look for "Started TechZoneApplication")

### 4. Test the API
1. Go to "Authentication" folder
2. Run "Login" or "Login as Admin" request
3. JWT token is automatically saved to environment
4. All other requests will now work with authentication

---

## 📁 Collection Structure

### 1. **Authentication** (6 requests)
- Register New User
- Login (Regular User)
- Login as Admin
- Get Current User Profile
- Refresh Token
- Logout

**Auto-save feature**: Login requests automatically save JWT token to `{{jwt_token}}` variable!

### 2. **Products (Public)** (6 requests)
No authentication required:
- Get All Products (with pagination & sorting)
- Get Product by ID
- Get Products by Category
- Search Products
- Get Promotional Products
- Get In-Stock Products

### 3. **Products (Admin)** (3 requests)
Requires ADMIN role:
- Create Product
- Update Product
- Delete Product

### 4. **Categories** (5 requests)
- Get All Categories (public)
- Get Category by ID (public)
- Create Category (ADMIN)
- Update Category (ADMIN)
- Delete Category (ADMIN)

### 5. **Shopping Cart** (5 requests)
All require authentication:
- Get My Cart
- Add Item to Cart
- Update Cart Item Quantity
- Remove Item from Cart
- Clear Entire Cart

### 6. **Orders** (7 requests)
User endpoints:
- Create Order from Cart
- Get My Orders
- Get Order by ID (own orders only)
- Cancel My Order

Admin endpoints:
- Get All Orders (ADMIN)
- Get Order Details (ADMIN)
- Update Order Status (ADMIN)

---

## 🔑 Authentication Flow

### How It Works

1. **Login**: Send credentials to `/auth/login`
2. **Auto-save**: JWT token automatically saved to `{{jwt_token}}` environment variable
3. **Use Token**: All authenticated requests include token in Authorization header
4. **Expiration**: Token expires in 24 hours
5. **Refresh**: Use `/auth/refresh-token` before expiration

### Test Accounts

After starting the app, create test accounts:

```bash
# Run the test user setup script
cd TechZone
./setup-test-users.sh
```

**Pre-configured accounts**:
- **User**: user@techzone.com / User123!
- **Admin**: admin@techzone.com / Admin123!

### Manual Token Usage

If auto-save doesn't work:
1. Copy `accessToken` from login response
2. Go to Environment (top right)
3. Set `jwt_token` variable manually
4. Or use "Bearer Token" auth in individual requests

---

## 🧪 Testing Workflows

### Workflow 1: Complete Shopping Flow

1. **Browse Products**
   - Run: `Get All Products`
   - Run: `Get Promotional Products`
   - Run: `Search Products` (query: "laptop")

2. **Login as User**
   - Run: `Login` (user@techzone.com)
   - Token auto-saved ✅

3. **Add to Cart**
   - Run: `Add Item to Cart` (productId: 1, quantity: 2)
   - Run: `Add Item to Cart` (productId: 5, quantity: 1)
   - Run: `Get My Cart` (verify contents)

4. **Create Order**
   - Run: `Create Order from Cart`
   - Note the order ID from response

5. **View Order**
   - Run: `Get My Orders`
   - Run: `Get Order by ID` (use order ID from step 4)

### Workflow 2: Admin Product Management

1. **Login as Admin**
   - Run: `Login as Admin` (admin@techzone.com)
   - Admin token saved ✅

2. **Manage Products**
   - Run: `Create Product` (creates new product)
   - Copy product ID from response
   - Run: `Update Product` (modify the new product)
   - Run: `Get Product by ID` (verify changes)

3. **Manage Orders**
   - Run: `Get All Orders (Admin)` (see all customer orders)
   - Run: `Update Order Status (Admin)` (change to "SHIPPED")

### Workflow 3: Category & Product Management

1. **Create Category** (as Admin)
   - Run: `Create Category (Admin)`
   - Note category ID

2. **Create Product in Category**
   - Run: `Create Product`
   - Set `categoryId` to new category ID

3. **Filter by Category**
   - Run: `Get Products by Category`
   - Verify new product appears

---

## 🔍 Request Examples

### Pagination & Sorting

Most list endpoints support:
```
?page=0&size=10&sortBy=name&sortDirection=asc
```

**Parameters**:
- `page`: Page number (0-indexed)
- `size`: Items per page (default: 10)
- `sortBy`: Field to sort by (name, price, createdAt)
- `sortDirection`: asc or desc

### Search

```
GET /api/products/search?query=laptop
```

Searches both product name and description.

### Filters

```
GET /api/products/promo/list        # Only promotional items
GET /api/products/in-stock          # Only in-stock items
GET /api/products/category/1        # Category filter
```

---

## 📋 Response Formats

### Success Response (Single Item)
```json
{
  "id": 1,
  "name": "Product Name",
  "price": 999.99,
  "status": "en_stock"
}
```

### Success Response (Paginated)
```json
{
  "content": [...],
  "pageable": {...},
  "totalPages": 5,
  "totalElements": 50,
  "size": 10,
  "number": 0
}
```

### Error Response
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with ID: 999",
  "path": "/api/products/999",
  "timestamp": "2026-01-29T15:22:00.000+00:00"
}
```

---

## 🔐 Authorization Levels

### Public (No Auth Required)
- GET /api/products/**
- GET /api/categories/**
- POST /api/auth/register
- POST /api/auth/login

### User (JWT Required)
- /api/auth/me
- /api/auth/logout
- /api/auth/refresh-token
- /api/cart/**
- /api/orders/** (own orders only)

### Admin (ADMIN Role Required)
- /api/admin/products/**
- /api/admin/categories/**
- /api/admin/orders/**

---

## 🛠️ Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `base_url` | API base URL | `http://localhost:8080/api` |
| `jwt_token` | Current JWT token | Auto-saved from login |
| `admin_token` | Admin JWT token | Auto-saved from admin login |
| `user_id` | Current user ID | Auto-saved from login |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `test_user_email` | Test user email | user@techzone.com |
| `test_user_password` | Test user password | User123! |
| `test_admin_email` | Test admin email | admin@techzone.com |
| `test_admin_password` | Test admin password | Admin123! |

---

## 🧩 Advanced Features

### Pre-request Scripts

Login requests include automatic token extraction:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set('jwt_token', jsonData.accessToken);
    pm.environment.set('user_id', jsonData.id);
    console.log('Token saved:', jsonData.accessToken);
}
```

### Authorization Inheritance

All requests in authenticated folders automatically use:
```
Authorization: Bearer {{jwt_token}}
```

### Dynamic Data

Use `{{$randomInt}}`, `{{$guid}}`, etc. for generating test data.

---

## 📊 Testing Checklist

### Authentication ✅
- [ ] Register new user
- [ ] Login as user
- [ ] Login as admin
- [ ] Get profile
- [ ] Refresh token
- [ ] Logout

### Products ✅
- [ ] List all products
- [ ] Get single product
- [ ] Search products
- [ ] Filter by category
- [ ] Filter by promo status
- [ ] Filter by stock status
- [ ] Create product (admin)
- [ ] Update product (admin)
- [ ] Delete product (admin)

### Categories ✅
- [ ] List categories
- [ ] Get single category
- [ ] Create category (admin)
- [ ] Update category (admin)
- [ ] Delete category (admin)

### Shopping Cart ✅
- [ ] Get empty cart
- [ ] Add item to cart
- [ ] Add same item again (increments)
- [ ] Update item quantity
- [ ] Remove item
- [ ] Clear cart

### Orders ✅
- [ ] Create order from cart
- [ ] List my orders
- [ ] Get order details
- [ ] Cancel order
- [ ] Admin: List all orders
- [ ] Admin: Get any order
- [ ] Admin: Update order status

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized

**Solution**:
1. Check if token is saved: View environment variables
2. Try logging in again
3. Verify token hasn't expired (24h validity)

### Issue: 403 Forbidden

**Solution**:
- Endpoint requires ADMIN role
- Login as admin using "Login as Admin" request
- Verify you're using `admin@techzone.com` account

### Issue: 404 Not Found

**Solution**:
- Check if app is running on port 8080
- Verify base_url: `http://localhost:8080/api`
- Check endpoint path (should include `/api` prefix)

### Issue: Connection Refused

**Solution**:
- Start the application: `./mvnw spring-boot:run`
- Wait for "Started TechZoneApplication" message
- Check port 8080 is not in use: `lsof -ti:8080`

### Issue: Token Not Auto-Saving

**Solution**:
1. Check Postman console for errors (View → Show Postman Console)
2. Manually copy token from response
3. Set `jwt_token` in environment variables
4. Verify test script is enabled in request

---

## 📚 Additional Resources

- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **H2 Console**: http://localhost:8080/api/h2-console
- **Frontend**: http://localhost:8080/app/
- **Project README**: ../README.md
- **Testing Guide**: TESTING.md

---

## 🎯 Tips & Best Practices

1. **Save Requests**: Modify and save requests for your specific test cases
2. **Use Folders**: Organize custom tests into folders by feature
3. **Environment per Stage**: Create separate environments for dev/staging/prod
4. **Test Suites**: Use Collection Runner for automated testing
5. **Monitor**: Use Postman Monitors for continuous API health checks
6. **Share**: Export and share collection with team members
7. **Version Control**: Commit collection JSON to git for team collaboration

---

## 📝 Collection Statistics

- **Total Requests**: 32
- **Folders**: 6
- **Environment Variables**: 8
- **Auto-saved Tokens**: Yes ✅
- **Test Scripts**: Login requests
- **Authorization**: JWT Bearer Token

---

## 🎓 Learning Resources

### Understanding JWT
- Token expires in 24 hours
- Stateless (no server-side session)
- Contains user ID and role
- Validated on each request

### REST Best Practices Demonstrated
- Consistent URL structure (/api prefix)
- Proper HTTP methods (GET, POST, PUT, DELETE)
- Pagination for list endpoints
- Meaningful status codes (200, 201, 400, 401, 403, 404)
- Descriptive error messages

### Security Features
- Password hashing (BCrypt)
- Role-based access control (USER, ADMIN)
- JWT authentication
- Protected endpoints
- Audit logging

---

**Last Updated**: 2026-01-29  
**Collection Version**: 1.0  
**Compatible with**: TechZone API v1.0

---

For questions or issues, refer to the main project documentation or contact the development team.
