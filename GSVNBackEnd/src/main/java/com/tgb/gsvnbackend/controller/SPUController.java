package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.domain.SPUDomain;
import com.tgb.gsvnbackend.model.dto.SPUDTO;
import com.tgb.gsvnbackend.model.entity.ProductDocument;
import com.tgb.gsvnbackend.service.SPUService;
import com.tgb.gsvnbackend.service.search.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spus")
public class SPUController {
    private final ProductService productService;
    private final SPUService spuService;

    @Autowired
    public SPUController(ProductService productService, SPUService spuService) {
        this.productService = productService;
        this.spuService = spuService;
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<SPUDTO> createSPU(@RequestBody @Valid SPUDTO spuDTO) {
        SPUDTO createdSPU = spuService.create(spuDTO);
        return new ResponseEntity<>(createdSPU, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<SPUDTO> updateSPU(@PathVariable int id, @RequestBody @Valid SPUDTO spuDTO) {
        SPUDTO updatedSPU = spuService.update(id, spuDTO);
        return new ResponseEntity<>(updatedSPU, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_per::spu::management','SCOPE_per::spu::other')or hasAnyAuthority('ROLE_admin')")
    public ResponseEntity<Void> deleteSPU(@PathVariable int id) {
        spuService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/exists")
    public Boolean check (@PathVariable int id) {
        return spuService.exists(id);
    }

    @GetMapping("/{id}/domain")
    public SPUDomain readDomain (@PathVariable int id) {
        return spuService.getDomain(id);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductDocument>> searchProducts(
            @RequestParam(required = false) String title,
            @RequestParam(value = "brandIds", required = false) List<Integer> brandIds,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "fandomIds", required = false) List<Integer> fandomIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy) {
        List<ProductDocument> products = productService.search(title, brandIds, categoryIds, fandomIds, minPrice, maxPrice, page, size, sortBy);
        return ResponseEntity.ok(products);
    }
}
