package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.news.news.service.NewsService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class ApiV1NewsController {

    private final NewsService newsService;
    private final OpenAiChatModel openAiChatModel;


    @GetMapping("/getAll")
    public String getAll() {
        return newsService.getAll().toString();
    }

    //임시로 만든 함수. 실제 구현 시에는 이 부분은 LLM과의 통신을 하는 private 메서드가 될 예정이다.
    //실제 동작 시에는 /api/news/analyze에서 request로 넘어온 id들을 newsService에서 ID로 news를 하나하나 찾아서 List에 담고
    //analyze가 매핑된 메서드에서 해당 메서드를 호출하면서 news의 List를 넘긴다.
    //해당 메서드에서 내부에서 하나하나 DTO로 바꾼 뒤 getAiResponse를 호출해 그 값을 반환하면 된다.
    @PostMapping("/ai")
    public String gptApiTest(@RequestBody Map<String, String> request){
        return getAiResponse(extractNews(request));
    }

    @RequiredArgsConstructor
    @Builder
    @Getter
    static class NewsDTO{
        private final String country;
        private final String publisher;
        private final String summary;
    }

    private List<NewsDTO> extractNews(Map<String,String> articleMap){
        List<String> summaryList=new ArrayList<>();
        for(int i=0;i<articleMap.keySet().size();i++){
            String keyName="article"+(i+1);
            if(articleMap.containsKey(keyName)){
                String summary=articleMap.get(keyName);
                if(summary!=null && !summary.isBlank()) summaryList.add(articleMap.get(keyName));
            }
        }

        List<NewsDTO> newsList=new ArrayList<>();

        addArticle(newsList, summaryList);

        return newsList;
    }

    private void addArticle(List<NewsDTO> newsList,List<String> summaryList) {
        if(summaryList.size()<2) throw new RuntimeException("유효하지 않은 요청입니다.");

        for(int i=0;i< summaryList.size();i++) {
            newsList.add(
                    NewsDTO.builder()
                            .country("나라"+(i+1))
                            .publisher("언론사"+(i+1))
                            .summary(summaryList.get(i))
                            .build()
            );
        }
    }

    private String getAiResponse(List<NewsDTO> newsList){
        StringBuilder promptBuilder=new StringBuilder();
        for(int i=0;i<newsList.size();i++){
            promptBuilder.append("기사").append(i+1).append(": ").append(newsList.get(i).getSummary()).append("\n");
        }

        for(int i=0;i<newsList.size();i++){
            promptBuilder.append("기사").append(i+1).append("은 ")
                    .append(newsList.get(i).getCountry()).append(" ").append(newsList.get(i).getPublisher()).append("언론사의 기사 요약문");
            if(i<newsList.size()-1) {
                promptBuilder.append("이고, ");
            }else{
                promptBuilder.append("이야.\n");
            }
        }

        promptBuilder.append(newsList.size()).append("개 기사는 비슷한 주제를 다루는 기사들이야.\n");
        promptBuilder.append(newsList.size()).append("개 기사 요약문을 읽고 그 주제가 무엇인지 알아낸 다음, 주제를 간단히 서술해. 그 다음 각 기사에 대해 아래 양식만 써서 답변을 해.\n");
        promptBuilder.append("(나라 이름) - (언론사)\n");
        promptBuilder.append("강조 포인트: (기사에서 강조하는 부분이 어디인지로 채워줘)\n");
        promptBuilder.append("어조: (어떤 어조로 기사를 작성했는지로 채워줘)\n");
        promptBuilder.append("바라보는 관점: (어떤 관점으로 해당 주제를 바라보는지로 채워줘)\n");

        return openAiChatModel.call(promptBuilder.toString());
    }


}
