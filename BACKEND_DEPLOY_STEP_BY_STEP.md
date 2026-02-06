# 🚀 Deploy Backend Lên Render - Hướng Dẫn Chi Tiết Từng Bước

## 📋 Mục Tiêu
Deploy Spring Boot backend lên Render và kết nối database để có API endpoint hoạt động.

---

## ✅ BƯỚC 1: Chuẩn Bị Database Trước (QUAN TRỌNG!)

### Option A: Sử dụng Render PostgreSQL (Khuyến nghị - Miễn phí)

1. **Trên Render Dashboard**
   - Click **"New +"** (góc trên phải)
   - Chọn **"PostgreSQL"**

2. **Điền thông tin:**
   ```
   Name: ecommerce-db
   Database Name: ecommerce_db
   User: (để Render tự tạo)
   Region: Singapore (cùng region với backend)
   Plan: Free
   ```

3. **Click "Create Database"**
   - Chờ 1-2 phút để database được tạo
   - ✅ Database sẽ hiện màu xanh khi ready

4. **Lấy Connection String:**
   - Vào database vừa tạo
   - Tab **"Info"**
   - Copy các thông tin sau:
     ```
     Internal Database URL: postgres://user:pass@host:5432/db
     Hostname: dpg-xxx.singapore.render.com
     Port: 5432
     Database: ecommerce_db
     Username: ecommerce_db_user
     Password: (một string dài)
     ```

5. **⚠️ Lưu ý quan trọng:**
   - PostgreSQL khác MySQL! Cần update `pom.xml`
   - Hoặc dùng MySQL từ bên ngoài (Planetscale)

---

### Option B: Sử dụng MySQL Bên Ngoài (Planetscale - Miễn phí)

1. **Đăng ký Planetscale:**
   - Vào: https://planetscale.com
   - Sign up (dùng GitHub login)

2. **Tạo Database:**
   - Click **"New database"**
   - Name: `ecommerce-db`
   - Region: `AWS ap-southeast` (gần Singapore)
   - Click **"Create database"**

3. **Lấy Connection String:**
   - Vào database vừa tạo
   - Tab **"Connect"**
   - Click **"New password"**
   - Chọn **"Java"** 
   - Copy thông tin:
     ```
     Host: aws.connect.psdb.cloud
     Username: xxx
     Password: pscale_pw_xxx
     Database: ecommerce-db
     ```

4. **Format Connection String cho Spring Boot:**
   ```
   jdbc:mysql://aws.connect.psdb.cloud/ecommerce-db?sslMode=VERIFY_IDENTITY&useSSL=true&serverTimezone=UTC
   ```

---

## 🔧 BƯỚC 2: Cập Nhật Code Cho PostgreSQL (Nếu Dùng Render DB)

### 2.1 Cập Nhật `pom.xml`

Thêm PostgreSQL driver vào `eCommersApp/pom.xml`:

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Giữ lại MySQL driver nếu muốn support cả 2 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2.2 Cập Nhật `application.properties`

File `eCommersApp/src/main/resources/application.properties` đã OK:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 2.3 Thêm Dialect Cho PostgreSQL

Thêm vào `application.properties`:

