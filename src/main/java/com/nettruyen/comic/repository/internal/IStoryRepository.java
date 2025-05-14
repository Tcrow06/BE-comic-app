package com.nettruyen.comic.repository.internal;

import com.nettruyen.comic.entity.StoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface IStoryRepository extends JpaRepository<StoryEntity, String> {

    StoryEntity findByTitle(String title);

    StoryEntity findByCode(String code);

    StoryEntity findByAuthor(String author);

    @Query("SELECT s FROM tbl_story s JOIN s.generates g WHERE g.code IN :generatesCode")
    List<StoryEntity> findAllByGeneratesCode(@RequestParam("generatesCode") List<String> generatesCode);
    @Query("SELECT s FROM tbl_story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StoryEntity> searchStory(@RequestParam("keyword") String keyword);
}
