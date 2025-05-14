package com.nettruyen.comic.service.impl;

import com.nettruyen.comic.dto.request.other.CommentRequest;
import com.nettruyen.comic.dto.response.other.CommentResponse;
import com.nettruyen.comic.entity.ChapterEntity;
import com.nettruyen.comic.entity.CommentEntity;
import com.nettruyen.comic.entity.StoryEntity;
import com.nettruyen.comic.entity.UserEntity;
import com.nettruyen.comic.exception.AppException;
import com.nettruyen.comic.exception.ErrorCode;
import com.nettruyen.comic.mapper.CommentMapper;
import com.nettruyen.comic.mapper.UserMapper;
import com.nettruyen.comic.repository.internal.IChapterRepository;
import com.nettruyen.comic.repository.internal.ICommentRepository;
import com.nettruyen.comic.repository.internal.IStoryRepository;
import com.nettruyen.comic.repository.internal.IUserRepository;
import com.nettruyen.comic.service.ICommentService;
import com.nettruyen.comic.service.IStoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements ICommentService {

    ICommentRepository commentRepository;
    IStoryRepository storyRepository;
    IUserRepository userRepository;
    CommentMapper commentMapper;
    UserMapper userMapper;

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        UserEntity user = userRepository.findByUsername(commentRequest.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        StoryEntity story = storyRepository.findByCode(commentRequest.getStoryCode());
//                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXITS));
        if(story == null){
            new AppException(ErrorCode.STORY_NOT_EXITS);
        }
        CommentEntity comment = commentMapper.toEntity(commentRequest);
        comment.setStory(story);
        comment.setUser(user);

        commentRepository.save(comment);

        CommentResponse result = CommentResponse.builder()
                .storyId(comment.getStory().getId())
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .build();
        return result;
    }

    @Override
    public List<CommentResponse> getAllCommentsOfStory(String storyCode) {

        if (storyCode == null) throw new RuntimeException("StoryId is null or invalid");

        List<CommentResponse> results = new ArrayList<>();

        StoryEntity story = storyRepository.findByCode(storyCode);

        if (story == null){
            throw new AppException(ErrorCode.STORY_NOT_EXITS);
        }
        List<CommentEntity> commentEntities = commentRepository.findAllByStory(story);
        for (CommentEntity commentEntity : commentEntities) {
            CommentResponse response = commentMapper.toResponse(commentEntity);
            response.setId(commentEntity.getId());
            response.setUsername(commentEntity.getUser().getUsername());
            response.setStoryId(commentEntity.getStory().getId());
            results.add(response);
        }

        return results;
    }


//    @Override
//    public CommentResponse createComment(CommentRequest commentRequest) {
//
//        // Độ sâu tối đa
//        int MAX_DEPTH = 5;
//
//        try {
//            UserEntity user = userRepository.findById(commentRequest.getUserId())
//                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//            ChapterEntity chapter = chapterRepository.findById(commentRequest.getChapterId())
//                    .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXITS));
//
//            CommentEntity parent = null;
//            if (commentRequest.getParentId() != null) {
//                parent = commentRepository.findById(commentRequest.getParentId())
//                        .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED));
//                if (parent.getDepth() > MAX_DEPTH) {
//                    log.error("Đã vượt quá độ sâu tối đa!");
//                    throw new AppException(ErrorCode.UNCATEGORIZED);
//                }
//            }
//
//            CommentEntity comment = CommentEntity.builder()
//                    .content(commentRequest.getContent())
//                    .chapter(chapter)
//                    .user(user)
//                    .parent(parent)
//                    .depth(parent != null ? parent.getDepth() + 1 : 0)
//                    .build();
//
//            CommentEntity savedComment = commentRepository.save(comment);
//
//            var commentResponse = commentMapper.toResponse(savedComment);
//            commentResponse.setUser(userMapper.toUserResponse(savedComment.getUser()));
//            commentResponse.setChapterId(savedComment.getChapter().getId());
//            commentResponse.setCreateAt(savedComment.getCreatedAt());
//            commentResponse.setParentId(
//                    savedComment.getParent() != null
//                            ? savedComment.getParent().getId()
//                            : null);
//
//            return commentResponse;
//
//        } catch (Exception e) {
//            throw new AppException(ErrorCode.UNCATEGORIZED);
//        }
//
//    }

//    @Override
//    public List<CommentResponse> getAllCommentForChapter(String chapterId) {
//
//        // Tìm kiếm chapter truyện
//        ChapterEntity chapterEntity = chapterRepository.findById(chapterId)
//                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXITS));
//
//        // Lấy danh sách các bình luận gốc
//        List<CommentEntity> commentEntityList =
//                commentRepository.findByChapterAndParentIsNullOrderByCreatedAtDesc(chapterEntity);
//
//        if (!commentEntityList.isEmpty()) {
//            return commentEntityList.stream().map(this::convertToResponse).toList();
//        }
//        return List.of();
//    }

//    private CommentResponse convertToResponse(CommentEntity commentEntity) {
//        var commentResponse = commentMapper.toResponse(commentEntity);
//
//        // User được map sẽ bị trống role
//        commentResponse.setUser(userMapper.toUserResponse(commentEntity.getUser()));
//        commentResponse.setChapterId(commentEntity.getChapter().getId());
//        commentResponse.setCreateAt(commentEntity.getCreatedAt());
//        commentResponse.setParentId(
//                commentEntity.getParent() != null
//                        ? commentEntity.getParent().getId()
//                        : null);
//
//        // Set danh sách comment con qua đệ quy
//        loadChildrenComment(commentEntity, commentResponse);
//
//        return commentResponse;
//    }

//    // Load children for comment response
//    private void loadChildrenComment(CommentEntity commentEntity, CommentResponse commentResponse) {
//        List<CommentEntity> replies = commentRepository.findByParentOrderByCreatedAtAsc(commentEntity);
//
//        if (!replies.isEmpty()) {
//            List<CommentResponse> commentResponsesList = replies.stream()
//                    .map(this::convertToResponse)
//                    .toList();
//            commentResponse.setReplies(commentResponsesList);
//        }
//    }
}
