package Ecom.Controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import Ecom.Model.Product;
import Ecom.ModelDTO.ProductDTO;
import Ecom.Service.ProductService;
import jakarta.validation.Valid;

import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/ecom/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // API thêm sản phẩm
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        Product newProduct = productService.addProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    // API sửa sản phẩm
    @PutMapping("/update/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId, @Valid @RequestBody ProductDTO updatedProduct) {
        Product updatedProductResult = productService.updateProduct(productId, updatedProduct);
        return new ResponseEntity<>(updatedProductResult, HttpStatus.OK);
    }

    // API lấy sản phẩm theo tên (Dạng PathVariable)
    @GetMapping("/product-By-name/{name}")
    public ResponseEntity<List<Product>> getProductByName(@PathVariable String name) {
        List<Product> products = productService.getProductByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // --- ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT (BỘ LỌC) ---
    // Hàm này nhận các tham số filter từ URL (sau dấu ?)
    // Ví dụ URL: /ecom/products/all?keyword=iphone&sort=desc&accessToken=XYZ...
    @GetMapping("/all")
    public ResponseEntity<List<Product>> search(
       @RequestParam(required = false) String keyword,
       @RequestParam(required = false, defaultValue = "asc") String sort,
       @RequestParam(required = false, defaultValue = "price") String sortBy
    ) {
        // Lưu ý: Access Token sẽ được "JwtTokenValidatorFilter" bắt và xử lý tự động.
        // Controller này chỉ cần lo việc lọc sản phẩm (keyword, sort...).
        List<Product> products = productService.getAllProduct(keyword, sort, sortBy);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // API lấy sản phẩm theo danh mục
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // API lấy chi tiết 1 sản phẩm
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getSingleProduct(@PathVariable Integer productId) {
        Product singleProsuct = productService.getSingleProduct(productId);
        return new ResponseEntity<>(singleProsuct, HttpStatus.OK);
	}

    // API xóa sản phẩm
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProduct(@PathVariable Integer productId) {
        productService.removeProduct(productId);
        return new ResponseEntity<>("Product removed successfully.", HttpStatus.OK);
    }

    @PostMapping("/insert")
public ResponseEntity<Map<String, Object>> insertProductByParams(
        Authentication authentication,

        @RequestParam(required = false) String name,
        @RequestParam(required = false) String imageUrl,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String price,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean isAvailable
) {
    Map<String, Object> res = new HashMap<>();

    // Nếu vào được đây => token hợp lệ
    String username = authentication.getName();

    // 1. Check missing data
    StringBuilder missing = new StringBuilder();
    if (!StringUtils.hasText(name)) missing.append("name, ");
    if (!StringUtils.hasText(imageUrl)) missing.append("imageUrl, ");
    if (!StringUtils.hasText(description)) missing.append("description, ");
    if (!StringUtils.hasText(price)) missing.append("price, ");
    if (!StringUtils.hasText(category)) missing.append("category, ");

    if (missing.length() > 0) {
        res.put("code", 1);
        res.put("message", "missing data: " + missing.substring(0, missing.length() - 2));
        return ResponseEntity.ok(res);
    }

    // 2. Validate price
    Double priceValue;
    try {
        priceValue = Double.valueOf(price);
    } catch (Exception ex) {
        res.put("code", 4);
        res.put("message", "invalid format: price must be a number");
        return ResponseEntity.ok(res);
    }

    // 3. Validate description
    String desc = description.trim();
    if (desc.length() < 10 || desc.length() > 50) {
        res.put("code", 4);
        res.put("message", "invalid format: description length must be 10..50");
        return ResponseEntity.ok(res);
    }

    // 4. Insert
    Product product = new Product();
    product.setName(name.trim());
    product.setImageUrl(imageUrl.trim());
    product.setDescription(desc);
    product.setPrice(priceValue);
    product.setCategory(category.trim());
    product.setAvailable(isAvailable != null ? isAvailable : true);

    Product saved = productService.addProduct(product);

    res.put("code", 0);
    res.put("data", saved);
    res.put("createdBy", username); // bonus
    return ResponseEntity.ok(res);
}


}