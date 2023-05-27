package com.gawpshardware.onlinestore.model.dao;

import com.gawpshardware.onlinestore.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
