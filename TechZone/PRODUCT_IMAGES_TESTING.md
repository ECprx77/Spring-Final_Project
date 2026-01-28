# Product Images System - Testing Guide

## Overview

The product images system has been implemented with local file storage. Images are uploaded to `uploads/products/` and served via `/api/uploads/products/`.

## Features Implemented

1. **ProductImage Entity**
   - Links to Product with cascade delete
   - Stores image URL, primary flag, and display order
   - Lazy-loaded relationship

2. **File Storage Service**
   - Validates image types (jpg, jpeg, png, gif, webp)
   - Generates UUID filenames to avoid conflicts
   - Stores files in `uploads/products/` directory
   - Max file size: 5MB

3. **API Endpoints**
   - `POST /api/products/{productId}/images` - Upload image (ADMIN)
   - `GET /api/products/{productId}/images` - List product images (PUBLIC)
   - `DELETE /api/products/images/{imageId}` - Delete image (ADMIN)
   - `PUT /api/products/images/{imageId}/primary` - Set primary image (ADMIN)

4. **Product DTO Enhancement**
   - ProductDTO now includes `images` list
   - Images are automatically loaded when fetching products

## Testing Steps

### 1. Start the Application

```bash
cd TechZone
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./mvnw spring-boot:run
```

### 2. Create Admin User (via H2 Console or SQL)

Access H2 Console: `http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:mem:techzonedb`
- Username: `sa`
- Password: `mdp`

Execute:
```sql
-- Update existing user to admin
UPDATE users SET role_id = 1 WHERE email = 'admin@test.com';
```

Or register and manually update in H2.

### 3. Login as Admin

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"Admin123"}' | \
  jq -r '.accessToken')

echo "Token: $TOKEN"
```

### 4. Create a Product

```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "Laptop professionnel",
    "price": 2499.99,
    "categoryId": 1,
    "status": "en_stock",
    "isPromo": false
  }'
```

Note the product ID from the response.

### 5. Upload an Image

```bash
# Create a test image
convert -size 200x200 xc:blue test-image.jpg

# Upload the image
curl -X POST http://localhost:8080/api/products/1/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test-image.jpg" \
  -F "isPrimary=true"
```

### 6. List Product Images

```bash
curl http://localhost:8080/api/products/1/images
```

### 7. Get Product with Images

```bash
curl http://localhost:8080/api/products/1 | jq '.'
```

The response should include an `images` array with image URLs.

### 8. Set Primary Image (if you have multiple)

```bash
curl -X PUT http://localhost:8080/api/products/images/2/primary \
  -H "Authorization: Bearer $TOKEN"
```

### 9. Delete an Image

```bash
curl -X DELETE http://localhost:8080/api/products/images/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 10. Access Uploaded Image

Open in browser: `http://localhost:8080/api/uploads/products/{filename}`

## Configuration

**application.properties:**
```properties
# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
file.upload-dir=uploads/products
```

## Important Notes

1. **Java Version**: This project requires Java 17. Set `JAVA_HOME=/usr/lib/jvm/java-17-openjdk` when running Maven commands on systems with multiple Java versions.

2. **File Storage**: Images are stored locally in `uploads/products/`. This directory is created automatically if it doesn't exist.

3. **Security**: 
   - Image uploads require ADMIN role
   - Image viewing is public
   - Uploaded files path `/uploads/**` is whitelisted in SecurityConfig

4. **Cascade Delete**: When a product is deleted, all associated images are automatically deleted from the database and filesystem.

5. **Primary Image**: Only one image per product can be primary. Setting a new primary image automatically unsets the previous one.

## API Documentation

After starting the application, visit:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- Look for "Product Images" tag for all image endpoints

## Troubleshooting

**Issue**: "403 Forbidden" when uploading
- **Solution**: Ensure you're using an ADMIN user token

**Issue**: "File too large"
- **Solution**: Check `application.properties` for `spring.servlet.multipart.max-file-size`

**Issue**: "Type de fichier non supporté"
- **Solution**: Only jpg, jpeg, png, gif, webp are allowed

**Issue**: Images not displaying
- **Solution**: Check that `uploads/products/` directory exists and has proper permissions

**Issue**: Maven compilation fails
- **Solution**: Ensure JAVA_HOME is set to Java 17: `JAVA_HOME=/usr/lib/jvm/java-17-openjdk`
