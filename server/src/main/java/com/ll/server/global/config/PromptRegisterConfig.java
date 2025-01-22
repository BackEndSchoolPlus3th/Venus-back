package com.ll.server.global.config;

import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class PromptRegisterConfig {

    //@Bean
    public String promptTemplate(){
        String filePath="server/src/main/resources/prompt.txt";
        File promptFile=new File(filePath.replace("/",File.separator));
        BufferedReader br=null;

        StringBuilder sb=new StringBuilder();
        try {
            br=new BufferedReader(new FileReader(promptFile));
            String line;
            while((line=br.readLine())!=null){
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("파일 경로가 잘못 되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기에 실패했습니다.");
        }

        return sb.toString();
    }
}
