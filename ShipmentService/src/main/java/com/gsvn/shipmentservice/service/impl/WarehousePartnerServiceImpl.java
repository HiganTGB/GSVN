package com.gsvn.shipmentservice.service.impl;

import com.gsvn.shipmentservice.common.utils.TokenEncryptionUtil;
import com.gsvn.shipmentservice.converter.WarehousePartnerConverter;
import com.gsvn.shipmentservice.exc.AppException;
import com.gsvn.shipmentservice.exc.ErrorCode;
import com.gsvn.shipmentservice.mapper.WarehousePartnerMapper;
import com.gsvn.shipmentservice.model.dto.request.WarehousePartnerRequest;
import com.gsvn.shipmentservice.model.dto.response.WarehousePartnerResponse;
import com.gsvn.shipmentservice.model.entity.WarehousePartner;
import com.gsvn.shipmentservice.service.WarehousePartnerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class WarehousePartnerServiceImpl implements WarehousePartnerService {
    private final WarehousePartnerConverter partnerConverter;
    private TokenEncryptionUtil encryptionUtil;
    private final WarehousePartnerMapper partnerMapper;
    @Transactional
    public WarehousePartnerResponse savePartnerToken(WarehousePartnerRequest request, String warehouseCode) {
        String encryptedToken = encryptionUtil.encrypt(request.getPartnerToken());

        WarehousePartner partner = partnerConverter.toEntity(request, warehouseCode);
        partner.setPartnerToken(encryptedToken);

        partnerMapper.save(partner);

        return partnerConverter.toResponse(partner);
    }
    public String getDecryptedToken(String warehouseCode, String partnerName) {
        WarehousePartner partner = partnerMapper.selectFirstByWarehouseAndPartner(warehouseCode, partnerName)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        return encryptionUtil.decrypt(partner.getPartnerToken());
    }
    public List<WarehousePartnerResponse> getPartnersByWarehouseId(String warehouseCode) {
        List<WarehousePartner> entities = partnerMapper.selectByWarehouseCode(warehouseCode);
        return partnerConverter.toResponseList(entities);
    }
    @Transactional
    public void deletePartner(String warehouseCode, String partnerName) {
        int deletedRows = partnerMapper.deleteByWarehouseAndPartner(warehouseCode, partnerName);
        if (deletedRows == 0) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
    }
}
