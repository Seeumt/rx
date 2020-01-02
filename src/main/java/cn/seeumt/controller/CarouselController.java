package cn.seeumt.controller;


import cn.seeumt.dataobject.Carousel;
import cn.seeumt.service.CarouselService;
import cn.seeumt.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Seeumt
 * @since 2020-01-02
 */
@RestController
@RequestMapping("/carousels")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;
//    @CrossOrigin(origins = "http://localhost:8081")
//    @CrossOrigin(origins = "http://192.168.3.77:8081")
    @PostMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResultVO carousels() {
        List<Carousel> carousels = carouselService.getCarousels();
        return ResultVO.success(carousels);
    }

}