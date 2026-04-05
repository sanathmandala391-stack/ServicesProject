package com.krishna.saibaba.Controller;


import com.krishna.saibaba.Model.Service;
import com.krishna.saibaba.Service.ServiceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.File;

@RestController
public class ServiceController {

    @Autowired
    private ServiceServices serviceServices;

@PostMapping("/addAllServices")
public ResponseEntity<?> addAllServices(@RequestBody java.util.List<Service> services) {
    return ResponseEntity.ok(serviceServices.saveAll(services));
}
    

    @PostMapping("/add-Service")
    public ResponseEntity<String> AddService(

            @RequestParam("serviceName") String serviceName,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("imageUrl") MultipartFile imageUrl) {

        try {

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File folder = new File(uploadDir);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + imageUrl.getOriginalFilename();

            File file = new File(uploadDir + fileName);
            imageUrl.transferTo(file);

            Service service = new Service();
            service.setServiceName(serviceName);
            service.setCategory(category);
            service.setDescription(description);
            service.setPrice(price);
            service.setImageUrl(fileName);

            serviceServices.AddService(service);

            return ResponseEntity.ok("Service Added Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Service Failed To Add");
        }
    }
    @GetMapping("/getAllServices")
    public ResponseEntity<?> GetAllServices(){
        return serviceServices.GetAllServices();
    }

    @GetMapping("/getService/{id}")
    public ResponseEntity<?> GetServiceById(@PathVariable Integer id){
        return serviceServices.GetServiceById(id);
    }
}
