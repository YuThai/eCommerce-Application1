package Ecom.SecurityConfig;

import java.io.IOException;
import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Ưu tiên lấy Token từ URL (Query String: ?accessToken=...)
        String jwt = request.getParameter("accessToken");

        // 2. Nếu URL không có, thì mới lấy từ Header (Authorization: Bearer ...)
        if (jwt == null) {
            String authHeader = request.getHeader(SecurityConstants.JWT_HEADER);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7); // Cắt bỏ chữ "Bearer "
            }
        }

        // 3. Tiến hành kiểm tra Token
        if (jwt != null) {
            try {
                // Lưu ý: Token lấy từ URL thường không có chữ "Bearer " nên không cần cắt chuỗi nữa
                // Code cũ của bạn cắt chuỗi ở đây, nhưng tôi đã xử lý logic cắt ở bước 2 rồi.
                
                SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("authorities");

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Có thể log lỗi ra đây để debug nếu cần
                throw new BadCredentialsException("Invalid Token received..");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/ecom/signIn");
    }
}