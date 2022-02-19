package com.mitocode.asyncdemo.controller;

import com.mitocode.asyncdemo.model.Product;
import com.mitocode.asyncdemo.service.ProductAsyncService;
import com.mitocode.asyncdemo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductAsyncService productAsyncService;
    private final ProductService productService;

    public ProductController(ProductService service, ProductAsyncService productAsyncService) {
        this.productService = service;
        this.productAsyncService = productAsyncService;
    }

    @GetMapping
    public List<Product> getAllProducts() throws Exception{
        long start = System.currentTimeMillis();
        List<Product> list1 = productService.getProducts1();
        List<Product> list2 = productService.getProducts2();
        List<Product> list3 = productService.getProducts3();

        List<Product> list4 = Stream.of(list1, list2, list3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        long end = System.currentTimeMillis();
        log.info("Tiempo de ejecución: {}", end - start);
        return list4;
    }

    @GetMapping("/async")
    public List<Product> getAllProductsAsync() throws Exception{
        CompletableFuture<List<Product>> c1 = productAsyncService.getProducts1();
        CompletableFuture<List<Product>> c2 = productAsyncService.getProducts2();
        CompletableFuture<List<Product>> c3 = productAsyncService.getProducts3();

        CompletableFuture.allOf(c1, c2, c3).join();

        List<Product> list4 = Stream.of(c1.get(), c2.get(), c3.get())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return list4;
    }
}
