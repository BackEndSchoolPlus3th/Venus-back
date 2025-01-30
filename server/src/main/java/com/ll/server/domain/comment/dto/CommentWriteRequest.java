package com.ll.server.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentWriteRequest {
    private String mentions;
    private String content;
    private Long writerId;

    public List<String> getMentionedNames(){
        if(mentions==null || mentions.isBlank()) return null;

        String[] mentionArr=mentions.strip().split(",");
        List<String> metionList=new ArrayList<>();

        for(String mention:mentionArr){
            if(mention==null || mention.isBlank()) continue;

            metionList.add(mention.strip());
        }

        return metionList;
    }
}
