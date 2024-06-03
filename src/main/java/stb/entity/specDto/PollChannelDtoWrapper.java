package stb.entity.specDto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PollChannelDtoWrapper {
    private UUID pollId;
    private List<PollChannelDto> wrapper;
}
