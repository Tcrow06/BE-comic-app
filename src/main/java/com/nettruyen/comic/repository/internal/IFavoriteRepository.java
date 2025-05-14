package com.nettruyen.comic.repository.internal;

import com.nettruyen.comic.entity.FavoriteEntity;
import com.nettruyen.comic.entity.StoryEntity;
import com.nettruyen.comic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFavoriteRepository extends JpaRepository<FavoriteEntity, String> {
    boolean existsByUserAndStory(UserEntity user, StoryEntity story);
    List<FavoriteEntity> findAllByUser(UserEntity user);

    FavoriteEntity findByUserAndStory(UserEntity user, StoryEntity story);
}
