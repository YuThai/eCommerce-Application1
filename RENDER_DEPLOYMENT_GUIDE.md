# Hướng Dẫn Deploy Dự Án eCommerce Lên Render

## 📋 Tổng Quan
Dự án này có 2 phần cần deploy trên Render:
- **Backend**: Java Spring Boot API (port 8081)
- **Frontend**: React Application
- **Database**: MySQL (hoặc sử dụng dịch vụ bên ngoài)

---

## 🔧 BƯỚC 1: Chuẩn Bị Trước Khi Deploy

### 1.1 Yêu Cầu Cần Có
- Tài khoản GitHub
- Tài khoản Render (https://render.com)
- Push code lên GitHub repo

### 1.2 Kiểm Tra Cấu Hình Hiện Tại
**Backend (application.properties)** đã được cấu hình để sử dụng environment variables:
```properties
server.port=${PORT:8081}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

✅ **Điều này rất tốt** - Render sẽ tự động inject environment variables

---

## 🚀 BƯỚC 2: Tạo File Cấu Hình Cho Render

### 2.1 Tạo File `render.yaml` (tùy chọn nhưng được khuyến nghị)

Tạo file `render.yaml` ở **thư mục gốc** của project:

```yaml
services:
  # ========== BACKEND SERVICE ==========
  - type: web
    name: ecommerce-backend
    env: java
    plan: free
    buildCommand: cd eCommersApp && mvn clean package -DskipTests
    startCommand: java -jar eCommersApp/target/Ecom-0.0.1-SNAPSHOT.jar
    envVars:
      - key: PORT
        value: 8081
      - key: JAVA_VERSION
        value: 17
      - key: DB_URL
        fromDatabase:
          name: ecommerce-db
          property: connectionString
      - key: DB_USERNAME
        fromDatabase:
          name: ecommerce-db
          property: user
      - key: DB_PASSWORD
        fromDatabase:
          name: ecommerce-db
          property: password

  # ========== FRONTEND SERVICE ==========
  - type: web
    name: ecommerce-frontend
    env: node
    plan: free
    buildCommand: cd frontend && npm install && npm run build
    startCommand: npx serve -s build -l 3000
    envVars:
      - key: REACT_APP_API_URL
        value: https://ecommerce-backend.onrender.com

  # ========== DATABASE SERVICE ==========
  - type: pserv
    dbName: ecommerce_db
    name: ecommerce-db
    plan: free
    ipAllowList: []
    postgresSqlVersion: 15
```

---

## 💾 BƯỚC 3: Tạo Các File Build Cần Thiết

### 3.1 Tạo File `.gitignore` (Nếu Chưa Có)

```
# Backend
eCommersApp/target/
eCommersApp/*.log
eCommersApp/.classpath
eCommersApp/.project

# Frontend
frontend/node_modules/
frontend/build/
frontend/.env.local
frontend/.env.*.local

# IDE
.idea/
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db
```

### 3.2 Tạo File `system.properties` trong thư mục `eCommersApp/`

File này khai báo phiên bản Java:

```properties
java.runtime.version=17
maven.version=3.8.7
```

---

## 📡 BƯỚC 4: Cấu Hình Backend Cho Render

### 4.1 Cập Nhật `pom.xml` (nếu cần)

Kiểm tra `eCommersApp/pom.xml` có chứa:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

✅ **Build jar file để Render có thể chạy**

### 4.2 Tạo File `Procfile` trong thư mục `eCommersApp/`

```
web: java -jar target/Ecom-0.0.1-SNAPSHOT.jar
```

---

## 🎨 BƯỚC 5: Cấu Hình Frontend Cho Render

### 5.1 Tạo File `.env` trong thư mục `frontend/`

```
REACT_APP_API_URL=http://localhost:8081
REACT_APP_API_BASE_URL=http://localhost:8081/api
```

### 5.2 Tạo File `render.yaml` riêng cho Frontend (nếu deploy riêng)

```yaml
services:
  - type: web
    name: ecommerce-frontend
    env: node
    plan: free
    buildCommand: npm install && npm run build
    startCommand: npx serve -s build -l 3000
    envVars:
      - key: REACT_APP_API_URL
        value: https://ecommerce-backend.onrender.com
```

---

## 🌐 BƯỚC 6: Deploy Lên Render

### 🔴 **Phương Pháp 1: Deploy Backend & Frontend Riêng Biệt (Đơn Giản)**

#### Deploy Backend:

1. **Đăng nhập vào Render**: https://render.com
2. **Click "New +"** → **"Web Service"**
3. **Kết nối GitHub**:
   - Chọn repo: `eCommerce-Application1-main`
   - Branch: `main`
4. **Điền thông tin**:
   - **Name**: `ecommerce-backend`
   - **Environment**: `Docker` (hoặc chọn Maven từ dropdown)
   - **Build Command**: 
     ```bash
     cd eCommersApp && mvn clean package -DskipTests
     ```
   - **Start Command**: 
     ```bash
     java -jar eCommersApp/target/Ecom-0.0.1-SNAPSHOT.jar
     ```
   - **Plan**: Free

5. **Cấu Hình Environment Variables** (quan trọng ⚠️):
   ```
   PORT=8081
   DB_URL=jdbc:mysql://[DB_HOST]:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
   DB_USERNAME=your_db_user
   DB_PASSWORD=your_db_password
   JAVA_VERSION=17
   ```

6. **Click "Create Web Service"**

#### Deploy Frontend:

1. **Click "New +"** → **"Web Service"**
2. **Kết nối GitHub** cùng repo
3. **Điền thông tin**:
   - **Name**: `ecommerce-frontend`
   - **Environment**: `Node`
   - **Build Command**: 
     ```bash
     cd frontend && npm install && npm run build
     ```
   - **Start Command**: 
     ```bash
     cd frontend && npx serve -s build -l 3000
     ```

4. **Environment Variables**:
   ```
   REACT_APP_API_URL=https://ecommerce-backend.onrender.com
   REACT_APP_API_BASE_URL=https://ecommerce-backend.onrender.com/api
   ```

5. **Click "Create Web Service"**

---

### 🟢 **Phương Pháp 2: Deploy Cùng Lúc Với `render.yaml` (Pro)**

**Ưu điểm**: Deploy cả backend + frontend + database cùng lúc, quản lý tập trung

1. **Push file `render.yaml` lên GitHub**
2. **Đăng nhập Render** → **"New +"** → **"Blueprint"**
3. **Kết nối GitHub** → Chọn repo
4. **Render sẽ tự động**:
   - Parse `render.yaml`
   - Tạo 3 services: Backend, Frontend, Database
   - Deploy tất cả
   - Kết nối tự động

5. **Verify deployment** trong Dashboard

---

## 🗄️ BƯỚC 7: Cấu Hình Database

### Phương Pháp A: Sử Dụng PostgreSQL của Render (Đơn Giản)

1. **Trên Render Dashboard** → **"New +"** → **"PostgreSQL"**
2. **Name**: `ecommerce-db`
3. **Region**: Chọn region gần bạn
4. **Connection String** sẽ được tạo tự động

### Phương Pháp B: Sử Dụng MySQL Bên Ngoài (Như Planetscale, Aiven)

1. **Tạo MySQL database trên Planetscale** (miễn phí):
   - Đăng ký: https://planetscale.com
   - Tạo database mới
   - Lấy connection string

2. **Copy connection string** vào environment variables của Backend

### Phương Pháp C: Sử Dụng MySQL Render

Render hỗ trợ MySQL thông qua **Private Services** (yêu cầu paid plan)

---

## 🔌 BƯỚC 8: Kết Nối Backend & Frontend

### 8.1 Cập Nhật API Endpoint trong Frontend

Chỉnh sửa file `frontend/src/Router/api.jsx`:

```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

export const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  withCredentials: true
});
```

### 8.2 Enable CORS trong Backend

Tạo file `SecurityConfig.java` nếu chưa có:

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://ecommerce-frontend.onrender.com",
                            "https://*.onrender.com"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
```

---

## ✅ BƯỚC 9: Kiểm Tra & Verify

### Sau khi deploy, kiểm tra:

1. **Backend Status**:
   ```bash
   curl https://ecommerce-backend.onrender.com/api/health
   ```

2. **Frontend Status**:
   - Truy cập: `https://ecommerce-frontend.onrender.com`
   - Mở DevTools (F12) → Console tab
   - Kiểm tra API calls

3. **Database Connection**:
   - Check Backend logs trên Render Dashboard
   - Tìm message: `"Hibernate: select 1"` (nghĩa là kết nối thành công)

4. **CORS Test**:
   - Đăng nhập từ frontend
   - Kiểm tra Network tab trong DevTools
   - Requests phải return 200 OK

---

## 🐛 BƯỚC 10: Troubleshooting

### Problem: Backend builds nhưng không start
**Giải pháp**:
```bash
# Check logs
# Settings → View logs (trên Render)

# Build lại locally để test
cd eCommersApp
mvn clean package -DskipTests
java -jar target/Ecom-0.0.1-SNAPSHOT.jar
```

### Problem: "Cannot connect to database"
**Giải pháp**:
- Kiểm tra `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables
- Kiểm tra database firewall settings
- Kiểm tra `application.properties` có `useSSL=false` cho MySQL

### Problem: Frontend requests không reach backend
**Giải pháp**:
- Kiểm tra `REACT_APP_API_URL` environment variable
- Kiểm tra CORS headers
- Mở DevTools → Network tab → Kiểm tra request URL

### Problem: 502 Bad Gateway Error
**Giải pháp**:
```
1. Backend service crash
2. Build command sai hoặc timeout
3. RAM/CPU không đủ (upgrade dari plan free)
4. Port configuration sai
```

---

## 📊 Environment Variables Tóm Tắt

### Backend (`ecommerce-backend`)
```
PORT=8081
JAVA_VERSION=17
DB_URL=jdbc:mysql://[HOST]:3306/[DB]?useSSL=false&serverTimezone=UTC
DB_USERNAME=[USER]
DB_PASSWORD=[PASSWORD]
```

### Frontend (`ecommerce-frontend`)
```
REACT_APP_API_URL=https://ecommerce-backend.onrender.com
REACT_APP_API_BASE_URL=https://ecommerce-backend.onrender.com/api
```

---

## 📝 Danh Sách Deploy Checklist

- [ ] Code commit & push lên GitHub
- [ ] Tạo file `system.properties` trong `eCommersApp/`
- [ ] Tạo file `.gitignore` (nếu cần)
- [ ] Cấu hình environment variables
- [ ] Deploy Backend service
- [ ] Deploy Database
- [ ] Deploy Frontend service
- [ ] Test Backend API endpoint
- [ ] Test Frontend access to Backend
- [ ] Test login/authentication flow
- [ ] Monitor logs cho errors

---

## 🎯 Kết Quả Mong Đợi

Sau khi hoàn thành:
- ✅ Backend API chạy tại: `https://ecommerce-backend.onrender.com`
- ✅ Frontend chạy tại: `https://ecommerce-frontend.onrender.com`
- ✅ Database kết nối thành công
- ✅ CORS hoạt động
- ✅ Authentication (JWT) hoạt động
- ✅ Tất cả routes và APIs hoạt động

---

## 📚 Tài Liệu Tham Khảo

- Render Docs: https://render.com/docs
- Spring Boot Deployment: https://spring.io/guides/gs/deploying-spring-boot-app-to-cloud/
- React Deployment: https://create-react-app.dev/deployment/
- Environment Variables: https://render.com/docs/environment-variables

---

**Cần giúp gì thêm? Liên hệ support hoặc check logs trên Render Dashboard!** 🚀
