package vn.edu.iuh.fit.frontend.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.models.Product;
import vn.edu.iuh.fit.backend.models.ProductImage;
import vn.edu.iuh.fit.backend.models.ProductPrice;
import vn.edu.iuh.fit.backend.repositories.IProductImageRepository;
import vn.edu.iuh.fit.backend.repositories.IProductRepository;
import vn.edu.iuh.fit.backend.services.ProductImageService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/product-images")
@AllArgsConstructor
public class ProductImageController {
    private IProductRepository productRepository;
    private IProductImageRepository productImageRepository;
    private ProductImageService productImageService;
    private Environment environment;

    @GetMapping("/{id}")
    public String handleOpenListProductImages(@PathVariable("id") long productID, Model model){
        List<ProductImage> productImages = productImageRepository.findProductImagesByProductID(productID);
        Product product = productRepository.findById(productID).orElse(null);
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImages);
        return "admin/product-images/crudProductImages";
    }
    @GetMapping("/add/{id}")
    public ModelAndView handleOpenAddProductImage(@PathVariable("id") long productID){
        ModelAndView modelAndView = new ModelAndView();
        ProductImage productImage = new ProductImage();
        Product product = productRepository.findById(productID).orElse(null);
        productImage.setProduct(product);
        modelAndView.addObject("productImage", productImage);
        modelAndView.setViewName("admin/product-images/addProductImages");
        return modelAndView;
    }
    @PostMapping("/add")
    public String handleAddProductImage(@ModelAttribute("productImage") ProductImage productImage
    , @RequestParam("pathMulti")MultipartFile pathMulti, @RequestParam("alterMulti") MultipartFile alterMulti,
                                        Model model) throws IOException {
        Cloudinary cloudinary = new Cloudinary(environment.getProperty("CLOUDINARY_URL"));
        cloudinary.config.secure = true;


        File primaryFile = new File(System.getProperty("java.io.tmpdir") + "/" + pathMulti.getOriginalFilename());
        pathMulti.transferTo(primaryFile);

        Map uploadResultImgPrimary = cloudinary.uploader().upload(primaryFile, ObjectUtils.emptyMap());
        productImage.setPath((String) uploadResultImgPrimary.get("url"));

        if (!alterMulti.getOriginalFilename().isEmpty()){
            File alterFile = new File(System.getProperty("java.io.tmpdir") + "/"
                    + alterMulti.getOriginalFilename());
            alterMulti.transferTo(alterFile);
            Map uploadResultImgAlter = cloudinary.uploader().upload(alterFile, ObjectUtils.emptyMap());
            productImage.setPath((String)uploadResultImgAlter.get("url"));
        }
        try {
            productImageRepository.save(productImage);
        } catch (Exception e){
            model.addAttribute("addPostProductImage", "Thêm thất bại!");
            return "admin/product-images/addProductImages";
        }
        return "redirect:/admin/product-images/"+productImage.getProduct().getProduct_id();
    }
    @GetMapping("/edit/{id}")
    public ModelAndView handleOpenEditProductImage(@PathVariable("id") long imageID){
        ModelAndView modelAndView = new ModelAndView();
        ProductImage productImage = productImageRepository.findById(imageID).orElse(null);

        modelAndView.addObject("productImage", productImage);
        modelAndView.setViewName("admin/product-images/editProductImage");

        return modelAndView;
    }
    @PostMapping("/edit")
    public String handleEditProductImage(@ModelAttribute("productImage") ProductImage productImage
    , @RequestParam("pathMulti")MultipartFile pathMulti, @RequestParam("alterMulti") MultipartFile alterMulti, Model model) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        Cloudinary cloudinary = new Cloudinary(environment.getProperty("CLOUDINARY_URL"));


        File primaryFile = new File(System.getProperty("java.io.tmpdir") + "/" + pathMulti.getOriginalFilename());
        pathMulti.transferTo(primaryFile);

        Map uploadResultImgPrimary = cloudinary.uploader().upload(primaryFile, ObjectUtils.emptyMap());
        productImage.setPath((String) uploadResultImgPrimary.get("url"));

        if (!alterMulti.getOriginalFilename().isEmpty()){
            File alterFile = new File(System.getProperty("java.io.tmpdir") + "/"
                    + alterMulti.getOriginalFilename());
            alterMulti.transferTo(alterFile);
            Map uploadResultImgAlter = cloudinary.uploader().upload(alterFile, ObjectUtils.emptyMap());
            productImage.setPath((String)uploadResultImgAlter.get("url"));
        }
        try {
            productImageRepository.save(productImage);
        } catch (Exception e){
            model.addAttribute("editPostProductImage", "Cập nhật thất bại!");
            return "admin/product-images/editProductImage";
        }
        return "redirect:/admin/product-images/"+productImage.getProduct().getProduct_id();
    }
    @GetMapping("/delete/{id}")
    public String handleDeleteProductImage(@PathVariable("id") long imageID, Model model){
        ProductImage productImage = productImageRepository.findById(imageID).orElse(null);
        long productID = productImage.getProduct().getProduct_id();
        productImageRepository.deleteById(imageID);
        return "redirect:/admin/product-images/" + productID;
    }
}
