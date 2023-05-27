package com.gawpshardware.onlinestore.service;

import com.gawpshardware.onlinestore.model.LocalUser;
import com.gawpshardware.onlinestore.model.WebOrder;
import com.gawpshardware.onlinestore.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO) {
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);

    }
}
