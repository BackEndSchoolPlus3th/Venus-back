package com.ll.server.domain.repost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RepostWriteRequest {
    private Long newsId;
    private Long writerId;
    private String content;
    private String mentions;

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
