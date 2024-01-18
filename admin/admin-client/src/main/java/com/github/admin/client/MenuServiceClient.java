package com.github.admin.client;

import com.github.admin.common.domain.Menu;
import com.github.framework.core.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeMap;

@FeignClient(name = "admin-server",url = "127.0.0.1:8082")
@RestController
public interface MenuServiceClient {

    @RequestMapping("/findMenuByUserId/{userId}")
    Result<TreeMap<Long, Menu>> findMenuByUserId(@PathVariable("userId") Long userId);
}
