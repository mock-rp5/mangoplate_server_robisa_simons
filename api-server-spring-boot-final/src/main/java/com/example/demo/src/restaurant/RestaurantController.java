package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.user.model.GetUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantProvider provider;
    private final RestaurantService service;

    final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    public RestaurantController(RestaurantProvider provider, RestaurantService service) {
        this.provider = provider;
        this.service = service;
    }
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRestaurantRes>> getRestaurant(@RequestParam(value = "search-mode",required = false) String search_mode,
                                                              @RequestParam(value = "lat",required = false) Double latitude,
                                                              @RequestParam(value = "long",required = false) Double longitude,
                                                              @RequestParam(value = "user-id",required = false) Long userId,
                                                              @RequestParam(value = "food-category",required = false) List<Integer> foodCategories){
        logger.info("user lat -> ", latitude);
        logger.info("user long -> ", longitude);

        try{
            List<GetRestaurantRes> getRestaurantRes = provider.getRestaurant(latitude, longitude, foodCategories.toString().replace("[","(").replace("]",")"));
            return new BaseResponse<>(getRestaurantRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
