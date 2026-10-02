package com.teacat.controller;

import com.teacat.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final AuthService auth;
    public AiController(AuthService auth){this.auth=auth;}

    public static class AnalyzeRequest { public String text; }
    public static class AnalyzeResponse { public String summary; public List<String> highlights; public String disclaimer; }

    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@RequestBody AnalyzeRequest request,@RequestHeader("Authorization") String h){
        auth.requireUser(h);
        String text=request.text==null?"":request.text.trim();
        if(text.isEmpty()) throw new IllegalArgumentException("請輸入報告文字");
        List<String> hits=new ArrayList<>(); String lower=text.toLowerCase(Locale.ROOT);
        Map<String,String> terms=new LinkedHashMap<>();
        terms.put("wbc","WBC／白血球"); terms.put("rbc","RBC／紅血球"); terms.put("creatinine","Creatinine／肌酸酐");
        terms.put("alt","ALT／肝指數"); terms.put("glucose","Glucose／血糖"); terms.put("vaccine","疫苗"); terms.put("weight","體重");
        terms.forEach((k,v)->{if(lower.contains(k)) hits.add("偵測到「"+v+"」相關內容");});
        if(hits.isEmpty()) hits.add("已讀取報告文字，未偵測到預設的常見檢驗關鍵字");
        AnalyzeResponse r=new AnalyzeResponse();
        r.summary="TeaCat 已將輸入內容整理為「"+Math.min(text.length(),9999)+" 字」的健康文件，請搭配下方重點與原始報告核對。";
        r.highlights=hits;
        r.disclaimer="此功能目前為作品集示範版的文字整理工具，不提供診斷；醫療判讀請以獸醫師意見為準。";
        return r;
    }
}
