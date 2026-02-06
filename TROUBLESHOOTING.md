# 🔧 Render Deployment - Troubleshooting Guide

## 🔴 Common Issues & Solutions

---

### Issue 1: Backend Service Won't Start

**Error in Logs:**
```
ERROR: Failed to start Java application
```

**Solutions:**
1. **Check Java Version**
   ```bash
   # Verify system.properties exists in eCommersApp/
   cat eCommersApp/system.properties
   # Should have: java.runtime.version=17
   ```

2. **Build Locally First**
   ```bash
   cd eCommersApp
   mvn clean package -DskipTests
   java -jar target/Ecom-0.0.1-SNAPSHOT.jar
   ```

3. **Check Build Command Output**
   - View Render logs: Dashboard → Backend Service → Logs
   - Look for Maven build errors
   - Common: `[ERROR] Failed to execute goal`

4. **Try Different Start Command**
   ```bash
   # Instead of:
   java -jar eCommersApp/target/Ecom-0.0.1-SNAPSHOT.jar
   
   # Try:
   java -jar target/Ecom-0.0.1-SNAPSHOT.jar
   ```

---

### Issue 2: Database Connection Failed

**Error in Logs:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
java.sql.SQLException: No appropriate driver found for jdbc:mysql://
```

**Solutions:**

1. **Verify Database Credentials**
   ```bash
   # Check Environment Variables in Render Dashboard
   DB_URL=jdbc:mysql://HOST:3306/DATABASE?useSSL=false&serverTimezone=UTC
   DB_USERNAME=correct_user
   DB_PASSWORD=correct_password
   ```

2. **Test MySQL Connection Locally**
   ```bash
   mysql -h YOUR_HOST -u USERNAME -p
   # Enter password and verify connection works
   ```

3. **Update Connection String**
   ```
   ❌ WRONG:
   DB_URL=jdbc:mysql://localhost:3306/ecom
   
   ✅ CORRECT:
   DB_URL=jdbc:mysql://external-mysql-host.com:3306/ecom?useSSL=false&serverTimezone=UTC
   ```

4. **Check MySQL Driver**
   - `pom.xml` should have:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
   </dependency>
   ```

5. **Allow External Connections**
   - If using Planetscale: Create password with "Branch" set to main
   - If using AWS RDS: Check security group for inbound port 3306

---

### Issue 3: Frontend 502 Bad Gateway

**Error:**
```
502 Bad Gateway / 503 Service Unavailable
```

**Solutions:**

1. **Check Backend Status**
   - Is Backend service running?
   - Check Backend logs for errors
   - If Backend crashed: restart service

2. **Verify Frontend Build**
   ```bash
   cd frontend
   npm install
   npm run build
   # Check build output for errors
   ```

3. **Check Start Command**
   ```bash
   # Render uses this:
   cd frontend && npx serve -s build -l 3000
   
   # Or try:
   npm start
   ```

4. **Increase Memory**
   - Free plan might not have enough RAM
   - Option: Upgrade to Starter plan

---

### Issue 4: CORS Errors

**Error in Browser Console:**
```
Access to XMLHttpRequest at 'https://ecommerce-backend.onrender.com/api/'
from origin 'https://ecommerce-frontend.onrender.com'
has been blocked by CORS policy
```

**Solutions:**

1. **Add CORS Configuration to Backend**

   Tạo file `SecurityConfig.java` trong `eCommersApp/src/main/java/Ecom/SecurityConfig/`:

   ```java
   package Ecom.SecurityConfig;
   
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.config.annotation.CorsRegistry;
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
   
   @Configuration
   public class CorsConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/api/**")
                   .allowedOrigins(
                       "http://localhost:3000",
                       "https://ecommerce-frontend.onrender.com",
                       "https://*.onrender.com"
                   )
                   .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                   .allowedHeaders("Authorization", "Content-Type", "*")
                   .allowCredentials(true)
                   .maxAge(3600);
       }
   }
   ```

2. **Verify API URL in Frontend**
   ```
   # In Render Dashboard → Frontend Service → Environment
   REACT_APP_API_URL=https://ecommerce-backend.onrender.com
   ```

3. **Check Backend Response Headers**
   ```bash
   curl -i https://ecommerce-backend.onrender.com/api/products
   
   # Should see:
   # Access-Control-Allow-Origin: https://ecommerce-frontend.onrender.com
   ```

4. **Update API calls in Frontend**
   ```javascript
   // api.jsx
   import axios from 'axios';
   
   const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';
   
   const api = axios.create({
     baseURL: API_BASE_URL,
     withCredentials: true,
     headers: {
       'Content-Type': 'application/json'
     }
   });
   ```

---

### Issue 5: Frontend Gets Blank Page

**Symptoms:**
- Page loads but shows nothing
- No errors in console

**Solutions:**

1. **Check React Build**
   ```bash
   cd frontend
   npm run build
   # Check if build/ folder created successfully
   ```

2. **Verify index.html**
   ```html
   <!-- public/index.html should have: -->
   <div id="root"></div>
   ```

3. **Check package.json**
   ```json
   "homepage": "./",
   "scripts": {
     "start": "react-scripts start",
     "build": "react-scripts build"
   }
   ```

4. **Restart Frontend Service**
   - Render Dashboard → Frontend Service → Manual Deployment

