# 📋 Render Deployment Checklist

## Pre-Deployment Check

- [ ] Code committed và push lên GitHub
- [ ] `system.properties` tạo trong `eCommersApp/` (Java 17)
- [ ] `Procfile` tạo trong `eCommersApp/`
- [ ] `.env.example` tạo trong `frontend/`
- [ ] `.gitignore` có `node_modules`, `target/`, `build/`
- [ ] `pom.xml` có `spring-boot-maven-plugin`
- [ ] `api.jsx` sử dụng environment variables
- [ ] CORS configuration có trong SecurityConfig

## Render Account Setup

- [ ] Tài khoản Render tạo (render.com)
- [ ] GitHub connected với Render
- [ ] SSH key added (nếu cần)

## Backend Deployment

- [ ] Service name: `ecommerce-backend`
- [ ] Build command: `cd eCommersApp && mvn clean package -DskipTests`
- [ ] Start command: `java -jar eCommersApp/target/Ecom-0.0.1-SNAPSHOT.jar`
- [ ] Environment variables cấu hình:
  - [ ] `DB_URL` set
  - [ ] `DB_USERNAME` set
  - [ ] `DB_PASSWORD` set
  - [ ] `JAVA_VERSION=17` set
- [ ] Health check passing
- [ ] Backend URL copied: `https://ecommerce-backend.onrender.com`

## Database

- [ ] Database service created (PostgreSQL hoặc MySQL)
- [ ] Connection string ready
- [ ] Credentials updated trong Backend env vars
- [ ] Database schema created (nếu cần)
- [ ] Test connection successful

## Frontend Deployment

- [ ] Service name: `ecommerce-frontend`
- [ ] Build command: `cd frontend && npm install && npm run build`
- [ ] Start command: `cd frontend && npx serve -s build -l 3000`
- [ ] Environment variables:
  - [ ] `REACT_APP_API_URL=https://ecommerce-backend.onrender.com`
- [ ] Frontend URL copied: `https://ecommerce-frontend.onrender.com`

## Post-Deployment Tests

### Backend Tests
- [ ] Ping backend: `curl https://ecommerce-backend.onrender.com`
- [ ] Check API endpoint: `curl https://ecommerce-backend.onrender.com/api/products`
- [ ] Check Swagger: `https://ecommerce-backend.onrender.com/swagger-ui.html`
- [ ] Database connection OK (check logs)
- [ ] JWT authentication working

### Frontend Tests
- [ ] Frontend loads: `https://ecommerce-frontend.onrender.com`
- [ ] No 404 errors
- [ ] No CORS errors (DevTools Console)
- [ ] API calls working (Network tab)

### Integration Tests
- [ ] Can login from frontend
- [ ] Can fetch products from API
- [ ] Can add to cart
- [ ] Can checkout (if applicable)
- [ ] Profile page loads correctly
- [ ] Admin panel accessible (if applicable)

## Performance & Monitoring

- [ ] Render logs accessible
- [ ] No memory/CPU warnings
- [ ] Response times acceptable (<2s)
- [ ] Set up monitoring alerts (optional)

## Security

- [ ] Database credentials secured (not in code)
- [ ] CORS properly configured
- [ ] JWT tokens working
- [ ] No sensitive data in logs
- [ ] Update dependencies for CVEs

## Documentation

- [ ] README updated with Render URLs
- [ ] Environment variables documented
- [ ] Deployment instructions complete
- [ ] Troubleshooting guide added

## Final Sign-Off

- [ ] All tests passing
- [ ] Team reviewed & approved
- [ ] Ready for production
- [ ] Backup created

---

**Date**: _______________
**Deployed By**: _______________
**Notes**: 

___________________________________
___________________________________
