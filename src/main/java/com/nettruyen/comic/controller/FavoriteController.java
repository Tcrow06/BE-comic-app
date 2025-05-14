package com.nettruyen.comic.controller;

import com.cloudinary.Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nettruyen.comic.dto.request.other.FavoriteRequest;
import com.nettruyen.comic.dto.response.ApiResponse;
import com.nettruyen.comic.dto.response.other.FavoriteResponse;
import com.nettruyen.comic.dto.response.story.StoryResponse;
import com.nettruyen.comic.service.IFavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteController {

    IFavoriteService favoriteService;

    @PostMapping()
    ApiResponse<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest request) {
        return ApiResponse.<FavoriteResponse>builder()
                .code(200)
                .message("Save favorite completed")
                .result(favoriteService.addFavorite(request))
                .build();
    }
    @DeleteMapping
    public ApiResponse<FavoriteResponse> deleteFavorite(
            @RequestParam String username,
            @RequestParam String code
    ) {
        FavoriteRequest request = new FavoriteRequest(username, code);
        return ApiResponse.<FavoriteResponse>builder()
                .code(200)
                .message("Delete favorite completed")
                .result(favoriteService.deleteFavorite(request))
                .build();
    }
    @GetMapping("/check-save")
    public ApiResponse<FavoriteResponse> checkFavorite(@RequestParam String username,
                                                       @RequestParam String code) {

        FavoriteRequest request = new FavoriteRequest(username, code);
        return ApiResponse.<FavoriteResponse>builder()
                .code(200)
                .message("Check favorite completed")
                .result(favoriteService.checkFavorite(request))
                .build();
    }


    // Lấy tất cả story mà user đã lưu
    @GetMapping()
    ApiResponse<List<StoryResponse>> getAllFavoritesFromUser(@RequestParam("username") String username) {
        return ApiResponse.<List<StoryResponse>>builder()
                .code(200)
                .message("Get story completed")
                .result(favoriteService.getAllFavoritesByUsername(username))
                .build();
    }
}
