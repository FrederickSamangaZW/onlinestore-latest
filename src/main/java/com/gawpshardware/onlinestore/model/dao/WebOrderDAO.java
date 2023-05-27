package com.gawpshardware.onlinestore.model.dao;

import com.gawpshardware.onlinestore.model.LocalUser;
import com.gawpshardware.onlinestore.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
