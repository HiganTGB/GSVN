package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.dto.SKUDTO;
import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import com.tgb.gsvnbackend.model.dto.SPUSKUDTO;
import com.tgb.gsvnbackend.service.SKUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/skus")
public class SKUController {
    private final SKUService skuService;
    @Autowired
    public SKUController(SKUService skuService) {
        this.skuService = skuService;
    }
    @PostMapping
    public ResponseEntity<SKUDTO> createSKU(@RequestBody SKUDTO skuDTO) {
        SKUDTO createdSKU = skuService.create(skuDTO);
        return new ResponseEntity<>(createdSKU, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<SKUDTO> updateSKU(@PathVariable int id, @RequestBody SKUDTO skuDTO) {
        SKUDTO updatedSKU = skuService.update(id, skuDTO);
        return new ResponseEntity<>(updatedSKU, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<Void> deleteSKU(@PathVariable int id) {
        skuService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<SKUDTO> getSKU(@PathVariable int id) {
        SKUDTO sku = skuService.read(id);
        return new ResponseEntity<>(sku, HttpStatus.OK);
    }

    @GetMapping("/spu/{spuId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<List<SPUSKUDTO>> getSKUsBySpuId(@PathVariable int spuId) {
        List<SPUSKUDTO> spuskuDTOs = skuService.getListBySpuID(spuId);
        return new ResponseEntity<>(spuskuDTOs, HttpStatus.OK);
    }
}
