package vn.edu.iuh.fit.frontend.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.dto.DTOProductAdd;
import vn.edu.iuh.fit.backend.dto.ProductWithNewestPrice;
import vn.edu.iuh.fit.backend.enums.ProductStatus;
import vn.edu.iuh.fit.backend.models.Product;
import vn.edu.iuh.fit.backend.models.ProductImage;
import vn.edu.iuh.fit.backend.models.ProductPrice;
import vn.edu.iuh.fit.backend.repositories.IProductImageRepository;
import vn.edu.iuh.fit.backend.repositories.IProductPriceRepository;
import vn.edu.iuh.fit.backend.repositories.IProductRepository;
import vn.edu.iuh.fit.backend.services.ProductService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
@AllArgsConstructor
public class ProductController {
    private IProductRepository productRepository;
    private ProductService productService;
    private IProductPriceRepository productPriceRepository;
    private Environment environment;
    private IProductImageRepository productImageRepository;


    @GetMapping("")
    public String getProductList(Model model){
        List<Product> products = productRepository.findAllByStatusNot(ProductStatus.TERMINATED);
        List<ProductWithNewestPrice> productDTO = new ArrayList<>();
        for (Product p : products){
            ProductPrice productPrice = productPriceRepository
                    .findProductPriceNewestByProductID(p.getProduct_id()).orElse(null);
            if (productPrice != null){
                productDTO.add(new ProductWithNewestPrice(p, productPrice));
            }
        }
        model.addAttribute("productWithNewestPrices", productDTO);
        return "admin/products/crudProduct";
    }
    @GetMapping("/add")
    public ModelAndView handleOpenAddProduct(){
        ModelAndView modelAndView = new ModelAndView();
        Product product = new Product();
        ProductPrice productPrice = new ProductPrice();
        ProductImage productImage = new ProductImage();

        DTOProductAdd productAdd = new DTOProductAdd(product, productPrice, productImage);

        modelAndView.addObject("productAdd", productAdd);
        modelAndView.addObject("productStatus", ProductStatus.values());

        modelAndView.setViewName("admin/products/addProduct");
        return modelAndView;
    }
    @PostMapping("/add")
    public String handleAddNewProduct(@ModelAttribute("productAdd") DTOProductAdd productAdd
            , @RequestParam("pathMulti")MultipartFile pathMulti, @RequestParam("alterMulti") MultipartFile alterMulti)  throws IOException {
        Product product = productAdd.getProduct();
        ProductPrice productPrice = productAdd.getProductPrice();
        ProductImage productImage = productAdd.getProductImage();

        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);

        productPrice.setProduct(product);
        productPriceRepository.save(productPrice);

        Cloudinary cloudinary = new Cloudinary(environment.getProperty("CLOUDINARY_URL"));
        cloudinary.config.secure = true;
        productImage = new ProductImage();

        File primaryFile = new File(pathMulti.getOriginalFilename());
        primaryFile.createNewFile();
//        FileOutputStream fos = new FileOutputStream(primaryFile);
//        fos.write(pathMulti.getBytes());
//        fos.close();
        Map uploadResultImgPrimary = cloudinary.uploader().upload(primaryFile, ObjectUtils.emptyMap());

        if (!alterMulti.getOriginalFilename().isEmpty()){
            File alterFile = new File(alterMulti.getOriginalFilename());
            alterFile.createNewFile();
//            FileOutputStream fosal = new FileOutputStream(alterFile);
//            fosal.write(alterMulti.getBytes());
//            fosal.close();
            Map uploadResultImgAlter = cloudinary.uploader().upload(alterFile, ObjectUtils.emptyMap());
            productImage.setPath((String)uploadResultImgAlter.get("url"));
        }

        productImage.setProduct(product);
        productImage.setPath((String) uploadResultImgPrimary.get("url"));
        productImageRepository.save(productImage);

        return "redirect:/admin/products";
    }
    @GetMapping("/delete/{id}")
    public String handleHiddenProduct(@PathVariable("id") long productID){
        productService.hiddenProduct(productID);
        return "redirect:/admin/products";
    }
    @GetMapping("/edit/{id}")
    public ModelAndView handleOpenProductEditPage(@PathVariable("id") long productID){
        ModelAndView modelAndView = new ModelAndView();
        Product product = productRepository.findById(productID).orElse(null);
        if (product == null){
            return new ModelAndView("redirect:/admin/products");
        }
        modelAndView.addObject("product", product);
        modelAndView.addObject("productStatuss", ProductStatus.values());
        modelAndView.setViewName("admin/products/editProduct");
        return modelAndView;
    }
    @PostMapping("/edit")
    public String handleEditProduct(@ModelAttribute("product") Product product, Model model){
        try {
            productRepository.save(product);
        } catch (Exception e){
            model.addAttribute("errUpdProduct", "Cập nhật thất bại!");
            return "admin/products/editProduct";
        }
        return "redirect:/admin/products";
    }

}
