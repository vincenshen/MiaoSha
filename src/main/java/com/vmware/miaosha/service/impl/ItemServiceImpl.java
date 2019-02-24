package com.vmware.miaosha.service.impl;

import com.vmware.miaosha.dao.ItemDOMapper;
import com.vmware.miaosha.dao.ItemStockDOMapper;
import com.vmware.miaosha.dataobject.ItemDO;
import com.vmware.miaosha.dataobject.ItemStockDO;
import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.service.ItemService;
import com.vmware.miaosha.service.PromoService;
import com.vmware.miaosha.service.model.ItemModel;
import com.vmware.miaosha.service.model.PromoModel;
import com.vmware.miaosha.validator.ValidationResult;
import com.vmware.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        if(itemModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 转换itemModel -> dataObject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);

        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        // 转换itemModel -> dataObject
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);

        // 写入数据库
        itemStockDOMapper.insertSelective(itemStockDO);
        // 返回创建完成后的对象


        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItems() {
        List<ItemDO> itemDOList = itemDOMapper.listItems();
//        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
//            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
//            ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
//            return itemModel;
//        }).collect(Collectors.toList());
        List<ItemModel> itemModelList = new ArrayList<>();
        for (ItemDO itemDO: itemDOList) {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            itemModelList.add(this.convertModelFromDataObject(itemDO, itemStockDO));
        }

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);

        if(itemDO == null){
            return null;
        }

        // 操作获取库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        // 将dataObject -> Model
        ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);

        // 获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if(promoModel != null && promoModel.getStatus() != 3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        return affectedRow > 0;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId, amount);
    }
}