```properties
# Nếu dùng PostgreSQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Nếu dùng MySQL (giữ nguyên)
# spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### 2.4 Commit & Push

```bash
git add .
git commit -m "Add PostgreSQL support for Render deployment"
git push origin main
```

---

## 🎯 BƯỚC 3: Deploy Backend Lên Render

### 3.1 Tạo Web Service

1. **Trên Render Dashboard:**
   - Click **"New +"**
   - Chọn **"Web Service"**

2. **Kết Nối GitHub:**
   - Click **"Connect account"** (nếu chưa connect)
   - Authorize Render
   - Chọn repository: **`eCommerce-Application1-main`**

3. **Điền Form Như Sau:**

   **✅ Name:**
   ```
   ecommerce-backend
   ```

   **✅ Region:**
   ```
   Singapore (Southeast Asia)
   ```
   *(Chọn cùng region với database)*

   **✅ Branch:**
   ```
   main
   ```

   **✅ Root Directory:**
   ```
   eCommersApp
   ```
   *(Quan trọng! Dockerfile nằm trong folder này)*

   **✅ Environment:**
   ```
   Docker
   ```
   *(Chọn từ dropdown)*

   **✅ Dockerfile Path:**
   ```
   ./Dockerfile
   ```
   *(Hoặc để trống, Render tự tìm)*

   **✅ Docker Build Context:**
   ```
   eCommersApp
   ```

   **✅ Instance Type:**
   ```
   Free
   ```

4. **Cuộn xuống phần "Advanced":**
   - Click **"Advanced"** để expand
   - Không cần điền gì thêm

---

## 🔑 BƯỚC 4: Cấu Hình Environment Variables

**Scroll xuống phần "Environment Variables"**

### Nếu Dùng PostgreSQL (Render):

1. Click **"Add Environment Variable"**

2. Thêm từng biến sau:

   **Biến 1:**
   ```
   Key: DB_URL
   Value: jdbc:postgresql://dpg-xxx.singapore.render.com:5432/ecommerce_db
   ```
   *(Thay `dpg-xxx...` bằng hostname của database bạn)*

   **Biến 2:**
   ```
   Key: DB_USERNAME
   Value: ecommerce_db_user
   ```
   *(Username từ database info)*

   **Biến 3:**
   ```
   Key: DB_PASSWORD
   Value: (paste password từ database info)
   ```

   **Biến 4:**
   ```
   Key: PORT
   Value: 8081
   ```

   **Biến 5:**
   ```
   Key: SPRING_PROFILES_ACTIVE
   Value: prod
   ```

### Nếu Dùng MySQL (Planetscale):

   **Biến 1:**
   ```
   Key: DB_URL
   Value: jdbc:mysql://aws.connect.psdb.cloud/ecommerce-db?sslMode=VERIFY_IDENTITY&useSSL=true&serverTimezone=UTC
   ```

   **Biến 2:**
   ```
   Key: DB_USERNAME
   Value: (username từ Planetscale)
   ```

   **Biến 3:**
   ```
   Key: DB_PASSWORD
   Value: pscale_pw_xxx... (password từ Planetscale)
   ```

   **Biến 4:**
   ```
   Key: PORT
   Value: 8081
   ```

---

## 🚀 BƯỚC 5: Deploy!

1. **Cuộn lên trên hoặc xuống dưới**
2. **Click nút "Create Web Service"** (màu xanh lá)
3. **Chờ Deploy:**
   - Render sẽ build Docker image
   - Thời gian: 5-10 phút
   - Bạn sẽ thấy logs realtime

### Theo Dõi Build Logs:

```
==> Cloning from GitHub...
==> Building Docker image...
==> [1/2] MAVEN BUILD STAGE
    Downloading dependencies...
    Building JAR file...
==> [2/2] RUN STAGE
    Copying JAR file...
==> Image built successfully
==> Starting service...
==> Service is live 🎉
```

4. **Khi Thấy "Your service is live":**
   - ✅ Backend đã deploy thành công!
   - Copy URL: `https://ecommerce-backend.onrender.com`

---

## ✅ BƯỚC 6: Verify Deployment

### 6.1 Kiểm Tra Service Status

1. Trên Render Dashboard → Vào service `ecommerce-backend`
2. Tab **"Logs"** → Xem logs
3. Tìm dòng:
   ```
   Started EcomApplication in X seconds
   Tomcat started on port(s): 8081
   ```

### 6.2 Test API Endpoints

**Mở Terminal (PowerShell) và test:**

```powershell
# Test 1: Health check
curl https://ecommerce-backend.onrender.com

# Test 2: API endpoint (ví dụ)
curl https://ecommerce-backend.onrender.com/api/products

# Test 3: Swagger UI (nếu có)
# Mở browser: https://ecommerce-backend.onrender.com/swagger-ui.html
```

### 6.3 Kiểm Tra Database Connection

