package cn.org.moying.infrastructure.adapter.port;

import cn.org.moying.domain.order.adapter.port.IProductPort;
import cn.org.moying.domain.order.model.entity.ProductEntity;
import cn.org.moying.infrastructure.gateway.ProductRPC;
import cn.org.moying.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductPort implements IProductPort {

    private final ProductRPC productRPC;

    public ProductPort(ProductRPC productRPC) {
        this.productRPC = productRPC;
    }

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }

}
