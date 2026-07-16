package com.gsvn.promotionservice.mapper;


import com.gsvn.promotionservice.model.entity.Inbox;
import com.gsvn.promotionservice.model.entity.Outbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface MessageLogMapper {

    int insertOutbox(Outbox outbox);

    List<Outbox> findPendingOutbox(@Param("limit") int limit);

    int updateOutboxStatus(@Param("id") String id,
                           @Param("status") String status,
                           @Param("retryCount") Integer retryCount);

    int insertInbox(Inbox inbox);

    Optional<Inbox> findInboxById(@Param("eventId") String eventId);

    int updateInboxStatus(@Param("eventId") String eventId,
                          @Param("status") String status,
                          @Param("errorLog") String errorLog);
    List<Inbox> findPendingInbox(@Param("limit") int limit);
}