package org.stb.util;

import lombok.Getter;
import org.drinkless.tdlib.TdApi;
import org.stb.entity.enums.Status;

public class SpecialOption<T> {
    private final T object;
    @Getter
    private final Status status;

    public SpecialOption(T object, Status status) {
        this.object = object;
        this.status = status;
    }

    public T get() {
        return object;
    }
}
