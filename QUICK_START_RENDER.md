# 🚀 Deploy Lên Render - Hướng Dẫn Nhanh

## 10 Bước Để Deploy

### ✅ Bước 1: Chuẩn Bị
```bash
# 1. Commit tất cả code
git add .
git commit -m "Prepare for Render deployment"
git push origin main

# 2. Verificate local build
cd eCommersApp
mvn clean package -DskipTests
```

### ✅ Bước 2: Đi Đến Render.com
1. Đăng nhập: https://render.com
2. Click **"New +"** button
3. Chọn **"Web Service"**

---

## 🔧 Bước 3: Deploy Backend

**Lựa chọn 1: Dùng GitHub Integration**
1. Kết nối GitHub account
2. Chọn repo: `eCommerce-Application1-main`
3. Điền:
   ```
   Name: ecommerce-backend
   Environment: Docker (hoặc Java)
   Build Command: cd eCommersApp && mvn clean package -DskipTests
   Start Command: java -jar eCommersApp/target/Ecom-0.0.1-SNAPSHOT.jar
   Instance Type: Free
   ```

4. **Environment Variables**:
   ```
   DB_URL=jdbc:mysql://YOUR_DB_HOST:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JAVA_VERSION=17
   ```

5. Click **"Create Web Service"**
6. ⏳ Chờ 5-10 phút để build xong
7. ✅ Copy URL: `https://ecommerce-backend.onrender.com`

---

## 🎨 Bước 4: Deploy Frontend

1. Click **"New +"** → **"Web Service"**
2. Cùng GitHub repo
3. Điền:
   ```
   Name: ecommerce-frontend
   Environment: Node
   Build Command: cd frontend && npm install && npm run build
   Start Command: cd frontend && npx serve -s build -l 3000
   Instance Type: Free
   ```

4. **Environment Variables**:
   ```
   REACT_APP_API_URL=https://ecommerce-backend.onrender.com
   ```

5. Click **"Create Web Service"**
6. ✅ Frontend URL: `https://ecommerce-frontend.onrender.com`

---

## 💾 Bước 5: Cấu Hình Database

### Phương Án A: PostgreSQL trên Render (miễn phí)
1. Click **"New +"** → **"PostgreSQL"**
2. Name: `ecommerce-db`
3. Region: `oregon` (hoặc gần bạn)
4. Click **"Create Database"**
5. Copy connection string

### Phương Án B: MySQL Bên Ngoài (Planetscale)
1. Đăng ký: https://planetscale.com
2. Tạo database
3. Lấy MySQL URL

---

## 🔌 Bước 6: Cập Nhật Backend Database Variables

1. Vào Backend service → **Settings**
2. Tìm **Environment** section
3. Update:
   ```
   DB_URL=jdbc:mysql://mysql-host:3306/db
   DB_USERNAME=user
   DB_PASSWORD=password
   ```
4. Click **"Save"**
5. ⏳ Backend tự động restart

---

## ✅ Bước 7: Verify Deployment

### Test Backend
```bash
curl https://ecommerce-backend.onrender.com/api/products
```

### Test Frontend
- Mở: https://ecommerce-frontend.onrender.com
- Mở DevTools (F12)
- Xem Console tab - không có errors?

### Test API Connection
1. Đăng nhập từ Frontend
2. Mở Network tab (DevTools)
3. Xem requests đi tới: `https://ecommerce-backend.onrender.com/api`?

---

## 🐛 Bước 8: Debug Nếu Có Lỗi

### Backend không start?
```bash
# Vào Render Dashboard → Service → Logs
# Tìm errors
# Solutions:
- Kiểm tra DB credentials
- Build locally: mvn clean package
- Check Java version = 17
```

### Frontend không load?
```bash
# Check Logs
# Solutions:
- Xác nhận REACT_APP_API_URL đúng
- npm run build locally
- Xóa node_modules + npm install
```

### Backend + Frontend không connect?
```bash
# DevTools → Network → Kiểm tra request header
# Solution:
- Enable CORS trong backend
- Check CORS headers trong response
- Verify baseURL đúng
```

---

## 🔄 Bước 9: Deploy Changes (Sau Này)

**Tự động deploy khi push code:**
```bash
git add .
git commit -m "New feature"
git push origin main
# Render tự động rebuild & deploy!
```

---

## 💡 Bước 10: Maintenance

### Daily Checks
- [ ] Logs có errors?
- [ ] API response time bình thường?
- [ ] Database connections ok?

### Weekly
- [ ] Update dependencies
- [ ] Check security updates
- [ ] Monitor Render usage

### Monthly
- [ ] Review logs
- [ ] Optimize performance
- [ ] Update documentation

---

## 🌐 URLs Sau Khi Deploy

```
✅ Backend API: https://ecommerce-backend.onrender.com/api
✅ Frontend: https://ecommerce-frontend.onrender.com
✅ Swagger/OpenAPI: https://ecommerce-backend.onrender.com/swagger-ui.html
```

---

## ⚡ Pro Tips

1. **Auto-deploy nếu push lên `main` branch**
   - Render dashboard tự monitor GitHub repo
   - Code được deploy tự động

2. **Caches & Performance**
   - Frontend: CDN caching tự động
   - Backend: Thêm Redis nếu cần

3. **Monitoring**
   - Render cung cấp logs, metrics
   - Cấu hình alerts nếu fail

4. **Upgrade Plan**
   - Free plan: 15-30 min auto-shutdown
   - Upgrade để luôn running

---

## 📞 Support

- **Render Docs**: https://render.com/docs
- **Spring Boot**: https://spring.io/guides/gs/deploying-spring-boot-app-to-cloud/
- **React Docs**: https://create-react-app.dev/deployment/

---

**🎉 Xong! Dự án của bạn giờ chạy trên Render!**

