package cn.org.moying.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public abstract class BaseEvent<T> {

    public abstract EventMessage<T> buildEventMessage(T data);

    public abstract String topic();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T> {
        private String id;
        private Date timestamp;
        private T data;
    }

}
