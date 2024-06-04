package org.stb.entity.specDto;

import lombok.Data;

import java.util.UUID;

@Data
public class PollChannelDto {
    private UUID callBackId;
    private UUID channelId;

}


