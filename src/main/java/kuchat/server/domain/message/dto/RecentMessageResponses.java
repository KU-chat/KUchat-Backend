package kuchat.server.domain.message.dto;

import kuchat.server.common.response.BaseResponse;
import org.springframework.data.domain.Page;

public class RecentMessageResponses extends BaseResponse {

    Page<RecentMessageResponse> messageResponses;

    public static class RecentMessageResponse{

    }
}
