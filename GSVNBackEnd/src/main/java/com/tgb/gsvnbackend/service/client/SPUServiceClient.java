package com.tgb.gsvnbackend.service.client;

import com.tgb.gsvnbackend.service.SPUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SPUServiceClient {
    private SPUService spuService;
    @Autowired
    public SPUServiceClient(SPUService spuService) {
        this.spuService = spuService;
    }
    public Boolean check(int id)
    {
        return spuService.exists(id);
    }

}
/*
    @FeignClient(value = "SPU-SERVICE", url = "http://localhost:8080")
    public interface SPUServiceClient {
        @GetMapping("/{id}/exists")
        public Boolean check (@PathVariable int id);
    }
*/
