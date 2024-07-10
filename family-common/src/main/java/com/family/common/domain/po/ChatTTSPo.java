package com.family.common.domain.po;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ChatTTSPo {
    private String text;
    private String prompt = "[break_6]";
    private String voice = "4099";
    private double temperature = 0.00001;
    @SerializedName("top_p")
    private double topP = 0.7;
    @SerializedName("top_k")
    private int topK = 20;
    @SerializedName("refine_max_new_token")
    private String refineMaxNewToken = "384";
    @SerializedName("infer_max_new_token")
    private String inferMaxNewToken = "2048";
    @SerializedName("skip_refine")
    private int skipRefine = 1;
    @SerializedName("skip_infer")
    private int isSplit = 1;
    @SerializedName("custom_voice")
    private int customVoice = 0;
}
