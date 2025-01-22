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
    //private final String promptTemplate;

    @GetMapping("/getAll")
    public String getAll() {
        return newsService.getAll().toString();
    }

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
        String article1=articleMap.getOrDefault("article1","");
        String article2=articleMap.getOrDefault("article2","");
        String article3=articleMap.getOrDefault("article3","");
        String article4=articleMap.getOrDefault("article4","");
        List<NewsDTO> newsList=new ArrayList<>();

        addArticle(newsList, article1,article2,article3,article4);

        return newsList;
    }

    private void addArticle(List<NewsDTO> newsList,String... articles) {
        if(articles.length<2) throw new RuntimeException("유효하지 않은 요청입니다.");

        String[] mockCountry={"한국","미국","중국","일본"};
        String[] mockPublisher={"연합뉴스","BBC","CCTV","아사히"};
        for(int i=0;i< articles.length;i++) {
            newsList.add(
                    NewsDTO.builder()
                            .country(mockCountry[i])
                            .publisher(mockPublisher[i])
                            .summary(articles[i])
                            .build()
            );
        }
    }

    private String getAiResponse(List<NewsDTO> newsList){
        StringBuilder promptBuilder=new StringBuilder();
        for(int i=0;i<newsList.size();i++){
            String line="기사"+(i+1)+": "+newsList.get(i).getSummary()+"\n";
            promptBuilder.append(line);
        }

        for(int i=0;i<newsList.size();i++){
            String line=null;
            if(i<newsList.size()-1) {
                line = "기사" + (i + 1) + "은 " + newsList.get(i).getCountry() + " " + newsList.get(i).getPublisher() + " 언론사의 기사 요약문이고, ";
            }else{
                line = "기사" + (i + 1) + "은 " + newsList.get(i).getCountry() + " " + newsList.get(i).getPublisher() + " 언론사의 기사 요약문이야. ";
            }
            promptBuilder.append(line);
        }


//        String prompt=promptTemplate.replace("[summary1]",article1Summary).replace("[summary2]",article2Summary)
//                .replace("[publisher1]","언론사").replace("[country2]","미국").replace("[publisher2]","언론사");

//        String prompt=
//                "기사1: [summary1]\n기사2: [summary2]\n\n기사1은 대한민국 언론사의 기사의 요약문이고, 기사2는 미국의 언론사의 기사의 요약문이야. 두 기사는 비슷한 주제를 다루는 기사야. 두 요약문을 읽고 그 주제가 무엇인지 알아낸 다음, 주제를 간단히 서술해. 그 다음 각 기사에 대해 아래 양식만 써서 답변을 해.\n"
//                        +"나라 이름\n"
//                        +"강조 포인트: (기사에서 강조하는 부분이 어디인지로 채워줘)\n"
//                        +"어조: (어떤 어조로 기사를 작성했는지로 채워줘)\n"
//                        +"바라보는 관점: (어떤 관점으로 해당 주제를 바라보는지로 채워줘)\n"
//                ;
        //return openAiChatModel.call(prompt);
        return null;
    }


}
