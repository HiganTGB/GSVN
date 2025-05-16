package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.dto.SKUAttributeDTO;
import com.tgb.gsvnbackend.service.SKUAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku-attributes")
public class SKUAttributeController {

    private final SKUAttributeService skuAttributeService;

    @Autowired
    public SKUAttributeController(SKUAttributeService skuAttributeService) {
        this.skuAttributeService = skuAttributeService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<SKUAttributeDTO> getSKUAttributeById(@PathVariable int id) {
        SKUAttributeDTO skuAttributeDTO = skuAttributeService.read(id);
        if (skuAttributeDTO != null) {
            return new ResponseEntity<>(skuAttributeDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public ResponseEntity<Page<SKUAttributeDTO>> getSKUAttributesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<SKUAttributeDTO> skuAttributeDTOPage = skuAttributeService.readByPage(page, size, sortBy, sortDirection);
        return new ResponseEntity<>(skuAttributeDTOPage, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<List<SKUAttributeDTO>> searchSKUAttributesByAttrs(
            @RequestParam String key,
            @RequestParam String value) {
        List<SKUAttributeDTO> skuAttributeDTOList = skuAttributeService.searchByAttrs(key, value);
        return new ResponseEntity<>(skuAttributeDTOList, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<SKUAttributeDTO> createSKUAttribute(@RequestBody SKUAttributeDTO skuAttributeDTO) {
        SKUAttributeDTO createdSKUAttribute = skuAttributeService.create(skuAttributeDTO);
        return new ResponseEntity<>(createdSKUAttribute, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SKUAttributeDTO> updateSKUAttribute(@PathVariable int id, @RequestBody SKUAttributeDTO skuAttributeDTO) {
        SKUAttributeDTO updatedSKUAttribute = skuAttributeService.update(id, skuAttributeDTO);
        if (updatedSKUAttribute != null) {
            return new ResponseEntity<>(updatedSKUAttribute, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSKUAttribute(@PathVariable int id) {
        skuAttributeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}