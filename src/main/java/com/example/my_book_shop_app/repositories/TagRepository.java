package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.tags.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<TagsEntity, Integer> {

    @Query("select t.bookId from Book2TagsEntity t where t.tagId=:tagId")
    List<Integer> getBookIdListByTagId(int tagId);

    TagsEntity getTagsEntityById(int id);
}
