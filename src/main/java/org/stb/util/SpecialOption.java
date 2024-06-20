package org.stb.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.drinkless.tdlib.TdApi;
import org.stb.entity.enums.Status;

@AllArgsConstructor
@Getter
public class SpecialOption {
    TdApi.Message message;
    Status status;
}
