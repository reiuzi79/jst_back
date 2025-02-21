package com.su.JobTracker.helper;
import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LLMHelper {
	private final String api_key = "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";
	private final String endpoint = "https://api.openai.com/v1/chat/completions";
	private final String systemPrompt = "あなたは求人作成をサポートするAIアシスタント  \r\n"
			+ "以下の方針に従い、ユーザーからの入力に回答せよ\r\n"
			+ "\r\n"
			+ "1. 回答は基本的に若い日本人に好まれるカジュアルで明るい口調を使う  \r\n"
			+ "   - ただし、ユーザーが「厳格な文体」「フォーマルな文体」などを明示的に求める場合は、それに合わせた文体・言葉遣いに切り替える\r\n"
			+ "\r\n"
			+ "2. ユーザーの入力内容と意図に応じ、以下の2つを判断  \r\n"
			+ "   (a) 「入力内容をそのまま少し良くしたい」(例: 文法・表現・用語のブラッシュアップ)  \r\n"
			+ "   (b) 「より充実した内容を作ってほしい」(例: キーワードだけ与えられた場合、または“もっと詳しく書いて”と依頼された場合)  \r\n"
			+ "\r\n"
			+ "   要求が(a)の場合、言い回しや語彙などを修正・改善せよ  \r\n"
			+ "   要求が(b)の場合、与えられた情報に加え、自然に推測できる情報や追加要素を盛り込み、より完成度の高い文章に仕上げろ  \r\n"
			+ "\r\n"
			+ "3. 以下の5つの項目に関する要望・質問が対象のみ  \r\n"
			+ "   - タイトル（求人のタイトル）  \r\n"
			+ "   - 給与範囲  \r\n"
			+ "   - 仕事内容  \r\n"
			+ "   - 職務内容  \r\n"
			+ "   - 応募要件  \r\n"
			+ "\r\n"
			+ "4. 「職務内容」(responsibilities)は「仕事内容」(description)と似ているが、「職務内容」がより具体的・専門的な業務記述やポジションごとの職務範囲などを想定  \r\n"
			+ "5. 会話は一回だけ、変更が必要な場合は新しい会話を作るなので、改善した内容だけで返事　\r\n";
	public String sendGPTJob(int type, String userPrompt, String companyName){
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
	    root.put("model", "gpt-4o-mini");
	    ArrayNode messages = mapper.createArrayNode();
	    ObjectNode sysMsg = mapper.createObjectNode();
	    sysMsg.put("role", "system");
	    sysMsg.put("content", systemPrompt);
	    messages.add(sysMsg);
	    String prePrompt = buildPrePrompt(type, userPrompt, companyName);
	    ObjectNode userMsg = mapper.createObjectNode();
	    userMsg.put("role", "user");
	    userMsg.put("content", prePrompt);
	    messages.add(userMsg);
	    root.set("messages", messages);
        try {
        	String jsonBody = mapper.writeValueAsString(root);
            // 创建 HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // 构建请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + api_key)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 输出响应
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return getGPTAnswer(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
	}
	
	private String getGPTAnswer(String responseBody) {
        try {
            // 创建一个 Jackson 的 ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            // 读取 JSON 字符串为树状结构
            JsonNode root = mapper.readTree(responseBody);

            // 找到 "choices" 数组
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                // 取第 0 个元素
                JsonNode firstChoice = choices.get(0);

                // 在 firstChoice 下找到 "message" 字段
                JsonNode message = firstChoice.get("message");
                if (message != null && message.has("content")) {
                    // 取 "content" 的值
                    return message.get("content").asText();
                }
            }
            // 如果解析不到，就返回空字符串或自行处理
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
	}
	
	private String buildPrePrompt(int type, String userPrompt, String companyName) {
		String prePrompt = "";
		switch(type) {
		case 1:
			prePrompt = "【役割】タイトルの作成・編集  \r\n"
					+ "【文体】日本の若い人に好まれるカジュアルで魅力的な口調（ユーザーが厳格さやフォーマルさを希望する場合は、その要望に合わせる）  \r\n"
					+ "【タスク】  \r\n"
					+ "以下のユーザー入力を参考に、\r\n"
					+ "- ユーザーの意図を「最適化」(文言の改善)か「より内容を充実」かで判断する  \r\n"
					+ "- 適切にタイトルを作成またはリライトする  \r\n"
					+ "- 若者受けするキャッチーなフレーズを工夫する  \r\n"
					+ "\r\n"
					+ "【ユーザーの会社名】  \r\n"
					+ companyName
					+ "\r\n"
					+ "【ユーザー入力】  \r\n"
					+ "{{"+ userPrompt +"}}\r\n"
					+ "\r\n"
					+ "【注意】  \r\n"
					+ "- 「タイトル」に関する内容以外だと判断した場合は回答を拒否する  \r\n"
					+ "- 出力は必ず日本語にする  \r\n"
					+ "\r\n"
					+ "【出力指示】  \r\n"
					+ "- タイトルのみを出力してもOKですし、希望に応じて短い解説や追加ポイントを含めても構いません  \r\n"
					+ "- ただし過度に長くならないように注意する  \r\n"
					+ "- 改善した内容だけで返事。変更についての質問は不要  \r\n";
			break;
		case 2:
			prePrompt = "【役割】給与範囲の作成・編集  \r\n"
					+ "【文体】日本の若い人に好まれるカジュアルでポジティブなトーン（ただしユーザーがフォーマルや厳格な文体を求める場合はそれに合わせる）  \r\n"
					+ "【タスク】  \r\n"
					+ "以下のユーザー入力を参考に、\r\n"
					+ "- ユーザーの意図を「最適化」(表現修正)か「より内容を充実」かで判断する  \r\n"
					+ "- 給与範囲について、魅力的かつ分かりやすい表記に書き直す  \r\n"
					+ "  (例: 月給◯万円～◯万円スタート、経験・能力に応じて優遇など)  \r\n"
					+ "- 若者が気になるポイント（初任給、昇給タイミング、手当の種類、福利厚生など）があれば補足的に入れる  \r\n"
					+ "\r\n"
					+ "【ユーザーの会社名】  \r\n"
					+ companyName
					+ "\r\n"
					+ "【ユーザー入力】  \r\n"
					+ "{{"+ userPrompt +"}}\r\n"
					+ "\r\n"
					+ "【注意】  \r\n"
					+ "- 「給与範囲」に関する内容以外だと判断した場合は回答を拒否する  \r\n"
					+ "- 出力は必ず日本語にする  \r\n"
					+ "\r\n"
					+ "【出力指示】  \r\n"
					+ "- シンプルに「給与例：●万円〜●万円」などを示すだけでも良い  \r\n"
					+ "- ユーザーがより詳しい内容を求めている場合は、可能な範囲で追加の説明を入れる  \r\n"
					+ "- 改善した内容だけで返事。変更についての質問は不要  \r\n";
			break;
		case 3:
			prePrompt = "【役割】仕事内容の作成・編集  \r\n"
					+ "【文体】日本の若い人に好まれる話し方（またはユーザーが希望する文体）  \r\n"
					+ "【タスク】  \r\n"
					+ "以下のユーザー入力を参考に、\r\n"
					+ "- 「最適化」か「内容を充実」かを判断  \r\n"
					+ "- 求人の「仕事内容」をわかりやすく、魅力的に記述する  \r\n"
					+ "- 若者にアピールできるやりがい・楽しさや、業務フローのイメージをふんわり伝える  \r\n"
					+ "\r\n"
					+ "【ユーザーの会社名】  \r\n"
					+ companyName
					+ "\r\n"
					+ "【ユーザー入力】  \r\n"
					+ "{{"+ userPrompt +"}}\r\n"
					+ "\r\n"
					+ "【注意】  \r\n"
					+ "- 「仕事内容」に関する内容以外だと判断した場合は回答を拒否する  \r\n"
					+ "- 出力は必ず日本語にする  \r\n"
					+ "\r\n"
					+ "【出力指示】  \r\n"
					+ "- できるだけ具体的な業務内容や、やりがい、キャリアパスなどのヒントを盛り込む  \r\n"
					+ "- 長すぎない程度に要点をうまくまとめる  \r\n"
					+ "- 改善した内容だけで返事。変更についての質問は不要  \r\n";
			break;
		case 4:
			prePrompt = "【役割】職務内容の作成・編集  \r\n"
					+ "【文体】基本は若い人に好まれる口調（ユーザーが希望する場合はフォーマルに）  \r\n"
					+ "【タスク】  \r\n"
					+ "以下のユーザー入力を踏まえ、\r\n"
					+ "- 「最適化」か「内容を充実」かを判別  \r\n"
					+ "- 職務内容を具体的に記述する（例：開発なら使用ツール、営業なら具体的な活動内容など）  \r\n"
					+ "- ユーザーがキーワードだけを入力している場合は推測で補完しつつ、専門用語をやさしく解説する  \r\n"
					+ "\r\n"
					+ "【ユーザーの会社名】  \r\n"
					+ companyName
					+ "\r\n"
					+ "【ユーザー入力】  \r\n"
					+ "{{"+ userPrompt +"}}\r\n"
					+ "\r\n"
					+ "【注意】  \r\n"
					+ "- 「職務内容」に関する内容以外だと判断した場合は回答を拒否する  \r\n"
					+ "- 出力は必ず日本語にする  \r\n"
					+ "\r\n"
					+ "【出力指示】  \r\n"
					+ "- 必要に応じて短い見出しや箇条書きを使い、分かりやすくする  \r\n"
					+ "- あまりにも推測が大きくなりすぎないよう注意  \r\n"
					+ "- 改善した内容だけで返事。変更についての質問は不要  \r\n";
			break;
		case 5:
			prePrompt = "【役割】応募要件の作成・編集  \r\n"
					+ "【文体】若い日本人に好まれるフレンドリーさをベース（ユーザーの要望があれば柔軟に調整）  \r\n"
					+ "【タスク】  \r\n"
					+ "以下のユーザー入力を参考に、\r\n"
					+ "- 「最適化」か「内容を充実」かを判断  \r\n"
					+ "- 応募要件（必須スキル・歓迎スキル、資格、経験年数など）をわかりやすく書く  \r\n"
					+ "- 若者が興味を持ちやすい言い回しやキャッチコピーを取り入れる  \r\n"
					+ "\r\n"
					+ "【ユーザーの会社名】  \r\n"
					+ companyName
					+ "\r\n"
					+ "【ユーザー入力】  \r\n"
					+ "{{"+ userPrompt +"}}\r\n"
					+ "\r\n"
					+ "【注意】  \r\n"
					+ "- 「応募要件」に関する内容以外だと判断した場合は回答を拒否する  \r\n"
					+ "- 出力は必ず日本語にする  \r\n"
					+ "\r\n"
					+ "【出力指示】  \r\n"
					+ "- あまり長くなりすぎず、ポイントをまとめる  \r\n"
					+ "- ユーザーがフォーマルを求めるならそれに合わせる  \r\n"
					+ "- 改善した内容だけで返事。変更についての質問は不要  \r\n";
			break;
			default:
				return "";
		}
		return prePrompt;
	}
}
