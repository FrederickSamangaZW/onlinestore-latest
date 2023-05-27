package com.gawpshardware.onlinestore.api.controller.auth.product;

import com.gawpshardware.onlinestore.model.Product;
import com.gawpshardware.onlinestore.model.dao.ProductDAO;
import com.gawpshardware.onlinestore.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @GetMapping
    public List<Product> getProducts(){
        return productService.getProduct();

    }
}
