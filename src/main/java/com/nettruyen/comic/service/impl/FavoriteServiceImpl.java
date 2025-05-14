package com.nettruyen.comic.service.impl;

import com.nettruyen.comic.dto.request.other.FavoriteRequest;
import com.nettruyen.comic.dto.response.chapter.ChapterResponse;
import com.nettruyen.comic.dto.response.other.FavoriteResponse;
import com.nettruyen.comic.dto.response.story.StoryResponse;
import com.nettruyen.comic.entity.FavoriteEntity;
import com.nettruyen.comic.entity.GenerateEntity;
import com.nettruyen.comic.entity.StoryEntity;
import com.nettruyen.comic.entity.UserEntity;
import com.nettruyen.comic.exception.AppException;
import com.nettruyen.comic.exception.ErrorCode;
import com.nettruyen.comic.mapper.StoryMapper;
import com.nettruyen.comic.repository.internal.IFavoriteRepository;
import com.nettruyen.comic.repository.internal.IStoryRepository;
import com.nettruyen.comic.repository.internal.IUserRepository;
import com.nettruyen.comic.service.IFavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteServiceImpl implements IFavoriteService {

    IStoryRepository storyRepository;
    IUserRepository userRepository;
    IFavoriteRepository favoriteRepository;

    StoryMapper storyMapper;

    @Transactional
    @Override
    public FavoriteResponse addFavorite(FavoriteRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername());
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        StoryEntity story = storyRepository.findByCode(request.getStoryCode());
        if (story == null) throw new AppException(ErrorCode.STORY_NOT_EXITS);

        boolean checkExisted = favoriteRepository.existsByUserAndStory(user, story);
        if (checkExisted) throw new AppException(ErrorCode.STORY_SAVED);

        FavoriteEntity favorite = FavoriteEntity.builder()
                .user(user)
                .story(story)
                .build();
        favoriteRepository.save(favorite);

        return FavoriteResponse.builder()
                .username(favorite.getUser().getUsername())
                .storyCode(favorite.getStory().getCode())
                .build();
    }

    @Transactional
    @Override
    public FavoriteResponse deleteFavorite(FavoriteRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername());
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        StoryEntity story = storyRepository.findByCode(request.getStoryCode());
        if (story == null) throw new AppException(ErrorCode.STORY_NOT_EXITS);

        FavoriteEntity favorite= favoriteRepository.findByUserAndStory(user,story);
        if (favorite == null) throw new AppException(ErrorCode.FAVORITE_NOT_EXIST);
        user.getFavorites().remove(favorite);
        story.getFavorites().remove(favorite);
        favoriteRepository.delete(favorite);

        log.info("Deleting favorite with ID: {}", favorite.getId());
//        favoriteRepository.delete(favorite);
        log.info("Deleted favorite successfully.");
//        favoriteRepository.delete(favorite);

        return FavoriteResponse.builder()
                .check(true)
                .build();
    }
    @Override
    public FavoriteResponse checkFavorite(FavoriteRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername());
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        StoryEntity story = storyRepository.findByCode(request.getStoryCode());
        if (story == null) throw new AppException(ErrorCode.STORY_NOT_EXITS);
        boolean check = favoriteRepository.existsByUserAndStory(user,story);
        return FavoriteResponse.builder()
                .check(check)
                .build();
    }

    @Override
    public List<StoryResponse> getAllFavoritesByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        List<FavoriteEntity> favorites = favoriteRepository.findAllByUser(user);
        List<StoryResponse> stories = new ArrayList<>();
        favorites.stream().map(FavoriteEntity::getStory).forEach(story -> {
            StoryEntity existed = storyRepository.findByCode(story.getCode());
            if (existed != null) {

                StoryResponse storyResponse = storyMapper.toResponse(existed);

                // Set generate cho từng storyResponse
                storyResponse.setGenerates(story.getGenerates().stream()
                        .map(GenerateEntity::getName)
                        .collect(Collectors.toSet()));

                // Có thể cân nhắc add thêm response chapter
                // ...

                stories.add(storyResponse);
            }
        });

        return stories;
    }
}
