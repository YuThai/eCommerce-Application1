# Environment Variables for Render Backend Deployment

## 🔧 Copy & Paste These Into Render Dashboard

### ✅ For PostgreSQL (Render Database)

```
DB_URL=jdbc:postgresql://dpg-xxxxx.singapore.render.com:5432/ecommerce_db
DB_USERNAME=ecommerce_db_user
DB_PASSWORD=YOUR_PASSWORD_HERE
PORT=8081
SPRING_PROFILES_ACTIVE=prod
```

**⚠️ Replace:**
- `dpg-xxxxx.singapore.render.com` → Your database hostname
- `YOUR_PASSWORD_HERE` → Your database password

---

### ✅ For MySQL (Planetscale)

```
DB_URL=jdbc:mysql://aws.connect.psdb.cloud/ecommerce-db?sslMode=VERIFY_IDENTITY&useSSL=true&serverTimezone=UTC
DB_USERNAME=your_planetscale_username
DB_PASSWORD=pscale_pw_xxxxxxxxxxxxxxxx
PORT=8081
SPRING_PROFILES_ACTIVE=prod
```

**⚠️ Replace:**
- `your_planetscale_username` → Username from Planetscale
- `pscale_pw_xxxxxxxxxxxxxxxx` → Password from Planetscale

---

### ✅ For Local MySQL

```
DB_URL=jdbc:mysql://your-mysql-host.com:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=your_mysql_user
DB_PASSWORD=your_mysql_password
PORT=8081
SPRING_PROFILES_ACTIVE=prod
```

---

## 📋 How to Add Environment Variables on Render

### Step 1: Scroll Down on Service Creation Form
- Tìm section **"Environment Variables"**

### Step 2: Click "Add Environment Variable"

### Step 3: Add Each Variable One by One

**Variable 1:**
```
Key: DB_URL
Value: jdbc:postgresql://dpg-xxx...
```

**Variable 2:**
```
Key: DB_USERNAME
Value: ecommerce_db_user
```

**Variable 3:**
```
Key: DB_PASSWORD
Value: (paste password here)
```

**Variable 4:**
```
Key: PORT
Value: 8081
```

**Variable 5:**
```
Key: SPRING_PROFILES_ACTIVE
Value: prod
```

### Step 4: Click "Create Web Service"

---

## 🔑 Where to Get Database Credentials?

### If Using Render PostgreSQL:

1. Go to Render Dashboard
2. Click on your database service (e.g., `ecommerce-db`)
3. Tab: **"Info"**
4. Section: **"Connections"**
5. Find:
   - **Internal Database URL** (use this for DB_URL)
   - **Username**
   - **Password** (click "Show" to reveal)

### If Using Planetscale MySQL:

1. Go to Planetscale Dashboard
2. Select your database
3. Tab: **"Connect"**
4. Click: **"New password"**
5. Select: **"Java"** from dropdown
6. Copy credentials shown

---

## ✅ Verify Environment Variables Are Set

After creating service:

1. Go to Service Settings
2. Tab: **"Environment"**
3. You should see all 5 variables listed
4. Click "eye icon" to reveal values if needed

---

## 🧪 Test Database Connection

After deployment, check logs for:

```
✅ SUCCESS:
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: select version()
Database connected successfully!

❌ FAIL:
Could not connect to database
Communications link failure
```

If connection fails:
- Check DB_URL format is correct
- Verify username and password
- Ensure database service is running
- Check region matches (both in Singapore)

---

## 💡 Pro Tips

1. **Use Internal Database URL for Render PostgreSQL**
   - Faster connection
   - No external traffic charges
   - Format: `postgres://` or `jdbc:postgresql://`

2. **Keep Passwords Secret**
   - Never commit to Git
   - Use Render's secret management

3. **Test Locally First**
   ```bash
   # Set env vars locally
   export DB_URL="jdbc:postgresql://..."
   export DB_USERNAME="user"
   export DB_PASSWORD="pass"
   
   # Run Spring Boot
   mvn spring-boot:run
   ```

---

## 🆘 Common Issues

### Issue: "No suitable driver found"
**Fix:** Ensure `postgresql` dependency in `pom.xml`

### Issue: "Access denied for user"
**Fix:** Double-check username and password

### Issue: "Unknown database"
**Fix:** Verify database name in DB_URL matches actual DB name

---

**Done! Ready to deploy!** 🚀