**Xem logs để verify database:**

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed
Hibernate: select 1
Database connected successfully ✓
```

Nếu thấy error:
```
Could not connect to database
```
→ Kiểm tra lại DB_URL, DB_USERNAME, DB_PASSWORD

---

## 🧪 BƯỚC 7: Test API Với Postman/Thunder Client

### 7.1 Test Register User

**POST Request:**
```
URL: https://ecommerce-backend.onrender.com/api/auth/register
Method: POST
Headers: Content-Type: application/json
Body:
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "phone": "0123456789"
}
```

**Expected Response:**
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

### 7.2 Test Login

**POST Request:**
```
URL: https://ecommerce-backend.onrender.com/api/auth/login
Method: POST
Body:
{
  "username": "testuser",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser"
}
```

### 7.3 Test Get Products

**GET Request:**
```
URL: https://ecommerce-backend.onrender.com/api/products
Method: GET
Headers: Authorization: Bearer <token_từ_login>
```

---

## 📊 BƯỚC 8: Xem Logs & Monitor

### 8.1 Realtime Logs

1. Render Dashboard → Service `ecommerce-backend`
2. Tab **"Logs"**
3. Xem requests realtime:
   ```
   2026-02-06 10:30:45 | GET /api/products | 200 OK | 125ms
   2026-02-06 10:30:50 | POST /api/auth/login | 200 OK | 85ms
   ```

### 8.2 Check Errors

Nếu có lỗi, logs sẽ hiện:
```
ERROR: Could not connect to database
    at HikariPool.getConnection()
```

→ Fix bằng cách update environment variables

---

## 🔧 BƯỚC 9: Troubleshooting

### Issue 1: Service Won't Start

**Error:**
```
Service exited
Error: could not find or load main class
```

**Fix:**
- Check Dockerfile path đúng chưa
- Root Directory = `eCommersApp`
- Rebuild: Click **"Manual Deploy"** → **"Clear build cache & deploy"**

### Issue 2: Database Connection Failed

**Error:**
```
Communications link failure
Could not connect to database
```

**Fix:**
1. Check environment variables:
   ```
   DB_URL đúng format?
   DB_USERNAME đúng?
   DB_PASSWORD đúng?
   ```

2. Test connection từ local:
   ```bash
   # PostgreSQL
   psql "postgres://user:pass@host:5432/db"
   
   # MySQL
   mysql -h host -u user -p
   ```

3. Check database status trên Render
   - Database service có running?
   - Region có giống backend không?

### Issue 3: 502 Bad Gateway

**Nguyên nhân:**
- Backend crash
- Port sai
- Build failed

**Fix:**
1. Check logs
2. Verify PORT=8081 trong environment variables
3. Check Dockerfile EXPOSE 8081

### Issue 4: API Returns 404

**Nguyên nhân:**
- Controller path sai
- Context path configured

**Fix:**
- Check `application.properties`:
  ```properties
  # Xóa dòng này nếu có
  # server.servlet.context-path=/api
  ```
- Controller mapping:
  ```java
  @RestController
  @RequestMapping("/api")
  public class ProductController { ... }
  ```

---

## 🎉 BƯỚC 10: Hoàn Tất!

### Checklist Cuối Cùng:

- [ ] Backend service status: **Live** ✅
- [ ] Database connected ✅
- [ ] API endpoints responding ✅
- [ ] No errors in logs ✅
- [ ] Swagger UI accessible (optional) ✅

### URLs Của Bạn:

```
✅ Backend API: https://ecommerce-backend.onrender.com
✅ Swagger UI: https://ecommerce-backend.onrender.com/swagger-ui.html
✅ Database: (internal connection)
```

---

## 📝 Next Steps

### 1. Kết Nối Frontend

Sau khi backend chạy OK, update frontend `api.jsx`:

```javascript
const API_BASE_URL = 'https://ecommerce-backend.onrender.com';
```

### 2. Enable CORS

Update `SecurityConfig.java`:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "https://ecommerce-frontend.onrender.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 3. Deploy Frontend

Sau khi backend OK, deploy frontend service tiếp theo.

---

## 🆘 Cần Giúp?

**Check logs first!** 99% vấn đề được giải quyết bằng cách đọc logs.

1. Render Dashboard → Service → **Logs**
2. Tìm error message
3. Search error trên Google
4. Hoặc check file `TROUBLESHOOTING.md`

---

## 💡 Pro Tips

1. **Free Plan Limitations:**
   - Service sleep sau 15 phút không dùng
   - Cold start ~30 giây khi wake up
   - Upgrade lên Starter ($7/month) để always running

2. **Database Backups:**
   - Render tự backup PostgreSQL hàng ngày
   - Download backup từ Dashboard

3. **Custom Domain:**
   - Setting → Custom Domain
   - Add your domain
   - Update DNS records

4. **Auto Deploy:**
   - Mỗi lần push lên GitHub `main` branch
   - Render tự động rebuild & deploy
   - Không cần làm gì thêm!

---

**🎊 Chúc mừng! Backend của bạn đã live!** 🚀

