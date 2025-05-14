package com.nettruyen.comic.service;

import com.nettruyen.comic.dto.request.other.FavoriteRequest;
import com.nettruyen.comic.dto.response.other.FavoriteResponse;
import com.nettruyen.comic.dto.response.story.StoryResponse;

import java.util.List;

public interface IFavoriteService {
    FavoriteResponse addFavorite(FavoriteRequest request);
    FavoriteResponse deleteFavorite(FavoriteRequest request);
    FavoriteResponse checkFavorite(FavoriteRequest request);
    List<StoryResponse> getAllFavoritesByUsername(String username);
}