---

### Issue 6: Slow Performance / Timeouts

**Symptoms:**
- Requests timeout (>30s)
- API responds slowly

**Solutions:**

1. **Check Database Performance**
   ```
   - View slow queries in database logs
   - Add indexes to frequently queried columns
   - Check connection pool settings
   ```

2. **Optimize Backend**
   ```java
   // Add caching
   @Cacheable(value = "products")
   public List<Product> getAllProducts() { ... }
   
   // Implement pagination
   Page<Product> products = repo.findAll(pageable);
   ```

3. **Upgrade Render Plan**
   ```
   Free Plan Limits:
   - Service spins down after 15 min inactivity
   - Limited CPU/RAM
   - 100 GB bandwidth/month
   
   Starter Plan:
   - Always running
   - More CPU/RAM
   - Better performance
   ```

4. **Add CDN Caching**
   - Frontend: Already have browser cache
   - Backend: Add Redis for database queries

---

### Issue 7: JWT Token Errors

**Error:**
```
401 Unauthorized
Invalid or expired token
```

**Solutions:**

1. **Check Token Storage**
   ```javascript
   // localStorage.getItem('jwtToken') should exist
   console.log(localStorage.getItem('jwtToken'));
   ```

2. **Check Token Format**
   ```javascript
   // Should be: Bearer <token>
   Authorization: Bearer eyJhbGc...
   ```

3. **Verify Token Expiration**
   ```bash
   # Decode JWT at jwt.io
   # Check exp claim
   ```

4. **Ensure CORS allows Authorization Header**
   ```java
   registry.addMapping("/api/**")
       .allowedHeaders("Authorization", "Content-Type", "*")
       .allowCredentials(true);
   ```

---

### Issue 8: npm Dependencies Not Installing

**Error:**
```
npm ERR! code ERESOLVE
npm ERR! ERESOLVE unable to resolve dependency tree
```

**Solutions:**

1. **Use Older npm Version**
   ```bash
   npm install --legacy-peer-deps
   ```

2. **Check package.json**
   ```json
   // Remove conflicting dependencies
   // Update to compatible versions
   ```

3. **Clear npm Cache**
   ```bash
   npm cache clean --force
   npm install
   ```

4. **Update package-lock.json**
   ```bash
   rm package-lock.json
   npm install
   ```

---

### Issue 9: Backend Returns 404 for All Routes

**Error:**
```
GET /api/products → 404 Not Found
```

**Solutions:**

1. **Check Application Context Path**
   ```properties
   # application.properties
   server.servlet.context-path=/api
   # This means routes should be: /api/api/products (wrong!)
   # Remove if not needed
   ```

2. **Verify Controllers Exist**
   ```bash
   find eCommersApp/src -name "*Controller.java" -type f
   ```

3. **Check @RequestMapping**
   ```java
   @RestController
   @RequestMapping("/api/products")  // ✓ Correct
   public class ProductController { ... }
   ```

4. **Check Servlet Registration**
   - If using custom servlet: verify path mapping

---

### Issue 10: Database Migrations Failed

**Error:**
```
Liquibase or Flyway migration failed
SQL syntax error
```

**Solutions:**

1. **Check DDL Settings**
   ```properties
   # application.properties
   spring.jpa.hibernate.ddl-auto=update  # ✓ Auto create tables
   # OR specify create, create-drop, validate
   ```

2. **Manual Migration**
   ```bash
   # Connect to database
   mysql -h HOST -u USER -p DATABASE
   
   # Run SQL manually
   CREATE TABLE IF NOT EXISTS products (...);
   ```

3. **Check Schema Name**
   ```
   Database name in URL: jdbc:mysql://host:3306/ecommerce_db
   Should match actual database name
   ```

---

## 🆘 Still Stuck?

### Debug Steps

1. **Check Render Logs** (Always First!)
   ```
   Dashboard → Service → Logs
   Look for errors/stack traces
   ```

2. **Test Build Locally**
   ```bash
   # Backend
   cd eCommersApp && mvn clean package
   java -jar target/Ecom-0.0.1-SNAPSHOT.jar
   
   # Frontend
   cd frontend && npm start
   ```

3. **Verify Credentials**
   ```bash
   # Database connection
   mysql -h RENDER_DB_HOST -u USER -pPASSWORD
   
   # Test Backend endpoint
   curl -v https://ecommerce-backend.onrender.com/api/health
   ```

4. **Check Network Connectivity**
   ```bash
   # From Render Backend to Database
   # Only accessible if in same region/network
   ```

### Getting Help

- **Render Support**: https://render.com/support
- **Spring Boot Issues**: https://stackoverflow.com/questions/tagged/spring-boot
- **React Issues**: https://stackoverflow.com/questions/tagged/reactjs
- **Database Issues**: Check database provider's documentation

---

## 📝 Enable Debug Mode

### Backend Debug Logging
```properties
# application.properties
logging.level.root=INFO
logging.level.Ecom=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate=DEBUG
```

### Frontend Debug Logging
```javascript
// api.jsx
api.interceptors.response.use(
  response => {
    console.log('API Response:', response);
    return response;
  },
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);
```

---

**Remember: Check logs first! 99% of issues are revealed in the logs.** 🔍

