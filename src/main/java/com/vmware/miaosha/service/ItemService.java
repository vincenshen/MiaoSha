package com.vmware.miaosha.service;

import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    // 商品列表浏览
    List<ItemModel> listItems();

    // 商品详情浏览
    ItemModel getItemById(Integer id);

    // 商品库存扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    // 商品销量增加
    void increaseSales(Integer itemId, Integer amount);


}
