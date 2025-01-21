package com.ll.server.domain.news.news.controller;

import com.ll.server.domain.news.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/ai")
    public String gptApiTest(@RequestBody Map<String, String> request){
        String article1=request.getOrDefault("article1","");
        String article2=request.getOrDefault("article2","");
        if(article1==null || article1.isBlank()) {
            article1 = "도널드 트럼프 미국 대통령이 20일 열린 취임식에서 “미국의 황금기는 이제 막 시작되었다”고 연설하며 남부 국경에서의 국가 비상 사태를 선포했다.";
        }
        if(article2==null || article2.isBlank()) {
            article2 = "①인플레 종식 ②감세 ③국경강화 ④힘 통한 평화 ⑤에너지 패권 ⑥안전한 도시 홈페이지에 바이든 썼던 구호 재사용 “바이든 정책 모두 되돌리겠다” 시사";
        }
        return getAiResponse(article1,article2);
    }

    private String getAiResponse(String article1Summary, String article2Summary){
        String prompt="기사1: [summary1]\n기사2: [summary]\n\n기사1은 대한민국의 헤럴드 경제 언론사의 기사고, 기사2는 미국의 BBC 언론사의 기사야. 두 기사는 비슷한 주제를 다루는 기사야. 그 주제가 무엇인지 두 기사를 보고 알아낸 다음, 두 나라가 그 주제에 대해 어떤 시각을 가지는지 각각에 대해 다음과 같이 답을 만들어줘.\n"
                +"나라 - 언론사: (여기에 각 기사가 어떤 것을 중점으로 보고 있는지 작성해줘.)";
        prompt=prompt.replace("[summary1]",article1Summary).replace("[summary2]",article2Summary);
        return openAiChatModel.call(prompt);
    }


}
