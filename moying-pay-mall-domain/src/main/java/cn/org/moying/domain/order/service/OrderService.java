package cn.org.moying.domain.order.service;

import cn.org.moying.domain.order.adapter.port.IProductPort;
import cn.org.moying.domain.order.adapter.repository.IOrderRepository;
import cn.org.moying.domain.order.model.aggregate.CreateOrderAggregate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService extends AbstractOrderService{

    public OrderService(IOrderRepository repository, IProductPort port) {
        super(repository, port);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        repository.doSaveOrder(orderAggregate);
    }

}
