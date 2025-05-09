package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.dto.BrandDTO;
import com.tgb.gsvnbackend.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable int id) {
        BrandDTO brandDTO = brandService.read(id);
        return ResponseEntity.ok(brandDTO);
    }

    @GetMapping
    public ResponseEntity<Page<BrandDTO>> getBrandsByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "brand_id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<BrandDTO> brandPage = brandService.readByPage(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(brandPage);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandDTO>> searchBrandsByTitle(@RequestParam String title) {
        List<BrandDTO> brandDTOList = brandService.searchByTitle(title);
        return ResponseEntity.ok(brandDTOList);
    }

    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.create(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable int id, @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.update(id, brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable int id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

