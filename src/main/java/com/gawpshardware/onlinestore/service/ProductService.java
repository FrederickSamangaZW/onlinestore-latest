package com.gawpshardware.onlinestore.service;

import com.gawpshardware.onlinestore.model.Product;
import com.gawpshardware.onlinestore.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getProduct(){
        return productDAO.findAll();

    }
}
